package com.tianwen.springcloud.microservice.base.api;

import com.tianwen.springcloud.commonapi.base.ICRUDMicroApi;
import com.tianwen.springcloud.commonapi.base.response.Response;
import com.tianwen.springcloud.commonapi.query.QueryTree;
import com.tianwen.springcloud.microservice.base.entity.Catalog;
import com.tianwen.springcloud.microservice.base.entity.Organization;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.IOException;

/**
 * 用户相关对外接口
 * 
 * @author wangbin
 * @version [版本号, 2017年5月11日]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */
@FeignClient(value = "base-service", url = "http://localhost:2226/base-service/organization")
public interface OrganizationMicroApi extends ICRUDMicroApi<Organization>{
    @RequestMapping(value = "/makeESDb", method = RequestMethod.GET)
    public Response<Organization> makeESDb() throws IOException;

    @RequestMapping(value = "/batchSaveToES", method = RequestMethod.POST)
    public Response<Organization> batchSaveToES(@RequestBody QueryTree queryTree);

    @RequestMapping(value = "/saveToES", method = RequestMethod.POST)
    public Response<Organization> saveToES(@RequestBody Organization entity);

    @RequestMapping(value = "/deleteFromES", method = RequestMethod.POST)
    public Response<Organization> removeFromES(@RequestBody Organization entity);

    @RequestMapping(value = "/deleteFromES/{id}", method = RequestMethod.GET)
    public Response<Organization> removeFromES(@PathVariable(value = "id") String id);
}
