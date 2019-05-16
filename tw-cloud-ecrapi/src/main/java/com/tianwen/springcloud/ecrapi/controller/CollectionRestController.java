package com.tianwen.springcloud.ecrapi.controller;

import com.tianwen.springcloud.commonapi.base.response.Response;
import com.tianwen.springcloud.commonapi.query.OrderMethod;
import com.tianwen.springcloud.commonapi.query.Pagination;
import com.tianwen.springcloud.commonapi.query.QueryCondition;
import com.tianwen.springcloud.commonapi.query.QueryTree;
import com.tianwen.springcloud.ecrapi.api.CollectionRestApi;
import com.tianwen.springcloud.ecrapi.constant.ICommonConstants;
import com.tianwen.springcloud.ecrapi.constant.IErrorMessageConstants;
import com.tianwen.springcloud.ecrapi.util.SensitiveWordFilter;
import com.tianwen.springcloud.microservice.activity.constant.IActivityMicroConstants;
import com.tianwen.springcloud.microservice.activity.entity.Activity;
import com.tianwen.springcloud.microservice.base.entity.UserLoginInfo;
import com.tianwen.springcloud.microservice.resource.constant.IResourceMicroConstants;
import com.tianwen.springcloud.microservice.resource.entity.ActivityCountInfo;
import com.tianwen.springcloud.microservice.resource.entity.ExportMan;
import com.tianwen.springcloud.microservice.resource.entity.Resource;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping(value = "/collection")
public class CollectionRestController extends BaseRestController implements CollectionRestApi
{
    @Override
    public Response<Activity> get(@PathVariable(value = "activityid") String activityid) {
        return activityMicroApi.get(activityid);
    }

    @Override
    public Response<Map<String, Object>> getCollectionCountInfo(@PathVariable(value = "activityid") String activityid) {
        Integer resourcecount = resourceMicroApi.getResourceCount(activityid).getResponseEntity();
        ActivityCountInfo info = resourceMicroApi.getActivityInfo(activityid).getResponseEntity();
        Map<String, Object> resultmap = new HashMap<>();
        resultmap.put("resourcecount", resourcecount);
        resultmap.put("joinercount", info.getJoinercount());
        return new Response<>(resultmap);
    }

    @Override
    public Response addCollectionActivity(@RequestBody Activity collectionActivity, @RequestHeader(value = "token") String token) {
        Response<UserLoginInfo> userResp = userMicroApi.getByToken(token);
        if (!userResp.getServerResult().getResultCode().equalsIgnoreCase(IErrorMessageConstants.OPERATION_SUCCESS))
            return userResp;

        /**
         * @author: jong
         * @date: 2019-02-08,11
         * @comment: check sensitive words
         */
        SensitiveWordFilter swFilter = new SensitiveWordFilter();
        collectionActivity.setActivityname(swFilter.process(collectionActivity.getActivityname()));
        collectionActivity.setDescription(swFilter.process(collectionActivity.getDescription()));
        /*
         * ----------------------------------------------------------------------
         */

        UserLoginInfo userLoginInfo = userResp.getResponseEntity();
        String userid = userLoginInfo.getUserId();
        if (collectionActivity.getAccordings() != null)
            collectionActivity.setAccording(StringUtils.join(collectionActivity.getAccordings(), ","));
        collectionActivity.setActivitytype(IActivityMicroConstants.ACTIVITY_TYPE_COLLECTION);
        collectionActivity.setCreator(userid);
        collectionActivity = activityMicroApi.add(collectionActivity).getResponseEntity();

        if (collectionActivity == null || StringUtils.isEmpty(collectionActivity.getActivityid()))
        {
            return new Response<>(IErrorMessageConstants.ERR_PARAMETER_INVALID, IErrorMessageConstants.ERR_MSG_DUPLICATE_ACTIVITY_NAME);
        }

        logOptionEntity(collectionActivity.getActivityid(), collectionActivity.getActivityid(), ICommonConstants.OPTION_OPTIONTYPE_COLLECTION_NEW, token);
        return new Response<Activity>(collectionActivity);
    }

    @Override
    public Response<Activity> delete(@PathVariable(value = "id")String id, @RequestHeader(value = "token") String token) {
        logOptionEntity(id, id, ICommonConstants.OPTION_OPTIONTYPE_COLLECTION_DELETE, token);
        return  activityMicroApi.delete(id);
    }

    @Override
    public Response<Activity> batchRemoveActivity(@RequestBody String[] ids, @RequestHeader(value = "token") String token) {
        for (String id : ids){
            logOptionEntity(id, id, ICommonConstants.OPTION_OPTIONTYPE_COLLECTION_DELETE, token);
        }
        return activityMicroApi.batchRemove(ids);
    }

    @Override
    public Response<Activity> endCollectionActivity(@PathVariable(value = "activityid") String activityid){
        resourceMicroApi.setStopStatusByActivityId(activityid);
        return activityMicroApi.end(activityid);
    }

    @Override
    public Response editCollectionActivity(@RequestBody Activity collectionActivity, @RequestHeader(value = "token") String token) {
        if (collectionActivity.getAccordings() != null)
            collectionActivity.setAccording(StringUtils.join(collectionActivity.getAccordings(), ","));
        Response logininforesponse = getUserByToken(token);
        if (!StringUtils.equals(logininforesponse.getServerResult().getResultCode(), ICommonConstants.RESPONSE_RESULT_SUCCESS)) return logininforesponse;

        /**
         * --------------------------------------------------------------------->>
         * @author: jong
         * @date: 2019-02-17
         * filter collection activity name & description
         */
        SensitiveWordFilter swFilter = new SensitiveWordFilter();
        collectionActivity.setActivityname(swFilter.process(collectionActivity.getActivityname()));
        collectionActivity.setDescription(swFilter.process(collectionActivity.getDescription()));
        /*
         * ----------------------------------------------------------------------<<
         */

        collectionActivity.setCreator(((UserLoginInfo)logininforesponse.getResponseEntity()).getUserId());
        logOptionEntity(collectionActivity.getActivityid(), collectionActivity.getActivityid(), ICommonConstants.OPTION_OPTIONTYPE_COLLECTION_MODIFY, token);
        return activityMicroApi.edit(collectionActivity);
    }

    @Override
    public Response <Activity> getCollectionActivityList(@RequestBody QueryTree queryTree) {
//        List<String> userids = userMicroApi.getUserIdsByQueryTree(queryTree).getPageInfo().getList();
//        queryTree.addCondition(new QueryCondition("creatorids", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, userids));
        queryTree.addCondition(new QueryCondition("orderBycreatetime", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, ""));
        queryTree.addCondition(new QueryCondition("activitytype", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, IActivityMicroConstants.ACTIVITY_TYPE_COLLECTION));
        Response<Activity> response = activityMicroApi.getList(queryTree);
        Long start = System.currentTimeMillis();
        validateActivityList(response);
        Long diff = System.currentTimeMillis() - start;
        return response;
    }

    @Override
    public Response<Activity> getRecentCollectionList(@RequestBody QueryTree queryTree) {
        QueryCondition queryCondition = new QueryCondition("activitytype", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL,  IActivityMicroConstants.ACTIVITY_TYPE_COLLECTION);
        if (queryTree.getConditions() == null)
            queryTree.setConditions(new ArrayList<>());
        queryTree.getConditions().add(queryCondition);

        OrderMethod orderMethod = new OrderMethod("orderBycreatetime", OrderMethod.Method.DESC);
        if (queryTree.getOrderMethods() == null)
            queryTree.setOrderMethods(new ArrayList<>());
        queryTree.getOrderMethods().add(orderMethod);

        return activityMicroApi.getList(queryTree);
    }

    @Override
    public Response getMyCollectionList(@RequestBody QueryTree queryTree, @RequestHeader(value = "token") String token) {
        Response<UserLoginInfo> loginInfoResponse = getUserByToken(token);
        String resultCode = loginInfoResponse.getServerResult().getResultCode();
        if (!StringUtils.equals(resultCode, ICommonConstants.RESPONSE_RESULT_SUCCESS)) return loginInfoResponse;

        Map<String, String> map = new HashMap<>();
        map.put("activitytype", IActivityMicroConstants.ACTIVITY_TYPE_COLLECTION);
        map.put("userid", loginInfoResponse.getResponseEntity().getUserId());
        List<String> activityids = resourceMicroApi.getActivitIds(map).getPageInfo().getList();
        List<Activity> resultList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(activityids)) {
            for (String activityid : activityids) {
                if (StringUtils.isEmpty(activityid)) continue;
                Activity activity = activityMicroApi.get(activityid).getResponseEntity();
                if (activity != null)
                    resultList.add(activity);
            }
        }

        Pagination pagination = queryTree.getPagination();
        Map<String, Object> pageMap = new HashMap<>();
        pageMap.put("list", resultList);
        pageMap.put("start", pagination.getStart());
        pageMap.put("numperpage", pagination.getNumPerPage());
        pageMap.put("pageno", pagination.getPageNo());

        Response<Activity> response = activityMicroApi.regetPageInfoWithList(pageMap);
        validateActivityList(response);

        return response;
    }

    @Override
    public Response<ExportMan> getCollectionExportManList(@RequestBody QueryTree queryTree) {
        queryTree.getConditions().add(new QueryCondition("sourceid", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, IResourceMicroConstants.RES_SOURCE_COLLECTION));
        return getExportManList(queryTree);
    }

    @Override
    public Response<String> getManyResourceTypeList() {
        return resourceMicroApi.getActivityCountInfo(IResourceMicroConstants.RES_SOURCE_COLLECTION);
    }

    @Override
    public Response<Resource> getCollectionActivityResourceList(@RequestBody QueryTree queryTree) {
        queryTree.getConditions().add(new QueryCondition("activitytype", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, IActivityMicroConstants.ACTIVITY_TYPE_COLLECTION));
        queryTree.getConditions().add(new QueryCondition("status", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, IResourceMicroConstants.RES_STATUS_PASS));
        queryTree.getConditions().add(new QueryCondition("isgoods", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, IResourceMicroConstants.RES_SOURCE_GOODS));
        Response<Resource> resourceResponse = resourceMicroApi.activityResourceList(queryTree);
        List<Resource> resultList = resourceResponse.getPageInfo().getList();
        if (!CollectionUtils.isEmpty(resultList)) {
            for (Resource resource : resultList) {
                validateResource(resource);
            }
        }
        return resourceResponse;
    }
}
