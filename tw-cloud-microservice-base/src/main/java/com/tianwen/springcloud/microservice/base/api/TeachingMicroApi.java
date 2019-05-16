package com.tianwen.springcloud.microservice.base.api;

import com.tianwen.springcloud.commonapi.base.ICRUDMicroApi;
import com.tianwen.springcloud.commonapi.base.response.Response;
import com.tianwen.springcloud.commonapi.query.QueryTree;
import com.tianwen.springcloud.microservice.base.entity.Teaching;
import org.springframework.cloud.netflix.feign.FeignClient;
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
@FeignClient(value = "base-service", url = "http://localhost:2226/base-service/teaching")
public interface TeachingMicroApi extends ICRUDMicroApi<Teaching>
{
    @RequestMapping(value = "/getTeachingList", method = RequestMethod.POST)
    public Response<Teaching> getTeachingList(@RequestBody QueryTree queryTree);

    @RequestMapping(value = "/insertTeaching", method = RequestMethod.POST)
    public Response<Teaching> insertTeaching(@RequestBody Teaching entity);

    @RequestMapping(value = "/removeTeaching", method = RequestMethod.POST)
    public Response<Teaching> removeTeaching(@RequestBody Teaching entity);
}
