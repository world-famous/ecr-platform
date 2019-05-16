package com.tianwen.springcloud.microservice.base.controller;

import com.alibaba.fastjson.JSONObject;
import com.tianwen.springcloud.commonapi.base.response.Response;
import com.tianwen.springcloud.commonapi.constant.IStateCode;
import com.tianwen.springcloud.commonapi.log.SystemControllerLog;
import com.tianwen.springcloud.commonapi.query.OrderMethod;
import com.tianwen.springcloud.commonapi.query.QueryTree;
import com.tianwen.springcloud.datasource.base.AbstractCRUDController;
import com.tianwen.springcloud.datasource.util.QueryUtils;
import com.tianwen.springcloud.microservice.base.api.LabelMicroApi;
import com.tianwen.springcloud.microservice.base.constant.IBaseMicroConstants;
import com.tianwen.springcloud.microservice.base.entity.*;
import com.tianwen.springcloud.microservice.base.service.LabelService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.utils.DateUtils;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/label")
public class LabelController extends AbstractCRUDController<Label> implements LabelMicroApi
{
    @Autowired
    private LabelService labelService;

    @Value("${ESEARCH_SERVER}")
    private String esearchServer;

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

    @Override
    public Response<Label> makeESDb() throws Exception{
        String indexUrl = "http://" + esearchServer + "/" + IBaseMicroConstants.INDEX_LABEL;

        try{
            HttpResponse response;
            HttpClient httpClient = new DefaultHttpClient();
            HttpDelete httpDelete = new HttpDelete(indexUrl);
            response = httpClient.execute(httpDelete);
        }
        catch (Exception e){
            e.printStackTrace();
        }

        String[] keys = {"labelid", "labelname", "parentlabelid", "businesstype", "labeltype", "sequence", "status", "localpath", "property",
            "sortmethod", "schoolsectionid", "subjectid", "gradeid", "editiontypeid", "bookmodelid", "schoolid", "creator", "creatortime", "lastmodifytime"};
        String[] types = {"text", "text", "text", "text", "text", "text", "text", "text", "text", "text", "text", "text", "text", "text", "text", "text", "text", "date", "date"};

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
        type.put(IBaseMicroConstants.TYPE_LABEL, properties);
        mapping.put("mappings", type);

        HttpClient httpClient = new DefaultHttpClient();
        HttpPut httpPut = new HttpPut(indexUrl);

        StringEntity paramEntity = new StringEntity(mapping.toJSONString(), ContentType.APPLICATION_JSON);
        httpPut.setEntity(paramEntity);
        httpPut.setHeader("Content-Type", "application/json");
        httpClient.execute(httpPut);

        return new Response<>();
    }

    @Override
    public Response<Label> batchSaveToES(@RequestBody QueryTree queryTree) {
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

        resQuery.orderBy("parentlabelid", OrderMethod.Method.ASC);
        resQuery.orderBy("sortmethod", OrderMethod.Method.ASC);
        resQuery.orderBy("sequence", OrderMethod.Method.ASC);

        List<Label> dataList = search(resQuery).getPageInfo().getList();

        if (!CollectionUtils.isEmpty(dataList)) {
            for (Label item : dataList)
                saveToES(item);
        }
        else
            return new Response<>(IStateCode.PARAMETER_IS_INVALID, "");

        return new Response<>();
    }

    @Override
    public Response<Label> saveToES(@RequestBody Label entity) {
        JSONObject json = new JSONObject();

        json.put("labelid", entity.getLabelid());
        json.put("labelname", entity.getLabelname());
        json.put("parentlabelid", entity.getParentlabelid());
        json.put("bussinesstype", entity.getBusinesstype());
        json.put("labeltype", entity.getLabeltype());
        json.put("sequence", entity.getSequence());
        json.put("status", entity.getStatus());
        json.put("localpath", entity.getLocalpath());
        json.put("property", entity.getProperty());
        json.put("sortmethod", entity.getSortmethod());
        json.put("schoolsectionid", entity.getSchoolsectionid());
        json.put("subjectid", entity.getSubjectid());
        json.put("gradeid", entity.getGradeid());
        json.put("bookmodelid", entity.getBookmodelid());
        json.put("editiontypeid", entity.getEditiontypeid());
        json.put("schoolid", entity.getSchoolid());
        json.put("creator", entity.getCreator());
        if (entity.getCreatetime() != null)
            json.put("createtime", DateUtils.formatDate(entity.getCreatetime(), "yyyy-MM-dd HH:mm:ss"));
        if (entity.getLastmodifytime() != null)
            json.put("lastmodifytime", DateUtils.formatDate(entity.getLastmodifytime(), "yyyy-MM-dd HH:mm:ss"));

        HttpPut httpPut = new HttpPut("http://" + esearchServer + "/" + IBaseMicroConstants.INDEX_LABEL + "/" + IBaseMicroConstants.INDEX_LABEL + "/" + entity.getLabelid());
        HttpClient httpClient = new DefaultHttpClient();

        try
        {
            StringEntity paramEntity = new StringEntity(json.toJSONString(), ContentType.APPLICATION_JSON);
            httpPut.setEntity(paramEntity);
            httpPut.setHeader("Content-Type", "application/json");
            httpPut.setHeader("Accept", "application/json");
            httpClient.execute(httpPut);
        }
        catch (Exception e){
            return new Response<>(IStateCode.HTTP_404, IBaseMicroConstants.ES_SERVER_ERROR);
        }

        return new Response<>();
    }

    @Override
    public Response<Label> removeFromES(@RequestBody Label entity) {
        HttpDelete httpDelete = new HttpDelete("http://" + esearchServer + "/" + IBaseMicroConstants.INDEX_LABEL + "/" + IBaseMicroConstants.TYPE_LABEL + "/" + entity.getLabelid());
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
    public Response<Label> removeFromES(@PathVariable(value = "id") String id) {
        HttpDelete httpDelete = new HttpDelete("http://" + esearchServer + "/" + IBaseMicroConstants.INDEX_LABEL + "/" + IBaseMicroConstants.TYPE_LABEL + "/" + id);
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
    @ApiOperation(value = "根据ID删除用户信息", notes = "根据ID删除用户信息")
    @ApiImplicitParam(name = "id", value = "用户ID", required = true, dataType = "String", paramType = "path")
    @SystemControllerLog(description = "根据用户ID删除用户信息")
    public Response<Label> delete(@PathVariable(value = "id") String id)
    {
        Response<Label> resp = super.delete(id);
        if (resp.getServerResult().getResultCode().equalsIgnoreCase(IStateCode.HTTP_200))
            removeFromES(id);
        return resp;
    }

    @Override
    @ApiOperation(value = "根据实体对象删除用户信息", notes = "根据实体对象删除用户信息")
    @ApiImplicitParam(name = "entity", value = "用户信息实体", required = true, dataType = "UserLoginInfo", paramType = "body")
    @SystemControllerLog(description = "根据实体对象删除用户信息")
    public Response<Label> deleteByEntity(@RequestBody Label entity)
    {
        int ret = labelService.delete(entity);
        if (ret == 1) {
            ret = 200;
            if (entity.getLabelid() != null)
                removeFromES(entity);
        }
        Response<Label> resp = new Response<>(entity);
        resp.getServerResult().setResultCode(Integer.toString(ret));
        return resp;
    }

    @Override
    public Response<LabelTree> getLabelTree(@RequestBody QueryTree queryTree) {
        Map<String, Object> labelData = QueryUtils.queryTree2Map(queryTree);
        List<LabelTree> result = new ArrayList<>();
        String searchKey;

        if (labelData.get("labelname") != null)
        {
            searchKey = labelData.get("labelname").toString();
            if (labelData.get("labelname").toString().isEmpty())
                labelData.put("labelname", "%%");
            else
                labelData.put("labelname", "%" + labelData.get("labelname").toString() + "%");
        }
        else
            searchKey = "";

        List<Label> oneList = labelService.searchOneLabel(labelData);

        for(Label one : oneList)
        {
            LabelTree oneNode = new LabelTree(one.getLabelname());
            Label oneLabel = one;
            oneLabel.setOnelabel(one.getLabelname());oneLabel.setTwolabel("");oneLabel.setThreelabel("");
            oneNode.setLabelData(oneLabel);
            labelData.put("parentlabelid", one.getLabelid());
            List<Label> twoList = labelService.searchTwoLabel(labelData);
            for (Label two : twoList)
            {
                LabelTree twoNode = new LabelTree(two.getLabelname());
                Label twoLabel = two;
                twoLabel.setOnelabel(one.getLabelname());twoLabel.setTwolabel(two.getLabelname());twoLabel.setThreelabel("");
                twoNode.setLabelData(twoLabel);
                labelData.put("parentlabelid", two.getLabelid());
                List<Label> threeList = labelService.searchThreeLabelList(labelData);
                for(Label three : threeList)
                {
                    LabelTree threeNode = new LabelTree(three.getLabelname());
                    Label threeLabel = three;
                    threeLabel.setOnelabel(one.getLabelname());threeLabel.setTwolabel(two.getLabelname());threeLabel.setThreelabel(three.getLabelname());
                    threeNode.setLabelData(threeLabel);

                    if (one.getLabelname().indexOf(searchKey) != -1
                            || two.getLabelname().indexOf(searchKey) != -1
                            || three.getLabelname().indexOf(searchKey) != -1) {
                        twoNode.addChildren(threeNode);
                    }
                }
                if (one.getLabelname().indexOf(searchKey) != -1
                        || two.getLabelname().indexOf(searchKey) != -1
                        || twoNode.getChildren().size() > 0) {
                    oneNode.addChildren(twoNode);
                }
            }
            if (one.getLabelname().indexOf(searchKey) != -1
                    || oneNode.getChildren().size() > 0) {
                result.add(oneNode);
            }
        }

        return new Response<>(result);
    }

    @Override
    @ApiOperation(value = "获取用户列表", notes = "获取用户列表")
    @ApiImplicitParam(name = "queryTree", value = "搜索条件树", required = true, dataType = "QueryTree", paramType = "body")
    @SystemControllerLog(description = "获取用户列表")
    public Response<Label> getList(@RequestBody QueryTree queryTree)
    {
        return labelService.getList(queryTree);
    }

    @Override
    @ApiOperation(value = "", notes = "")
    @ApiImplicitParam(name = "queryTree", value = "搜索条件树", required = true, dataType = "QueryTree", paramType = "body")
    @SystemControllerLog(description = "")
    public Response<Label> get(@PathVariable(value = "id") String id)
    {
        Label userInfo = labelService.selectByKey(id);

        Response<Label> resp = new Response<>(userInfo);

        return resp;
    }

    @Override
    @ApiOperation(value = "", notes = "")
    @ApiImplicitParam(name = "queryTree", value = "搜索条件树", required = true, dataType = "QueryTree", paramType = "body")
    @SystemControllerLog(description = "")
    public Response<Label> insert(@RequestBody Label entity)
    {
        entity.setStatus("1");

        //set sequence
        String sequence = labelService.getMaxSibling(entity.getParentlabelid());
        sequence = labelService.increment(sequence);
        entity.setSequence(sequence);

        //createtime, lastmodifytime
        entity.setCreatetime(new Timestamp(System.currentTimeMillis()));
        entity.setLastmodifytime(new Timestamp(System.currentTimeMillis()));

        int ret = labelService.insertLabel(entity);
        if (ret == 1) {
            ret = 200;
            saveToES(entity);
        }
        Response<Label> resp = new Response<>(entity);
        resp.getServerResult().setResultCode(Integer.toString(ret));
        return resp;
    }

    @Override
    @ApiOperation(value = "", notes = "")
    @ApiImplicitParam(name = "queryTree", value = "搜索条件树", required = true, dataType = "QueryTree", paramType = "body")
    @SystemControllerLog(description = "")
    public Response<Label> update(@RequestBody Label entity)
    {
        //last modify time
        entity.setLastmodifytime(new Timestamp(System.currentTimeMillis()));

        int ret = labelService.updateNotNull(entity);
        if (ret == 1) {
            ret = 200;
            Label entity1 = labelService.selectByKey(entity.getLabelid());
            entity1.setLabelname(entity.getLabelname());
            saveToES(entity1);
        }
        Response<Label> resp = new Response<>(entity);
        resp.getServerResult().setResultCode(Integer.toString(ret));
        if (ret != 0)
            resp.getServerResult().setResultMessage("");
        return resp;
    }

    @Override
    public Response<Label> move(@RequestBody Map<String, Object> param) {
        Label src = labelService.selectByKey(param.get("fromlabelid").toString());
        Label tar = labelService.selectByKey(param.get("tolabelid").toString());
        List<Label> siblings = labelService.getSiblings(src);

        String sequence;
        for(Label label : siblings)
        {
            sequence = label.getSequence();
            if (Integer.parseInt(sequence) >= Integer.parseInt(tar.getSequence()) && !label.getLabelid().equals(src.getLabelid()))
            {
                sequence = labelService.increment(sequence);
                label.setSequence(sequence);
                labelService.updateNotNull(label);
            }
        }

        src.setSequence(tar.getSequence());
        labelService.updateNotNull(src);

        return new Response<>(new Label());
    }

    @Override
    public Response<Label> getChildrenByParent(@RequestBody Map<String, Object> param) {
        List<Label> labelList = labelService.getChildrenByParent(param);
        return new Response<>(labelList);
    }

    @Override
    @ApiOperation(value = "获取一级标签列表", notes = "获取一级标签列表")
    @ApiImplicitParam(name = "labelData", value = "标签信息", required = false, dataType = "Map<String, Object>", paramType = "body")
    @SystemControllerLog(description = "获取一级标签列表")
    public Response<Label> getOneLabelList(@RequestBody  Map<String, Object> param)
    {
        List<Label> oneList = labelService.searchOneLabel(param);
        Long count = labelService.countOneLabel(param);
        Response<Label> resp = new Response<>(oneList);
        resp.getPageInfo().setTotal(count);
        return resp;
    }

    @Override
    @ApiOperation(value = "获取二级标签列表", notes = "获取二级标签列表")
    @ApiImplicitParam(name = "labelData", value = "标签信息", required = false, dataType = "Map<String, Object>", paramType = "body")
    @SystemControllerLog(description = "获取二级标签列表")
    public Response<Label> getTwoLabelList(@RequestBody Map<String, Object> param)
    {
        List<Label> twoList = labelService.searchTwoLabel(param);
        Long count = labelService.countTwoLabel(param);
        Response<Label> resp = new Response<>(twoList);
        resp.getPageInfo().setTotal(count);
        return resp;
    }

    @Override
    @ApiOperation(value = "获取三级标签列表", notes = "获取三级标签列表")
    @ApiImplicitParam(name = "labelData", value = "标签信息", required = false, dataType = "Map<String, Object>", paramType = "body")
    @SystemControllerLog(description = "获取三级标签列表")
    public Response<Label> getThreeLabelList(@RequestBody Map<String, Object> param)
    {
        List<Label> threeList = labelService.searchThreeLabelList(param);
        Long count = labelService.countThreeLabel(param);
        Response<Label> resp = new Response<>(threeList);
        resp.getPageInfo().setTotal(count);
        return resp;
    }

    @Override
    @ApiOperation(value = "获取标签布局", notes = "获取标签布局")
    @ApiImplicitParam(name = "labelData", value = "标签信息", required = false, dataType = "Map<String, Object>", paramType = "body")
    @SystemControllerLog(description = "获取标签布局")
    public Response<Map<String, Object>> getLabelStructure(@RequestBody Map<String ,Object> labelData)
    {
        String labelStructure = labelService.getLabelStructure(labelData);
        Map<String, Object> result = new HashMap<>();

        result.put("structure", labelStructure);
        return new Response<>(result);
    }

    @Override
    public Response<Label> getByExample(@RequestBody Label example) {
        return labelService.getByExample(example);
    }

    @Override
    public Response<String> getOneLabelIds(@RequestBody QueryTree queryTree) {
        return new Response<>(labelService.getOneLabelIds(queryTree));
    }

    @Override
    public void validate(MethodType methodType, Object p)
    {
        switch (methodType)
        {
            case ADD:
                break;
            case DELETE:
                break;
            case GET:
                break;
            case SEARCH:
                break;
            case UPDATE:
                break;
            case BATCHADD:
                break;
            case BATCHDELETEBYENTITY:
                break;
            case BATCHUPDATE:
                break;
            case DELETEBYENTITY:
                break;
            case GETBYENTITY:
                break;
            default:
                break;
        }
    }
}
