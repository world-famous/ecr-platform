package com.tianwen.springcloud.ecrapi.controller;

import ch.qos.logback.core.util.TimeUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.tianwen.springcloud.commonapi.base.response.Response;
import com.tianwen.springcloud.commonapi.base.response.ServerResult;
import com.tianwen.springcloud.commonapi.constant.IStateCode;
import com.tianwen.springcloud.commonapi.exception.ParameterException;
import com.tianwen.springcloud.commonapi.log.SystemControllerLog;
import com.tianwen.springcloud.commonapi.query.OrderMethod;
import com.tianwen.springcloud.commonapi.query.Pagination;
import com.tianwen.springcloud.commonapi.query.QueryCondition;
import com.tianwen.springcloud.commonapi.query.QueryTree;
import com.tianwen.springcloud.commonapi.utils.ValidatorUtil;
import com.tianwen.springcloud.ecrapi.api.ResourceRestApi;
import com.tianwen.springcloud.ecrapi.constant.ICommonConstants;
import com.tianwen.springcloud.ecrapi.constant.IErrorMessageConstants;
import com.tianwen.springcloud.ecrapi.util.*;
import com.tianwen.springcloud.microservice.activity.constant.IActivityMicroConstants;
import com.tianwen.springcloud.microservice.activity.entity.Activity;
import com.tianwen.springcloud.microservice.base.constant.IBaseMicroConstants;
import com.tianwen.springcloud.microservice.base.entity.*;
import com.tianwen.springcloud.microservice.bussinessassist.api.ResourceCollectionMicroApi;
import com.tianwen.springcloud.microservice.bussinessassist.api.ResourceStarMicroApi;
import com.tianwen.springcloud.microservice.bussinessassist.api.ResourceViewMicroApi;
import com.tianwen.springcloud.microservice.bussinessassist.api.ResourceVoteMicroApi;
import com.tianwen.springcloud.microservice.bussinessassist.entity.*;
import com.tianwen.springcloud.microservice.operation.constant.IOperationConstants;
import com.tianwen.springcloud.microservice.operation.entity.Integral;
import com.tianwen.springcloud.microservice.operation.entity.Member;
import com.tianwen.springcloud.microservice.resource.constant.IResourceMicroConstants;
import com.tianwen.springcloud.microservice.resource.entity.ExportMan;
import com.tianwen.springcloud.microservice.resource.entity.FileInfo;
import com.tianwen.springcloud.microservice.bussinessassist.entity.Good;
import com.tianwen.springcloud.microservice.resource.entity.Resource;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.util.*;

import java.io.*;
import java.nio.file.*;
import java.util.zip.*;


@RestController
@RequestMapping(value = "/resource")
public class ResourceRestController extends BaseRestController implements ResourceRestApi
{
    @Autowired
    private ResourceVoteMicroApi resourceVoteMicroApi;

    @Autowired
    private ResourceViewMicroApi resourceViewMicroApi;

    @Autowired
    private ResourceStarMicroApi resourceStarMicroApi;

    @Autowired
    private ResourceCollectionMicroApi resourceCollectionMicroApi;

    private static final String EXCEL_END_COMMENT = "打*标红的是必填字段";

    @Override
    public Response getResource(@PathVariable(value = "contentid") String contentid) {
        Response<Resource> response = resourceMicroApi.get(contentid);
        Resource resource = response.getResponseEntity();
        if (resource != null) {
            validateResource(resource);
        }
        return response;
    }

    @Override
    public Response uploadResource(@RequestBody  Resource resource, @RequestHeader  String token) {
        if (esSyncFlag.equalsIgnoreCase(ICommonConstants.ES_SAVING_FLAG))
            return new Response<>(IErrorMessageConstants.ERR_CODE_ES_SAVING_MODIFY_DISABLE, IErrorMessageConstants.ERR_MSG_ES_SAVING_MODIFY_DISABLE);

        Response<UserLoginInfo> userResponse = getUserByToken(token);
        UserLoginInfo userLoginInfo = userResponse.getResponseEntity();
        if (userLoginInfo == null) return userResponse;

        /**
         * @author: jong
         * @date: 2019-02-08, 02-11
         * @comment: check sensitive words
         */
        SensitiveWordFilter swFilter = new SensitiveWordFilter();
        resource.setName(swFilter.process(resource.getName()));
        resource.setDescription(swFilter.process(resource.getDescription()));
        /*if (swFilter.getSensitiveWordList().size() > 0) {
            return new Response<>(IErrorMessageConstants.ERR_CODE_SENSITIVE_WORD_DETECTED,
                    IErrorMessageConstants.ERR_MSG_SENSITIVE_WORD_DETECTED + ": " + swFilter.getSensitiveWordList().get(0));
        }*/
            /*
             * ----------------------------------------------------------------------
             */

        //set creator id by token
        resource.setCreator(userLoginInfo.getUserId());

        String sharerangekey = getSharerangeKey(userLoginInfo);
        resource.setSharerangekey(sharerangekey);

        //validate subject navigation to resource
        List<String> catalogIds = resource.getCatalogids();
        if (!CollectionUtils.isEmpty(catalogIds)) {
            String catalogid = catalogIds.get(0);
            if (!StringUtils.isEmpty(catalogid)) {
                Catalog catalog = catalogMicroApi.get(catalogid).getResponseEntity();
                if (catalog != null) {
                    QueryTree bookQuery = new QueryTree();
                    bookQuery.addCondition(new QueryCondition("bookid", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, catalog.getBookid()));
                    List<Book> books = bookMicroApi.getList(bookQuery).getPageInfo().getList();
                    if (CollectionUtils.isEmpty(books)) {
                        Book book = books.get(0);
                        resource.setEditiontypeid(book.getEditiontypeid());
                        resource.setBookmodelid(book.getBookmodelid());
                    }
                }
            }
        }

        //save resource
        resource.setImportstatus(IResourceMicroConstants.RES_IMPORT_STATUS_SUCCESS);
        resource.setStatus(IResourceMicroConstants.RES_STATUS_HOLD);
        resource.setIsgoods(IResourceMicroConstants.RES_SOURCE_NOT_GOODS);
        Response<Resource> resp = resourceMicroApi.add(resource);
        Resource entity = resp.getResponseEntity();

        FileInfo fileinfo = resource.getFileInfo();

        if (fileinfo != null) {
            fileinfo = fileMicroApi.add(fileinfo).getResponseEntity();
            if (fileinfo == null) {
                throw new ParameterException(IStateCode.PARAMETER_IS_EMPTY, IErrorMessageConstants.ERR_MSG_RESOURCE_ALREADY_EXIST);
            }

            resource.setFileInfo(fileinfo);
            reconnectFileinfo(resource);
        }

        reconnectBookChapter(entity, catalogIds);
        logOptionEntity(entity.getContentid(), entity.getContentid(), ICommonConstants.OPTION_OPTIONTYPE_UPLOAD, token);
        return resp;
    }

    @Override
    public Response uploadCollectionResource(@RequestBody  Resource resource, @RequestHeader  String token) {
        resource.setSourceid(IResourceMicroConstants.RES_SOURCE_COLLECTION);
        resource.setSharerange(IResourceMicroConstants.RES_SHARE_RANGE_ALL_NET);
        return uploadResource(resource, token);
    }

    @Override
    public Response uploadRewardResource(@RequestBody  Resource resource, @RequestHeader  String token) {
        resource.setSourceid(IResourceMicroConstants.RES_SOURCE_REWARD);
        resource.setSharerange(IResourceMicroConstants.RES_SHARE_RANGE_ALL_NET);
        return uploadResource(resource, token);
    }

    @Override
    public Response uploadEstimateResource(@RequestBody Resource resource, @RequestHeader String token) {
        resource.setSharerange(IResourceMicroConstants.RES_SHARE_RANGE_ALL_NET);

        Response<UserLoginInfo> userResp = getUserByToken(token);
        if (!userResp.getServerResult().getResultCode().equalsIgnoreCase(IErrorMessageConstants.OPERATION_SUCCESS))
            return userResp;

        UserLoginInfo userLoginInfo = userResp.getResponseEntity();
        List<Role> list = userMicroApi.getUserRole(userLoginInfo.getUserId()).getPageInfo().getList();

        if (list == null)
            list = new ArrayList<>();
        boolean isTeacher = false;

        for(Role item : list) {
            if (item.getRoleid().equalsIgnoreCase(ICommonConstants.ECO_USER_ROLE_TEACHER)) {
                isTeacher = true;
                break;
            }
        }

        if (isTeacher != true)
            return new Response(IStateCode.PARAMETER_IS_INVALID, IErrorMessageConstants.ERR_MSG_PERMISSION);

        UserLoginInfo teacher = new UserLoginInfo();
        teacher.setUserId(userLoginInfo.getUserId());
        teacher.setLastuploadtime(new Timestamp(System.currentTimeMillis()));
        userMicroApi.update(teacher);

        resource.setSourceid(IResourceMicroConstants.RES_SOURCE_ESTIMATE);
        return uploadResource(resource, token);
    }

    public Resource getHotValue(Resource resource)
    {
        int hotVal = resourceVoteMicroApi.getHotValue(resource.getContentid()).getResponseEntity();
        resource.setHotvalue(hotVal);
        return resource;
    }

    @Override
    @RequestMapping(value = "/getResourceList", method = RequestMethod.POST)
    public Response getResourceList(@RequestBody QueryTree queryTree, @RequestHeader  String token) {
        fixResourceQueryTree(queryTree);

        //author:han 韩哲国
        //reason:add filter by 作者
        if(queryTree.getQueryCondition("searchkey") != null && queryTree.getQueryCondition("searchkey").getFieldValues()[0] != ""){
            List<String> userIds = userMicroApi.getUserIdsByQueryTreeExtra(queryTree).getPageInfo().getList();
            String creators = "";
            for(String id: userIds){
                creators += id + " ";
            }
            creators.trim();
            queryTree.addCondition(new QueryCondition("creatorswithkey", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, creators));
        }
        //end han

        Response<Resource> resp = resourceMicroApi.getList(queryTree);
        //author:han 韩哲国
        //reason: get activityname from activityid
        List<Resource> mylist = resp.getPageInfo().getList();
        for(int i = 0;i < mylist.size();i++){
            String activityid = mylist.get(i).getActivityid();
            if(activityid != null){
                Activity activity  = activityMicroApi.get(activityid).getResponseEntity();
                if(activity != null){
                    mylist.get(i).setActivityName(activity.getActivityname());
                } else{
                    mylist.get(i).setActivityName("");
                }
            } else{
                mylist.get(i).setActivityName("");
            }
        }
        resp.getPageInfo().setList(mylist);
        //end han
        List<Resource> resList = resp.getPageInfo().getList();
        if (!CollectionUtils.isEmpty(resList))
            for(Resource res : resList)
                validateResource(res);

        return resp;
    }

    @Override
    @RequestMapping(value = "/getUploadResourceList", method = RequestMethod.POST)
    public Response getUploadResourceList(@RequestBody QueryTree queryTree, @RequestHeader  String token) {
        List<String> sourceids = new ArrayList<>();
        sourceids.add(IResourceMicroConstants.RES_SOURCE_UPLOAD);
        sourceids.add(IResourceMicroConstants.RES_SOURCE_TIANWEN);
        QueryCondition queryCondition = new QueryCondition("sourceids", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, sourceids);
        queryTree.getConditions().add(queryCondition);
        return getResourceList(queryTree, token);
    }

    @Override
    public Response getUploadResourceListByUser(@RequestBody QueryTree queryTree, @RequestHeader  String token) {
        UserLoginInfo user = getUserByToken(token).getResponseEntity();
        QueryCondition queryCondition = new QueryCondition("creator", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, user.getUserId());
        queryTree.getConditions().add(queryCondition);
        Response<Resource> resourceResponse = getResourceList(queryTree, token);
        List<Resource> uploadedList = resourceResponse.getPageInfo().getList();
        if (!CollectionUtils.isEmpty(uploadedList)) {
            for (Resource resource : uploadedList) {
                Audit audit = auditMicroApi.getByContentid(resource.getContentid()).getResponseEntity();
                if (audit != null) {
                    resource.setAuditinfo(audit);
                }
            }
        }
        return resourceResponse;
    }

    @Override
    @RequestMapping(value = "/isAlreadyDownload/{contentid}", method = RequestMethod.GET)
    public Response isAlreadyDownload(@PathVariable(value = "contentid") String contentid, @RequestHeader(value = "token") String token){
        Response<UserLoginInfo> userResponse = getUserByToken(token);
        UserLoginInfo user = userResponse.getResponseEntity();
        if (user == null) return userResponse;
        ResourceDownload entity = new ResourceDownload();
        entity.setObjectid(contentid);
        entity.setUserid(user.getUserId());
        List<ResourceDownload> downloads = resourceDownloadMicroApi.getByExample(entity).getPageInfo().getList();
        return new Response<>(CollectionUtils.isEmpty(downloads) ? 0:1);
    }

    @Override
    public Response getDownloadResourceListByUser(@RequestBody QueryTree queryTree, @RequestHeader String token) {
        //get me(user)
        UserLoginInfo userLoginInfo = getUserByToken(token).getResponseEntity();

        List<Resource> resourceList, pageList;
        QueryTree downQuery;
        List<ResourceDownload> downList;
        Pagination oldPagination = queryTree.getPagination(), newPagination = new Pagination();
        queryTree.setPagination(newPagination);

        //download info
        downQuery = new QueryTree();
        downQuery.addCondition(new QueryCondition("userid", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, userLoginInfo.getUserId()));
        PageInfo pageInfo = resourceDownloadMicroApi.getList(downQuery).getPageInfo();
        if (pageInfo == null) return new Response<>();

        downList = pageInfo.getList();
        //get downloaded resources
        resourceList = new ArrayList<>();
        queryTree.addCondition(new QueryCondition("contentid", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, ""));
        for(ResourceDownload down : downList)
        {
            String[] ids = {down.getObjectid() };
            queryTree.getQueryCondition("contentid").setFieldValues(ids);
            List<Resource> resData = resourceMicroApi.getList(queryTree).getPageInfo().getList();
            if (!CollectionUtils.isEmpty(resData)) {
                Resource resource = resData.get(0);
                resource.setDownTime(down.getDowntime());
                resourceList.add(resource);
            }
        }

        int start = oldPagination.getStart().intValue(), pageSize = oldPagination.getNumPerPage().intValue();
        if (start > resourceList.size())
            pageList = new ArrayList<>();
        else
            pageList = resourceList.subList(start ,start + pageSize <= resourceList.size() ? start + pageSize : resourceList.size());

        validateResourceList(pageList);

        Response<Resource> response = new Response<>(pageList);
        pageInfo.setList(pageList);
        pageInfo.setTotal(resourceList.size());
        response.setPageInfo(pageInfo);

        return response;
    }

    @Override
    public Response getFavoriteResourceListByUser(@RequestBody QueryTree queryTree, @RequestHeader String token) {
        //get me(user)
        Response<UserLoginInfo> userResponse = getUserByToken(token);
        if (null != userResponse) {
            UserLoginInfo userLoginInfo = userResponse.getResponseEntity();

            QueryTree favorQuery = new QueryTree();
            favorQuery.addCondition(new QueryCondition("userid", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, userLoginInfo.getUserId()));
            favorQuery.addCondition(new QueryCondition("status", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, "1"));
            favorQuery.orderBy("collecttime", OrderMethod.Method.ASC);

            List<ResourceCollection> collections = resourceCollectionMicroApi.search(favorQuery).getPageInfo().getList();
            List<String> contentids = new ArrayList<>();

            if(collections.size() == 0)
                contentids.add("12321-1-1");
            else
                for(ResourceCollection collection : collections)
                    contentids.add(collection.getObjectid());

            queryTree.addCondition(new QueryCondition("contentids", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, contentids));
            queryTree.addCondition(new QueryCondition("sortconids", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, StringUtils.join(contentids, "")));

            return getResourceList(queryTree, token);
        }
        else {
            return userResponse;
        }
    }

    @Override
    @RequestMapping(value = "/getBasketResourceListByUser", method = RequestMethod.POST)
    public Response getBasketResourceListByUser(@RequestBody QueryTree queryTree, @RequestHeader String token) {
        //get me(user)
        Response<UserLoginInfo> userResponse = getUserByToken(token);
        if (null != userResponse) {
            UserLoginInfo userLoginInfo = userResponse.getResponseEntity();

            QueryTree basketQuery = new QueryTree();
            basketQuery.addCondition(new QueryCondition("creator", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, userLoginInfo.getUserId()));
            basketQuery.addCondition(new QueryCondition("status", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, "1"));
            basketQuery.orderBy("createtime", OrderMethod.Method.ASC);
            List<ResourceBasket> basketItems = resourceBasketMicroApi.getList(basketQuery).getPageInfo().getList();

            List<String> contentids = new ArrayList<>();

            if (basketItems.size() == 0)
                contentids.add("1-2-1-1-2-13-2-3");
//                basketItems = new ArrayList<>();
            else
                for(ResourceBasket item : basketItems)
                    contentids.add(item.getObjectid());

            queryTree.addCondition(new QueryCondition("contentids", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, contentids));
            queryTree.addCondition(new QueryCondition("sortconids", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, StringUtils.join(contentids, "")));



            Response<Resource> resp = getResourceList(queryTree, token);

            if(basketItems.size()>0){
                List<Resource> mylist = resp.getPageInfo().getList();
                for(int i = 0;i < mylist.size();i++){
                    for(ResourceBasket item : basketItems) {
                        if (item.getObjectid().equals(mylist.get(i).getContentid())) {
                            mylist.get(i).setCollectid(item.getCollectid());
                            break;
                        }
                    }
                }
                resp.getPageInfo().setList(mylist);
            }

            return resp;

        }
        else {
            return userResponse;
        }
    }

    @Override
    public Response getBasketResourceCountByUser(@RequestHeader String token) {
        Response<UserLoginInfo> loginInfoResponse = getUserByToken(token);
        String resultCode = loginInfoResponse.getServerResult().getResultCode();
        if (!StringUtils.equals(resultCode, ICommonConstants.RESPONSE_RESULT_SUCCESS)) return loginInfoResponse;

//        QueryTree queryTree = new QueryTree();
//        queryTree.getPagination().setNumPerPage(1);
//
//        long total = getBasketResourceListByUser(queryTree, token).getPageInfo().getTotal();
        UserLoginInfo userLoginInfo = loginInfoResponse.getResponseEntity();

        QueryTree basketQuery = new QueryTree();
        basketQuery.addCondition(new QueryCondition("creator", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, userLoginInfo.getUserId()));
        basketQuery.addCondition(new QueryCondition("status", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, "1"));
        basketQuery.orderBy("createtime", OrderMethod.Method.ASC);
        List<ResourceBasket> basketItems = resourceBasketMicroApi.search(basketQuery).getPageInfo().getList();
        int total = basketItems.size();
        return new Response<>(total);
    }

    @Override
    @RequestMapping(value = "/addToBasket/{contentid}", method = RequestMethod.GET)
    public Response addToBasket(@PathVariable(value = "contentid") String contentid, @RequestHeader(value = "token") String token)
    {
        Response<UserLoginInfo> loginInfoResponse = getUserByToken(token);
        String resultCode = loginInfoResponse.getServerResult().getResultCode();
        if (!StringUtils.equals(resultCode, ICommonConstants.RESPONSE_RESULT_SUCCESS)) return loginInfoResponse;

        Resource resource = resourceMicroApi.get(contentid).getResponseEntity();
        if (resource == null)
            return new Response(IErrorMessageConstants.ERR_CODE_RESOURCE_NOT_EXIST, IErrorMessageConstants.ERR_MSG_RESOURCE_NOT_EXIST);

        //resource download allow
        if (!resource.getIsallowdownload().equalsIgnoreCase("1"))
            return new Response(IErrorMessageConstants.ERR_CODE_NOT_ABLE_TO_ADD_BASKET, IErrorMessageConstants.ERR_MSG_NOT_ABLE_TO_ADD_BASKET);

        UserLoginInfo userLoginInfo = loginInfoResponse.getResponseEntity();
        Map<String, String> map = new HashMap<>();
        map.put("userid", userLoginInfo.getUserId());
        map.put("contentid", contentid);
        Integer allreadyByCount = orderMicroApi.getCount4User(map).getResponseEntity();
        if (allreadyByCount > 0)
            return new Response(IErrorMessageConstants.ERR_CODE_GOOD_ISPAID_BY_ORDER, IErrorMessageConstants.ERR_MSG_GOOD_ISPAID_BY_ORDER);

        ResourceBasket entity = new ResourceBasket();
        entity.setObjectid(contentid);
        entity.setCreator(userLoginInfo.getUserId());
        entity.setStatus("1");
        entity.setCreatetime(new Timestamp(System.currentTimeMillis()));

        ResourceBasket searchEntity = new ResourceBasket();
        searchEntity.setObjectid(contentid);
        searchEntity.setCreator(userLoginInfo.getUserId());

        long count = resourceBasketMicroApi.searchByEntity(searchEntity).getPageInfo().getTotal();

        Response<ResourceBasket> resp;

        if (count != 0) {
            resp = new Response<>();
            resp.getServerResult().setResultCode(IStateCode.PARAMETER_IS_DUPLICATE);
            resp.getServerResult().setResultMessage(IErrorMessageConstants.ERR_MSG_ALREADY_BASKETED);
        }
        else {
            resp = resourceBasketMicroApi.insert(entity);
            if (resp.getServerResult().getResultCode().equals("1")) {
                resp.getServerResult().setResultCode(IErrorMessageConstants.OPERATION_SUCCESS);
                resp.getServerResult().setResultMessage(IErrorMessageConstants.OPERATION_SUCCESS_MESSAGE);
            }
        }

        return resp;
    }

    @Override
    @RequestMapping(value = "/removeFromBasket/{contentid}", method = RequestMethod.GET)
    public Response removeFromBasket(@PathVariable(value = "contentid") String contentid, @RequestHeader(value = "token") String token)
    {
        Response<UserLoginInfo> loginInfoResponse = getUserByToken(token);
        String resultCode = loginInfoResponse.getServerResult().getResultCode();
        if (!StringUtils.equals(resultCode, ICommonConstants.RESPONSE_RESULT_SUCCESS)) return loginInfoResponse;
        UserLoginInfo userLoginInfo = getUserByToken(token).getResponseEntity();

        Map<String, Object> param = new HashMap<>();
        param.put("contentid", contentid);
        param.put("userid", userLoginInfo.getUserId());

        Response<ResourceBasket> resp = resourceBasketMicroApi.remove(param);
        if (resp.getServerResult().getResultCode().equals("1")) {
            resp.getServerResult().setResultCode(IErrorMessageConstants.OPERATION_SUCCESS);
            resp.getServerResult().setResultMessage(IErrorMessageConstants.OPERATION_SUCCESS_MESSAGE);
        }

        return resp;
    }

    //author:han 韩哲国
    public QueryTree addParamsToQueryTree(@RequestBody QueryTree queryTree){
        if(!queryTree.getQueryCondition("activityidkey").getFieldValues()[0].equals("") || !queryTree.getQueryCondition("activitynamekey").getFieldValues()[0].equals("")){
            List<String> activityIds = activityMicroApi.getIdsByQueryTreeExtra(queryTree).getPageInfo().getList();
            if(activityIds.size() == 2){
                return null;
            }
            queryTree.addCondition(new QueryCondition("activityids", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, activityIds));
            List<String> contentIds = resourceMicroApi.getResIdsByActivity(queryTree).getPageInfo().getList();
            if(contentIds.size() == 2){
                return null;
            }
            queryTree.addCondition(new QueryCondition("contentids", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, contentIds));
        }
        return queryTree;
    }
    //end han

    @Override
    public Response getCollectedResourceList(@RequestBody QueryTree queryTree, @RequestHeader  String token) {
        QueryCondition queryCondition = new QueryCondition("sourceid", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, IResourceMicroConstants.RES_SOURCE_COLLECTION);
        queryTree.getConditions().add(queryCondition);
        //author:han 韩哲国
        if(queryTree.getQueryCondition("activityidkey") != null && queryTree.getQueryCondition("activitynamekey") != null){
            queryTree = addParamsToQueryTree(queryTree);
            if(queryTree == null){
                List<Resource> pageList = new ArrayList<>();
                Response<Resource> resp = new Response<>(pageList);
                return resp;
            }
        }
        //end han
        return getResourceList(queryTree, token);
    }

    @Override
    public Response getRewardResourceList(@RequestBody QueryTree queryTree, @RequestHeader String token) {
        QueryCondition queryCondition = new QueryCondition("sourceid", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, IResourceMicroConstants.RES_SOURCE_REWARD);
        queryTree.getConditions().add(queryCondition);
        //author:han 韩哲国
        if(queryTree.getQueryCondition("activityidkey") != null && queryTree.getQueryCondition("activitynamekey") != null){
            queryTree = addParamsToQueryTree(queryTree);
            if(queryTree == null){
                List<Resource> pageList = new ArrayList<>();
                Response<Resource> resp = new Response<>(pageList);
                return resp;
            }
        }
        //end han
        return getResourceList(queryTree, token);
    }

    @Override
    public Response getEstimateResourceList(@RequestBody QueryTree queryTree, @RequestHeader String token) {
        QueryCondition queryCondition = new QueryCondition("sourceid", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, IResourceMicroConstants.RES_SOURCE_ESTIMATE);
//        activityMicroApi.getIdsByQueryTree(queryTree).getPageInfo();
        queryTree.getConditions().add(queryCondition);
        //author:han 韩哲国
        if(queryTree.getQueryCondition("activityidkey") != null && queryTree.getQueryCondition("activitynamekey") != null){
            queryTree = addParamsToQueryTree(queryTree);
            if(queryTree == null){
                List<Resource> pageList = new ArrayList<>();
                Response<Resource> resp = new Response<>(pageList);
                return resp;
            }
        }
        //end han
        return getResourceList(queryTree, token);
    }

    @Override
    public Response getUploadResourceByUser(@RequestBody QueryTree queryTree, @RequestHeader  String token) {
        UserLoginInfo loginInfo = getUserByToken(token).getResponseEntity();
        QueryCondition queryCondition = new QueryCondition("creator", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, loginInfo.getUserId());
        queryTree.addCondition(queryCondition);
        return getResourceList(queryTree, token);
    }

    @Override
    public Response getImportedList(@RequestBody QueryTree queryTree, @RequestHeader(value = "token") String token) {
//        QueryCondition queryCondition = new QueryCondition("remarks", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, "");
//        queryTree.getConditions().add(queryCondition);
        Response<Resource> resp = resourceMicroApi.getImportedList(queryTree);
        List<Resource> resList = resp.getPageInfo().getList();
        if (resList != null){
            for (Resource res : resList)
            {
                validateResource(res);
                //Author : GOD 2019-2-22 Reason: BatchFileUpload
                validateNaviInfoByCatalogids(res, res.getCatalogids());
                //Author : GOD 2019-2-22 Reason: BatchFileUpload
            }
        }
        return resp;
    }


    @Override
    @ApiOperation(value = "资源详细", notes = "资源详细")
    @ApiImplicitParam(name = "contentid", value = "资源信息", required = true, dataType = "String", paramType = "path")
    @SystemControllerLog(description = "资源更改状态")
    public Response batchStateUpdate(@RequestBody Map<String, Object> statusinfo, @RequestHeader(value = "token") String token) {
        if (esSyncFlag.equalsIgnoreCase(ICommonConstants.ES_SAVING_FLAG))
            return new Response<>(IErrorMessageConstants.ERR_CODE_ES_SAVING_MODIFY_DISABLE, IErrorMessageConstants.ERR_MSG_ES_SAVING_MODIFY_DISABLE);

        Response<UserLoginInfo> loginInfoResponse = getUserByToken(token);
        String resultCode = loginInfoResponse.getServerResult().getResultCode();
        if (!StringUtils.equals(resultCode, ICommonConstants.RESPONSE_RESULT_SUCCESS)) return loginInfoResponse;
        UserLoginInfo userLoginInfo = getUserByToken(token).getResponseEntity();

        List<String> contentids = (List<String>) statusinfo.get("contentids");
        String status = (String) statusinfo.get("status");
        String importstatus = (String) statusinfo.get("importstatus");

        List<Resource> result = new ArrayList<>();
        if (!CollectionUtils.isEmpty(contentids)) {
            for (String contentid : contentids) {
                Resource resObject = resourceMicroApi.get(contentid).getResponseEntity();
                if (resObject != null) {
                    Resource resource = new Resource();
                    resource.setContentid(contentid);
                    resource.setStatus(status);
                    if (!StringUtils.isEmpty(importstatus)) {
                        resource.setImportstatus(importstatus);
                        if (StringUtils.equals(importstatus, IResourceMicroConstants.RES_IMPORT_STATUS_SUCCESS)
                                && !StringUtils.isEmpty(resource.getRemarks())
                                && StringUtils.equals(resObject.getStatus(), IResourceMicroConstants.RES_STATUS_PASS)) {
                            resource.setIsgoods(IResourceMicroConstants.RES_SOURCE_GOODS);
                        }
                    }
                    resource = resourceMicroApi.update(resource).getResponseEntity();

                    if (StringUtils.equals(resource.getImportstatus(), IResourceMicroConstants.RES_IMPORT_STATUS_SUCCESS) && !StringUtils.isEmpty(resource.getRemarks())) {
                        addDefaultGood(resource, userLoginInfo);
                    }
                    result.add(resource);
                }
            }
        }
        return new Response<>(result);
    }

    private Good addDefaultGood(Resource resource, UserLoginInfo userLoginInfo) {
        if (resource == null) return null;
        Good good = new Good();
        good.setOnshelftime(new Timestamp(System.currentTimeMillis()));
        good.setDownshelftime(new Timestamp(TimeUtil.computeStartOfNextMonth(System.currentTimeMillis())));
        good.setUserid(userLoginInfo.getUserId());
        good.setStatus(IOperationConstants.GOOD_STATUS_ALLOW);
        good.setGoodname(resource.getName());
        good.setProductid(resource.getContentid());
        good.setGoodprice(resource.getScore());
        good.setGoodtype(IOperationConstants.GOOD_TYPE_RESROUCE);
        return goodMicroApi.add(good).getResponseEntity();
    }

    @Override
    @ApiOperation(value = "资源详细", notes = "资源详细")
    @ApiImplicitParam(name = "contentid", value = "资源信息", required = true, dataType = "String", paramType = "path")
    @SystemControllerLog(description = "资源详细")
    public Response detailResource(@PathVariable(value = "contentid") String contentid, @RequestHeader(value = "token") String token) {
        UserLoginInfo userLoginInfo = null;
        if (!StringUtils.isEmpty(token)) {
            Response<UserLoginInfo> loginInfoResponse = getUserByToken(token);
            String resultCode = loginInfoResponse.getServerResult().getResultCode();
            if (!StringUtils.equals(resultCode, ICommonConstants.RESPONSE_RESULT_SUCCESS)) return loginInfoResponse;
            userLoginInfo = loginInfoResponse.getResponseEntity();
        }

        Resource resource = (Resource) getResource(contentid).getResponseEntity();
        if (resource == null)
            return new Response(IErrorMessageConstants.ERR_CODE_RESOURCE_NOT_EXIST, IErrorMessageConstants.ERR_MSG_RESOURCE_NOT_EXIST);

        if (resource.getFileInfo() == null)
            return new Response(IErrorMessageConstants.ERR_CODE_FILE_NOT_EXIST, IErrorMessageConstants.ERR_MSG_FILE_NOT_EXIST);

        if (userLoginInfo != null) {
            Map<String, String> param = new HashMap<>();
            param.put("userid", userLoginInfo.getUserId());
            param.put("objectid", contentid);
            Response<ResourceFlags> resourceFlagsResponse = resourceVoteMicroApi.getResourceFlags(param);

//            ResourceFlags flags = resourceVoteMicroApi.getResourceFlags(param).getResponseEntity();
            ResourceFlags flags = resourceFlagsResponse.getResponseEntity();

            if (null != flags) {
                resource.setIsbasket(flags.getIsbasket());
                resource.setIsfavourite(flags.getIscollect());
                resource.setIsvote(flags.getIsvote());
                resource.setIsnew(flags.getIsnew());

                //save view log
                String userRoleid = userLoginInfo.getRoleid();
                String manager = ecoUserRoleManager;
                if (!StringUtils.isEmpty(userRoleid) &&
                        !userRoleid.equals(manager) &&// check user manager or not
                        CollectionUtils.arrayToList(StringUtils.split(userRoleid, ",")).indexOf(manager) == -1 &&//check user manager or not
                        flags.getIsnew() == 1) {

                    ResourceView resourceView = new ResourceView();
                    resourceView.setResourceid(resource.getContentid());
                    resourceView.setUserid(userLoginInfo.getUserId());
                    resourceView.setViewtime(new Timestamp(System.currentTimeMillis()));
                    ResourceView view = resourceViewMicroApi.insert(resourceView).getResponseEntity();

                    if (null != view) {
                        getHotValue(resource);
                        int viewtimes = resource.getViewtimes();
                        resource.setViewtimes(viewtimes + 1);

                        //update view times and hot value
//                        resourceMicroApi.update(resource);
                        resourceMicroApi.view(resource);
                        resourceMicroApi.updateHot(resource);
                    }
                }
            }
        }

        if (resource.getCatalogids() != null)
            validateNaviInfoByCatalogids(resource, resource.getCatalogids());
        else
            resource.setSubnaviInfos(new ArrayList<>());
        return new Response<>(resource);
    }

    @Override
    @ApiOperation(value = "资源更改", notes = "资源更改")
    @ApiImplicitParam(name = "resource", value = "资源更改信息", required = true, dataType = "Resource", paramType = "body")
    @SystemControllerLog(description = "资源更改")
    public Response editResource(@RequestBody Resource resource, @RequestHeader(value = "token") String token) {
        if (esSyncFlag.equalsIgnoreCase(ICommonConstants.ES_SAVING_FLAG))
            return new Response<>(IErrorMessageConstants.ERR_CODE_ES_SAVING_MODIFY_DISABLE, IErrorMessageConstants.ERR_MSG_ES_SAVING_MODIFY_DISABLE);

        UserLoginInfo userLoginInfo = getUserByToken(token).getResponseEntity();

        if (null != userLoginInfo)
            resource.setLastmodifier(userLoginInfo.getUserId());

        if (null == resource) return null;

        /**
         * ------------------------------------------------------------------>>
         * @author: jong
         * @date:2019-02-17
         * filter name and description when edit a resource
         */
        SensitiveWordFilter swFilter = new SensitiveWordFilter();
        resource.setName(swFilter.process(resource.getName()));
        resource.setDescription(swFilter.process(resource.getDescription()));
        /*
        ---------------------------------------------------------------------<<
         */

        Resource object = resourceMicroApi.get(resource.getContentid()).getResponseEntity();

        FileInfo fileinfo = resource.getFileInfo();
        if (fileinfo != null && fileinfo.getFileid() != null) {
            FileInfo orgInfo = fileMicroApi.get(fileinfo.getFileid()).getResponseEntity();
            if (orgInfo != null) {
                fileMicroApi.update(fileinfo);
            } else {
                fileinfo = fileMicroApi.add(fileinfo).getResponseEntity();
                if (fileinfo == null) {
                    return new Response(IStateCode.PARAMETER_IS_INVALID, IErrorMessageConstants.ERR_MSG_RESOURCE_ALREADY_EXIST);
                } else {
                    resource.setFileInfo(fileinfo);
                }

                reconnectFileinfo(resource);
            }
        }

        List<String> catalogids = resource.getCatalogids();
        reconnectBookChapter(resource, catalogids);
        validateNaviInfoByCatalogids(resource, catalogids);

        Good goodinfo = getGoodFromHashMap(resource);
        if (resource.getIsgoods() != null) {
            boolean isGood = StringUtils.equals(resource.getIsgoods(), IResourceMicroConstants.RES_SOURCE_GOODS) ? true : false;
            if (goodinfo != null &&  object != null) {
                if (isGood) {
                    if (StringUtils.isEmpty(goodinfo.getGoodid())) {
                        goodinfo.setGoodtype(IOperationConstants.GOOD_TYPE_RESROUCE);
                        goodinfo.setProductid(resource.getContentid());
                        goodinfo.setUserid(userLoginInfo.getUserId());
//                        goodinfo.setStatus(IOperationConstants.GOOD_STATUS_ALLOW);
                        goodinfo.setGoodid(null);
                        goodMicroApi.add(goodinfo);


                    } else {
                        goodinfo = goodMicroApi.update(goodinfo).getResponseEntity();
                    }

                    if (goodinfo != null)
                        resource.setScore(goodinfo.getGoodprice());
                } else {
                    Response<Good> resp = goodMicroApi.removeFromES(goodinfo.getGoodid());
                    if (resp.getServerResult().getResultCode().equalsIgnoreCase(IErrorMessageConstants.OPERATION_SUCCESS)) {
                        goodMicroApi.delete(goodinfo.getGoodid());
                        resource.setIsgoods(IResourceMicroConstants.RES_SOURCE_NOT_GOODS);
                        String creatorid = object.getCreator();
                        if (creatorid != null) {
                            UserLoginInfo resourcecreator = userMicroApi.get(creatorid).getResponseEntity();
                            if (resourcecreator != null) {
                                Integer goodscount = resourcecreator.getGoodscount();
                                resourcecreator.setGoodscount(goodscount--);
                                userMicroApi.update(resourcecreator);
                            }
                        }
                    }
                }

/*                if (userLoginInfo != null)
                {
                    UserLoginInfo user = userMicroApi.get(userLoginInfo.getUserId()).getResponseEntity();
                    Long value = goodMicroApi.getCountByCreator(userLoginInfo.getUserId()).getResponseEntity();
                    if (value == null)
                        value = Long.valueOf(0);
                    user.setGoodscount(value.intValue());
                }*/
            }
        }

/*        Audit auditInfo = resource.getAuditinfo();
        if (auditInfo != null) {
            fileinfo = fileMicroApi.getByContentid(resource.getContentid()).getResponseEntity();
            if (fileinfo == null) {
                return new Response(IErrorMessageConstants.ERR_CODE_FILE_NOT_EXIST, IErrorMessageConstants.ERR_MSG_FILE_NOT_EXIST);
            }
            auditMicroApi.update(auditInfo);
        }*/

        String userRoleid = userLoginInfo.getRoleid();
        String manager = ecoUserRoleManager;
        if (object != null && !StringUtils.equals(userRoleid, manager) && CollectionUtils.arrayToList(StringUtils.split(userRoleid, ",")).indexOf(manager) == -1
                && StringUtils.equals(object.getStatus(), IResourceMicroConstants.RES_STATUS_NO_PASS))
            resource.setStatus(IResourceMicroConstants.RES_STATUS_HOLD);

        resource.setScore(null);
        resourceMicroApi.update(resource);

        //update good info
        Resource example = new Resource();

        //update sharerangekey
        if (StringUtils.isEmpty(resource.getSharerangekey()) && !StringUtils.isEmpty(resource.getCreator())) {
            UserLoginInfo resourceCreator = userMicroApi.get(resource.getCreator()).getResponseEntity();
            if (resourceCreator != null) {
                example.setSharerangekey(getSharerangeKey(resourceCreator));
            }
        }

        //batch import resource update
        String remarksString = null, importstatus = null;
        if (!StringUtils.equals(resource.getImportstatus(), IResourceMicroConstants.RES_IMPORT_STATUS_SUCCESS) &&
                object != null && !StringUtils.isEmpty(object.getRemarks())) {
            resource.setRemarks(object.getRemarks());
            Map<String, String> remarkRes = parseRemarks(resource, userLoginInfo);
            if (remarkRes != null) {
                remarksString = remarkRes.get("remarks");
                importstatus = remarkRes.get("importstatus");
            }
            resource.setIsgoods(resource.getGoodinfo() == null ? IResourceMicroConstants.RES_SOURCE_NOT_GOODS : IResourceMicroConstants.RES_SOURCE_GOODS);
        }

//        Good goodInfo = getGoodFromHashMap(resource);
//        if (goodinfo != null && StringUtils.equals(goodinfo.getStatus(), IResourceMicroConstants.RES_SOURCE_NOT_GOODS))
//            resource.setIsgoods(goodInfo.getStatus());

        if (!StringUtils.isEmpty(resource.getFilepath()))
            example.setFilepath(resource.getFilepath());
        example.setContentid(resource.getContentid());
        example.setIsgoods(resource.getIsgoods());
        example.setContenttype(resource.getContenttype());
        example.setScore(goodinfo==null?object.getScore():goodinfo.getGoodprice());
        example.setImportstatus(resource.getImportstatus());
        example.setRemarks(remarksString==null?resource.getRemarks():remarksString);
        example.setImportstatus(importstatus==null?resource.getImportstatus():importstatus);
        resourceMicroApi.update(example);

        logOptionEntity(resource.getContentid(), resource.getContentid(), ICommonConstants.OPTION_OPTIONTYPE_MODIFY, token);

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return new Response<>(resource);
    }

    @Override
    @ApiOperation(value = "商品批量生成", notes = "商品批量生成")
    @ApiImplicitParam(name = "goods", value = "商品批量生成信息", required = true, dataType = "Map<String, Object>", paramType = "body")
    @SystemControllerLog(description = "商品批量生成")
    public Response addBatchGoods(@RequestBody Map<String, Object> goods, @RequestHeader(value = "token") String token) {
        Response<UserLoginInfo> loginInfoResponse = getUserByToken(token);
        String resultCode = loginInfoResponse.getServerResult().getResultCode();
        if (!StringUtils.equals(resultCode, ICommonConstants.RESPONSE_RESULT_SUCCESS)) return loginInfoResponse;

        UserLoginInfo loginInfo = loginInfoResponse.getResponseEntity();
        List<Good> result = new ArrayList<>();

        List<String> contentids = (List<String>) goods.get("resourceids");
        Integer score = (Integer) goods.get("goodprice");
        String onshelftime = (String) goods.get("sell_begindate");
        String downshelftime = (String) goods.get("sell_enddate");

        for (String contentid : contentids) {
            Resource resource = resourceMicroApi.get(contentid).getResponseEntity();

            if (null == resource) continue;

            UserLoginInfo userLoginInfo = userMicroApi.get(resource.getCreator()).getResponseEntity();
            if (userLoginInfo != null) {
                Integer goodscount = userLoginInfo.getGoodscount();
                userLoginInfo.setGoodscount(goodscount+1);
            }
            if (resource == null) {
                throw new ParameterException(IStateCode.PARAMETER_IS_EMPTY, IErrorMessageConstants.ERR_MSG_RESOURCE_NOT_EXIST);
            }

            if (resource.getFileInfo() == null) {
                resultCode = IErrorMessageConstants.ERR_CODE_FILE_NOT_EXIST;
                continue;
            }

            Good good = getSimpleGoodByContentId(resource);
            if (good != null) {
                Response<Good> resp = goodMicroApi.removeFromES(good.getGoodid());
                if (resp.getServerResult().getResultCode().equalsIgnoreCase(IErrorMessageConstants.OPERATION_SUCCESS)) {
                    goodMicroApi.delete(good.getGoodid());
                }
            }

            good = new Good();
            good.setProductid(contentid);

            if (onshelftime != null && !onshelftime.isEmpty()) {
                Timestamp beginDate = Timestamp.valueOf(onshelftime.toString());
                good.setOnshelftime(beginDate);
            }

            if (downshelftime != null && !downshelftime.isEmpty()) {
                Timestamp endDate = Timestamp.valueOf(downshelftime.toString());
                good.setDownshelftime(endDate);
            }
            good.setGoodtype(IOperationConstants.GOOD_TYPE_RESROUCE);
            good.setUserid(loginInfo.getUserId());
            good.setStatus(IOperationConstants.GOOD_STATUS_ALLOW);
            good.setGoodprice(score);
            goodMicroApi.add(good);
            result.add(good);

            //update resource
            Resource example = new Resource();
            example.setContentid(resource.getContentid());
            example.setIsgoods("1");
            example.setScore(score);
            resourceMicroApi.update(example);
        }

        if (StringUtils.equals(resultCode, IErrorMessageConstants.ERR_CODE_FILE_NOT_EXIST)) {
            return new Response(resultCode, IErrorMessageConstants.ERR_MSG_FILE_NOT_EXIST);
        }

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Response<Good> resp = new Response<>(result);
        return resp;
    }

    @Override
    @RequestMapping(value = "/downloadResource/{contentid}", method = RequestMethod.GET)
    public Response downloadResource(@PathVariable(value = "contentid") String contentid, @RequestHeader(value = "token") String token) throws Exception {
        Response<UserLoginInfo> userResponse = getUserByToken(token);
        UserLoginInfo user = userResponse.getResponseEntity();
        if (user == null) return userResponse;

        Response result = new Response();
        Resource resource = resourceMicroApi.get(contentid).getResponseEntity();
        Good good = goodMicroApi.getByContentid(contentid).getResponseEntity();

        String url = getRootURL() + "/file/download/"+contentid + "?token="+token;
        if (good == null)
        {
            if (user.getUserId().equals(resource.getCreator())) {
                result.setResponseEntity(url);
                return result;
            }

            result.getServerResult().setResultCode(IErrorMessageConstants.ERR_NOT_GOODS);
            result.getServerResult().setResultMessage(IErrorMessageConstants.ERR_MSG_NOT_GOODS);
            return result;
        }

        //resource download allow
        if (!resource.getIsallowdownload().equalsIgnoreCase("1")) {
            result.getServerResult().setResultCode(IErrorMessageConstants.ERR_CODE_DOWNLOAD_IS_NOT_ALLOWED);
            result.getServerResult().setResultMessage(IErrorMessageConstants.ERR_MSG_DOWNLOAD_IS_NOT_ALLOWED);
            return result;
        }

        boolean isPay = getPayFlag(resource, user);
        if (isPay)
        {
            Integer score = memberMicroApi.getMyScore(user.getUserId()).getResponseEntity();
            Integer resourceScore = good.getGoodprice();

            String commnet = "下载该资源需扣"+ resourceScore + "个积分，是否下载";
            List<String> receivers = new ArrayList<>();
            receivers.add(user.getUserId());

            saveMessage("1","系统",commnet,receivers,ecoPlatformClientID,token);

            int updateScore =score - resourceScore;
            if (updateScore < 0) {
                result.getServerResult().setResultCode(IErrorMessageConstants.ERR_SCORE_NOT_ENOUGH);
                result.getServerResult().setResultMessage(IErrorMessageConstants.ERR_MSG_SCORE_NOT_ENOUGH);
                return result;
            }
//            Author : GOD, 2019-2-17, Bug ID: #772
//            Member userMember = memberMicroApi.getByUserId(user.getUserId()).getResponseEntity();
//            if (userMember != null) {
//                userMember.setUserid(user.getUserId());
//                userMember.setUseintegral(updateScore);
//                memberMicroApi.update(userMember);
//            }
//            Author : GOD, 2019-2-17, Bug ID: #772
        }

        downloadFinished(resource,token);
        resource.setGoodinfo(good);
        doAction(ICommonConstants.ACTION_DOWNLOAD, resource, user, isPay?null:0);
        result.setResponseEntity(url);
        return result;
    }

    //author:han
    @Override
    @ApiOperation(value = "文件上传", notes = "文件上传")
    @ApiImplicitParam(name = "resourceIds", value = "资源Ids", required = true, dataType = "String", paramType = "path")
    @SystemControllerLog(description = "文件上传")
    @RequestMapping(value = "/downloadResourceExtra", method = RequestMethod.GET)
    public void downloadResourceExtra(HttpServletResponse httpServletResponse, @RequestParam Map<String, Object> param) throws Exception {
        String resourceIds = param.get("resourceIds").toString();
        String token = param.get("token").toString();
        String twolabelid = param.get("twolabelid").toString();

        Response<UserLoginInfo> userResponse = getUserByToken(token);
        UserLoginInfo user = userResponse.getResponseEntity();
        if (user == null) return;

        Label twolabel = labelMicroApi.get(twolabelid).getResponseEntity();
        String labelname = twolabel.getLabelname();
        Integer twolabelScore = twolabel.getScore();
        Integer score = memberMicroApi.getMyScore(user.getUserId()).getResponseEntity();
        int updateScore =score - twolabelScore;
        if(updateScore < 0){
            return;
        }

        try {
        httpServletResponse.setContentType("application/octet-stream");
        httpServletResponse.addHeader("Content-encoding","UTF-8");
        String fileName = URLEncoder.encode(labelname + ".zip", "UTF-8");
        fileName = URLDecoder.decode(fileName, "ISO8859_1");
        httpServletResponse.addHeader("Content-Disposition", "attachment; filename=" + fileName );
        OutputStream outstream = httpServletResponse.getOutputStream();
        ZipOutputStream zos = new ZipOutputStream(outstream);

            for (String contentid : resourceIds.split(",")) {
                try {
                    FileInfo fileInfo = fileMicroApi.getByContentid(contentid).getResponseEntity();
                    String localpath = fileInfo.getLocalpath();
                    URL url = new URL(ecoServerIp + ICommonConstants.ECO_FILE_DOWNLOAD_URL + localpath);
                    zos.putNextEntry(new ZipEntry(fileInfo.getFilename()));
                    InputStream inputStream = url.openStream();
                    int FILE_CHUNK_SIZE = 1024 * 4;
                    byte[] chunk = new byte[FILE_CHUNK_SIZE];
                    int n = 0;
                    while ((n = inputStream.read(chunk)) != -1) {
                        zos.write(chunk, 0, n);
                    }
                    zos.write(chunk);
                    zos.closeEntry();
                } catch (FileNotFoundException ex) {
                    System.err.println("A file does not exist: " + ex);
                } catch (IOException ex) {
                    System.err.println("I/O error: " + ex);
                }
            }
            zos.close();
            doAction(ICommonConstants.ACTION_DOWNLOAD, null, user, twolabelScore);
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    //end

    private boolean getPayFlag(Resource resource, UserLoginInfo user) {
        //check current score
        ResourceDownload entity = new ResourceDownload();
        entity.setObjectid(resource.getContentid());
        entity.setUserid(user.getUserId());
        boolean isPay = true;
        List<ResourceDownload> downloads = resourceDownloadMicroApi.getByExample(entity).getPageInfo().getList();
        UserLoginInfo resourceUploader = userMicroApi.get(resource.getCreator()).getResponseEntity();
        isPay = (CollectionUtils.isEmpty(downloads) || resourceUploader == null)?true:false;
        isPay = !StringUtils.equals(resource.getCreator(), user.getUserId()) && isPay;

        if (resourceUploader != null && isPay) {
            boolean isarearesearcher = StringUtils.equals(resourceUploader.getRoleid(), IBaseMicroConstants.USER_ROLE_ID_AREA_RESEARCHER);
            boolean iscityresearcher = StringUtils.equals(resourceUploader.getRoleid(), IBaseMicroConstants.USER_ROLE_ID_AREA_RESEARCHER);
            if (isarearesearcher) {
                if (StringUtils.equals(user.getArea(), resourceUploader.getArea())) isPay = false;
            } else if (iscityresearcher)
                if (StringUtils.equals(user.getCity(), resourceUploader.getCity())) isPay = false;
        }
        return isPay;
    }

    @Override
    @RequestMapping(value = "/rateResource", method = RequestMethod.POST)
    public Response rateResource(@RequestBody Map<String, Object> mapList, @RequestHeader(value = "token") String token) {
        Response<UserLoginInfo> resp = getUserByToken(token);
        if (!resp.getServerResult().getResultCode().equalsIgnoreCase(IErrorMessageConstants.OPERATION_SUCCESS))
            return resp;

        UserLoginInfo userLoginInfo = resp.getResponseEntity();

        mapList.put("userid", userLoginInfo.getUserId());
        String contentid = mapList.get("contentid").toString();

        Resource resource = resourceMicroApi.get(contentid).getResponseEntity();
        if (resource == null)
            throw new ParameterException(IStateCode.PARAMETER_IS_INVALID, IErrorMessageConstants.ERR_MSG_RESOURCE_NOT_EXIST);

        Integer userRate = resourceStarMicroApi.getRatingByUser(mapList).getResponseEntity();
        if (userRate != 0) {
            return new Response(IStateCode.PARAMETER_IS_INVALID, IErrorMessageConstants.ERR_MSG_RATING);
        } else {

            //save rate result
            ResourceStar resourceStar = new ResourceStar();
            resourceStar.setObjectid(contentid);
            resourceStar.setStaruser(userLoginInfo.getUserId());
            resourceStar.setObjecttype("1");
            resourceStar.setStartime(new Timestamp(System.currentTimeMillis()));
            resourceStar.setResult(Integer.parseInt(mapList.get("rating").toString()));
            resourceStarMicroApi.insert(resourceStar);

            int rateSum = resourceStarMicroApi.getRateSumByResource(resource.getContentid()).getResponseEntity();
            int rateCount = resourceStarMicroApi.getRateCountByResource(resource.getContentid()).getResponseEntity();

            resource.setRatesum((float)rateSum / rateCount);
            resourceMicroApi.rate(resource);
            getHotValue(resource);
            resourceMicroApi.updateHot(resource);

            //score update
            doAction(ICommonConstants.ACTION_RATING, resource, userLoginInfo, null);

            Map<String, Object> mapResult = new HashMap<>();
            mapResult.put("ratingcount", rateCount);
            mapResult.put("rateSum", (float)rateSum/rateCount);
            return new Response<>(mapResult);
        }
    }

    @Override
    public Response addToFavorite(@PathVariable(value = "contentid") String contentid, @RequestHeader(value = "token") String token) {
        Response<UserLoginInfo> loginInfoResponse = getUserByToken(token);
        String resultCode = loginInfoResponse.getServerResult().getResultCode();
        if (!StringUtils.equals(resultCode, ICommonConstants.RESPONSE_RESULT_SUCCESS)) return loginInfoResponse;

        UserLoginInfo userLoginInfo = loginInfoResponse.getResponseEntity();
        String userid = userLoginInfo.getUserId();

        Resource resource = resourceMicroApi.get(contentid).getResponseEntity();
        if (resource == null)
            throw new ParameterException(IStateCode.PARAMETER_IS_INVALID, IErrorMessageConstants.ERR_MSG_RESOURCE_NOT_EXIST);

        QueryTree totalQuery = new QueryTree();
        totalQuery.addCondition(new QueryCondition("userid", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, userid));
        totalQuery.addCondition(new QueryCondition("status", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, "1"));
        long count = resourceCollectionMicroApi.getList(totalQuery).getPageInfo().getTotal();

        Map<String, Object> result = new HashMap<>();
        Response<Map<String, Object>> resp = new Response<>(result);

        if (count == 200)
        {
            resp.getServerResult().setResultCode(IErrorMessageConstants.ERR_CODE_COLLECT_SIZE_OVER);
            resp.getServerResult().setResultMessage(IErrorMessageConstants.ERR_MSG_COLLECT_SIZE_OVER);
        }
        else
        {
            QueryTree queryTree = new QueryTree();
            queryTree.addCondition(new QueryCondition("userid", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, userid));
            queryTree.addCondition(new QueryCondition("contentid", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, contentid));

            List<ResourceCollection> exist = resourceCollectionMicroApi.getList(queryTree).getPageInfo().getList();
            if (CollectionUtils.isEmpty(exist) || exist.get(0).getStatus().equalsIgnoreCase("0")) {
                ResourceCollection resourceCollection;

                if (CollectionUtils.isEmpty(exist)) {
                    resourceCollection = new ResourceCollection();
                    resourceCollection.setUserid(userid);
                    resourceCollection.setContenttype(resource.getContenttype());
                    resourceCollection.setObjectid(resource.getContentid());
                    resourceCollection.setCollecttime(new Timestamp(System.currentTimeMillis()));
                    resourceCollection.setObjectCode(resource.getContentno() == null ? "" : resource.getContentno());
                    resourceCollection.setObjecttype("1");
                    resourceCollection.setStatus("1");

                    resourceCollection = resourceCollectionMicroApi.insert(resourceCollection).getResponseEntity();

                    try{
                        doAction(ICommonConstants.ACTION_ADD_FAVOURIT, resource, userLoginInfo, null);
                    }
                    catch (Exception e){}
                } else {
                    resourceCollection = exist.get(0);
                    resourceCollection.setStatus("1");
                    resourceCollectionMicroApi.update(resourceCollection);
                }

                int collectTimes = resource.getCollectiontimes() + 1;
                resource.setCollectiontimes(collectTimes);
                resourceMicroApi.collection(resource);

                logOptionEntity(resource.getContentid(), resourceCollection.getCollectid(), ICommonConstants.OPTION_OPTIONTYPE_FAVORITE_ADD, token);
            }
        }

        result.put("collectiontimes", resource.getCollectiontimes());

        return resp;
    }

    @Override
    public Response removeFromFavorite(@PathVariable(value = "contentid") String contentid, @RequestHeader(value = "token") String token) {
        Response<UserLoginInfo> loginInfoResponse = getUserByToken(token);
        String resultCode = loginInfoResponse.getServerResult().getResultCode();
        if (!StringUtils.equals(resultCode, ICommonConstants.RESPONSE_RESULT_SUCCESS)) return loginInfoResponse;

        UserLoginInfo userLoginInfo = loginInfoResponse.getResponseEntity();

        Resource resource = resourceMicroApi.get(contentid).getResponseEntity();
        if (resource == null)
            throw new ParameterException(IStateCode.PARAMETER_IS_INVALID, IErrorMessageConstants.ERR_MSG_RESOURCE_NOT_EXIST);

        Map<String, Object> param = new HashMap<>();
        param.put("userid", userLoginInfo.getUserId());
        param.put("contentid", contentid);
        Map<String, Object> result = new HashMap<>();
        QueryTree queryTree = new QueryTree();
        queryTree.addCondition(new QueryCondition("userid", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, userLoginInfo.getUserId()));
        queryTree.addCondition(new QueryCondition("contentid", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, contentid));

        List<ResourceCollection> exist = resourceCollectionMicroApi.getList(queryTree).getPageInfo().getList();
        if (!CollectionUtils.isEmpty(exist)) {
            ResourceCollection colInfo = exist.get(0);
            colInfo.setStatus("0");
            Response<ResourceCollection> response = resourceCollectionMicroApi.update(colInfo);
            ServerResult removeResult = response.getServerResult();
            if (!removeResult.getResultCode().equals("-1")) {
                int collectTimes = resource.getCollectiontimes() - 1;
                if (collectTimes < 0) collectTimes = 0;
                resource.setCollectiontimes(collectTimes);
                resourceMicroApi.collection(resource);

                ResourceCollection resourceCollection = response.getResponseEntity();
                if (resourceCollection != null) {
                    if (resourceCollection.getCollectid() == null)
                        resourceCollection.setCollectid("");
                    logOptionEntity(resource.getContentid(), resourceCollection.getCollectid(), ICommonConstants.OPTION_OPTIONTYPE_FAVORITE_DELETE, token);
                }
            }
        }

        result.put("collectiontimes", resource.getCollectiontimes());
        return new Response<>(result);
    }

    @Override
    public Response batchRemoveFromFavorite(@RequestBody List<String> contentids, @RequestHeader(value = "token") String token) {
        Response<UserLoginInfo> loginInfoResponse = getUserByToken(token);
        String resultCode = loginInfoResponse.getServerResult().getResultCode();
        if (!StringUtils.equals(resultCode, ICommonConstants.RESPONSE_RESULT_SUCCESS)) return loginInfoResponse;

        UserLoginInfo userLoginInfo = loginInfoResponse.getResponseEntity();

        Map<String, Object> result = new HashMap<>();
        for(String contentid : contentids) {

            Resource resource = resourceMicroApi.get(contentid).getResponseEntity();
            if (resource == null)
                continue;
                //throw new ParameterException(IStateCode.PARAMETER_IS_INVALID, IErrorMessageConstants.ERR_MSG_RESOURCE_NOT_EXIST);


            QueryTree queryTree = new QueryTree();
            queryTree.addCondition(new QueryCondition("userid", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, userLoginInfo.getUserId()));
            queryTree.addCondition(new QueryCondition("contentid", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, contentid));

            List<ResourceCollection> exist = resourceCollectionMicroApi.getList(queryTree).getPageInfo().getList();
            if (!CollectionUtils.isEmpty(exist)) {
                ResourceCollection colInfo = exist.get(0);
                colInfo.setStatus("0");
                Response<ResourceCollection> response = resourceCollectionMicroApi.update(colInfo);
                ServerResult removeResult = response.getServerResult();
                if (!removeResult.getResultCode().equals("-1")) {
                    int collectTimes = resource.getCollectiontimes() - 1;
                    if (collectTimes < 0) collectTimes = 0;
                    resource.setCollectiontimes(collectTimes);
                    resourceMicroApi.collection(resource);

                    ResourceCollection resourceCollection = response.getResponseEntity();
                    if (resourceCollection != null) {
                        if (resourceCollection.getCollectid() == null)
                            resourceCollection.setCollectid("");
                        logOptionEntity(resource.getContentid(), resourceCollection.getCollectid(), ICommonConstants.OPTION_OPTIONTYPE_FAVORITE_DELETE, token);
                    }
                }
            }
        }
//        result.put("collectiontimes", resource.getCollectiontimes());
        return new Response<>(result);
    }
    @Override
    public Response voteResource(@PathVariable(value = "contentid") String contentid, @RequestHeader(value = "token") String token) {
        Response<UserLoginInfo> loginInfoResponse = getUserByToken(token);
        String resultCode = loginInfoResponse.getServerResult().getResultCode();
        if (!StringUtils.equals(resultCode, ICommonConstants.RESPONSE_RESULT_SUCCESS)) return loginInfoResponse;

        UserLoginInfo userLoginInfo = loginInfoResponse.getResponseEntity();
        int isvote = 0;

        Resource resource = resourceMicroApi.get(contentid).getResponseEntity();
        ResourceVote vote = new ResourceVote();

        if (resource == null)
            throw new ParameterException(IStateCode.PARAMETER_IS_INVALID, IErrorMessageConstants.ERR_MSG_RESOURCE_NOT_EXIST);

        vote.setUserid(userLoginInfo.getUserId());
        vote.setResourceid(contentid);
        List<ResourceVote> exist = resourceVoteMicroApi.searchByEntity(vote).getPageInfo().getList();
        if (CollectionUtils.isEmpty(exist) || exist.get(0).getStatus().equalsIgnoreCase("0")) {
            if (CollectionUtils.isEmpty(exist)) {
                vote = new ResourceVote();
                vote.setResourceid(contentid);
                vote.setUserid(userLoginInfo.getUserId());
                vote.setVotetime(new Timestamp(System.currentTimeMillis()));
                vote.setStatus("1");
                resourceVoteMicroApi.insert(vote);
                try {
                    doAction(ICommonConstants.ACTION_VOTE, resource, userLoginInfo, null);
                }
                catch (Exception e){}
            }
            else
            {
                vote = exist.get(0);
                vote.setStatus("1");
                resourceVoteMicroApi.update(vote);
            }

            int voteCount = resource.getClicktimes();
            resource.setClicktimes(voteCount + 1);
            isvote = 1;
        }
//        else if (!CollectionUtils.isEmpty(exist) && resource.getIsvote() != 0){
        else if (!CollectionUtils.isEmpty(exist) && exist.get(0).getStatus().equalsIgnoreCase("1")){
            vote = exist.get(0);
            vote.setStatus("0");
            resourceVoteMicroApi.update(vote);

            int voteCount = resource.getClicktimes();
            if (voteCount != 0)
                resource.setClicktimes(voteCount - 1);
        }

        resourceMicroApi.click(resource);
        getHotValue(resource);
        resourceMicroApi.updateHot(resource);

        Map<String, Object> mapResult = new HashMap<>();
        mapResult.put("clicktimes", resource.getClicktimes());
        mapResult.put("isvote", isvote);
        return new Response<>(mapResult);
    }

    @Override
    public Response getResourceStatus(@PathVariable(value = "mode") int mode, @RequestHeader(value = "token") String token) {
        return  resourceMicroApi.getResourceStatus(mode);
    }

    @Override
    public Response getResourceStatistics(@RequestHeader(value = "token") String token)
    {
        QueryTree statisticsQuery = new QueryTree();
        statisticsQuery.addCondition(new QueryCondition("isgoods", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, "1"));
        return resourceMicroApi.getResourceStatistics(statisticsQuery);
    }

    @Override
    public Response exportToExcel(@PathVariable(value = "mode") int mode, @RequestHeader(value = "token") String token) throws Exception{
        Map<String, Object> result = new HashMap<>();

        Boolean isEcrDirExist = new File(ecrFileLocation).isDirectory();
        if (isEcrDirExist == false) {
            boolean res = new File(ecrFileLocation).mkdir();
            if (!res) {
                return new Response<>("", "");
            }
        }

        if (mode == 1) {
            QueryTree allowTree, forbidTree;
            long allow, forbid;

            allowTree = new QueryTree();
            allowTree.addCondition(new QueryCondition("isgoods", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, "1"));
            forbidTree = new QueryTree();
            forbidTree.addCondition(new QueryCondition("isgoods", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, "0"));

            allow = resourceMicroApi.getList(allowTree).getPageInfo().getTotal();
            forbid = resourceMicroApi.getList(forbidTree).getPageInfo().getTotal();

            String homePath = httpSession.getServletContext().getRealPath("/") + "excelFiles/";
            boolean isDirExist = new File(homePath).isDirectory();
            if (isDirExist == false) {
                boolean res = new File(homePath).mkdir();
                if (!res) {
                    return new Response<>(IErrorMessageConstants.ERR_CODE_FILE_NOT_EXIST, IErrorMessageConstants.ERR_MSG_FILE_NOT_EXIST);
                }
            }
            String fileName = CommonUtil.convertToMd5(new Timestamp(System.currentTimeMillis()).toString(), true) + ".xls";
            File chartFile = new File(homePath + fileName);

            String templateFile = ecrFileLocation + "/GoodStatusTemplate/PieChartTemplate_1.xls";

            if (templateFile.charAt(1) == ':' && templateFile.charAt(2) == '/')
                templateFile = "file://" + templateFile;

            URL url = new URL(templateFile);
            InputStream inputStream = url.openStream();
            HSSFWorkbook workbook = new HSSFWorkbook(inputStream);
            HSSFSheet sheet = workbook.getSheet("Data");

            sheet.createRow(0);sheet.getRow(0).createCell(1);
            sheet.getRow(0).getCell(1).setCellValue("Allowed/Forbit Goods");
            sheet.createRow(1);sheet.getRow(1).createCell(0);sheet.getRow(1).createCell(1);
            sheet.getRow(1).getCell(0).setCellValue("Allowed Goods");
            sheet.getRow(1).getCell(1).setCellValue(allow);
            sheet.createRow(2);sheet.getRow(2).createCell(0);sheet.getRow(2).createCell(1);
            sheet.getRow(2).getCell(0).setCellValue("Forbit Goods");
            sheet.getRow(2).getCell(1).setCellValue(forbid);
            sheet.createRow(3);sheet.getRow(3).createCell(0);sheet.getRow(3).createCell(1);
            sheet.getRow(3).getCell(0).setCellValue("Total Goods");
            sheet.getRow(3).getCell(1).setCellValue(allow + forbid);

            inputStream.close();
            FileOutputStream outputStream = new FileOutputStream(chartFile);
            workbook.write(outputStream);
            outputStream.close();

            result.put("chartDataUrl", getRootURL() + "/excelFiles/" + fileName);
        }

        else
        {
            Map<String, Object> statData = resourceMicroApi.getResourceStatus(mode > 1 ? mode - 1 : 5).getResponseEntity();
            ArrayList<String> title = (ArrayList<String>)statData.get("title");
            ArrayList<Integer> data = (ArrayList<Integer>)statData.get("data");

            String homePath = httpSession.getServletContext().getRealPath("/") + "excelFiles/";
            boolean isDirExist = new File(homePath).isDirectory();
            if (isDirExist == false) {
                boolean res = new File(homePath).mkdir();
                if (!res) {
                    return new Response<>("", "");
                }
            }
// Author : GOD, 管理端-资源统计-统计概况-图形 切换图形，点导出按钮，提示加载失败.
// Modify Reason : Bug Id 496
// Data Start : 2019-1-29 3:30 PM

//            String fileName = CommonUtil.convertToMd5(new Timestamp(System.currentTimeMillis()).toString(), true) + ".xls";
            String fileName = "";
            if (mode > 1)
                fileName += "BarChartTemplate_" + Integer.toString(mode - 1) + ".xls";
            else
                fileName +="PieChartTemplate_0.xls";

            File chartFile = new File(homePath + fileName);


//            String templateFile = ecrFileLocation + "/GoodStatusTemplate/";
//            if (mode > 1)
//                templateFile += "BarChartTemplate_" + Integer.toString(mode - 1) + ".xls";
//            else
//                templateFile +="PieChartTemplate_0.xls";
//
//            if (templateFile.charAt(1) == ':' && templateFile.charAt(2) == '/')
//                templateFile = "file://" + templateFile;
//
//            URL url = new URL(templateFile);
//            InputStream inputStream = url.openStream();
//            HSSFWorkbook workbook = new HSSFWorkbook(inputStream);
            FileOutputStream outputStream = new FileOutputStream(chartFile);
            HSSFWorkbook workbook = new HSSFWorkbook();
//            HSSFSheet sheet = workbook.getSheet();

            HSSFSheet sheet = workbook.createSheet();
            sheet.createRow(0);
            sheet.getRow(0).createCell(1);
            sheet.getRow(0).getCell(1).setCellValue("Goods Statistics");

            for (int i = 1; i <= title.size(); i++)
            {
                sheet.createRow(i);
                sheet.getRow(i).createCell(0);sheet.getRow(i).getCell(0).setCellValue(title.get(i - 1));
                sheet.getRow(i).createCell(1);sheet.getRow(i).getCell(1).setCellValue(data.get(i - 1));
            }

//            inputStream.close();

            workbook.write(outputStream);
            outputStream.close();

            result.put("chartDataUrl", getRootURL() + "/excelFiles/" + fileName);
        }

        return new Response<Map<String, Object>>(result);
    }
// Author : GOD, 管理端-资源统计-统计概况-图形 切换图形，点导出按钮，提示加载失败.
// Modify Reason : Bug Id 496
// Data End : 2019-1-28 4:15 PM
    @Override
    public Response getTopTenUsers(@RequestHeader(value = "token") String token) {
        QueryTree queryTree = new QueryTree();
        List<ExportMan> exportManList = resourceMicroApi.getExportManList(queryTree).getPageInfo().getList();
        List<ExportMan> result;
        for (ExportMan man :exportManList)
        {
            UserLoginInfo user = userMicroApi.get(man.getUserid()).getResponseEntity();
            if (user != null)
                man.setUsername(user.getRealName());
        }

        result = exportManList.subList(0, exportManList.size() >= 10 ? 10 : exportManList.size());

        return new Response<List<ExportMan>>(result);
    }


    @Override
    public Response getActivityResourceList(@RequestBody QueryTree queryTree, @RequestHeader(value = "token") String token) {
        Response<UserLoginInfo> userResponse = getUserByToken(token);
        UserLoginInfo user = userResponse.getResponseEntity();
        if (user == null) return userResponse;

        List<String> activityids = activityMicroApi.getIdsByQueryTree(queryTree).getPageInfo().getList();
        QueryCondition nameCond = queryTree.getQueryCondition("activityname");
        QueryCondition statusCond = queryTree.getQueryCondition("status");
        if (nameCond != null && statusCond != null) {
            queryTree.addCondition(new QueryCondition("activityids", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, activityids));
        }

        //author:han, reason:no need to add 'activityjoiner'
        //queryTree.getConditions().add(new QueryCondition("activityjoiner", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, user.getUserId()));
        //end:han
        if (statusCond != null)
            queryTree.getConditions().remove(statusCond);

        boolean isReward = false;
        QueryCondition activitytypeCond = queryTree.getQueryCondition("activitytype");
        if (activitytypeCond != null) {
            Object[] values = activitytypeCond.getFieldValues();
            if (!ObjectUtils.isEmpty(values)) {
                String activitytype = (String) values[0];
                isReward = StringUtils.equals(activitytype, IActivityMicroConstants.ACTIVITY_TYPE_REWARD);
            }
        }

        QueryCondition creatornameCond = queryTree.getQueryCondition("realname");
        if (creatornameCond != null) {
            List<String> creatorids = userMicroApi.getUserIdsByQueryTree(queryTree).getPageInfo().getList();
            queryTree.addCondition(new QueryCondition("creatorids", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, creatorids));
        }

        Response<Resource>  resourceResponse = resourceMicroApi.getList(queryTree);
        List<Resource> resources = resourceResponse.getPageInfo().getList();
        if (!CollectionUtils.isEmpty(resources)) {
            for (Resource resource : resources) {
                validateResource(resource);
                if (isReward && !StringUtils.isEmpty(resource.getActivityid())) {
                    Activity rewardActivity = activityMicroApi.get(resource.getActivityid()).getResponseEntity();
                    if (rewardActivity != null) resource.setActivitystatus(rewardActivity.getStatus());
                }
            }
        }
        return  resourceResponse;
    }


    @Override
    public Response getResourceCountByArea(@RequestBody Map<String, Object> param, @RequestHeader(value = "token") String token)
    {
        List<String> areaIdList = (List<String>)param.get("areaids");
        List<Map<String, Object>> resourceCounts = new ArrayList<>();

        for(String areaId : areaIdList)
        {
            if (StringUtils.isEmpty(areaId))
                continue;

            QueryTree areaTree = new QueryTree();
            areaTree.addCondition(new QueryCondition("areaid", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, areaId));
            List<UserLoginInfo> users = userMicroApi.getList(areaTree).getPageInfo().getList();
            Area area = areaMicroApi.get(areaId).getResponseEntity();

            Map<String, Object> result;

            if (!CollectionUtils.isEmpty(users))
            {
                List<String> creatorIds = new ArrayList<>();
                for(UserLoginInfo user : users)
                    creatorIds.add(user.getUserId());
                creatorIds.add("");

                QueryTree areaQuery = new QueryTree();
                areaQuery.addCondition(new QueryCondition("isgoods", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL,"1"));
                areaQuery.addCondition(new QueryCondition("creatorids", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL,creatorIds));

                result = resourceMicroApi.getResourceStatistics(areaQuery).getResponseEntity();

            }
            else{
                result = new HashMap<>();
                result.put("total", 0);
                result.put("today", 0);
                result.put("yesterday", 0);
                result.put("lastweek", 0);
                result.put("lastmonth", 0);
            }

            result.put("name", area.getAreaname());
            resourceCounts.add(result);
        }

        return new Response<List<Map<String, Object>>>(resourceCounts);
    }

    @Override
    public Response setResourceLabel(@RequestBody Map<String, String> param, @RequestHeader(value = "token") String token) {
        Response<UserLoginInfo> resp = getUserByToken(token);
        if (!resp.getServerResult().getResultCode().equalsIgnoreCase(IErrorMessageConstants.OPERATION_SUCCESS))
            return resp;

        UserLoginInfo userLoginInfo = resp.getResponseEntity();
        String contentid = param.get("contentid");
        String onelabelid = param.get("onelabel");
        String twolabelid = param.get("twolabel");
        String threelabelid = param.get("threelabel");

        Resource resource = new Resource();
        resource.setContentid(contentid);
        resource.setLastmodifier(userLoginInfo.getUserId());
        resource.setOnelabelid(onelabelid);
        resource.setTwolabelid(twolabelid);
        resource.setThreelabelid(threelabelid);
        return resourceMicroApi.update(resource);
    }

    @Override
    public Response deleteBatchResources(@RequestBody List<String> contentids) {
        if (esSyncFlag.equalsIgnoreCase(ICommonConstants.ES_SAVING_FLAG))
            return new Response<>(IErrorMessageConstants.ERR_CODE_ES_SAVING_MODIFY_DISABLE, IErrorMessageConstants.ERR_MSG_ES_SAVING_MODIFY_DISABLE);
//
//        List<Resource> result = new ArrayList<>();
//        for (String contentid : contentids) {
//            Resource resource = resourceMicroApi.get(contentid).getResponseEntity();
//            if (resource != null) {
//                resourceMicroApi.delete(contentid);
//                resourceMicroApi.disconnectFile(contentid);
//                resourceMicroApi.disconnectBookChapter(contentid);
//                UserLoginInfo userLoginInfo = userMicroApi.get(resource.getCreator()).getResponseEntity();
//                if (userLoginInfo != null) {
//                    Integer goodscount = userLoginInfo.getGoodscount();
//                    if (goodscount != null)
//                        userLoginInfo.setGoodscount(goodscount-1);
//                }
//                result.add(resource);
//            }
//        }

        Response ret = resourceMicroApi.batchDelete(contentids);

        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return ret;
    }

    @Override
    @ApiOperation(value = "根据ID资源文件上传", notes = "根据ID资源文件上传")
    @ApiImplicitParam(name = "fileInfos", value = "资源文件信息", required = true, dataType = "Map<String, Object>", paramType = "body")
    @SystemControllerLog(description = "根据ID资源文件上传")
    public Response uploadFileInfo(@RequestBody Map<String, Object> fileInfos, @RequestHeader(value = "token") String token) {
        if (esSyncFlag.equalsIgnoreCase(ICommonConstants.ES_SAVING_FLAG))
            return new Response<>(IErrorMessageConstants.ERR_CODE_ES_SAVING_MODIFY_DISABLE, IErrorMessageConstants.ERR_MSG_ES_SAVING_MODIFY_DISABLE);

        String contentid = fileInfos.get("contentid").toString();
        Resource resource = resourceMicroApi.get(contentid).getResponseEntity();

        Object thumbpath = fileInfos.get("thumbnailpath");
        if (thumbpath != null) {
            String path = thumbpath.toString();
            if (!StringUtils.isEmpty(path)) {
                resource.setThumbnailpath(path);
                resourceMicroApi.update(resource).getResponseEntity();
            }
        }

        Response<UserLoginInfo> userResponse = getUserByToken(token);
        UserLoginInfo user = userResponse.getResponseEntity();
        if (user == null) {
            if (resource != null)
                resourceMicroApi.delete(resource.getContentid());
            return userResponse;}

        if (resource == null) {
            throw new ParameterException(IStateCode.PARAMETER_IS_INVALID,IErrorMessageConstants.ERR_MSG_RESOURCE_NOT_EXIST);
        }

        FileInfo fileInfo = new FileInfo();
        Map<String, Object> fileinfo = (Map<String, Object>) fileInfos.get("fileinfo");
        if (fileinfo.get("fileid") != null) {
            fileInfo.setFileid(fileinfo.get("fileid").toString());
        }
        if (fileinfo.get("filename") != null)
            fileInfo.setFilename(fileinfo.get("filename").toString());
        if (fileinfo.get("size") != null)
            fileInfo.setSize(fileinfo.get("size").toString());
        if (fileinfo.get("localpath") != null)
            fileInfo.setLocalpath(fileinfo.get("localpath").toString());
        if (fileinfo.get("format") != null)
            fileInfo.setFormat(fileinfo.get("format").toString());
        if (fileinfo.get("md5") != null)
            fileInfo.setFile_md5(fileinfo.get("md5").toString());
        if (fileinfo.get("width") != null)
            fileInfo.setWidth(Integer.parseInt(fileinfo.get("width").toString()));
        if (fileinfo.get("height") != null)
            fileInfo.setHeight(Integer.parseInt(fileinfo.get("height").toString()));

        Object activityid = fileInfos.get("activityid");
        if (activityid != null) {
            Activity activity = activityMicroApi.get(activityid.toString()).getResponseEntity();
            if (activity != null) {
                boolean isCorrectFormat = StringUtils.contains(activity.getFormat(), fileInfo.getFormat());
                if (!isCorrectFormat) {
                    resourceMicroApi.delete(resource.getContentid());
                    return new Response(IErrorMessageConstants.ERR_CODE_FORMAT_NOT_MATCH, IErrorMessageConstants.ERR_MSG_FORMAT_NOT_MATCH);
                }
                if (!StringUtils.equals(user.getUserId(), activity.getCreator()) && matchRoleId2User(user.getUserId(), IBaseMicroConstants.USER_ROLE_ID_TEACHER)) {
                    Map<String, String> connectInfo = new HashMap<>();
                    connectInfo.put("activityid", activityid.toString());
                    connectInfo.put("contentid", contentid);
                    connectInfo.put("contenttype", activity.getActivitytype());
                    resourceMicroApi.connectActivity(connectInfo);
                } else {
                    resourceMicroApi.delete(contentid);
                    userResponse.getServerResult().setResultCode(IErrorMessageConstants.ERR_CODE_NOT_JOIN);
                    userResponse.getServerResult().setResultMessage(IErrorMessageConstants.ERR_MSG_NOT_JOIN);
                    return userResponse;
                }
            }
        }

        fileInfo.setCreatetime(new Timestamp(System.currentTimeMillis()));
        fileInfo.setLastmodifytime(new Timestamp(System.currentTimeMillis()));
        fileInfo = fileMicroApi.add(fileInfo).getResponseEntity();

        if (fileInfo == null) {
            resourceMicroApi.delete(contentid);

            Response<Resource> resp = new Response<>(resource);
            resp.getServerResult().setResultCode(IStateCode.PARAMETER_IS_INVALID);
            resp.getServerResult().setResultMessage(IErrorMessageConstants.ERR_MSG_RESOURCE_ALREADY_EXIST);
            return resp;
        }

        resource.setFileInfo(fileInfo);
        connect2Fileinfo(resource);


        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        return new Response<>(resource);
    }

    @Override
    public Response getGoodsCount(@RequestBody QueryTree queryTree, @RequestHeader(value = "token") String token) {
        Map<String, Object> result = new HashMap<>();

        QueryTree totalTree = new QueryTree();
        totalTree.getPagination().setNumPerPage(1);
        long total = goodMicroApi.getList(totalTree).getPageInfo().getTotal();

        QueryTree allowTree = new QueryTree();
        allowTree.getPagination().setNumPerPage(1);
        allowTree.addCondition(new QueryCondition("status", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, "1"));
        long count = goodMicroApi.getList(allowTree).getPageInfo().getTotal();

        result.put("total", total);
        result.put("count", count);

        return new Response<>(result);
    }

    @Override
    public Response downResourceTemplate(@RequestHeader(value = "token") String token) {
        Map<String, Object> result = new HashMap<>();

        result.put("downloadUrl", getRootURL() + "/template.xlsx");
        return new Response<>(result);
    }

    private void connect2Fileinfo(Resource resource) {
        if (resource == null) return;
        Map<String, String> map = new HashMap<>();
        map.put("contentid", resource.getContentid());
        map.put("contenttype", resource.getContenttype());
        map.put("fileid", resource.getFileInfo().getFileid());
        resourceMicroApi.connectFile(map);
    }

    private FileInfo reconnectFileinfo(Resource resource) {
        FileInfo result = null;
        if (resource != null) {
            String contentid = resource.getContentid();
            resourceMicroApi.disconnectFile(contentid);
            if (resource.getFileInfo() != null && !StringUtils.isEmpty(resource.getFileInfo().getFileid())) {
                Map<String, String> map = new HashMap<>();
                map.put("contentid", contentid);
                map.put("contenttype", resource.getContenttype());
                map.put("fileid", resource.getFileInfo().getFileid());
                resourceMicroApi.connectFile(map);
            }
        }

        return result;
    }

    //for resource list
    private List<Resource> validateResourceList(List<Resource> resources) {
        for (Resource resource : resources) {
            validateResource(resource);
        }

        return resources;
    }

    private Map<String, String> parseRemarks(Resource param, UserLoginInfo userLoginInfo) {
        Map<String, String> resultMap = null;

        Resource resource = resourceMicroApi.get(param.getContentid()).getResponseEntity();
        if (resource != null && !StringUtils.isEmpty(resource.getRemarks())) {
            JSONObject remarksJson = JSON.parseObject(resource.getRemarks());
            String chapter = remarksJson.getString("chapter");
            String lesson = remarksJson.getString("lesson");
            String bookmodel = remarksJson.getString("bookmodel");
            String subject = remarksJson.getString("subject");
            String onelabel = remarksJson.getString("onelabel");
            String grade = remarksJson.getString("grade");
            String editiontype = remarksJson.getString("editiontype");
            String section = remarksJson.getString("section");
            String schoolsection = remarksJson.getString("schoolsection");
            String twolabel = remarksJson.getString("twolabel");
            String contenttype = remarksJson.getString("contenttype");

            //check filepath
            String curfilepath = resource.getFilepath();
            if (!StringUtils.isEmpty(curfilepath)) {
                File file = new File(curfilepath);
                if (file.exists()) {
                    String response = HttpClientUtil.doMultiPartPost(ecoServerIp+ICommonConstants.ECO_FILE_UPlOAD_URL, file);
                    String filename = file.getName();
                    FileInfo fileinfo = parseFileInfoResponse(response, filename);
                    if (fileinfo != null) {
                        fileinfo = fileMicroApi.add(fileinfo).getResponseEntity();
                        if (fileinfo != null) {
                            resource.setFileInfo(fileinfo);
                            reconnectFileinfo(resource);
                        }
                        remarksJson.fluentPut("filepath", fileinfo!=null?"":curfilepath);
                    }
                }
            }

            //check schoolsection id
            if (!StringUtils.isEmpty(schoolsection) && (getDictItemById(resource.getSchoolsectionid()) != null || resource.getSchoolsectionid().isEmpty())) {
                remarksJson.fluentPut("schoolsection", "");
            }

            //check subject id
            if (!StringUtils.isEmpty(subject) && (getDictItemById(resource.getSubjectid()) != null || resource.getSubjectid().isEmpty())) {
                remarksJson.fluentPut("subject", "");
            }

            //check schoolsection id
            if (!StringUtils.isEmpty(grade) && (getDictItemById(resource.getGradeid()) != null || resource.getGradeid().isEmpty())) {
                remarksJson.fluentPut("grade", "");
            }

            //check schoolsection id
            if (!StringUtils.isEmpty(editiontype) && (getDictItemById(resource.getEditiontypeid()) != null || resource.getEditiontypeid().isEmpty())) {
                remarksJson.fluentPut("editiontype", "");
            }

            //check schoolsection id
            if (!StringUtils.isEmpty(bookmodel) && (getDictItemById(resource.getBookmodelid()) != null || resource.getBookmodelid().isEmpty())) {
                remarksJson.fluentPut("bookmodel", "");
            }

            //check schoolsection id
            if (!StringUtils.isEmpty(onelabel) && !StringUtils.isEmpty(resource.getOnelabelid())) {
                remarksJson.fluentPut("onelabel", "");
            }

            //check schoolsection id
            if (!StringUtils.isEmpty(twolabel) && !StringUtils.isEmpty(resource.getTwolabel())) {
                remarksJson.fluentPut("twolabel", "");
            }

            if (!StringUtils.isEmpty(chapter) && !CollectionUtils.isEmpty(param.getCatalogids())) {
                remarksJson.fluentPut("chapter", "");
            }

            if (!StringUtils.isEmpty(section) && !CollectionUtils.isEmpty(param.getCatalogids())) {
                remarksJson.fluentPut("section", "");
            }

            if (!StringUtils.isEmpty(lesson) && !CollectionUtils.isEmpty(param.getCatalogids())) {
                remarksJson.fluentPut("lesson", "");
            }

            if (!StringUtils.isEmpty(contenttype) && !StringUtils.isEmpty(resource.getContenttype())) {
                remarksJson.fluentPut("contenttype", "");
            }

            resultMap = new HashMap<>();
            boolean isSuccess = StringUtils.isEmpty(remarksJson.getString("filepath"))
                    && StringUtils.isEmpty(remarksJson.getString("schoolsection"))
                    && StringUtils.isEmpty(remarksJson.getString("subject"))
                    && StringUtils.isEmpty(remarksJson.getString("grade"))
                    && StringUtils.isEmpty(remarksJson.getString("editiontype"))
                    && StringUtils.isEmpty(remarksJson.getString("bookmodel"))
                    && StringUtils.isEmpty(remarksJson.getString("onelabel"))
                    && StringUtils.isEmpty(remarksJson.getString("twolabel"))
                    && StringUtils.isEmpty(remarksJson.getString("chapter"))
                    && StringUtils.isEmpty(remarksJson.getString("section"))
                    && StringUtils.isEmpty(remarksJson.getString("contenttype"));
            if (isSuccess && StringUtils.equals(param.getIsgoods(), IResourceMicroConstants.RES_SOURCE_NOT_GOODS)) {
                param.setGoodinfo(addDefaultGood(resource, userLoginInfo));
                addAuditInfo2Resource(resource, userLoginInfo);
            }

            resultMap.put("importstatus", isSuccess?"1":"0");
            remarksJson.fluentPut("filepath", !isSuccess?curfilepath:remarksJson.getString("filepath"));
            resultMap.put("remarks", remarksJson.toJSONString());
        }
        return resultMap;
    }

    private void reconnectBookChapter(Resource entity, List<String> catalogIds) {
        if (entity != null && !CollectionUtils.isEmpty(catalogIds)) {
            resourceMicroApi.disconnectBookChapter(entity.getContentid());

            for (String catalogid : catalogIds) {
                if (StringUtils.isEmpty(catalogid)) continue;
                Catalog catalog = catalogMicroApi.get(catalogid).getResponseEntity();
                if (catalog == null)
                    continue;

                String bookid = catalog.getBookid();
                Map<String, String> map = new HashMap<>();
                map.put("contentid", entity.getContentid());
                map.put("resourcetype", entity.getContenttype());

                map.put("chapterid", catalogid);

                map.put("bookid", bookid);

                resourceMicroApi.connectBookChapter(map);
            }
        }
    }

    public Response batchUploadByFile(File file, UserLoginInfo userLoginInfo) {

        if (!file.exists())
            return null;
        else {
            List<Resource> resources = new ArrayList<>();

            ReadExcel excel = new ReadExcel();
            List<List<String>> data = excel.read(file.getPath());
            List<Resource> resourceDatas = parseResourceData(data);
//            Author : GOD 2019-2-18 Bug ID: #784
            List<String> reportMessages = new ArrayList<>();
            for (Resource resourcedata : resourceDatas) {
                Resource resource = new Resource();
                JSONObject jsonObject = new JSONObject();

                String importStatus = IResourceMicroConstants.RES_IMPORT_STATUS_SUCCESS;
                String importFileStatus = IResourceMicroConstants.RES_IMPORT_STATUS_SUCCESS;

                String resourcename = resourcedata.getName();
                String schoolSectionName = resourcedata.getSchoolsection();
                String subjectName = resourcedata.getSubject();
                String editionTypeName = resourcedata.getEditiontype();
                String gradeName = resourcedata.getGrade();
                String bookmodelname = resourcedata.getBookmodel();
                String knowlege = resourcedata.getKnowledgepoint();
                String bookname = resourcedata.getBookname();
                String chapter = resourcedata.getChapter();
                String section = resourcedata.getSection();
                String lesson = resourcedata.getContentno();
                String contenttype = resourcedata.getContenttype();
                String onelabel = resourcedata.getOnelabel();
                String twolabel = resourcedata.getTwolabel();
                String threelabel = resourcedata.getThreelabel();
                String fitObject = resourcedata.getFitobject();
                String description = resourcedata.getDescription();
                String sourceid = resourcedata.getSourceid();
                String sharerange = resourcedata.getSharerange();
                String creatorname = resourcedata.getCreatorname();
                String status = resourcedata.getStatus();
                Integer score = resourcedata.getScore();
                String isallowdownload = resourcedata.getIsallowdownload();
                String filepath = resourcedata.getFilepath();

                if (StringUtils.isEmpty(contenttype)) continue;
                DictItem contenttypeDictItem = null;
                DictItem dict = new DictItem();
                dict.setDictvalue(contenttype);
                dict.setDicttypeid("CONTENT_TYPE");
                contenttypeDictItem = dictItemMicroApi.getByDictInfo(dict).getResponseEntity();

                FileInfo connectFileInfo = null;
                boolean isRight = false;
                if (!StringUtils.isEmpty(filepath)) {
//                    filepath = CommonUtil.trimString(filepath);
                    File uploadFile = new File(filepath);
                    isRight = checkContenttype(filepath, contenttypeDictItem);
                    if (!isRight)
                        importStatus = IResourceMicroConstants.RES_IMPORT_STATUS_FAIL;

                    if (uploadFile.exists()) {
                        String filename = uploadFile.getName();
                        String response = HttpClientUtil.doMultiPartPost(ecoServerIp+ICommonConstants.ECO_FILE_UPlOAD_URL, uploadFile);
                        FileInfo fileInfo = parseFileInfoResponse(response, filename);
                        connectFileInfo = fileMicroApi.add(fileInfo).getResponseEntity();
                        resource.setFileInfo(connectFileInfo);
                    } else {
                        JSONObject reportMessage = new JSONObject();
                        reportMessage.put("fileNotExist", resourcename);
                        reportMessages.add(reportMessage.toJSONString());
                        continue;
                    }
                    importFileStatus = (uploadFile.exists() && connectFileInfo != null) ? IResourceMicroConstants.RES_IMPORT_STATUS_SUCCESS : IResourceMicroConstants.RES_IMPORT_STATUS_FAIL;
                    jsonObject.put("filepath", (uploadFile.exists() && connectFileInfo != null)?"":filepath);

                }
                resource.setContenttype(contenttype);
                resource.setFilepath(filepath);
                resource.setName(resourcename);
                resource.setIsallowdownload(isallowdownload);
                resource.setSharerange(sharerange);
                resource.setFitobject(fitObject);
                resource.setDescription(description);
                resource.setKnowledgepoint(knowlege);

                DictItem schoolsectionDictItem = null;
                DictItem subjectDictItem = null;
                DictItem gradeDictItem = null;
                DictItem editiontypeDictItem = null;
                DictItem bookmodelDictItem = null;

                //validate resource for schoolsection id
                if (!StringUtils.isEmpty(schoolSectionName)) {
                    schoolsectionDictItem = getDictItemByName(schoolSectionName, null);
                    if (schoolsectionDictItem != null) {
                        resource.setSchoolsectionid(schoolsectionDictItem.getDictvalue());
                        resource.setSchoolsection(schoolSectionName);
                    } else {
                        importStatus = IResourceMicroConstants.RES_IMPORT_STATUS_FAIL;
                        schoolsectionDictItem = null;
                    }
                } else {
                    importStatus = IResourceMicroConstants.RES_IMPORT_STATUS_FAIL;
                }

                //validate resource for subject id
                if (!StringUtils.isEmpty(subjectName) && schoolsectionDictItem != null) {
                    subjectDictItem = getDictItemByName(subjectName, schoolsectionDictItem.getDictid());
                    if (subjectDictItem != null) {
                        resource.setSubjectid(subjectDictItem.getDictvalue());
                        resource.setSubject(subjectName);
                    } else {
                        importStatus = IResourceMicroConstants.RES_IMPORT_STATUS_FAIL;
                        subjectDictItem = null;
                    }
                } else {
                    importStatus = IResourceMicroConstants.RES_IMPORT_STATUS_FAIL;
                }

                QueryTree queryTree = new QueryTree();
                if (schoolsectionDictItem != null) {
                    queryTree.addCondition(new QueryCondition("schoolsectionid", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, schoolsectionDictItem.getDictvalue()));
                    if (subjectDictItem != null) {
                        queryTree.addCondition(new QueryCondition("subjectid", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, subjectDictItem.getDictvalue()));
                    }
                }

                //validate resource for grade id
                if (!StringUtils.isEmpty(gradeName) && schoolsectionDictItem != null) {
                    gradeDictItem = getDictItemByName(gradeName, schoolsectionDictItem.getDictid());
                    boolean isGrade = (gradeDictItem != null);
                    if (isGrade) {
                        resource.setGradeid(gradeDictItem.getDictvalue());
                        resource.setGrade(gradeName);
                    } else {
                        importStatus = IResourceMicroConstants.RES_IMPORT_STATUS_FAIL;
                        gradeDictItem = null;
                    }
                } else {
                    importStatus = IResourceMicroConstants.RES_IMPORT_STATUS_FAIL;
                }

                boolean iseditiontype = false;
                //validate resource for editiontype id
                if (!StringUtils.isEmpty(editionTypeName)) {
                    editiontypeDictItem = getDictItemByName(editionTypeName, null);
                    iseditiontype = (schoolsectionDictItem != null && subjectDictItem != null && editiontypeDictItem != null);
                    if (iseditiontype) {
                        queryTree.addCondition(new QueryCondition("editiontypeid", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, editiontypeDictItem.getDictvalue()));
                    }
                    iseditiontype = iseditiontype && !CollectionUtils.isEmpty(bookMicroApi.getList(queryTree).getPageInfo().getList());
                    if (iseditiontype) {
                        resource.setEditiontypeid(editiontypeDictItem.getDictvalue());
                        resource.setEditiontype(editionTypeName);
                    } else {
                        importStatus = IResourceMicroConstants.RES_IMPORT_STATUS_FAIL;
                        editiontypeDictItem = null;
                    }
                }

                //validate resource for bookmodel id
                if (!StringUtils.isEmpty(bookmodelname)) {
                    bookmodelDictItem = getDictItemByName(bookmodelname, null);
                    boolean isbookmodel = (iseditiontype && bookmodelDictItem != null);
                    if (isbookmodel) {
                        queryTree.addCondition(new QueryCondition("bookmodelid", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, bookmodelDictItem.getDictvalue()));
                    }
                    isbookmodel = isbookmodel && !CollectionUtils.isEmpty(bookMicroApi.getList(queryTree).getPageInfo().getList());
                    if (isbookmodel) {
                        resource.setBookmodelid(bookmodelDictItem.getDictvalue());
                        resource.setBookmodel(bookmodelname);
                    } else {
                        importStatus = IResourceMicroConstants.RES_IMPORT_STATUS_FAIL;
                        bookmodelDictItem = null;
                    }
                }

                List<String> catalogids = new ArrayList<>();

                Navigation example = new Navigation();
//                if(bookname != null) {
//                    example.setBookname(bookname);
//                }

//                Map<String, Object> bookMap =  new HashMap<>();
//                bookMap.put("bookname", bookname);

                if (schoolsectionDictItem != null) {
                    example.setSchoolsectionid(schoolsectionDictItem.getDictvalue());
                }
                if (subjectDictItem != null) {
                    example.setSubjectid(subjectDictItem.getDictvalue());
                }
                if (gradeDictItem != null) {
                    example.setGradeid(gradeDictItem.getDictvalue());
                }
                if (editiontypeDictItem != null) {
                    example.setEditiontypeid(editiontypeDictItem.getDictvalue());
                }
                if (bookmodelDictItem != null) {
                    example.setBookmodelid(bookmodelDictItem.getDictvalue());
                }
//                Book book = bookMicroApi.getByNavigation(example).getResponseEntity();
                List<Book> books = bookMicroApi.getBookByNavigation(example).getPageInfo().getList();
//                Book book = bookMicroApi.getBookByBookname(bookMap).getResponseEntity();
                for (Book book : books) {
                    if(book.getBookname().equalsIgnoreCase(bookname)) {
                        String[] chapters = chapter.split(",");
                        Catalog chaptercatalog = null, sectioncatalog=null, lessoncatalog = null;
                        if(!StringUtils.isEmpty(chapter)) {
                            for (String catalogname : chapters) {
                                catalogname = CommonUtil.trimString(catalogname);
                                try{
                                    chaptercatalog = getCatalogByName(resource, catalogname, book.getBookid(), "0");
                                } catch (NullPointerException e) {
                                    JSONObject reportMessage = new JSONObject();
                                    reportMessage.put("chapterNotExist", resourcename);
                                    reportMessages.add(reportMessage.toJSONString());
                                    importStatus = IResourceMicroConstants.RES_IMPORT_STATUS_FAIL;
                                }

                                if (chaptercatalog != null) {
                                    resource.setChapter(chaptercatalog.getCatalogname());
                                    catalogids.add(chaptercatalog.getCatalogid());
                                } else {
                                    JSONObject reportMessage = new JSONObject();
                                    reportMessage.put("chapterNotExist", resourcename);
                                    reportMessages.add(reportMessage.toJSONString());
                                    importStatus = IResourceMicroConstants.RES_IMPORT_STATUS_FAIL;
                                }
                            }
                        }

                        if(!StringUtils.isEmpty(section)) {
                            String[] sections = section.split(",");
                            for (String catalogname : sections) {
                                catalogname = CommonUtil.trimString(catalogname);

                                try{
                                    sectioncatalog = getCatalogByName(resource, catalogname, book.getBookid(), chaptercatalog.getCatalogid());
                                } catch (NullPointerException e) {
                                    JSONObject reportMessage = new JSONObject();
                                    reportMessage.put("sectionNotExist", resourcename);
                                    reportMessages.add(reportMessage.toJSONString());
                                    importStatus = IResourceMicroConstants.RES_IMPORT_STATUS_FAIL;
                                }
                                if (sectioncatalog != null) {
                                    catalogids.add(sectioncatalog.getCatalogid());
                                    resource.setSection(sectioncatalog.getCatalogname());
                                }
                            }
                        }

                        if(!StringUtils.isEmpty(lesson)) {
                            String[] lessons = lesson.split(",");
                            for (String catalogname : lessons) {
                                catalogname = CommonUtil.trimString(catalogname);

                                try{
                                    lessoncatalog = getCatalogByName(resource, catalogname, book.getBookid(), sectioncatalog.getCatalogid());
                                } catch (NullPointerException e) {
                                    JSONObject reportMessage = new JSONObject();
                                    reportMessage.put("lessonNotExist", resourcename);
                                    reportMessages.add(reportMessage.toJSONString());
                                    importStatus = IResourceMicroConstants.RES_IMPORT_STATUS_FAIL;
                                }

                                if (lessoncatalog != null) {
                                    catalogids.add(lessoncatalog.getCatalogid());
                                }
                            }
                        }


                        jsonObject.put("chapter", chaptercatalog == null && !StringUtils.isEmpty(chapter)? chapter : "");
                        jsonObject.put("section", sectioncatalog == null && !StringUtils.isEmpty(section)? section : "");
                        jsonObject.put("lesson", lessoncatalog == null && !StringUtils.isEmpty(lesson)? lesson : "");
                    }
                }

                //validate resource for onelabel id
                Label oneLabelEntity = null, twoLabelEntity = null, threeLabelEntity = null;
                if (!StringUtils.isEmpty(onelabel)) {
                    oneLabelEntity = getLabelByName(onelabel);
                    if (oneLabelEntity != null) {
                        resource.setOnelabelid(oneLabelEntity.getLabelid());
                        resource.setOnelabel(onelabel);
                    } else {
                        importStatus = IResourceMicroConstants.RES_IMPORT_STATUS_FAIL;
                        oneLabelEntity = null;
                    }
                }

                //validate resource for twolabel id
                if (!StringUtils.isEmpty(twolabel)) {
                    twoLabelEntity = getLabelByName(twolabel);
                    if (twoLabelEntity != null && oneLabelEntity != null &&
                            StringUtils.equals(twoLabelEntity.getParentlabelid(), oneLabelEntity.getLabelid())) {
                        resource.setTwolabelid(twoLabelEntity.getLabelid());
                        resource.setTwolabel(twolabel);
                    } else {
                        importStatus = IResourceMicroConstants.RES_IMPORT_STATUS_FAIL;
                        twoLabelEntity = null;
                    }
                }

                //validate resource for threelabel id
                if (!StringUtils.isEmpty(threelabel)) {
                    threeLabelEntity = getLabelByName(threelabel);
                    if (threeLabelEntity != null && twoLabelEntity != null
                        && StringUtils.equals(threeLabelEntity.getParentlabelid(), twoLabelEntity.getLabelid())) {
                        resource.setThreelabelid(threeLabelEntity.getLabelid());
                        resource.setThreelabel(threelabel);
                    } else {
                        importStatus = IResourceMicroConstants.RES_IMPORT_STATUS_FAIL;
                        threeLabelEntity = null;
                    }
                }

                UserLoginInfo creator = null;
                if (!StringUtils.isEmpty(creatorname)) {
                    creator = userMicroApi.getUserByName(creatorname).getResponseEntity();
                    if (creator != null) {
                        resource.setCreator(creator.getUserId());
                        String sharerangekey = getSharerangeKey(creator);
                        resource.setSharerangekey(sharerangekey);
                    }
                } else {
                    continue;
                }

                importStatus = StringUtils.equals(importFileStatus, IResourceMicroConstants.RES_IMPORT_STATUS_SUCCESS) &&
                            StringUtils.equals(importStatus, IResourceMicroConstants.RES_IMPORT_STATUS_SUCCESS)? IResourceMicroConstants.RES_IMPORT_STATUS_SUCCESS:
                            IResourceMicroConstants.RES_IMPORT_STATUS_FAIL;

                schoolSectionName = StringUtils.isEmpty(schoolSectionName)?"-1":schoolSectionName;
                jsonObject.put("schoolsection", schoolsectionDictItem==null?schoolSectionName:"");
                subjectName = StringUtils.isEmpty(subjectName)?"-1":subjectName;
                jsonObject.put("subject", subjectDictItem==null?subjectName:"");
                gradeName = StringUtils.isEmpty(gradeName)?"-1":gradeName;
                jsonObject.put("grade", gradeDictItem==null?gradeName:"");
                jsonObject.put("editiontype", editiontypeDictItem==null?editionTypeName:"");
                jsonObject.put("bookmodel", bookmodelDictItem==null?bookmodelname:"");
                jsonObject.put("onelabel", oneLabelEntity==null?onelabel:"");
                jsonObject.put("twolabel", twoLabelEntity==null || !isRight?twolabel:"");
                jsonObject.put("threelabel", threeLabelEntity==null?threelabel:"");
                jsonObject.put("contenttype", contenttypeDictItem==null || !isRight?contenttype:"");
                jsonObject.put("creator", creator==null?creatorname:"");

                resource.setRemarks(jsonObject.toJSONString());
                resource.setImportstatus(importStatus);
                resource.setScore(score);
                resource.setSourceid(sourceid);
                resource.setCreator(userLoginInfo.getUserId());
                resource.setCreatorname(userLoginInfo.getRealName());
                resource.setCreatetime(new Timestamp(System.currentTimeMillis()));
                resource.setLastmodifier(userLoginInfo.getUserId());
                resource.setLastupdatetime(new Timestamp(System.currentTimeMillis()));
                resource.setStatus(status);

                boolean isgood = StringUtils.equals(importStatus, IResourceMicroConstants.RES_IMPORT_STATUS_SUCCESS) && StringUtils.equals(status, IResourceMicroConstants.RES_STATUS_PASS);
                resource.setIsgoods(isgood?"1":"0");

                // check if import successed
                if (StringUtils.equals(status, IResourceMicroConstants.RES_STATUS_PASS)) {
                    if (StringUtils.equals(importStatus, IResourceMicroConstants.RES_IMPORT_STATUS_SUCCESS))
                        resource.setStatus(status);
                    else
                        resource.setStatus(IResourceMicroConstants.RES_STATUS_HOLD);
                } else {
                    resource.setStatus(status);
                }

                resource = resourceMicroApi.add(resource).getResponseEntity();

                if (isgood) {
                    addDefaultGood(resource, userLoginInfo);
                    addAuditInfo2Resource(resource, userLoginInfo);
                }

                if (resource != null && connectFileInfo != null) {
                    Map<String, String> map = new HashMap<>();
                    map.put("fileid", connectFileInfo.getFileid());
                    map.put("contentid", resource.getContentid());
                    map.put("contenttype", resource.getContenttype());
                    resourceMicroApi.connectFile(map);
                }

                if (!CollectionUtils.isEmpty(catalogids)) {
                    List<String> catalogid = new ArrayList<>();
                    catalogid.add(catalogids.get(catalogids.size() - 1));
                    reconnectBookChapter(resource, catalogid);
                }

                validateNaviInfoByCatalogids(resource, catalogids);
                resources.add(resource);
                if (resource != null && StringUtils.isNotEmpty(resource.getContentid())) {
                    logOptionEntity(resource.getContentid(), resource.getContentid(), ICommonConstants.OPTION_OPTIONTYPE_BATCHIMPORT, userLoginInfo.getToken());
                }
            }


            Response<Resource> resp = new Response<>(resources);
            resp.getServerResult().setInternalMessage(reportMessages.toString());
            return resp;
        }
    }
    //            Author : GOD 2019-2-18 Bug ID: #784
    private List<Resource> parseResourceData(List<List<String>> data) {
        List<Resource> resources = new ArrayList<>();
        if (!CollectionUtils.isEmpty(data)) data.remove(0);
        else return resources;

        for (List<String> resourcedata : data) {
            int cn = resourcedata.size();
            if (cn < 24) break;

            Resource resource = new Resource();
            String resourcename = resourcedata.get(0);

            if (StringUtils.contains(resourcename, EXCEL_END_COMMENT) || StringUtils.isEmpty(resourcename)) break;

            resource.setName(CommonUtil.trimString(resourcename));
            resource.setSchoolsection(CommonUtil.trimString(resourcedata.get(1)));
            resource.setSubject(CommonUtil.trimString(resourcedata.get(2)));
            resource.setEditiontype(CommonUtil.trimString(resourcedata.get(3)));
            resource.setGrade(CommonUtil.trimString(resourcedata.get(4)));
            resource.setBookmodel(CommonUtil.trimString(resourcedata.get(5)));
            resource.setKnowledgepoint(CommonUtil.trimString(resourcedata.get(6)));
            resource.setBookname(CommonUtil.trimString(resourcedata.get(7)));
            resource.setChapter(CommonUtil.trimString(resourcedata.get(8)));
            resource.setSection(CommonUtil.trimString(resourcedata.get(9)));
            resource.setContentno(CommonUtil.trimString(resourcedata.get(10)));
            resource.setContenttype(CommonUtil.trimString(resourcedata.get(11)).substring(0, 1));
            resource.setOnelabel(CommonUtil.trimString(resourcedata.get(12)));
            resource.setTwolabel(CommonUtil.trimString(resourcedata.get(13)));
            resource.setThreelabel(CommonUtil.trimString(resourcedata.get(14)));
            resource.setFitobject(CommonUtil.trimString(resourcedata.get(15)));
            resource.setDescription(CommonUtil.trimString(resourcedata.get(16)));
            resource.setSourceid(CommonUtil.trimString(resourcedata.get(17)).substring(0, 1));
            resource.setSharerange(CommonUtil.trimString(resourcedata.get(18)).substring(0, 1));
            resource.setCreatorname(CommonUtil.trimString(resourcedata.get(19)));
            resource.setStatus(CommonUtil.trimString(resourcedata.get(20)).substring(0, 1));
            String scorestr = CommonUtil.trimString(resourcedata.get(21));
            int score = 0;
            try {
                score = Integer.parseInt(scorestr);
            } catch (NumberFormatException ex) {}
            resource.setScore(score);
            resource.setIsallowdownload(CommonUtil.trimString(resourcedata.get(22)).substring(0, 1));
            resource.setFilepath(resourcedata.get(23));

            resources.add(resource);
        }
        return resources;
    }

    private boolean checkContenttype(String filepath, DictItem contenttypeDictItem) {
        boolean result = false;
        File file = new File(filepath);
        if (contenttypeDictItem != null && file.exists()) {
            String contenttype = contenttypeDictItem.getDictvalue();
            String extension = FileUtil.getFileExtension(file.getName()).toLowerCase();
            switch (Integer.parseInt(contenttype)) {
                case 1:
                    result = ICommonConstants.CONTENT_TYPE_CURRICULUM.contains(extension);
                    break;

                case 2:
                    result = ICommonConstants.CONTENT_TYPE_TEACHING_PLAN.contains(extension);
                    break;

                case 3:
                    result = ICommonConstants.CONTENT_TYPE_EXTEND_TEXT.contains(extension);
                    break;

                case 4:
                    result = ICommonConstants.CONTENT_TYPE_PAPER.contains(extension);
                    break;

                case 5:
                    result = ICommonConstants.CONTENT_TYPE_IMAGE.contains(extension);
                    break;

                case 6:
                    result = ICommonConstants.CONTENT_TYPE_VIDEO.contains(extension);
                    break;

                case 7:
                    result = ICommonConstants.CONTENT_TYPE_AUDIO.contains(extension);
                    break;

                case 8:
                    result = ICommonConstants.CONTENT_TYPE_CARTOON1.contains(extension);
                    break;

                case 9:
                    result = ICommonConstants.CONTENT_TYPE_CARTOON2.contains(extension);
                    break;

                case 10:
                    result = ICommonConstants.CONTENT_TYPE_EBOOK.contains(extension);
                    break;

                case 11:
                    result = ICommonConstants.CONTENT_TYPE_ZIP.contains(extension);
                    break;

                default:
                    break;
            }
        }
        return result;
    }

    private Audit addAuditInfo2Resource(Resource resource, UserLoginInfo userLoginInfo) {
        Audit audit = new Audit();
        audit.setAudituser(userLoginInfo.getUserId());
        audit.setObjectid(resource.getContentid());
        audit.setObjecttype("1");
        audit.setAudittype("1");
        audit.setAudittime(new Timestamp(System.currentTimeMillis()));
        audit.setResult("1");
        audit = auditMicroApi.add(audit).getResponseEntity();
        if (audit != null) {
            generateContentNo(resource);
            userLoginInfo.setGoodscount(userLoginInfo.getGoodscount()+1);
            userMicroApi.update(userLoginInfo);
        }
        return audit;
    }

    private FileInfo parseFileInfoResponse(String response, String filename) {
        FileInfo fileInfo = new FileInfo();
        if (!StringUtils.isEmpty(response)) {
            JSONObject object = JSON.parseObject(response);
            if (object != null) {
                JSONObject serverResults = object.getJSONObject("serverReults");
                String resultCode = "";
                if (serverResults != null)
                    resultCode = serverResults.getString("resultCode");
                if (StringUtils.equals(resultCode, ICommonConstants.RESPONSE_RESULT_SUCCESS)) {
                    JSONArray fileinfoObjects = object.getJSONArray("fileInfos");
                    if (!CollectionUtils.isEmpty(fileinfoObjects)) {
                        JSONObject fileinfoObject = fileinfoObjects.getJSONObject(0);
                        if (fileinfoObject != null) {
                            fileInfo.setLocalpath(fileinfoObject.getString("localPath"));
                            fileInfo.setSize(fileinfoObject.getString("size"));
                            fileInfo.setFormat(fileinfoObject.getString("format"));
                            fileInfo.setFileid(fileinfoObject.getString("fileId"));
                            fileInfo.setFile_md5(fileinfoObject.getString("md5"));
                            String tempName = fileinfoObject.getString("fileName");
                            String extension = FileUtil.getFileExtension(tempName);
                            filename = filename.substring(0, filename.lastIndexOf('.'));
                            fileInfo.setFilename(filename+"."+extension);
                            System.out.printf("filepath:"+filename);

                            //image width, height
                            String width = fileinfoObject.getString("width");
                            String height = fileinfoObject.getString("height");
                            fileInfo.setWidth(width==null?0:Integer.parseInt(width));
                            fileInfo.setHeight(height==null?0:Integer.parseInt(height));

                            fileInfo.setCreatetime(new Timestamp(System.currentTimeMillis()));
                            ValidatorUtil.parameterValidate(fileInfo);
                        }
                    }
                }
            }
        }
        return fileInfo;
    }

    private Catalog getCatalogByName(Resource resource, String catalogname, String bookid, String parentid) {
        Catalog example = new Catalog();
        example.setBookid(bookid);
        example.setCatalogname(catalogname);
        example.setParentcatalogid(parentid);
        Catalog catalog = catalogMicroApi.getByExample(example).getResponseEntity();

        if (catalog != null && resource.getContentid() != null) {
            Map<String, String> map = new HashMap<>();
            map.put("bookid", bookid);

            map.put("chapterid", catalog.getCatalogid());

            map.put("contentid", resource.getContentid());
            map.put("contenttype", resource.getContenttype());

            resourceMicroApi.connectBookChapter(map);
        }
        return catalog;
    }

    @Override
    public Response getActivityDetailList(@RequestBody  QueryTree queryTree) {
        queryTree.getConditions().add(new QueryCondition("isgoods", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, IResourceMicroConstants.RES_SOURCE_GOODS));
        Response<Resource>  resourceResponse = resourceMicroApi.getListByActivityId(queryTree);
        Response<Map<String, Object>> response = new Response<>(getActivityDetailData(queryTree, resourceResponse));
        response.getPageInfo().setTotal(resourceResponse.getPageInfo().getTotal());
        return response;
    }

    private List getActivityDetailData(QueryTree queryTree, Response<Resource> resourceResponse) {
        List<Map<String, Object>> list = new ArrayList<>();
        List<Resource> resourceList = resourceResponse.getPageInfo().getList();
        if (!CollectionUtils.isEmpty(resourceList)) {
            for (Resource resource : resourceList) {
                Map<String, Object> map = new HashMap<>();
                map.put("name", resource.getName());
                UserLoginInfo userLoginInfo = userMicroApi.getUserInfo(resource.getCreator()).getResponseEntity();
                if (userLoginInfo != null) {
                    map.put("realName", userLoginInfo.getRealName());
                    map.put("orgName", userLoginInfo.getOrgName());
                    map.put("areaName", userLoginInfo.getAreaName());
                    ClassInfo classinfo = userLoginInfo.getClassinfo().get(0);
                    map.put("gradename", classinfo==null?"":classinfo.getGrade());
                    map.put("schoolsectionname", classinfo==null?"":classinfo.getSchoolsection());
                }
                map.put("createtime", resource.getCreatetime());
                map.put("viewtimes", resource.getViewtimes());
                map.put("votetimes", resource.getClicktimes());
                map.put("collectiontimes", resource.getCollectiontimes());
                map.put("downtimes", resource.getDowntimes());
                list.add(map);
            }
        }
        return list;
    }

    @Override
    public Response exportActivityDetail(@RequestBody QueryTree queryTree) throws Exception{
        queryTree.getConditions().add(new QueryCondition("isgoods", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, IResourceMicroConstants.RES_SOURCE_GOODS));
        Response<Resource>  resourceResponse = resourceMicroApi.getListByActivityId(queryTree);
        List<Map<String, Object>> datas = getActivityDetailData(queryTree, resourceResponse);
        Map<String, Object> result = new HashMap<>();

        String homePath = httpSession.getServletContext().getRealPath("/") + "excelFiles/";
        boolean isDirExist = new File(homePath).isDirectory();
        if (isDirExist == false) {
            boolean res = new File(homePath).mkdir();
            if (!res) {
                return new Response<>("", "");
            }
        }
        String fileName = CommonUtil.convertToMd5(new Timestamp(System.currentTimeMillis()).toString(), true) + ".xls";
        File chartFile = new File(homePath + fileName);
        if (!chartFile.createNewFile()) return null;

        FileOutputStream fileOutputStream = new FileOutputStream(chartFile);
        HSSFWorkbook hssfWorkbook = new HSSFWorkbook();
        HSSFSheet hssfSheet = hssfWorkbook.createSheet();

        HSSFRow topRow = hssfSheet.createRow(1);
        topRow.createCell(0);
        topRow.createCell(1);
        topRow.createCell(2);
        hssfSheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 2));
        topRow.getCell(0).setCellValue("资源排行");

        HSSFRow titleRow = hssfSheet.createRow(1);
        titleRow.createCell(0);
        titleRow.getCell(0).setCellValue("姓名");
        titleRow.createCell(1);
        titleRow.getCell(1).setCellValue("区域");
        titleRow.createCell(2);
        titleRow.getCell(2).setCellValue("学校");
        titleRow.createCell(3);
        titleRow.getCell(3).setCellValue("年级");
        titleRow.createCell(4);
        titleRow.getCell(4).setCellValue("任教学科");
        titleRow.createCell(5);
        titleRow.getCell(5).setCellValue("上传资源时间");
        titleRow.createCell(6);
        titleRow.getCell(6).setCellValue("上传资源名称");
        titleRow.createCell(7);
        titleRow.getCell(7).setCellValue("资源浏览次数");
        titleRow.createCell(8);
        titleRow.getCell(8).setCellValue("资源点赞次数");
        titleRow.createCell(9);
        titleRow.getCell(9).setCellValue("资源收藏次数");
        titleRow.createCell(10);
        titleRow.getCell(10).setCellValue("资源下载次数");

        for (int i = 0; i < datas.size(); i++) {
            Map<String, Object> data = datas.get(i);
            HSSFRow row = hssfSheet.createRow(i + 2);
            row.createCell(0);
            row.getCell(0).setCellValue((String) data.get("realName"));
            row.createCell(1);
            row.getCell(1).setCellValue((String) data.get("areaName"));
            row.createCell(2);
            row.getCell(2).setCellValue((String) data.get("orgName"));
            row.createCell(3);
            row.getCell(3).setCellValue((String) data.get("gradename"));
            row.createCell(4);
            row.getCell(4).setCellValue((String) data.get("schoolsectionname"));
            row.createCell(5);
            row.getCell(5).setCellValue(data.get("createtime").toString());
            row.createCell(6);
            row.getCell(6).setCellValue((String) data.get("name"));
            row.createCell(7);
            row.getCell(7).setCellValue((Integer) data.get("viewtimes"));
            row.createCell(8);
            row.getCell(8).setCellValue((Integer) data.get("votetimes"));
            row.createCell(9);
            row.getCell(9).setCellValue((Integer) data.get("collectiontimes"));
            row.createCell(10);
            row.getCell(10).setCellValue((Integer) data.get("downtimes"));
        }

        hssfWorkbook.write(fileOutputStream);

        result.put("downloadUrl", getRootURL() + "/excelFiles/" + fileName);

        return new Response<>(result);
    }

    public void downloadFinished(Resource resource, String token) {
        UserLoginInfo user = getUserByToken(token).getResponseEntity();
        if (user == null || resource == null) return;

        ResourceDownload entity = new ResourceDownload();
        entity.setObjectid(resource.getContentid());
        entity.setUserid(user.getUserId());

        List<ResourceDownload> downloads = resourceDownloadMicroApi.getByExample(entity).getPageInfo().getList();

        if (CollectionUtils.isEmpty(downloads)) {
            ResourceDownload resourceDownload = new ResourceDownload();
            resourceDownload.setUserid(user.getUserId());
            resourceDownload.setObjectid(resource.getContentid());
            resourceDownload.setObjectcode(resource.getContentno() == null ? "" : resource.getContentno());
            resourceDownload.setObjecttype("1");
            resourceDownload.setContenttype(resource.getContenttype());

            Good goodInfo = getSimpleGoodByContentId(resource);
            resourceDownload.setStatus(goodInfo.getStatus());
            resourceDownload.setDowntime(new Timestamp(System.currentTimeMillis()));

            resource.setGoodinfo(null);

            int downTimes = resource.getDowntimes();
            resource.setDowntimes(downTimes + 1);
            resourceMicroApi.download(resource);

            getHotValue(resource);
            resourceMicroApi.updateHot(resource);
            resourceDownload = resourceDownloadMicroApi.insert(resourceDownload).getResponseEntity();

            //remove from collect
//            removeFromFavorite(resource.getContentid(), token);//should check client
            logOptionEntity(resource.getContentid(), resourceDownload.getDownid(), ICommonConstants.OPTION_OPTIONTYPE_DOWNLOAD, token);
        }
    }
}
