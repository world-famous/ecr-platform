package com.tianwen.springcloud.microservice.base.api;

import com.tianwen.springcloud.commonapi.base.ICRUDMicroApi;
import com.tianwen.springcloud.commonapi.base.response.Response;
import com.tianwen.springcloud.commonapi.query.QueryTree;
import com.tianwen.springcloud.microservice.base.entity.*;
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
@FeignClient(value = "base-service", url = "http://localhost:2226/base-service/dictitem")
public interface DictItemMicroApi extends ICRUDMicroApi<DictItem>
{
    @RequestMapping(value = "/makeESDb", method = RequestMethod.GET)
    public Response<DictItem> makeESDb() throws Exception;

    @RequestMapping(value = "/batchSaveToES", method = RequestMethod.POST)
    public Response<DictItem> batchSaveToES(@RequestBody QueryTree queryTree);

    @RequestMapping(value = "/saveToES", method = RequestMethod.POST)
    public Response<DictItem> saveToES(@RequestBody DictItem entity);

    @RequestMapping(value = "/deleteFromES", method = RequestMethod.POST)
    public Response<DictItem> removeFromES(@RequestBody DictItem entity);

    @RequestMapping(value = "/getESearchInfo", method = RequestMethod.POST)
    Response<ESearchInfo> getESearchInfo(@RequestBody Map<String, Object> param);

    @RequestMapping(value = "/getByDictInfo", method = RequestMethod.POST)
    Response<DictItem> getByDictInfo(@RequestBody DictItem dictItem);

    @RequestMapping(value = "/getList", method = RequestMethod.POST)
    public Response<DictItem> getList(@RequestBody QueryTree queryTree);

    @RequestMapping(value = "/getCount", method = RequestMethod.POST)
    public Response<Integer> getCount(@RequestBody QueryTree queryTree);

    @RequestMapping(value = "/insert", method = RequestMethod.POST)
    public Response<DictItem> insert(@RequestBody DictItem entity);

    @RequestMapping(value = "/modify", method = RequestMethod.POST)
    public Response<DictItem> modify(@RequestBody DictItem entity);

    @RequestMapping(value = "/remove/{dictid}", method = RequestMethod.GET)
    public Response<DictItem> remove(@PathVariable(value = "dictid") String dictid);
}
