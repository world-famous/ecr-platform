package com.tianwen.springcloud.ecrapi.api;

import com.tianwen.springcloud.commonapi.base.response.Response;
import com.tianwen.springcloud.microservice.activity.entity.UserActivityCountInfo;
import com.tianwen.springcloud.microservice.base.entity.UserLoginInfo;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * ECRApi对外接口
 * 
 * @author wangbin
 * @version [版本号, 2017年5月11日]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */

public interface UserRestApi
{
    @RequestMapping(value = "/requestCaptcha", method = RequestMethod.GET)
    public Response<Map<String, Object>> requestCaptcha() throws Exception;

    @RequestMapping(value = "/getTeacherById/{userid}", method = RequestMethod.GET)
    public Response getTeacherById(@PathVariable(value = "userid") String userid);

    @RequestMapping(value = "/autoLogin", method = RequestMethod.GET)
    public Response autoLogin(@RequestHeader(value = "token") String token);

    @RequestMapping(value = "/loginUser", method = RequestMethod.POST)
    public Response<UserLoginInfo> loginUser(@RequestBody  Map<String, Object> param) throws Exception;

    @RequestMapping(value = "/loginSSO", method = RequestMethod.POST)
    public Response<UserLoginInfo> loginSSO(@RequestBody Map<String, Object> param);

    @RequestMapping(value = "/logoutUser", method = RequestMethod.GET)
    public Response logoutUser(@RequestHeader(value = "token") String  token);

    @RequestMapping(value = "/getUserInfo/{userid}", method = RequestMethod.GET)
    public Response<UserLoginInfo> getUserInfo(@PathVariable(value = "userid") String userid);

    @RequestMapping(value = "/getUserCountInfo/{userid}", method = RequestMethod.GET)
    Response<UserActivityCountInfo> getUserCountInfo(@PathVariable(value = "userid") String userid);

    @RequestMapping(value = "/changePassword", method = RequestMethod.POST)
    public Response<UserLoginInfo> changePassword(@RequestBody Map<String, Object> param);

    @RequestMapping(value = "/updateUser", method = RequestMethod.POST)
    public Response updateUser(@RequestBody UserLoginInfo entity, @RequestHeader(value = "token") String token);

    @RequestMapping(value = "/resetPassword", method = RequestMethod.POST)
    public Response<UserLoginInfo> resetPassword(@RequestBody Map<String, Object> param);

    @RequestMapping(value = "/getResourceInfo", method = RequestMethod.GET)
    public Response getResourceInfo(@RequestHeader(value = "token") String token);
}

