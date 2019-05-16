package com.tianwen.springcloud.ecrapi.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.tianwen.springcloud.commonapi.base.response.Response;
import com.tianwen.springcloud.commonapi.constant.IStateCode;
import com.tianwen.springcloud.microservice.resource.constant.IResourceMicroConstants;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
public class ESearchUtil {
    public static JSONObject makeSimpleCondition(String type, String key, String value){
        JSONObject item = new JSONObject();
        item.put(key, value);

        JSONObject node = new JSONObject();
        node.put(type, item);

        return node;
    }

    public static String doRequest(HttpUriRequest request) throws IOException {
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

    public static String doRequestOperation(HttpEntityEnclosingRequestBase request, JSONObject json) throws Exception{
        StringEntity paramEntity = new StringEntity(json.toJSONString(), ContentType.APPLICATION_JSON);
        request.setEntity(paramEntity);
        return doRequest(request);
    }

    public static Response notFoundServerError() {
        return new Response(IStateCode.HTTP_404, IResourceMicroConstants.ES_SERVER_ERROR);
    }

    public static String getSqlQueryResult(String esearchServer, String query) throws Exception{
        JSONObject jsonQuery = new JSONObject();

        jsonQuery.put("query", query);
        HttpPost httpPost = new HttpPost("http://" + esearchServer + "/_xpack/sql?format=json");

        return doRequestOperation(httpPost, jsonQuery);
    }

    public static List<JSONObject> getDictMatchIds(String esearchServer, String index, List<String> values)
    {
        List<JSONObject> result = new ArrayList<>();

        String value = StringUtils.join(values, " ");
        String sql = "select dicttypeid, dictvalue from " + index + " where dictname like '" + value + "' and (dicttypeid = 'SCHOOL_SECTION'" +
                " or dicttypeid = 'SUBJECT' or dicttypeid = 'EDITION' or dicttypeid = 'VOLUME' or dicttypeid = 'CONTENT_TYPE')";

        try {
            int i, len;
            String retVal = ESearchUtil.getSqlQueryResult(esearchServer, sql);

            JSONObject resData = JSONObject.parseObject(retVal);
            JSONArray rows = resData.getJSONArray("rows");
            if (rows == null)
                rows = new JSONArray();
            len = rows.size();
            for (i = 0; i < len ;i++) {
                JSONArray array = rows.getJSONArray(i);
                String dicttype = array.getString(0);
                String dictvalue = array.getString(1);

                if (dicttype.equalsIgnoreCase("SCHOOL_SECTION")) {
                    result.add(makeSimpleCondition("match", "schoolsectionid", dictvalue));
                    result.add(makeSimpleCondition("match", "gradeid", dictvalue));
                }
                else if (dicttype.equalsIgnoreCase("SUBJECT")) {
                    result.add(makeSimpleCondition("match", "subjectid", dictvalue));
                }
                else if (dicttype.equalsIgnoreCase("EDITION")) {
                    result.add(makeSimpleCondition("match", "editiontypeid", dictvalue));
                }
                else if (dicttype.equalsIgnoreCase("VOLUME")) {
                    result.add(makeSimpleCondition("match", "bookmodelid", dictvalue));
                }
                else if (dicttype.equalsIgnoreCase("CONTENT_TYPE")){
                    result.add(makeSimpleCondition("match", "contenttype", dictvalue));
                }
            }
        } catch (Exception e) {
            return new ArrayList<>();
        }

        return result;
    }

    public static List<String> getObjectIdsByQuery(String esearchServer, String query) throws Exception{
        String retVal = getSqlQueryResult(esearchServer, query);

        JSONObject resData = JSONObject.parseObject(retVal);
        JSONArray rows = resData.getJSONArray("rows");
        if (rows == null)
            rows = new JSONArray();
        List<String> objectids = new ArrayList<>();

        int i, len = rows.size();
        objectids.add("");
        objectids.add("");
        for (i = 0; i < len; i++) {
            JSONArray array = rows.getJSONArray(i);
            objectids.add(array.getString(0));
        }

        objectids.add("");
        objectids.add("");

        return objectids;
    }

    public static JSONObject getQueryString(String field, String value){
        JSONObject query = new JSONObject();
        String searchValue = "*" + value + "*";
        query.put("query", searchValue);
        List<String> fields = new ArrayList<>();
        fields.add(field);
        query.put("fields", fields);

        JSONObject query_string = new JSONObject();
        query_string.put("query_string", query);

        return query_string;
    }

    public static JSONObject getMatchConditions(String key, Object value){
        JSONObject item = new JSONObject();
        item.put(key, value);

        JSONObject match = new JSONObject();
        match.put("match", item);

        return match;
    }
}
