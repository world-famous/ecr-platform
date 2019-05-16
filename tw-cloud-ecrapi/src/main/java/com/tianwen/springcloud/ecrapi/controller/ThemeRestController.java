package com.tianwen.springcloud.ecrapi.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.tianwen.springcloud.commonapi.base.response.Response;
import com.tianwen.springcloud.commonapi.query.QueryCondition;
import com.tianwen.springcloud.commonapi.query.QueryTree;
import com.tianwen.springcloud.ecrapi.api.ThemeRestApi;
import com.tianwen.springcloud.ecrapi.constant.ICommonConstants;
import com.tianwen.springcloud.ecrapi.util.ESearchUtil;
import com.tianwen.springcloud.ecrapi.util.SensitiveWordFilter;
import com.tianwen.springcloud.microservice.base.api.ThemeMicroApi;
import com.tianwen.springcloud.microservice.base.constant.IBaseMicroConstants;
import com.tianwen.springcloud.microservice.base.entity.Theme;
import com.tianwen.springcloud.microservice.base.entity.ThemeTree;
import com.tianwen.springcloud.microservice.base.entity.UserLoginInfo;
import com.tianwen.springcloud.microservice.bussinessassist.entity.Good;
import com.tianwen.springcloud.microservice.resource.entity.Resource;

import org.apache.commons.collections.ArrayStack;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.apache.poi.util.StringUtil;
import org.apache.poi.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by gems on 2018.12.19.
 */

@RestController
@RequestMapping(value = "/theme")
public class ThemeRestController extends BaseRestController implements ThemeRestApi {
    @Autowired
    private ThemeMicroApi themeMicroApi;

    @Override
    public Response insertTheme(@RequestBody Theme entity, @RequestHeader(value = "token") String token) {
        Response<UserLoginInfo> loginInfoResponse = getUserByToken(token);
        String resultCode = loginInfoResponse.getServerResult().getResultCode();

        if (!StringUtils.equals(resultCode, ICommonConstants.RESPONSE_RESULT_SUCCESS))
            return loginInfoResponse;

        UserLoginInfo user = loginInfoResponse.getResponseEntity();
        entity.setCreator(user.getUserId());
        entity.setStatus(IBaseMicroConstants.THEME_STATUS_VALID);
        entity.setCreatetime(new Timestamp(System.currentTimeMillis()));
        entity.setLastmodifytime(new Timestamp(System.currentTimeMillis()));
// Author : GOD Date : 2019-2-26 Bug ID: #911
        SensitiveWordFilter swFilter = new SensitiveWordFilter();
        entity.setThemename(swFilter.process((entity.getThemename())));
        entity.setDescription(swFilter.process(entity.getDescription()));
// Author : GOD Date : 2019-2-26 Bug ID: #911
        return themeMicroApi.insert(entity);
    }

    @Override
    public Response modifyTheme(@RequestBody Theme entity, @RequestHeader(value = "token") String token) {
        Response<UserLoginInfo> loginInfoResponse = getUserByToken(token);
        String resultCode = loginInfoResponse.getServerResult().getResultCode();

        if (!StringUtils.equals(resultCode, ICommonConstants.RESPONSE_RESULT_SUCCESS))
           return loginInfoResponse;

        UserLoginInfo user = loginInfoResponse.getResponseEntity();
        entity.setCreator(user.getUserId());
        entity.setLastmodifytime(new Timestamp(System.currentTimeMillis()));
// Author : GOD Date : 2019-2-26 Bug ID: #911
        SensitiveWordFilter swFilter = new SensitiveWordFilter();
        entity.setThemename(swFilter.process((entity.getThemename())));
        entity.setDescription(swFilter.process(entity.getDescription()));
// Author : GOD Date : 2019-2-26 Bug ID: #911
        return themeMicroApi.modify(entity);
    }

    @Override
    public Response removeTheme(@PathVariable(value = "themeid") String themeid, @RequestHeader(value = "token") String token) {
        Response<UserLoginInfo> loginInfoResponse = getUserByToken(token);
        String resultCode = loginInfoResponse.getServerResult().getResultCode();
        if (!StringUtils.equals(resultCode, ICommonConstants.RESPONSE_RESULT_SUCCESS)) return loginInfoResponse;

        return themeMicroApi.remove(themeid);
    }

    @Override
    public Response getTheme(@PathVariable(value = "themeid") String themeid, @RequestHeader(value = "token") String token) {
        Theme theme = themeMicroApi.get(themeid).getResponseEntity();
        setGoodCount(theme);
        return new Response(theme);
    }

    @Override
    public Response getThemeById(@PathVariable(value = "themeid") String themeid, @RequestHeader(value = "token") String token) {
        Theme theme = themeMicroApi.get(themeid).getResponseEntity();

        return new Response(theme);
    }
// Author : GOD
    // Date Start : 2019-1-31 3 PM
    // Reason : 系统管理 专题管理 Page Loading Speed, Select First Theme as Default
    @Override
    public Response getThemeList(@RequestBody QueryTree queryTree, @RequestHeader(value = "token") String token) {

        /**
         * @author hong
         * @date 2/6/2019
         * commented below code to fix showing login dialog on every page
         */
        /*Response<UserLoginInfo> loginInfoResponse = getUserByToken(token);
        String resultCode = loginInfoResponse.getServerResult().getResultCode();
        if (!StringUtils.equals(resultCode, ICommonConstants.RESPONSE_RESULT_SUCCESS)) return loginInfoResponse;
        */

        Response<Theme> resp = themeMicroApi.getListAndChildCount(queryTree);
        List<ThemeTree> result = new ArrayList<>();
        List<Theme> themes = resp.getPageInfo().getList();

        if (!CollectionUtils.isEmpty(themes)) {
            for (Theme theme : themes) {

                ThemeTree item = new ThemeTree();
                item.setThemeName(theme.getThemename());
                item.setThemeData(theme);
                result.add(item);
//                setGoodCount(theme);
            }
        }

        Response finalResp = new Response(result);
        finalResp.getPageInfo().setTotal(resp.getPageInfo().getTotal());
        return finalResp;
    }

    @Override
    public Response getResourceList(@RequestBody QueryTree queryTree, @RequestHeader(value = "token") String token) {
        Response<UserLoginInfo> loginInfoResponse = getUserByToken(token);
        String resultCode = loginInfoResponse.getServerResult().getResultCode();
        if (!StringUtils.equals(resultCode, ICommonConstants.RESPONSE_RESULT_SUCCESS)) return loginInfoResponse;

        QueryCondition catCond = queryTree.getQueryCondition("themeids");
        if (catCond != null)
        {
            queryTree.getConditions().remove(catCond);

            if (catCond.getFieldValues() != null && catCond.getFieldValues().length > 0)
            {
                String parent = null;
                if (catCond.getFieldValues()[0] != null)
                    parent = catCond.getFieldValues()[0].toString();
                if (!StringUtils.isEmpty(parent)){
                    List<String> allItems;
                    allItems = themeMicroApi.getMyAllThemes(parent).getPageInfo().getList();
                    if (allItems == null)
                        allItems = new ArrayList<>();
                    allItems.add(parent);
                    allItems.add("");

                    String value = "";
                    int ii = 0;
                    for(String item: allItems) {
                        value += item + " ";
                        ii++;
                        if(ii > 100) break;
                    }
//                    String value = StringUtil.join(values, " ");

                    String query = "select resourceno from res_con_theme where themeid like '" + value + "'";
                    String retVal = "";
                    try {
                        retVal = getSqlQueryResult(query);
                    }catch(Exception e) {

                    }
                    JSONObject resData = JSONObject.parseObject(retVal);
                    JSONArray rows = resData.getJSONArray("rows");
                    if (rows.size() == 0)
                    {
                        Response<Resource> resp = new Response<>(new ArrayList<Resource>());
                        return resp;
                    }
                    else
                    {
                        int i, len = 0;
                        List<String> resources = new ArrayList<>();
                        len = rows.size();

                        for (i = 0; i < len; i++)
                        {
                            JSONArray array = rows.getJSONArray(i);
                            resources.add(array.getString(0));
                        }

                        resources.add("");
                        resources.add("");
                        queryTree.addCondition(new QueryCondition("contentid", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, resources));
                    }
                }
            }
        }

        Response<Resource> resp = resourceMicroApi.getList(queryTree);
        List<Resource> resList = resp.getPageInfo().getList();
        if (!CollectionUtils.isEmpty(resList))
        {
            for(Resource res : resList)
            {
                validateResource(res);
            }
        }
        return resp;
    }

    // Author : GOD
    // Date End : 2019-2-1 6 PM
    // Reason : 系统管理 专题管理 Page Loading Speed, Select First Theme as Default
    @Override
    public Response getHomeList(@RequestBody QueryTree queryTree, @RequestHeader(value = "token") String token) {
        fixResourceQueryTree(queryTree);
        queryTree.addCondition(new QueryCondition("isgoods", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, "1"));
        Response<Resource> resp = resourceMicroApi.getResourcesInThemes(queryTree);
        List<Resource> resources = resp.getPageInfo().getList();

        List<Good> goodList = new ArrayList<>();
        for (Resource resource : resources){
            Good good = goodMicroApi.getByContentid(resource.getContentid()).getResponseEntity();
            if (good != null) {
                good.setResource(resource);
                goodList.add(good);
            }
        }

        Response<Good> finalResult = new Response<>(goodList);
        finalResult.getPageInfo().setTotal(resp.getPageInfo().getTotal());

        return finalResult;
    }

    public void setGoodCount(Theme theme) {
        List<String> themes = themeMicroApi.getMyAllThemes(theme.getThemeid()).getPageInfo().getList();
        String themeid = StringUtils.join(themes, " ");
        try{
            String retVal = ESearchUtil.getSqlQueryResult(esearchServer, "select resourceno from res_con_theme where themeid like '" + themeid + "'");
            JSONObject totalObj = JSONObject.parseObject(retVal);
            JSONArray rowsObj = totalObj.getJSONArray("rows");
            List<String> contentids = new ArrayList<>();
            int i, len = rowsObj.size();
            for (i = 0; i < len; i++){
                JSONArray item = rowsObj.getJSONArray(i);
                contentids.add(item.getString(0));
            }
            contentids.add("");
            contentids.add("");

            QueryTree queryTree = new QueryTree();
            queryTree.getPagination().setNumPerPage(1);
            queryTree.addCondition(new QueryCondition("contentids", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, contentids));
            theme.setGoodscount(goodMicroApi.getList(queryTree).getPageInfo().getTotal());
        }
        catch (Exception e){
            theme.setGoodscount(Long.valueOf(0));
        }
    }
    // Author : GOD
    // Date Start : 2019-1-31 3 PM
    // Reason : 系统管理 专题管理 Page Loading Speed, Select First Theme as Default
    public String getSqlQueryResult(String query) throws Exception{
        JSONObject jsonQuery = new JSONObject();

        jsonQuery.put("query", query);
        HttpPost httpPost = new HttpPost("http://" + esearchServer + "/_xpack/sql?format=json");

        return doRequestOperation(httpPost, jsonQuery);
    }

    public String doRequestOperation(HttpEntityEnclosingRequestBase request, JSONObject json) throws Exception{
        StringEntity paramEntity = new StringEntity(json.toJSONString(), ContentType.APPLICATION_JSON);
        request.setEntity(paramEntity);
        return doRequest(request);
    }

    public String doRequest(HttpUriRequest request) throws IOException {
        HttpClient httpClient = new DefaultHttpClient();

        request.setHeader("Content-Type", "application/json");
        request.setHeader("Accept", "application/json");

        HttpResponse response = httpClient.execute(request);
        HttpEntity entity = response.getEntity();

        String retVal = "";
        if (entity != null)
            retVal = EntityUtils.toString(entity, HTTP.UTF_8);

        return retVal;
    }
    // Author : GOD
    // Date End : 2019-2-1 6 PM
    // Reason : 系统管理 专题管理 Page Loading Speed, Select First Theme as Default
}
