package com.tianwen.springcloud.microservice.base.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.tianwen.springcloud.commonapi.base.response.Response;
import com.tianwen.springcloud.commonapi.constant.IStateCode;
import com.tianwen.springcloud.commonapi.log.SystemControllerLog;
import com.tianwen.springcloud.commonapi.query.OrderMethod;
import com.tianwen.springcloud.commonapi.query.QueryCondition;
import com.tianwen.springcloud.commonapi.query.QueryTree;
import com.tianwen.springcloud.datasource.base.AbstractCRUDController;
import com.tianwen.springcloud.microservice.base.api.ThemeMicroApi;
import com.tianwen.springcloud.microservice.base.constant.IBaseMicroConstants;
import com.tianwen.springcloud.microservice.base.entity.Theme;
import com.tianwen.springcloud.microservice.base.service.ESearchService;
import com.tianwen.springcloud.microservice.base.service.ThemeService;
import org.apache.commons.lang.StringUtils;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.utils.DateUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gems on 2018.12.19.
 */
@RestController
@RequestMapping(value = "/theme")
public class ThemeController extends AbstractCRUDController<Theme> implements ThemeMicroApi{
    @Autowired
    private ThemeService themeService;

    @Value("${ESEARCH_SERVER}")
    private String esearchServer;

    @Autowired
    private ESearchService eSearchService;

    @Override
    public Response<Theme> makeESDb(){
        String indexUrl = "http://" + esearchServer + "/" + IBaseMicroConstants.INDEX_THEME;
        HttpDelete httpDelete = new HttpDelete(indexUrl);

        try{
            eSearchService.doRequest(httpDelete);
        }
        catch (Exception e){
            return eSearchService.notFoundServerError();
        }

        String[] keys = {"themeid", "description", "schoolsectionid", "gradeid", "bookmodelid", "subjectid", "logopath", "themetype", "subthemetype",
                "parentthemeid", "copyright", "isallowdownload", "orderno", "status", "creator", "createtime", "lastmodifytime", "themename"};
        String[] types = {"text", "text", "text", "text", "text", "text", "text", "text", "text",
                "text", "text", "text", "long", "text", "text", "date", "date", "text"};

        JSONObject mapping = eSearchService.makeMapping(IBaseMicroConstants.TYPE_THEME, keys, types);
        HttpPut httpPut = new HttpPut(indexUrl);

        try{
            eSearchService.doRequestOperation(httpPut, mapping);
        }
        catch (Exception e){
            return eSearchService.notFoundServerError();
        }

        return new Response<>();
    }

    @Override
    public Response<Theme> batchSaveToES(@RequestBody QueryTree queryTree) {
        int start;
        try{
            start = (int)queryTree.getQueryCondition("start").getFieldValues()[0];
        }
        catch (Exception e) { start = 1; }

        int pageSize = 50;
        int pageNo = start / pageSize;

        if (pageNo == 0)
            pageNo = 1;

        QueryTree resQuery = new QueryTree();
        resQuery.getPagination().setNumPerPage(pageSize);
        resQuery.getPagination().setPageNo(pageNo);
        resQuery.orderBy("createtime", OrderMethod.Method.ASC);
        List<Theme> dataList = search(resQuery).getPageInfo().getList();

        if (!CollectionUtils.isEmpty(dataList)) {
            for (Theme item : dataList)
                saveToES(item);
        }
        else
            return new Response<>(IStateCode.PARAMETER_IS_INVALID, "");

        return new Response<>();
    }

    @Override
    public Response<Theme> saveToES(@RequestBody Theme entity) {
        JSONObject json = new JSONObject();

        json.put("themeid", entity.getThemeid());
        json.put("description", entity.getDescription());
        json.put("schoolsectionid", entity.getSchoolsection());
        json.put("gradeid", entity.getGrade());
        json.put("bookmodelid", entity.getTerm());
        json.put("subjectid", entity.getSubjectid());
        json.put("logopath", entity.getLogopath());
        json.put("creator", entity.getCreator());
        json.put("themetype", entity.getThemetype());
        json.put("subthemetype", entity.getSubthemetype());
        json.put("parentthemeid", entity.getParentthemeid());
        json.put("copyright", entity.getCopyright());
        json.put("isallowdownload", entity.getIsallowdownload());
        json.put("orderno", entity.getOrderno());
        json.put("status", entity.getStatus());
        json.put("creator", entity.getCreator());
        if (entity.getCreatetime() != null)
            json.put("createtime", DateUtils.formatDate(entity.getCreatetime(), "yyyy-MM-dd HH:mm:ss"));
        if (entity.getLastmodifytime() != null)
            json.put("lastmodifytime", DateUtils.formatDate(entity.getLastmodifytime(), "yyyy-MM-dd HH:mm:ss"));
        json.put("themename", entity.getThemename());

        HttpPut httpPut = new HttpPut("http://" + esearchServer + "/" + IBaseMicroConstants.INDEX_THEME + "/" + IBaseMicroConstants.TYPE_THEME + "/" + entity.getThemeid());

        try
        {
            eSearchService.doRequestOperation(httpPut, json);
        }
        catch (Exception e){
            return eSearchService.notFoundServerError();
        }

        return new Response<>();
    }

    @Override
    public Response<Theme> removeFromES(@RequestBody Theme entity) {
        HttpDelete httpDelete = new HttpDelete("http://" + esearchServer + "/" + IBaseMicroConstants.INDEX_THEME + "/" + IBaseMicroConstants.TYPE_THEME + "/" + entity.getThemeid());
        try {
            eSearchService.doRequest(httpDelete);
        }
        catch (Exception e){
            return eSearchService.notFoundServerError();
        }

        return new Response<>();
    }

    @Override
    public Response<Theme> removeFromES(@PathVariable(value = "id") String id) {
        HttpDelete httpDelete = new HttpDelete("http://" + esearchServer + "/" + IBaseMicroConstants.INDEX_THEME + "/" + IBaseMicroConstants.TYPE_THEME + "/" + id);
        HttpClient httpClient = new DefaultHttpClient();
        try {
            httpClient.execute(httpDelete);
        }
        catch (Exception e){
            return new Response<>(IStateCode.HTTP_404, IBaseMicroConstants.ES_SERVER_ERROR);
        }

        return new Response<>();
    }

    @Override
    @SystemControllerLog(description = "insertTheme")
    public Response<Theme> insert(@RequestBody Theme entity) {
        themeService.save(entity);
        return new Response<>(entity);
    }

    @Override
    @SystemControllerLog(description = "modifyTheme")
    public Response<Theme> modify(@RequestBody Theme entity) {
        themeService.updateNotNull(entity);
        saveToES(themeService.selectByKey(entity.getThemeid()));
        return new Response<>(entity);
    }

    @Override
    @SystemControllerLog(description = "removeTheme")
    public Response<Theme> remove(@PathVariable(value = "id") String id) {
        Response<Theme> esResp = removeFromES(id);
        if (!esResp.getServerResult().getResultCode().equalsIgnoreCase(IStateCode.HTTP_200))
            return new Response<>(IStateCode.HTTP_404, IBaseMicroConstants.ES_SERVER_ERROR);

        Theme entity = themeService.selectByKey(id);
        themeService.delete(entity);
        return new Response<>(entity);
    }

    @Override
    @SystemControllerLog(description = "getThemeList")
    public Response<Theme> getList(@RequestBody QueryTree queryTree) {
        Response<Theme> resp = themeService.search(queryTree);
        return resp;
    }

    // Author : GOD
    // Date Start : 2019-1-31 3 PM
    // Reason : 系统管理 专题管理 Page Loading Speed, Select First Theme as Default
    @Override
    @SystemControllerLog(description = "getThemeListAndChildCount")
    public Response<Theme> getListAndChildCount(@RequestBody QueryTree queryTree) {
        Response<Theme> resp = themeService.getListAndChildCount(queryTree);
        return resp;
    }
    // Author : GOD
    // Date End : 2019-2-1 6 PM
    // Reason : 系统管理 专题管理 Page Loading Speed, Select First Theme as Default
    @Override
    public Response<String> getMyAllThemes(@PathVariable(value = "parent") String parent) {
        List<String> path = themeService.getMyAllThemes(parent);
        return new Response<>(path);
    }

    @Override
    public void validate(MethodType methodType, Object p) {

    }

    @Override
    public Response<Theme> get(@PathVariable(value = "themeid") String themeid) {
        Theme theme = themeService.selectByKey(themeid);
        return new Response<>(theme);
    }
}
