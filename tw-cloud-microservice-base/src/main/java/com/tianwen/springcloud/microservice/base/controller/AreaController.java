package com.tianwen.springcloud.microservice.base.controller;

import com.alibaba.fastjson.JSONObject;
import com.tianwen.springcloud.commonapi.base.response.Response;
import com.tianwen.springcloud.commonapi.constant.IStateCode;
import com.tianwen.springcloud.commonapi.query.QueryTree;
import com.tianwen.springcloud.datasource.base.AbstractCRUDController;
import com.tianwen.springcloud.microservice.base.api.AreaMicroApi;
import com.tianwen.springcloud.microservice.base.constant.IBaseMicroConstants;
import com.tianwen.springcloud.microservice.base.entity.*;
import com.tianwen.springcloud.microservice.base.service.AreaService;
import com.tianwen.springcloud.microservice.base.service.OrganizationService;
import com.tianwen.springcloud.microservice.base.service.UserService;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPut;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/area")
public class AreaController extends AbstractCRUDController<Area> implements AreaMicroApi
{
    @Autowired
    private AreaService areaService;

    @Autowired
    private UserService userService;

    @Autowired
    private OrganizationService organizationService;

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
    public Response<Area> makeESDb() throws Exception{
        String indexUrl = "http://" + esearchServer + "/" + IBaseMicroConstants.INDEX_AREA;

        try{
            HttpResponse response;
            HttpClient httpClient = new DefaultHttpClient();
            HttpDelete httpDelete = new HttpDelete(indexUrl);
            response = httpClient.execute(httpDelete);
        }
        catch (Exception e){
            return new Response<>(IStateCode.HTTP_404, IBaseMicroConstants.ES_SERVER_ERROR);
        }

        String[] keys = {"areaid", "areaname", "parentareaid"};

        int i, len = keys.length;

        JSONObject fields = new JSONObject();
        JSONObject properties = new JSONObject();
        JSONObject areas = new JSONObject();
        JSONObject mapping = new JSONObject();

        for (i = 0; i < len; i++)
        {
            JSONObject item = new JSONObject();

            item.put("type", "text");
            item.put("fields", getKeyword());
            item.put("search_analyzer", "ik_smart");
            item.put("analyzer", "ik_smart");

            fields.put(keys[i], item);
        }

        properties.put("properties", fields);
        areas.put(IBaseMicroConstants.TYPE_AREA, properties);
        mapping.put("mappings", areas);

        HttpClient httpClient = new DefaultHttpClient();
        HttpPut httpPut = new HttpPut(indexUrl);

        StringEntity paramEntity = new StringEntity(mapping.toJSONString(), ContentType.APPLICATION_JSON);
        httpPut.setEntity(paramEntity);
        httpPut.setHeader("Content-Type", "application/json");
        httpClient.execute(httpPut);

        return new Response<>();
    }

    @Override
    public Response<Area> batchSaveToES(@RequestBody QueryTree queryTree){
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

        List<Area> dataList = getList(resQuery).getPageInfo().getList();

        if (!CollectionUtils.isEmpty(dataList)) {
            for (Area item : dataList) {
                try {
                    saveToES(item);
                }
                catch (Exception e) {
                    return new Response<>(IStateCode.PARAMETER_IS_INVALID, e.getMessage());
                }
            }
        }
        else
            return new Response<>(IStateCode.PARAMETER_IS_INVALID, "");

        return new Response<>();
    }

    @Override
    public Response<Area> saveToES(@RequestBody Area entity) throws IOException {
        JSONObject json = new JSONObject();

        json.put("areaid", entity.getAreaid());
        json.put("areaname", entity.getAreaname());
        json.put("parentareaid", entity.getParentareaid());

        HttpPut httpPut = new HttpPut("http://" + esearchServer + "/" + IBaseMicroConstants.INDEX_AREA + "/" + IBaseMicroConstants.TYPE_AREA + "/" + entity.getAreaid());
        HttpClient httpClient = new DefaultHttpClient();

        StringEntity paramEntity = new StringEntity(json.toJSONString(), ContentType.APPLICATION_JSON);
        httpPut.setEntity(paramEntity);
        httpPut.setHeader("Content-Type", "application/json");
        httpPut.setHeader("Accept", "application/json");
        httpClient.execute(httpPut);

        return new Response<>();
    }

    @Override
    public Response<Area> removeFromES(@PathVariable(value = "id") String id) {
        HttpDelete httpDelete = new HttpDelete("http://" + esearchServer + "/" + IBaseMicroConstants.INDEX_AREA + "/" + IBaseMicroConstants.TYPE_AREA + "/" + id);
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
    public Response<Area> removeFromES(@RequestBody  Area entity) {
        HttpDelete httpDelete = new HttpDelete("http://" + esearchServer + "/" + IBaseMicroConstants.INDEX_AREA + "/" + IBaseMicroConstants.TYPE_AREA + "/" + entity.getAreaid());
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

    public void makeChildren(AreaTree parent)
    {
        List<Area> children = areaService.getChildrenArea(parent.getAreaId());
        for(Area child : children)
        {
            AreaTree childTree = new AreaTree();
            childTree.setAreaId(child.getAreaid());
            childTree.setLabel(child.getAreaname());
            parent.getChildren().add(childTree);
            makeChildren(childTree);
        }
    }

    @Override
    public Response<AreaTree> getAreaTree(@RequestBody QueryTree queryTree) {
        List<Area> areas = areaService.getAllData(queryTree);
        List<AreaTree> result = new ArrayList<>();
        for(Area area : areas)
        {
            AreaTree tree = new AreaTree();
            tree.setLabel(area.getAreaname());
            tree.setAreaId(area.getAreaid());
            if (area.getParentareaid().equals("-1")) {
                result.add(tree);
                makeChildren(tree);
            }
        }

        return new Response<>(result);
    }

    @Override
    public Response<Area> getList(@RequestBody QueryTree queryTree) {
        return areaService.search(queryTree);
    }

    @Override
    public Response<UserLoginInfo> getUsers(@PathVariable(value = "areaid") String areaid) {
        List<UserLoginInfo> userList = userService.getList();
        List<UserLoginInfo> myUsers = new ArrayList<>();

        for(UserLoginInfo user : userList)
        {
            Organization org = organizationService.selectByKey(user.getOrgId());
            if (org == null) continue;

            Area subArea = areaService.selectByKey(org.getAreaid());
            if (subArea == null) continue;;

            if(areaService.isAncestor(areaid, subArea.getAreaid()))
                myUsers.add(user);
        }

        return new Response<>(myUsers);
    }

    @Override
    public Response<Boolean> getAreaRelation(@RequestBody Map<String, Object> param) {
        boolean result;

        if (param.get("bigareaid") == null || param.get("smallareaid") == null)
            result = false;
        else {
            String bigArea = param.get("bigareaid").toString();
            String smallArea = param.get("smallareaid").toString();


            if (bigArea == null || smallArea == null)
                result = false;
            else if (bigArea.equals(smallArea))
                result = true;
            else
                result = areaService.isAncestor(bigArea, smallArea);
        }
        return new Response<>(result);
    }

    @Override
    public Response<Area> getAreasByParent(@PathVariable(value = "parentid") String parentid) {
        return areaService.getAreaByParent(parentid);
    }

    @Override
    public Response<Area> getParentAreaId(@PathVariable(value = "areaid") String areaid) {

        return areaService.getParentAreaId(areaid);
    }

    @Override
    public Response<Area> getAreaInfoList() {
        List<Area> areaList = areaService.getAreaByParent("-1").getPageInfo().getList();
        if (!CollectionUtils.isEmpty(areaList)) {
            for (Area area : areaList) {
                List<String> userids = userService.getByArea(area.getAreaid());
                area.setUserids(userids);
            }
        }
        return new Response<>(areaList);
    }
}
