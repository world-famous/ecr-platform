package com.tianwen.springcloud.microservice.base.api;

import com.tianwen.springcloud.commonapi.base.ICRUDMicroApi;
import com.tianwen.springcloud.commonapi.base.response.Response;
import com.tianwen.springcloud.commonapi.query.QueryTree;
import com.tianwen.springcloud.microservice.base.entity.Teacher;
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
@FeignClient(value = "base-service", url = "http://localhost:2226/base-service/teacher")
public interface TeacherMicroApi extends ICRUDMicroApi<Teacher>
{
    @RequestMapping(value = "/getTeacherList", method = RequestMethod.POST)
    public Response<Teacher> getTeacherList(@RequestBody QueryTree queryTree);

    @RequestMapping(value = "/getOrdernoByUserid/{userid}", method = RequestMethod.GET)
    public Response<Integer> getOrdernoByUserid(@PathVariable(value = "userid") String userid);

    @RequestMapping(value = "/setOrdernoByUserid", method = RequestMethod.POST)
    public Response<Teacher> setOrdernoByUserid(@RequestBody Teacher teacher);

    @RequestMapping(value = "/getByUserid/{userid}", method = RequestMethod.GET)
    public Response<Teacher> getByUserid(@PathVariable(value = "userid") String userid);

    @RequestMapping(value = "/getTeacherIds", method = RequestMethod.POST)
    public Response<String> getTeacherIds(@RequestBody QueryTree queryTree);

    @RequestMapping(value = "/getNamedTeacherList", method = RequestMethod.POST)
    public Response<Teacher> getNamedTeacherList(@RequestBody QueryTree queryTree);
}
