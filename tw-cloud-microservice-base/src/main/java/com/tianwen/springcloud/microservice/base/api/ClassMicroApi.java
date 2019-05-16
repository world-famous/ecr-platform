package com.tianwen.springcloud.microservice.base.api;

import com.tianwen.springcloud.commonapi.base.ICRUDMicroApi;
import com.tianwen.springcloud.commonapi.base.response.Response;
import com.tianwen.springcloud.commonapi.query.QueryTree;
import com.tianwen.springcloud.microservice.base.entity.ClassInfo;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * 用户相关对外接口
 * 
 * @author wangbin
 * @version [版本号, 2017年5月11日]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */
@FeignClient(value = "base-service", url = "http://localhost:2226/base-service/class")
public interface ClassMicroApi extends ICRUDMicroApi<ClassInfo>
{
    @RequestMapping(value = "/getClassList", method = RequestMethod.POST)
    public Response<ClassInfo> getClassList(@RequestBody QueryTree queryTree);

    @RequestMapping(value = "/getClassById/{id}", method = RequestMethod.POST)
    public Response<ClassInfo> getClassById(@PathVariable(value = "id") String id);

    @RequestMapping(value = "/insertClass", method = RequestMethod.POST)
    public Response<ClassInfo> insertClass(@RequestBody ClassInfo entity);

    @RequestMapping(value = "/removeClass/{classid}", method = RequestMethod.GET)
    public Response<ClassInfo> removeClass(@PathVariable(value = "classid") String classid);
}
