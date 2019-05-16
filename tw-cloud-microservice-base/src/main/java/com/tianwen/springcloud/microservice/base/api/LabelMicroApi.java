package com.tianwen.springcloud.microservice.base.api;

import com.tianwen.springcloud.commonapi.base.ICRUDMicroApi;
import com.tianwen.springcloud.commonapi.base.response.Response;
import com.tianwen.springcloud.commonapi.query.QueryTree;
import com.tianwen.springcloud.microservice.base.entity.Catalog;
import com.tianwen.springcloud.microservice.base.entity.Label;
import com.tianwen.springcloud.microservice.base.entity.LabelTree;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Map;

/**
 * 用户相关对外接口
 * 
 * @author wangbin
 * @version [版本号, 2017年5月11日]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */
@FeignClient(value = "base-service", url = "http://localhost:2226/base-service/label")
public interface LabelMicroApi extends ICRUDMicroApi<Label>
{
    @RequestMapping(value = "/makeESDb", method = RequestMethod.GET)
    public Response<Label> makeESDb() throws Exception;

    @RequestMapping(value = "/batchSaveToES", method = RequestMethod.POST)
    public Response<Label> batchSaveToES(@RequestBody QueryTree queryTree);

    @RequestMapping(value = "/saveToES", method = RequestMethod.POST)
    public Response<Label> saveToES(@RequestBody Label entity);

    @RequestMapping(value = "/deleteFromES", method = RequestMethod.POST)
    public Response<Label> removeFromES(@RequestBody Label entity);

    @RequestMapping(value = "/deleteFromES/{id}", method = RequestMethod.GET)
    public Response<Label> removeFromES(@PathVariable(value = "id") String id);

    @RequestMapping(value = "/getLabelTree", method = RequestMethod.POST)
    public Response<LabelTree> getLabelTree(@RequestBody QueryTree queryTree);

    @RequestMapping(value = "/getList", method = RequestMethod.POST)
    public Response<Label> getList(@RequestBody QueryTree queryTree);

    @RequestMapping(value = "/get/{id}", method = RequestMethod.GET)
    public Response<Label> get(@PathVariable(value = "id") String id);

    @RequestMapping(value = "/delete/{id}", method = RequestMethod.GET)
    public Response<Label> delete(@PathVariable(value = "id") String id);

    @RequestMapping(value = "/insert", method = RequestMethod.POST)
    public Response<Label> insert(@RequestBody Label entity);

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public Response<Label> update(@RequestBody Label entity);

    @RequestMapping(value = "/move", method = RequestMethod.POST)
    public Response<Label> move(@RequestBody Map<String, Object> param);

    @RequestMapping(value = "/getChildrenByParent", method = RequestMethod.POST)
    public Response<Label> getChildrenByParent(@RequestBody Map<String, Object> param);

    @RequestMapping(value = "/getOneLabelList", method = RequestMethod.POST)
    public Response<Label> getOneLabelList(@RequestBody Map<String, Object> param);

    @RequestMapping(value = "/getTwoLabelList", method = RequestMethod.POST)
    public Response<Label> getTwoLabelList(@RequestBody Map<String, Object> param);

    @RequestMapping(value = "/getThreeLabelList", method = RequestMethod.POST)
    public Response<Label> getThreeLabelList(@RequestBody Map<String, Object> param);

    @RequestMapping(value = "/getLabelStructure", method = RequestMethod.POST)
    public Response<Map<String, Object>> getLabelStructure(@RequestBody Map<String ,Object> labelData);

    @RequestMapping(value = "/getByExample", method = RequestMethod.POST)
    Response<Label> getByExample(@RequestBody Label example);

    @RequestMapping(value = "/getOneLabelIds", method = RequestMethod.POST)
    Response<String> getOneLabelIds(@RequestBody QueryTree queryTree);
}
