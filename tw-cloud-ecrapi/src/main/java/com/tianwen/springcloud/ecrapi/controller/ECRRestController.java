package com.tianwen.springcloud.ecrapi.controller;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.tianwen.springcloud.commonapi.base.response.Response;
import com.tianwen.springcloud.commonapi.base.response.ServerResult;
import com.tianwen.springcloud.commonapi.constant.IStateCode;
import com.tianwen.springcloud.commonapi.exception.ParameterException;
import com.tianwen.springcloud.commonapi.query.Pagination;
import com.tianwen.springcloud.commonapi.query.QueryCondition;
import com.tianwen.springcloud.commonapi.query.QueryTree;
import com.tianwen.springcloud.ecrapi.api.ECRApi;
import com.tianwen.springcloud.ecrapi.constant.IErrorMessageConstants;
import com.tianwen.springcloud.microservice.base.entity.Book;
import com.tianwen.springcloud.microservice.base.entity.Catalog;
import com.tianwen.springcloud.microservice.base.entity.Navigation;
import com.tianwen.springcloud.microservice.resource.constant.IResourceMicroConstants;
import com.tianwen.springcloud.microservice.resource.entity.FileInfo;
import com.tianwen.springcloud.microservice.resource.entity.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Timestamp;
import java.util.*;

@RestController
@RequestMapping(value = "")
public  class ECRRestController extends BaseRestController implements ECRApi {
    @Autowired
    private ResourceRestController resourceRestController;

    public ServerResult getServerResult(String resultCode, String resultMessage, String internalMessage)
    {
        ServerResult serverResult = new ServerResult();
        serverResult.setResultCode(resultCode);
        serverResult.setResultMessage(resultMessage);
        serverResult.setInternalMessage(internalMessage);
        return serverResult;
    }

    @Override
    public JSONObject queryTeachmaterialList(@RequestBody JSONObject param)
    {
        PageInfo pageInfo = null;
        Book responseBook = null;
        ServerResult serverResult;
        JSONObject totalResult = new JSONObject();

        JSONObject bookInfo = param.getJSONObject("teachMaterial");

        if (bookInfo == null)
            serverResult = getServerResult(IErrorMessageConstants.ERR_PARAMETER_INVALID, IErrorMessageConstants.ERR_MSG_PARAMETER_INVALID, "");
        else
        {
            QueryTree queryTree = new QueryTree();
            queryTree.addCondition(new QueryCondition("schoolsectionid", QueryCondition.Prepender.AND,
                    QueryCondition.Operator.EQUAL, bookInfo.getString("schoolsection")));
            queryTree.addCondition(new QueryCondition("subjectid", QueryCondition.Prepender.AND,
                    QueryCondition.Operator.EQUAL, bookInfo.getString("subjectid")));
            queryTree.addCondition(new QueryCondition("gradeid", QueryCondition.Prepender.AND,
                    QueryCondition.Operator.EQUAL, bookInfo.getString("grade")));
            queryTree.addCondition(new QueryCondition("editiontypeid", QueryCondition.Prepender.AND,
                    QueryCondition.Operator.EQUAL, bookInfo.getString("edition")));
            queryTree.addCondition(new QueryCondition("bookmodelid", QueryCondition.Prepender.AND,
                    QueryCondition.Operator.EQUAL, bookInfo.getString("bookmodel")));

            List<Book> bookList;
            bookList = bookMicroApi.getList(queryTree).getPageInfo().getList();
            if (!CollectionUtils.isEmpty(bookList))
                responseBook = bookList.get(0);

            serverResult = getServerResult(IErrorMessageConstants.OPERATION_SUCCESS, IErrorMessageConstants.OPERATION_SUCCESS_MESSAGE, "");
        }
        totalResult.put("responseTeachMaterial", responseBook);
        totalResult.put("pageInfo", pageInfo);

        totalResult.put("serverResult", serverResult);

        return totalResult;
    }

    @Override
    public JSONObject queryTeachmaterialCatalog(@RequestBody JSONObject param) {
        PageInfo pageInfo = new PageInfo();
        Catalog responseTeachmaterialCatalog = null;
        ServerResult serverResult = null;
        List<Catalog> catalogList = null;

        JSONObject totalResult = new JSONObject();
        JSONObject catalogInfo = param.getJSONObject("teachmaterialCatalog");

        if (catalogInfo == null)
            serverResult = getServerResult(IErrorMessageConstants.ERR_PARAMETER_INVALID, IErrorMessageConstants.ERR_MSG_PARAMETER_INVALID, "");
        else
        {
            QueryTree queryTree = new QueryTree();
            queryTree.addCondition(new QueryCondition("bookid", QueryCondition.Prepender.AND,
                    QueryCondition.Operator.EQUAL, catalogInfo.getString("bookid")));

            catalogList = catalogMicroApi.search(queryTree).getPageInfo().getList();
            if (catalogList == null)
                catalogList = new ArrayList<>();

            serverResult = getServerResult(IErrorMessageConstants.OPERATION_SUCCESS, IErrorMessageConstants.OPERATION_SUCCESS_MESSAGE, "");
        }
        totalResult.put("responseTeachmaterialCatalog", responseTeachmaterialCatalog);

        pageInfo.setList(catalogList);
        pageInfo.setTotal(catalogList==null?0:catalogList.size());
        totalResult.put("pageInfo", pageInfo);

        totalResult.put("serverResult", serverResult);

        return totalResult;
    }

    @Override
    public JSONObject querySubjectnaviInfo(@RequestBody JSONObject param) {
        PageInfo pageInfo = new PageInfo();
        Navigation responseSubjectnavi = null;
        ServerResult serverResult = null;
        List<Navigation> navigationList = null;

        JSONObject totalResult = new JSONObject();

        QueryTree queryTree = new QueryTree();
        queryTree.addCondition(new QueryCondition("producttype", QueryCondition.Prepender.AND,
                QueryCondition.Operator.EQUAL, param.getString("productType")));

        navigationList = navigationMicroApi.getList(queryTree).getPageInfo().getList();
        if (navigationList == null)
            navigationList = new ArrayList<>();

        serverResult = getServerResult(IErrorMessageConstants.OPERATION_SUCCESS, IErrorMessageConstants.OPERATION_SUCCESS_MESSAGE, "");

        totalResult.put("responseSubjectnavi", responseSubjectnavi);

        pageInfo.setList(navigationList);
        pageInfo.setTotal(navigationList.size());
        totalResult.put("pageInfo", pageInfo);

        totalResult.put("serverResult", serverResult);

        return totalResult;
    }

    @Override
    public JSONObject uploadResources(@RequestBody JSONObject param) {
        Resource responseContent = null;
        PageInfo pageInfo = null;
        ServerResult serverResult = null;
        JSONObject totalResult = new JSONObject();

        JSONObject contentInfo = param.getJSONObject("content");
        if (contentInfo == null)
            serverResult = getServerResult(IErrorMessageConstants.ERR_PARAMETER_INVALID, IErrorMessageConstants.ERR_MSG_PARAMETER_INVALID, "");
        else
        {
            responseContent = param.getJSONObject("content").toJavaObject(Resource.class);
            if (responseContent != null) {
                List<String> catalogIds = responseContent.getCatalogids();
                if (!org.springframework.util.CollectionUtils.isEmpty(catalogIds)) {
                    validateNaviInfoByCatalogids(responseContent, catalogIds);
                }

                //save resource
                responseContent.setImportstatus(IResourceMicroConstants.RES_IMPORT_STATUS_SUCCESS);
                responseContent.setStatus(IResourceMicroConstants.RES_STATUS_HOLD);
                responseContent.setIsgoods(IResourceMicroConstants.RES_SOURCE_NOT_GOODS);
                Response<Resource> resp = resourceMicroApi.add(responseContent);
                responseContent = resp.getResponseEntity();

                FileInfo fileinfo = responseContent.getFileInfo();

                if (fileinfo != null) {
                    fileinfo = fileMicroApi.add(fileinfo).getResponseEntity();
                    if (fileinfo == null) {
                        throw new ParameterException(IStateCode.PARAMETER_IS_EMPTY, IErrorMessageConstants.ERR_MSG_RESOURCE_ALREADY_EXIST);
                    }

                    resourceMicroApi.disconnectFile(responseContent.getContentid());

                    Map<String, String> map = new HashMap<>();
                    map.put("contentid", responseContent.getContentid());
                    map.put("contenttype", responseContent.getContenttype());
                    map.put("fileid", fileinfo.getFileid());
                    resourceMicroApi.connectFile(map);
                }

                if (!CollectionUtils.isEmpty(catalogIds)) {
                    resourceMicroApi.disconnectBookChapter(responseContent.getContentid());

                    for (String catalogid : catalogIds) {
                        if (org.apache.commons.lang.StringUtils.isEmpty(catalogid)) continue;
                        Catalog catalog = catalogMicroApi.get(catalogid).getResponseEntity();
                        if (catalog == null)
                            continue;

                        String bookid = catalog.getBookid();
                        Map<String, String> map = new HashMap<>();
                        map.put("contentid", responseContent.getContentid());
                        map.put("resourcetype", responseContent.getContenttype());
                        map.put("chapterid", catalogid);
                        map.put("bookid", bookid);

                        resourceMicroApi.connectBookChapter(map);
                    }
                }

                if ( StringUtils.isEmpty(responseContent.getContentid()))
                    serverResult = getServerResult(IErrorMessageConstants.ERR_DB_SAVE_FAILED,IErrorMessageConstants.ERR_MESAGE_DB_SAVE_FAILED,"");
                else
                    serverResult = getServerResult(IErrorMessageConstants.OPERATION_SUCCESS, IErrorMessageConstants.OPERATION_SUCCESS_MESSAGE, "");
            }
        }

        totalResult.put("responseContent", responseContent);
        totalResult.put("pageInfo", pageInfo);
        totalResult.put("serverResult", serverResult);
        return totalResult;
    }

    @Override
    public JSONObject uploadResourcesfile(@RequestBody JSONObject param) {
        FileInfo responseFile = null;
        PageInfo pageInfo = null;
        ServerResult serverResult = null;
        JSONObject totalResult = new JSONObject();

        JSONObject fileinfo = param.getJSONObject("fileinfo");
        if (fileinfo == null)
            serverResult = getServerResult(IErrorMessageConstants.ERR_PARAMETER_INVALID, IErrorMessageConstants.ERR_MSG_PARAMETER_INVALID, "");
        else
        {
            String contentid = param.getString("contentid");
            if (StringUtils.isEmpty(contentid) || resourceMicroApi.get(contentid).getResponseEntity() == null)
                serverResult = getServerResult(IErrorMessageConstants.ERR_MSG_RESOURCE_NOT_EXIST, IErrorMessageConstants.ERR_MSG_RESOURCE_NOT_EXIST, "");
            else
            {
                Resource uploadedResource = resourceMicroApi.get(contentid).getResponseEntity();
                if (uploadedResource == null) {
                    serverResult = getServerResult(IStateCode.PARAMETER_IS_INVALID,IErrorMessageConstants.ERR_MSG_RESOURCE_NOT_EXIST, "");
                }
                else {
                    FileInfo fileInfo = new FileInfo();
                    if (fileinfo.get("fileid") != null)
                        fileInfo.setFileid(fileinfo.get("fileid").toString());
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

                    fileInfo.setCreatetime(new Timestamp(System.currentTimeMillis()));
                    fileInfo.setLastmodifytime(new Timestamp(System.currentTimeMillis()));
                    fileInfo = fileMicroApi.add(fileInfo).getResponseEntity();

                    if (fileInfo == null) {
                        resourceMicroApi.delete(contentid);
                        throw new ParameterException(IStateCode.PARAMETER_IS_INVALID, IErrorMessageConstants.ERR_MSG_RESOURCE_ALREADY_EXIST);
                    }

                    Map<String, String> connectInfo = new HashMap<>();
                    connectInfo.put("contentid", contentid);
                    connectInfo.put("contenttype", uploadedResource.getContenttype() != null ? uploadedResource.getContenttype() : "");
                    connectInfo.put("fileid", fileInfo.getFileid());
                    uploadedResource.setFileInfo(fileInfo);
                    resourceMicroApi.connectFile(connectInfo);

                    responseFile = uploadedResource.getFileInfo();
                    if (responseFile == null || StringUtils.isEmpty(responseFile.getFileid()))
                        serverResult = getServerResult(IErrorMessageConstants.ERR_DB_SAVE_FAILED, IErrorMessageConstants.ERR_MSG_RESOURCE_NOT_EXIST, "");
                    else
                        serverResult = getServerResult(IErrorMessageConstants.OPERATION_SUCCESS, IErrorMessageConstants.OPERATION_SUCCESS_MESSAGE, "");
                }
            }
        }

        totalResult.put("responseFile", responseFile);
        totalResult.put("pageInfo", pageInfo);
        totalResult.put("serverResult", serverResult);
        return totalResult;
    }

    @Override
    public JSONObject delResources(@RequestBody JSONObject param) {
        PageInfo pageInfo = null;
        ServerResult serverResult = null;
        JSONObject totalResult = new JSONObject();
        JSONObject resourceInfo = param.getJSONObject("content");

        if (resourceInfo == null || StringUtils.isEmpty(resourceInfo.getString("contentid")))
            serverResult = getServerResult(IErrorMessageConstants.ERR_PARAMETER_INVALID, IErrorMessageConstants.ERR_MSG_PARAMETER_INVALID, "");
        else
        {
            String contentid = resourceInfo.getString("contentid");
            List<String> ids = new ArrayList<>();
            ids.add(contentid);
            Response resp = resourceRestController.deleteBatchResources(ids);
            serverResult = resp.getServerResult();
        }

        totalResult.put("pageInfo", pageInfo);
        totalResult.put("serverResult", serverResult);
        return totalResult;
    }

    @Override
    public JSONObject searchResourcesList(@RequestBody JSONObject param) {
        Resource responseContent = null;
        PageInfo pageInfo = new PageInfo();
        ServerResult serverResult = null;
        List<Resource> resList = null;

        JSONObject totalResult = new JSONObject();

        String keyword = param.getString("keyword");
        JSONObject pagination = param.getJSONObject("pageInfo");
        JSONObject contentInfo = param.getJSONObject("content");
        QueryTree queryTree = new QueryTree();

        if (contentInfo == null)
            serverResult = getServerResult(IErrorMessageConstants.ERR_PARAMETER_INVALID, IErrorMessageConstants.ERR_MSG_PARAMETER_INVALID, "");
        else {
            contentInfo.put("searchkey", keyword);
            Set<String> keys = contentInfo.keySet();
            for (String key : keys)
                queryTree.addCondition(new QueryCondition(key, QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, contentInfo.getString(key)));
            Pagination pageData = new Pagination();
            int pageNo, pageSize;
            try{
                pageNo = Integer.parseInt(pagination.getString("pageNo"));
            }
            catch (Exception e) { pageNo = 1; }
            try{
                pageSize = Integer.parseInt(pagination.getString("numPerPage"));
            }
            catch (Exception e) { pageSize = 10; }

            pageData.setPageNo(pageNo); pageData.setNumPerPage(pageSize);
            queryTree.setPagination(pageData);

            queryTree = fixLabelParam(queryTree);
            pageInfo =  resourceMicroApi.getList(queryTree).getPageInfo();

            serverResult = getServerResult(IErrorMessageConstants.OPERATION_SUCCESS, IErrorMessageConstants.OPERATION_SUCCESS_MESSAGE, "");
        }

        totalResult.put("responseContent", responseContent);
        totalResult.put("pageInfo", pageInfo);
        totalResult.put("serverResult", serverResult);

        return totalResult;
    }

    @Override
    public JSONObject getResourcesInfo(@RequestBody JSONObject param) {
        Resource responseContent = null;
        FileInfo responseFile = null;
        PageInfo pageInfo = null;
        ServerResult serverResult = null;
        JSONObject totalResult = new JSONObject();

        String contentid = param.getString("contentid");

        if (StringUtils.isEmpty(contentid))
            serverResult = getServerResult(IErrorMessageConstants.ERR_PARAMETER_INVALID, IErrorMessageConstants.ERR_MSG_PARAMETER_INVALID, "");
        else {
            responseContent = resourceMicroApi.get(contentid).getResponseEntity();
            responseFile = fileMicroApi.getByContentid(contentid).getResponseEntity();
        }
        totalResult.put("responseContent", responseContent);
        totalResult.put("responseFile", responseFile);
        totalResult.put("pageInfo", pageInfo);
        totalResult.put("serverResult", serverResult);

        return totalResult;
    }

    @Override
    public JSONObject updateResources(@RequestBody JSONObject param) {
        Resource responseContent = null;
        PageInfo pageInfo = null;
        ServerResult serverResult = null;
        JSONObject totalResult = new JSONObject();

        JSONObject contentInfo = param.getJSONObject("content");
        if (contentInfo == null || StringUtils.isEmpty(contentInfo.getString("contentid")))
            serverResult = getServerResult(IErrorMessageConstants.ERR_PARAMETER_INVALID, IErrorMessageConstants.ERR_MSG_PARAMETER_INVALID, "");
        else
        {
            String contentid = contentInfo.getString("contentid");
            Resource resource = resourceMicroApi.get(contentid).getResponseEntity();
            if (resource == null || StringUtils.isEmpty(resource.getContentid()))
                serverResult = getServerResult(IErrorMessageConstants.ERR_MSG_RESOURCE_NOT_EXIST, IErrorMessageConstants.ERR_MSG_RESOURCE_NOT_EXIST, "");
            else
            {
                responseContent = (Resource)param.get("content");
                responseContent = resourceMicroApi.update(responseContent).getResponseEntity();
                if (StringUtils.isEmpty(responseContent.getContentid()))
                    serverResult = getServerResult(IErrorMessageConstants.ERR_DB_SAVE_FAILED,IErrorMessageConstants.ERR_MESAGE_DB_SAVE_FAILED,"");
                else
                    serverResult = getServerResult(IErrorMessageConstants.OPERATION_SUCCESS, IErrorMessageConstants.OPERATION_SUCCESS_MESSAGE, "");
            }
        }

        totalResult.put("responseContent", responseContent);
        totalResult.put("pageInfo", pageInfo);
        totalResult.put("serverResult", serverResult);
        return totalResult;
    }

    @Override
    public JSONObject updateResourcesfile(@RequestBody JSONObject param) {
        FileInfo responseFile = null;
        PageInfo pageInfo = null;
        ServerResult serverResult = null;
        JSONObject totalResult = new JSONObject();

        JSONObject fileInfo = param.getJSONObject("file");
        if (fileInfo == null)
            serverResult = getServerResult(IErrorMessageConstants.ERR_PARAMETER_INVALID, IErrorMessageConstants.ERR_MSG_PARAMETER_INVALID, "");
        else
        {
            String contentid = fileInfo.getString("contentid");
            if (StringUtils.isEmpty(contentid) || resourceMicroApi.get(contentid).getResponseEntity() == null)
                serverResult = getServerResult(IErrorMessageConstants.ERR_MSG_RESOURCE_NOT_EXIST, IErrorMessageConstants.ERR_MSG_RESOURCE_NOT_EXIST, "");
            else
            {
                responseFile = (FileInfo)param.get("file");
                if (fileMicroApi.get(responseFile.getFileid()).getResponseEntity() == null)
                    serverResult = getServerResult(IErrorMessageConstants.ERR_MSG_FILE_NOT_EXIST, IErrorMessageConstants.ERR_MSG_FILE_NOT_EXIST, "");
                else {
                    responseFile = fileMicroApi.update(responseFile).getResponseEntity();
                    if (responseFile == null || StringUtils.isEmpty(responseFile.getFileid()))
                        serverResult = getServerResult(IErrorMessageConstants.ERR_DB_SAVE_FAILED, IErrorMessageConstants.ERR_MSG_RESOURCE_NOT_EXIST, "");
                    else
                        serverResult = getServerResult(IErrorMessageConstants.OPERATION_SUCCESS, IErrorMessageConstants.OPERATION_SUCCESS_MESSAGE, "");
                }
            }
        }

        totalResult.put("responseFile", responseFile);
        totalResult.put("pageInfo", pageInfo);
        totalResult.put("serverResult", serverResult);
        return totalResult;
    }
}
