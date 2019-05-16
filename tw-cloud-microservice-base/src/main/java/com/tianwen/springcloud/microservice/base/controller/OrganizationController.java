package com.tianwen.springcloud.microservice.base.controller;

import com.alibaba.fastjson.JSONObject;
import com.tianwen.springcloud.commonapi.base.response.Response;
import com.tianwen.springcloud.commonapi.constant.IStateCode;
import com.tianwen.springcloud.commonapi.query.QueryTree;
import com.tianwen.springcloud.datasource.base.AbstractCRUDController;
import com.tianwen.springcloud.microservice.base.api.OrganizationMicroApi;
import com.tianwen.springcloud.microservice.base.constant.IBaseMicroConstants;
import com.tianwen.springcloud.microservice.base.entity.Catalog;
import com.tianwen.springcloud.microservice.base.entity.Organization;
import com.tianwen.springcloud.microservice.base.service.OrganizationService;
import org.apache.commons.collections.functors.ExceptionPredicate;
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

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping(value = "/organization")
public class OrganizationController extends AbstractCRUDController<Organization> implements OrganizationMicroApi
{
    @Value("${ESEARCH_SERVER}")
    private String esearchServer;

    @Autowired
    private OrganizationService orgService;

    @Override
    public void validate(MethodType methodType, Object p) {}

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
    public Response<Organization> makeESDb() throws IOException {
        String indexUrl = "http://" + esearchServer + "/" + IBaseMicroConstants.INDEX_ORG;

        try{
            HttpResponse response;
            HttpClient httpClient = new DefaultHttpClient();
            HttpDelete httpDelete = new HttpDelete(indexUrl);
            response = httpClient.execute(httpDelete);
        }
        catch (Exception e){
            e.printStackTrace();
        }

        String[] keys = {"orgid", "orgcode", "orgname", "parentorgid", "orgtype", "orgaddr", "zipcode", "orgmanager", "linkman", "linktel", "email",
            "weburl", "status", "linenum", "createtime", "lastmodifytime", "description", "creatorid", "lastmodifierid", "areaid", "tenantid", "logofileid",
            "orgdomain", "synurl", "syncode", "title"};
        String[] types = {"text", "text", "text", "text", "text", "text", "text", "text", "text", "text", "text", "text", "text", "long", "date", "date",
                "text", "text", "text", "text", "text", "text", "text", "text", "text", "text"};

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
        type.put(IBaseMicroConstants.TYPE_ORG, properties);
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
    public Response<Organization> batchSaveToES(@RequestBody QueryTree queryTree) {
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

        List<Organization> dataList = orgService.search(resQuery).getPageInfo().getList();

        if (!CollectionUtils.isEmpty(dataList)) {
            for (Organization item : dataList)
                saveToES(item);
        }
        else
            return new Response<>(IStateCode.PARAMETER_IS_INVALID, "");

        return new Response<>();
    }

    @Override
    public Response<Organization> saveToES(@RequestBody Organization entity) {
        JSONObject json = new JSONObject();

        json.put("orgid", entity.getOrgid());
        json.put("orgcode", entity.getOrgcode());
        json.put("orgname", entity.getOrgname());
        json.put("parentorgid", entity.getParentorgid());
        json.put("orgtype", entity.getOrgtype());
        json.put("orgaddr", entity.getOrgaddr());
        json.put("zipcode", entity.getZipcode());
        json.put("orgmanager", entity.getOrgmanager());
        json.put("linkman", entity.getLinkman());
        json.put("linktel", entity.getLinktel());
        json.put("email", entity.getEmail());
        json.put("weburl", entity.getWeburl());
        json.put("status", entity.getStatus());
        json.put("linenum", entity.getLinenum());
        if (entity.getCreatetime() != null)
            json.put("createtime", DateUtils.formatDate(entity.getCreatetime(), "yyyy-MM-dd HH:mm:ss"));
        if (entity.getLastmodifytime() != null)
            json.put("lastmodifytime", DateUtils.formatDate(entity.getLastmodifytime(), "yyyy-MM-dd HH:mm:ss"));
        json.put("description", entity.getDescription());
        json.put("creatorid", entity.getCreatorid());
        json.put("lastmodifierid", entity.getLastmodifierid());
        json.put("areaid", entity.getAreaid());
        json.put("tenantid", entity.getTenantid());
        json.put("logofileid", entity.getLogofileid());
        json.put("orgdomain", entity.getOrgdomain());
        json.put("synurl", entity.getSynurl());
        json.put("syncode", entity.getSyncode());
        json.put("title", entity.getTitle());

        HttpPut httpPut = new HttpPut("http://" + esearchServer + "/" + IBaseMicroConstants.INDEX_ORG + "/" + IBaseMicroConstants.INDEX_ORG + "/" + entity.getOrgid());
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
    public Response<Organization> removeFromES(@RequestBody Organization entity) {
        HttpDelete httpDelete = new HttpDelete("http://" + esearchServer + "/" + IBaseMicroConstants.INDEX_ORG + "/" + IBaseMicroConstants.TYPE_ORG + "/" + entity.getOrgid());
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
    public Response<Organization> removeFromES(@PathVariable(value = "id") String id) {
        HttpDelete httpDelete = new HttpDelete("http://" + esearchServer + "/" + IBaseMicroConstants.INDEX_ORG + "/" + IBaseMicroConstants.TYPE_ORG + "/" + id);
        HttpClient httpClient = new DefaultHttpClient();
        try {
            httpClient.execute(httpDelete);
        }
        catch (Exception e){
            return new Response<>(IStateCode.HTTP_404, IBaseMicroConstants.ES_SERVER_ERROR);
        }

        return new Response<>();
    }
}
