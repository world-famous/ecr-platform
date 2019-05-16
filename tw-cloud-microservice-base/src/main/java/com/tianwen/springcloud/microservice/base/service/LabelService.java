package com.tianwen.springcloud.microservice.base.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.tianwen.springcloud.commonapi.base.response.Response;
import com.tianwen.springcloud.commonapi.constant.IStateCode;
import com.tianwen.springcloud.commonapi.log.SystemServiceLog;
import com.tianwen.springcloud.commonapi.query.Pagination;
import com.tianwen.springcloud.commonapi.query.QueryCondition;
import com.tianwen.springcloud.commonapi.query.QueryTree;
import com.tianwen.springcloud.datasource.base.BaseService;
import com.tianwen.springcloud.datasource.util.QueryUtils;
import com.tianwen.springcloud.microservice.base.constant.IBaseMicroConstants;
import com.tianwen.springcloud.microservice.base.dao.AreaMapper;
import com.tianwen.springcloud.microservice.base.dao.LabelMapper;
import com.tianwen.springcloud.microservice.base.entity.Label;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.apache.poi.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class LabelService extends BaseService<Label>
{
    @Autowired
    private LabelMapper labelMapper;

    @Autowired
    private AreaMapper areaMapper;

    @Value("${ESEARCH_SERVER}")
    private String esearchServer;

    @Autowired
    private ESearchService eSearchService;


    public List<Label> validateLabel(List<Label> labels){
        return labelMapper.validateLabel(labels);
    }
    //        Author : GOD 2019-2-20 Bug ID: #792
    public void fixParam(Map<String, Object> map) {
        {
            Object begin = map.get("begin_time");
            if (begin != null && begin != "") {
                map.remove("begin_time");
                if (!begin.toString().isEmpty()) {
                    Timestamp beginDate = Timestamp.valueOf(begin.toString() + " 00:00:00");
                    map.remove("begin_time");
                    map.put("begin_time", beginDate);
                }
            }
        }

        {
            Object end = map.get("end_time");
            if (end != null && end != "") {
                map.remove("end_time");
                if (!end.toString().isEmpty()) {
                    Date endDate = Timestamp.valueOf(end.toString() + " 23:59:59");
                    map.remove("end_time");
                    map.put("end_time", endDate);
                }
            }
        }
    }
    //        Author : GOD 2019-2-20 Bug ID: #792
    public String increment(String s)
    {
        if (s == null)
            s = "00000";

        Matcher m = Pattern.compile("\\d+").matcher(s);
        if (!m.find())
            throw new NumberFormatException();
        String num = m.group();
        int inc = Integer.parseInt(num) + 1;
        String incStr = String.format("%0" + num.length() + "d", inc);
        return  m.replaceFirst(incStr);
    }

    @SystemServiceLog(description = "")
    public Response<Label> getList(QueryTree queryTree)
    {
        Pagination pagination = queryTree.getPagination();
        int size = pagination.getNumPerPage(), start = pagination.getStart(), totalSize = 0;
        List<QueryCondition> conditions = queryTree.getConditions();
        List<Label> result = new ArrayList<>();

        QueryCondition areaCond = queryTree.getQueryCondition("areaid");
        if (areaCond != null){
            String areaid;
            queryTree.getConditions().remove(areaCond);
            try {
                areaid = areaCond.getFieldValues()[0].toString();
            }
            catch (Exception e){
                areaid = "";
            }

            if (!StringUtils.isEmpty(areaid)){
                try{
                    List<String> areaids = areaMapper.getAllChildren(areaid);
                    areaids.add(areaid);

                    List<String> orgids = new ArrayList<>();
                    orgids.add("");
                    orgids.add("");

                    String retVal = eSearchService.getSqlQueryResult("select orgid from organizations where areaid like '" + StringUtils.join(areaids, " ") + "'");

                    JSONObject totalObj = JSONObject.parseObject(retVal);
                    JSONArray orgIdData = totalObj.getJSONArray("rows");
                    int i, len = orgIdData.size();
                    for (i = 0; i < len; i++){
                        JSONArray item = orgIdData.getJSONArray(i);
                        orgids.add(item.getString(0));
                    }

                    queryTree.addCondition(new QueryCondition("schoolid", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, orgids));
                }
                catch (Exception e){
                    return new Response<>(IStateCode.HTTP_404, IBaseMicroConstants.ES_SERVER_ERROR);
                }
            }
        }

        QueryCondition pNameCond = queryTree.getQueryCondition("parentlabelname");
        if (pNameCond != null){
            String parentname;
            queryTree.getConditions().remove(pNameCond);
            try {
                parentname = pNameCond.getFieldValues()[0].toString();
            }
            catch (Exception e){
                parentname = "";
            }

            if (!StringUtils.isEmpty(parentname)){
                try{
                    List<String> labelids = new ArrayList<>();
                    labelids.add("");
                    labelids.add("");

                    String retVal = eSearchService.getSqlQueryResult("select labelid from labels where labelname = '" + parentname + "'");

                    JSONObject totalObj = JSONObject.parseObject(retVal);
                    JSONArray labelIdData = totalObj.getJSONArray("rows");
                    int i, len = labelIdData.size();
                    for (i = 0; i < len; i++){
                        JSONArray item = labelIdData.getJSONArray(i);
                        labelids.add(item.getString(0));
                    }

                    queryTree.addCondition(new QueryCondition("parentlabelid", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, labelids));
                }
                catch (Exception e){
                    return new Response<>(IStateCode.HTTP_404, IBaseMicroConstants.ES_SERVER_ERROR);
                }
            }
        }
        QueryCondition nameCond = queryTree.getQueryCondition("labelname");
        if (nameCond != null){
            String labelname;
            queryTree.getConditions().remove(nameCond);
            try {
                labelname = nameCond.getFieldValues()[0].toString();
            }
            catch (Exception e){
                labelname = "";
            }

            if (!StringUtils.isEmpty(labelname)){
                try{
                    String retVal;
                    int i, len;

                    retVal = eSearchService.getSqlQueryResult("select labelid from labels where labelname = '" + labelname + "'");

                    List<String> labelids_0 = new ArrayList<>();

                    JSONObject totalObj_0 = JSONObject.parseObject(retVal);
                    JSONArray labelIdData_0 = totalObj_0.getJSONArray("rows");
                    len = labelIdData_0.size();
                    for (i = 0; i < len; i++){
                        JSONArray item = labelIdData_0.getJSONArray(i);
                        labelids_0.add(item.getString(0));
                    }

                    retVal = eSearchService.getSqlQueryResult("select parentlabelid from labels where labelid like '" + StringUtils.join(labelids_0, " ") + "'");

                    List<String> labelids_1 = new ArrayList<>();

                    JSONObject totalObj_1 = JSONObject.parseObject(retVal);
                    JSONArray labelIdData_1 = totalObj_1.getJSONArray("rows");
                    len = labelIdData_1.size();
                    for (i = 0; i < len; i++){
                        JSONArray item = labelIdData_1.getJSONArray(i);
                        labelids_1.add(item.getString(0));
                    }

                    retVal = eSearchService.getSqlQueryResult("select parentlabelid from labels where labelid like '" + StringUtils.join(labelids_1, " ") + "'");

                    List<String> labelids_2 = new ArrayList<>();

                    JSONObject totalObj_2 = JSONObject.parseObject(retVal);
                    JSONArray labelIdData_2 = totalObj_2.getJSONArray("rows");
                    len = labelIdData_2.size();
                    for (i = 0; i < len; i++){
                        JSONArray item = labelIdData_2.getJSONArray(i);
                        labelids_2.add(item.getString(0));
                    }

                    List<String> labelids = new ArrayList<>();
                    labelids.addAll(labelids_0);
                    labelids.addAll(labelids_1);
                    labelids.addAll(labelids_2);

                    labelids.add("");
                    labelids.add("");
                    labelids.add("");

                    queryTree.addCondition(new QueryCondition("labelid", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, labelids));
                }
                catch (Exception e){
                    return new Response<>(IStateCode.HTTP_404, IBaseMicroConstants.ES_SERVER_ERROR);
                }
            }
        }

        JSONObject totalBool = eSearchService.getBoolConditions("must");
        JSONArray totalConditions = totalBool.getJSONObject("bool").getJSONArray("must");

        for (QueryCondition condition : conditions){
            String key = condition.getFieldName();
            String rawVal = "", value;

            try {
                Object[] values = condition.getFieldValues();
                rawVal = StringUtil.join(values, " ");
            } catch (Exception e) {}

            if (key.equalsIgnoreCase("schoolsectionid") || key.equalsIgnoreCase("subjectid") || key.equalsIgnoreCase("gradeid")
                    || key.equalsIgnoreCase("editiontypeid") || key.equalsIgnoreCase("bookmodelid")
                    || key.equalsIgnoreCase("sourceids") || key.equalsIgnoreCase("contenttypes"))
                value = rawVal.replace(',', ' ');
            else
                value = rawVal;

            if (key.equalsIgnoreCase("labelnames"))
                key = "labelname";

            if (StringUtils.isEmpty(value))
                continue;

            totalConditions.add(eSearchService.getMatchConditions(key, value));
        }

        JSONObject searchQuery = new JSONObject();
        searchQuery.put("query", totalBool);
        searchQuery.put("from", start);
        searchQuery.put("size", size);

        HttpPost httpPost = new HttpPost("http://" + esearchServer + "/" + IBaseMicroConstants.INDEX_LABEL + "/" + IBaseMicroConstants.TYPE_LABEL + "/_search");
        HttpClient httpClient = new DefaultHttpClient();

        String retVal = "";
        JSONObject searchRes;
        JSONArray resData;

        try {
            StringEntity entity = new StringEntity(searchQuery.toJSONString(), ContentType.APPLICATION_JSON);
            httpPost.setEntity(entity);
            httpPost.setHeader("Content-Type", "application/json");
            httpPost.setHeader("Accept", "application/json");
            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            if (httpEntity != null)
                retVal = EntityUtils.toString(httpEntity, HTTP.UTF_8);
        } catch (Exception e) {
            return new Response<>(IStateCode.HTTP_404, IBaseMicroConstants.ES_SERVER_ERROR);
        }

        try {
            searchRes = JSONObject.parseObject(retVal);
            JSONObject resultData = searchRes.getJSONObject("hits");
            resData = resultData.getJSONArray("hits");
            int i, len = resData.size();
            totalSize = resultData.getInteger("total");

            for (i = 0; i < len; i++) {
                String id = resData.getJSONObject(i).get("_id").toString();
                Label label = selectByKey(id);
                if (label == null)
                    continue;
                label.setChildcount(getChildCount(label.getLabelid()));
                if (label != null)
                    result.add(label);
            }
        } catch (Exception e) {
            return new Response<>(IStateCode.HTTP_404, IBaseMicroConstants.ES_SERVER_ERROR);
        }

//        validateLabel(result);
        Response<Label> response = new Response<>(result);
        response.getPageInfo().setTotal(totalSize);

        return response;
    }

    private Integer getChildCount(String labelid) {
        return labelMapper.getChildCount(labelid);
    }

    @Override
    public boolean cacheable()
    {
        return false;
    }
    
    @Override
    public boolean listCacheable()
    {
        return false;
    }

    public int insertLabel(Label entity) {
        String maxSibling = labelMapper.getMaxSibling(entity.getParentlabelid());

        maxSibling = increment(maxSibling);

        entity.setSequence(maxSibling);
        return save(entity);
    }

    public List<Label> getSiblings(Label src) {
        return labelMapper.getSiblings(src);
    }

    public List<Label> getChildrenByParent(Map<String, Object> param) {
        return labelMapper.getChildrenByParent(param);
    }

    @SystemServiceLog(description = "OneLabelSearch")
    public List<Label> searchOneLabel(Map<String, Object> labelData)
    {
        List<Label> queryList = labelMapper.queryOneLabelForList(labelData);

        return queryList;
    }
    @SystemServiceLog(description = "OneLabelCount")
    public Long countOneLabel(Map<String, Object> labelData)
    {
        Long count = labelMapper.countOneLabelForList(labelData);

        return count;
    }

    @SystemServiceLog(description = "TwoLabelSearch")
    public List<Label> searchTwoLabel(Map<String, Object> labelData)
    {
        //        Author : GOD 2019-2-20 Bug ID: #792
        fixParam(labelData);
        //        Author : GOD 2019-2-20 Bug ID: #792
        List<Label> queryList = labelMapper.queryTwoLabelForList(labelData);

        return queryList;
    }

    @SystemServiceLog(description = "TwoLabelCount")
    public Long countTwoLabel(Map<String, Object> labelData)
    {
        Long count = labelMapper.countTwoLabelForList(labelData);

        return count;
    }

    @SystemServiceLog(description = "ThreeLabelSearch")
    public List<Label> searchThreeLabelList(Map<String, Object> labelData) {
        List<Label> queryList = labelMapper.queryThreeLabelForList(labelData);

        return queryList;
    }

    @SystemServiceLog(description = "ThreeLabelCount")
    public Long countThreeLabel(Map<String, Object> labelData)
    {
        Long count = labelMapper.countThreeLabelForList(labelData);

        return count;
    }

    public String getLabelStructure(Map<String, Object> labelData) {
        return labelMapper.getLabelStructure(labelData);
    }

    public Response<Label> getByExample(Label example) {
        return new Response<>(labelMapper.getByExample(example));
    }

    public String getMaxSibling(String parentlabelid) {
        return labelMapper.getMaxSibling(parentlabelid);
    }

    public List<String> getOneLabelIds(QueryTree queryTree) {
        Map<String, Object> map = QueryUtils.queryTree2Map(queryTree);
        List<String> ids = labelMapper.getOneLabelIds(map);
        if (ids != null) {
            ids.add("");
            ids.add("");
        }
        return ids;
    }
}
