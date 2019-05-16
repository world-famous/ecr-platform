package com.tianwen.springcloud.microservice.base.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.tianwen.springcloud.commonapi.base.response.Response;
import com.tianwen.springcloud.commonapi.constant.IStateCode;
import com.tianwen.springcloud.commonapi.query.QueryCondition;
import com.tianwen.springcloud.commonapi.query.QueryTree;
import com.tianwen.springcloud.microservice.base.constant.IBaseMicroConstants;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class ESearchService{
    @Value("${ESEARCH_SERVER}")
    protected String esearchServer;
    private Object importRemark;

    public JSONObject getKeyword()
    {
        JSONObject keyword, fields;

        keyword = new JSONObject();
        keyword.put("type", "keyword");
        keyword.put("ignore_above", 256);
        fields = new JSONObject();
        fields.put("keyword", keyword);

        return fields;
    }

    public String getSqlQueryResult(String query) throws Exception{
        JSONObject chapterQuery = new JSONObject();

        chapterQuery.put("query", query);

        HttpPost httpPost = new HttpPost("http://" + esearchServer + "/_xpack/sql?format=json");
        HttpClient httpClient = new DefaultHttpClient();

        String retVal = "";

        StringEntity entity = new StringEntity(chapterQuery.toJSONString(), ContentType.APPLICATION_JSON);
        httpPost.setEntity(entity);
        httpPost.setHeader("Content-Type", "application/json");
        httpPost.setHeader("Accept", "application/json");
        HttpResponse httpResponse = httpClient.execute(httpPost);
        HttpEntity httpEntity = httpResponse.getEntity();
        if (httpEntity != null)
            retVal = EntityUtils.toString(httpEntity, HTTP.UTF_8);

        return retVal;
    }

    public JSONObject getQueryString(String field, String value){
        JSONObject query = new JSONObject();
        /*
        --------------- begin -----------------------------
        @author: jong
        @date: 2019-01-30
        Enable partial-match in search. For Chinese words, "*" is not working, so compose query string as "AAA AAA*"
        to support search for both Chinese and English text
         */
        String searchValue = value.trim() + " " + value.trim() + "*";
        /*
        @author: jong
        ---------------- end -------------------------------
         */
        query.put("query", searchValue);
        List<String> fields = new ArrayList<>();
        fields.add(field);
        query.put("fields", fields);

        JSONObject query_string = new JSONObject();
        query_string.put("query_string", query);

        return query_string;
    }

    public JSONObject getBoolConditions(String operator){
        JSONArray conditions = new JSONArray();
        JSONObject oprObj = new JSONObject();
        JSONObject boolObj = new JSONObject();

        oprObj.put(operator, conditions);
        boolObj.put("bool", oprObj);

        return boolObj;
    }

    public JSONObject getMatchConditions(String key, Object value){
        JSONObject item = new JSONObject();
        item.put(key, value);

        JSONObject match = new JSONObject();
        match.put("match", item);

        return match;
    }

    public void fixQueryTree(QueryTree queryTree) throws Exception{
        int i, len;
        if (queryTree.getQueryCondition("orgid") != null) {
            QueryCondition orgCond = queryTree.getQueryCondition("orgid");
            String value = "";

            try {
                Object[] values = orgCond.getFieldValues();
                value = StringUtil.join(values, " ");
            } catch (Exception e) {
            }

            queryTree.getConditions().remove(orgCond);

            if (!value.isEmpty()) {
                String query = "select userid from users where orgid like '" + value + "'";
                String retVal = getSqlQueryResult(query);

                JSONObject resData = JSONObject.parseObject(retVal);
                JSONArray rows = resData.getJSONArray("rows");
                if (rows == null)
                    rows = new JSONArray();
                List<String> resources = new ArrayList<>();
                len = rows.size();

                resources.add("");
                resources.add("");
                for (i = 0; i < len; i++) {
                    JSONArray array = rows.getJSONArray(i);
                    resources.add(array.getString(0));
                }

                resources.add("");
                resources.add("");
                queryTree.addCondition(new QueryCondition("creatorids", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, resources));
            }
        }

        if (queryTree.getQueryCondition("onelabel") != null) {
            QueryCondition labelCond = queryTree.getQueryCondition("onelabel");
            List<String> ids = new ArrayList<>();

            queryTree.getConditions().remove(labelCond);

            len = labelCond.getFieldValues() == null ? 0 : labelCond.getFieldValues().length;
            Boolean noCondFlag = true;
            for (i = 0; i < len; i++) {
                String value = "";
                try {
                    value = labelCond.getFieldValues()[i].toString();
                } catch (Exception e) {
                }

                if (!value.isEmpty()) {
                    noCondFlag = false;
                    String query = "select labelid from labels where labelname = '" + value + "'";
                    String retVal = getSqlQueryResult(query);

                    JSONObject resData = JSONObject.parseObject(retVal);
                    JSONArray rows = resData.getJSONArray("rows");
                    if (rows == null)
                        rows = new JSONArray();
                    len = rows.size();

                    ids.add("");
                    for (i = 0; i < len; i++) {
                        JSONArray array = rows.getJSONArray(i);
                        ids.add(array.getString(0));
                    }
                }
            }

            if (ids != null && ids.size() < 2){
                ids.add("");
                ids.add("");
            }

            if (noCondFlag == false)
                queryTree.addCondition(new QueryCondition("onelabelid", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, ids));
        }

        if (queryTree.getQueryCondition("twolabel") != null) {
            QueryCondition labelCond = queryTree.getQueryCondition("twolabel");
            String value = "";

            try {
                value = labelCond.getFieldValues()[0].toString();
            } catch (Exception e) {
            }

            Boolean noCondFlag = true;
            queryTree.getConditions().remove(labelCond);
            List<String> ids = new ArrayList<>();
            if (!value.isEmpty()) {
                noCondFlag = false;
                String query = "select labelid from labels where labelname = '" + value + "'";
                String retVal = getSqlQueryResult(query);

                JSONObject resData = JSONObject.parseObject(retVal);
                JSONArray rows = resData.getJSONArray("rows");
                if (rows == null)
                    rows = new JSONArray();
                len = rows.size();

                ids.add("");
                for (i = 0; i < len; i++) {
                    JSONArray array = rows.getJSONArray(i);
                    ids.add(array.getString(0));
                }
            }
            if (ids != null && ids.size() < 2){
                ids.add("");
                ids.add("");
            }

            if (noCondFlag == false)
                queryTree.addCondition(new QueryCondition("twolabelid", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, ids));
        }

        if (queryTree.getQueryCondition("threelabel") != null) {
            QueryCondition labelCond = queryTree.getQueryCondition("threelabel");
            String value = "";

            try {
                value = labelCond.getFieldValues()[0].toString();
            } catch (Exception e) {
            }

            queryTree.getConditions().remove(labelCond);
            List<String> ids = new ArrayList<>();
            Boolean noCondFlag = true;
            if (!value.isEmpty()) {
                noCondFlag = false;
                String query = "select labelid from labels where labelname = '" + value + "'";
                String retVal = getSqlQueryResult(query);

                JSONObject resData = JSONObject.parseObject(retVal);
                JSONArray rows = resData.getJSONArray("rows");
                if (rows == null)
                    rows = new JSONArray();
                len = rows.size();

                ids.add("");
                for (i = 0; i < len; i++) {
                    JSONArray array = rows.getJSONArray(i);
                    ids.add(array.getString(0));
                }
            }
            if (ids != null && ids.size() < 2){
                ids.add("");
                ids.add("");
            }

            if (noCondFlag == false)
                queryTree.addCondition(new QueryCondition("threelabelid", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, ids));
        }

        if (queryTree.getQueryCondition("creatorname") != null) {
            QueryCondition creatorCond = queryTree.getQueryCondition("creatorname");
            String value = "";

            try {
                Object[] values = creatorCond.getFieldValues();
                value = StringUtil.join(values, " ");
            } catch (Exception e) {
            }

            queryTree.getConditions().remove(creatorCond);

            if (!value.isEmpty()) {
                String query = "select userid from users where realname like '" + value + "'";
                String retVal = getSqlQueryResult(query);

                JSONObject resData = JSONObject.parseObject(retVal);
                JSONArray rows = resData.getJSONArray("rows");
                if (rows == null)
                    rows = new JSONArray();
                List<String> resources = new ArrayList<>();
                len = rows.size();

                for (i = 0; i < len; i++) {
                    JSONArray array = rows.getJSONArray(i);
                    resources.add(array.getString(0));
                }

                resources.add("");
                queryTree.addCondition(new QueryCondition("creatorids", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, resources));
            }
        }

        if (queryTree.getQueryCondition("catalogids") != null) {
            QueryCondition chapterCond = queryTree.getQueryCondition("catalogids");
            String value = "";

            try {
                Object[] values = chapterCond.getFieldValues();
                value = StringUtil.join(values, " ");
            } catch (Exception e) {
            }

            queryTree.getConditions().remove(chapterCond);

            if (!value.isEmpty()) {
                String query = "select resourceno from res_con_catalog where chapterid like '" + value + "'";
                String retVal = getSqlQueryResult(query);

                JSONObject resData = JSONObject.parseObject(retVal);
                JSONArray rows = resData.getJSONArray("rows");
                if (rows == null)
                    rows = new JSONArray();
                List<String> resources = new ArrayList<>();
                len = rows.size();

                for (i = 0; i < len; i++) {
                    JSONArray array = rows.getJSONArray(i);
                    resources.add(array.getString(0));
                }
                resources.add("");
                queryTree.addCondition(new QueryCondition("contentid", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, resources));
            }
        }

        /////////////////////////////// special theme begin
        if (queryTree.getQueryCondition("themeids") != null)
        {
            QueryCondition chapterCond = queryTree.getQueryCondition("themeids");
            String value = "";

            try {
                Object[] values = chapterCond.getFieldValues();
                value = StringUtil.join(values, " ");
            } catch (Exception e) {}

            queryTree.getConditions().remove(chapterCond);

            if (!value.isEmpty()) {
                String query = "select resourceno from res_con_theme where themeid like '" + value + "'";
                String retVal = getSqlQueryResult(query);

                JSONObject resData = JSONObject.parseObject(retVal);
                JSONArray rows = resData.getJSONArray("rows");
                if (rows == null)
                    rows = new JSONArray();
                List<String> resources = new ArrayList<>();
                len = rows.size();

                for (i = 0; i < len; i++)
                {
                    JSONArray array = rows.getJSONArray(i);
                    resources.add(array.getString(0));
                }

                resources.add("");
                queryTree.addCondition(new QueryCondition("contentid", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, resources));
            }
        }
    }

    public JSONObject getNameSortScript(String key, String value){
        JSONObject script = new JSONObject();
        script.put("script", "(doc['" + key + ".keyword'].value != null && doc['" + key + ".keyword'].value.equalsIgnoreCase(\"" + value + "\") == true) ? 1 : 0");
        script.put("type", "number");
        script.put("order", "desc");

        JSONObject sort = new JSONObject();
        sort.put("_script", script);

        return sort;
    }

    public JSONObject getSpecialIdSortScript(String key, String value){
        JSONObject script = new JSONObject();
        script.put("script", "String str = \"" + value + "\"; return str.indexOf(doc['" + key + ".keyword'].value);");
        script.put("type", "number");
        script.put("order", "desc");

        JSONObject sort = new JSONObject();
        sort.put("_script", script);

        return sort;
    }

    public JSONObject getImportRemark() {
        JSONObject remarksObject = getBoolConditions("must");
        JSONArray remarksArray = remarksObject.getJSONObject("bool").getJSONArray("must");

        JSONObject existField = new JSONObject();
        existField.put("field", "remarks");
        existField.put("boost", 1);

        JSONObject exist = new JSONObject();
        exist.put("exists", existField);

        JSONObject valueObject = getBoolConditions("must_not");
        JSONArray valueArray = valueObject.getJSONObject("bool").getJSONArray("must_not");
        JSONObject keyword = new JSONObject();
        keyword.put("value", "");
        keyword.put("boost", 1);
        JSONObject term = new JSONObject();
        term.put("remarks.keyword", keyword);
        JSONObject valueItem = new JSONObject();
        valueItem.put("term", term);
        valueArray.add(valueItem);

        remarksArray.add(exist);
        remarksArray.add(valueObject);

        return remarksObject;
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

    public String doRequestOperation(HttpEntityEnclosingRequestBase request, JSONObject json) throws Exception{
        StringEntity paramEntity = new StringEntity(json.toJSONString(), ContentType.APPLICATION_JSON);
        request.setEntity(paramEntity);
        return doRequest(request);
    }

    public JSONObject makeMapping(String esType, String[] keys, String[] types){
        int i, len = keys.length;

        JSONObject fields = new JSONObject();
        JSONObject properties = new JSONObject();
        JSONObject type = new JSONObject();
        JSONObject mapping = new JSONObject();

        for (i = 0; i < len; i++)
        {
            JSONObject item = new JSONObject();

            item.put("type", types[i]);
            if (types[i].equalsIgnoreCase("text")) {
                item.put("fields", getKeyword());
                item.put("search_analyzer", "ik_smart");
                item.put("analyzer", "ik_smart");
            }
            else if (types[i].equalsIgnoreCase("date"))
            {
                item.put("fields", getKeyword());
                item.put("format", "yyyy-MM-dd HH:mm:ss");
            }

            fields.put(keys[i], item);
        }

        properties.put("properties", fields);
        type.put(esType, properties);
        mapping.put("mappings", type);

        return mapping;
    }

    public Response notFoundServerError() {
        return new Response(IStateCode.HTTP_404, IBaseMicroConstants.ES_SERVER_ERROR);
    }
}
