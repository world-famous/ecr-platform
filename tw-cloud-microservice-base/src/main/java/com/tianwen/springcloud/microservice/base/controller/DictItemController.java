package com.tianwen.springcloud.microservice.base.controller;

import com.alibaba.fastjson.JSONObject;
import com.tianwen.springcloud.commonapi.base.response.Response;
import com.tianwen.springcloud.commonapi.constant.IStateCode;
import com.tianwen.springcloud.commonapi.log.SystemControllerLog;
import com.tianwen.springcloud.commonapi.query.QueryCondition;
import com.tianwen.springcloud.commonapi.query.QueryTree;
import com.tianwen.springcloud.datasource.base.AbstractCRUDController;
import com.tianwen.springcloud.microservice.base.api.AreaMicroApi;
import com.tianwen.springcloud.microservice.base.api.DictItemMicroApi;
import com.tianwen.springcloud.microservice.base.constant.IBaseMicroConstants;
import com.tianwen.springcloud.microservice.base.entity.*;
import com.tianwen.springcloud.microservice.base.service.AreaService;
import com.tianwen.springcloud.microservice.base.service.DictItemService;
import com.tianwen.springcloud.microservice.base.service.OrganizationService;
import com.tianwen.springcloud.microservice.base.service.UserService;
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
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/dictitem")
public class DictItemController extends AbstractCRUDController<DictItem> implements DictItemMicroApi
{
    @Autowired
    private DictItemService dictItemService;

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

    @Override
    public Response<DictItem> makeESDb() throws Exception{
        String indexUrl = "http://" + esearchServer + "/" + IBaseMicroConstants.INDEX_DICT;

        try{
            HttpResponse response;
            HttpClient httpClient = new DefaultHttpClient();
            HttpDelete httpDelete = new HttpDelete(indexUrl);
            response = httpClient.execute(httpDelete);
        }
        catch (Exception e){
            e.printStackTrace();
        }

        String[] keys = {"dictid", "dictvalue", "dicttypeid", "dictname", "status", "sortno", "parentdictid", "remark", "iseditable", "lastmodifytime", "lang"};
        String[] types = {"text", "text", "text", "text", "text", "long", "text", "text", "text", "date", "text"};

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
        type.put(IBaseMicroConstants.TYPE_DICT, properties);
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
    public Response<DictItem> batchSaveToES(@RequestBody QueryTree queryTree) {
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

        List<DictItem> dataList = dictItemService.search(resQuery).getPageInfo().getList();

        if (!CollectionUtils.isEmpty(dataList)) {
            for (DictItem item : dataList)
                saveToES(item);
        }
        else
            return new Response<>(IStateCode.PARAMETER_IS_INVALID, "");

        return new Response<>();
    }

    @Override
    public Response<DictItem> saveToES(@RequestBody DictItem entity) {
        JSONObject json = new JSONObject();

        json.put("dictid", entity.getDictid());
        json.put("dictvalue", entity.getDictvalue());
        json.put("dicttypeid", entity.getDicttypeid());
        json.put("dictname", entity.getDictname());
        json.put("status", entity.getStatus());
        json.put("sortno", entity.getSortno());
        json.put("parentdictid", entity.getParentdictid());
        json.put("remark", entity.getRemark());
        json.put("iseditable", entity.getIseditable());
        if (entity.getLastmodifytime() != null)
            json.put("lastmodifytime", DateUtils.formatDate(entity.getLastmodifytime(), "yyyy-MM-dd HH:mm:ss"));
        json.put("lang", entity.getLang());

        HttpPut httpPut = new HttpPut("http://" + esearchServer + "/" + IBaseMicroConstants.INDEX_DICT + "/" + IBaseMicroConstants.TYPE_DICT + "/" + entity.getDictid() + "_" + entity.getLang());
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
    public Response<DictItem> removeFromES(@RequestBody DictItem entity) {
        HttpDelete httpDelete = new HttpDelete("http://" + esearchServer + "/" + IBaseMicroConstants.INDEX_DICT + "/" + IBaseMicroConstants.TYPE_DICT + "/" + entity.getDictid() + "_" + entity.getLang());
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
    public Response<ESearchInfo> getESearchInfo(@RequestBody Map<String, Object> param) {
        ESearchInfo esInfo = dictItemService.getESearchInfo(param);
        return new Response<>(esInfo);
    }

    @Override
    public Response<DictItem> getByDictInfo(@RequestBody DictItem dictItem) {
        return dictItemService.getByDictInfo(dictItem);
    }

    @Override
    public Response<DictItem> getList(@RequestBody QueryTree queryTree) {
        return dictItemService.search(queryTree);
    }

    @Override
    public Response<Integer> getCount(@RequestBody QueryTree queryTree) {
        int count = dictItemService.getCount(queryTree);
        return new Response<>(count);
    }

    @Override
    public Response<DictItem> insert(@RequestBody DictItem entity) {
        String maxId = dictItemService.getMaxId();
        maxId = dictItemService.increment(maxId);

        DictItem zhCn = new DictItem();
        // parameter data
        zhCn.setDictname(entity.getDictname());
        zhCn.setDictvalue(entity.getDictvalue());
        zhCn.setRemark(entity.getRemark());
        zhCn.setParentdictid(entity.getParentdictid());
        zhCn.setSortno(entity.getSortno());
        zhCn.setIseditable(entity.getIseditable());
        //???
        zhCn.setDicttypeid(entity.getDicttypeid());
        // self-calc value
        zhCn.setStatus("1");
        zhCn.setLang(IBaseMicroConstants.zh_CN);
        zhCn.setLastmodifytime(new Timestamp(System.currentTimeMillis()));
        zhCn.setDictid(maxId);
        dictItemService.save(zhCn);

        DictItem enUs = new DictItem();
        // parameter data
        enUs.setDictname(entity.getDictname());
        enUs.setDictvalue(entity.getDictvalue());
        enUs.setRemark(entity.getRemark());
        enUs.setParentdictid(entity.getParentdictid());
        enUs.setSortno(entity.getSortno());
        enUs.setIseditable(entity.getIseditable());
        //???
        enUs.setDicttypeid(entity.getDicttypeid());
        // self-calc value
        enUs.setStatus("1");
        enUs.setLang(IBaseMicroConstants.en_US);
        enUs.setLastmodifytime(new Timestamp(System.currentTimeMillis()));
        enUs.setDictid(maxId);
        dictItemService.save(enUs);

        saveToES(zhCn);saveToES(enUs);

        return new Response<>();
    }

    @Override
    public Response<DictItem> modify(@RequestBody DictItem entity) {
        entity.setLastmodifytime(new Timestamp(System.currentTimeMillis()));
        dictItemService.doUpdate(entity);
        saveToES(entity);
        return new Response<>(entity);
    }

    @Override
    public Response<DictItem> remove(@PathVariable(value = "dictid") String dictid) {
        QueryTree queryTree = new QueryTree();
        queryTree.addCondition(new QueryCondition("dictid", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, dictid));
        List<DictItem> dictItems = search(queryTree).getPageInfo().getList();
        if (dictItems != null)
            for (DictItem item : dictItems){
                removeFromES(item);
            }

        dictItemService.doRemove(dictid);

        return new Response<>();
    }
}
