package com.tianwen.springcloud.microservice.base.api;

import com.tianwen.springcloud.commonapi.base.ICRUDMicroApi;
import com.tianwen.springcloud.commonapi.base.response.Response;
import com.tianwen.springcloud.commonapi.query.QueryTree;
import com.tianwen.springcloud.microservice.base.entity.Role;
import com.tianwen.springcloud.microservice.base.entity.UserLoginInfo;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.IOException;
import java.util.Map;

/**
 * 用户相关对外接口
 * 
 * @author wangbin
 * @version [版本号, 2017年5月11日]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */
@FeignClient(value = "base-service", url = "http://localhost:2226/base-service/user")
public interface UserMicroApi extends ICRUDMicroApi<UserLoginInfo>
{
    @RequestMapping(value = "/makeESDb", method = RequestMethod.GET)
    public Response<UserLoginInfo> makeESDb() throws IOException;

    @RequestMapping(value = "/batchSaveToES", method = RequestMethod.POST)
    public Response<UserLoginInfo> batchSaveToES(@RequestBody QueryTree queryTree);

    @RequestMapping(value = "/saveToES", method = RequestMethod.POST)
    public Response<UserLoginInfo> saveToES(@RequestBody UserLoginInfo entity);

    @RequestMapping(value = "/deleteFromES", method = RequestMethod.POST)
    public Response<UserLoginInfo> removeFromES(@RequestBody UserLoginInfo entity);

    @RequestMapping(value = "/deleteFromES/{id}", method = RequestMethod.GET)
    public Response<UserLoginInfo> removeFromES(@PathVariable(value = "id") String id);

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public Response<UserLoginInfo> add(@RequestBody UserLoginInfo entity);

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public Response<UserLoginInfo> update(@RequestBody UserLoginInfo entity);

    @RequestMapping(value = "/delete/{id}", method = RequestMethod.GET)
    public Response<UserLoginInfo> delete(@PathVariable(value = "id") String id);

    @RequestMapping(value = "/getList", method = RequestMethod.POST)
    public Response<UserLoginInfo> getList(@RequestBody QueryTree queryTree);

    @RequestMapping(value = "/get/{userid}", method = RequestMethod.GET)
    public Response<UserLoginInfo> get(@PathVariable(value = "userid") String userid);

    @RequestMapping(value = "/insert", method = RequestMethod.POST)
    public Response<UserLoginInfo> insert(@RequestBody UserLoginInfo entity);

    @RequestMapping(value = "/changePassword", method = RequestMethod.POST)
    public Response<UserLoginInfo> changePassword(@RequestBody Map<String, Object> param);

    @RequestMapping(value = "/resetPassword", method = RequestMethod.POST)
    public Response<UserLoginInfo> resetPassword(@RequestBody Map<String, Object> param);

    @RequestMapping(value = "/lockUser", method = RequestMethod.POST)
    public Response<UserLoginInfo> lockUser(@RequestBody Map<String, Object> param);

    @RequestMapping(value = "/allowUser", method = RequestMethod.POST)
    public Response<UserLoginInfo> allowUser(@RequestBody Map<String, Object> param);

    @RequestMapping(value = "/registerUser", method = RequestMethod.POST)
    public Response<UserLoginInfo> registerUser(@RequestBody UserLoginInfo entity);

    @RequestMapping(value = "/getByToken/{token}", method = RequestMethod.GET)
    public Response<UserLoginInfo> getByToken(@PathVariable(value = "token") String token);

    @RequestMapping(value = "/getByLoginName/{loginname}", method = RequestMethod.GET)
    public Response<UserLoginInfo> getByLoginName(@PathVariable(value = "loginname") String loginname);

    @RequestMapping(value = "/getByRealName/{realname}", method = RequestMethod.POST)
    public Response<UserLoginInfo> getByRealName(@PathVariable(value = "realname") String realname);

    @RequestMapping(value = "/getByOrg/{orgid}", method = RequestMethod.POST)
    public Response<UserLoginInfo> getByOrg(@PathVariable(value = "orgid") String orgid);

    @RequestMapping(value = "/getByArea/{areaid}", method = RequestMethod.POST)
    public Response<String> getByArea(@PathVariable(value = "areaid") String areaid);

    @RequestMapping(value = "/getUserIdsByQueryTree", method = RequestMethod.POST)
    public Response<String> getUserIdsByQueryTree(@RequestBody QueryTree queryTree);

    //author:han
    @RequestMapping(value = "/getUserIdsByQueryTreeExtra", method = RequestMethod.POST)
    public Response<String> getUserIdsByQueryTreeExtra(@RequestBody QueryTree queryTree);
    //end han

    @RequestMapping(value = "/getUserInfo/{userid}", method = RequestMethod.GET)
    public Response<UserLoginInfo> getUserInfo(@PathVariable(value = "userid") String userid);

    @RequestMapping(value = "/getUserRole/{userid}", method = RequestMethod.GET)
    public Response<Role> getUserRole(@PathVariable(value = "userid") String userid);

    @RequestMapping(value = "/deleteUserRole/{userid}", method = RequestMethod.GET)
    public Response<UserLoginInfo> deleteUserRole(@PathVariable(value = "userid") String userid);

    @RequestMapping(value = "/insertUserRole", method = RequestMethod.POST)
    public Response<UserLoginInfo> insertUserRole(@RequestBody Map<String, Object> param);

    @RequestMapping(value = "/getUserByName", method = RequestMethod.POST)
    public Response<UserLoginInfo> getUserByName(@RequestBody String username);

}
