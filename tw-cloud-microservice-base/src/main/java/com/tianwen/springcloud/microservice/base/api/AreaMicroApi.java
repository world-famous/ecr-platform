package com.tianwen.springcloud.microservice.base.api;

import com.tianwen.springcloud.commonapi.base.ICRUDMicroApi;
import com.tianwen.springcloud.commonapi.base.response.Response;
import com.tianwen.springcloud.commonapi.query.QueryTree;
import com.tianwen.springcloud.microservice.base.entity.Area;
import com.tianwen.springcloud.microservice.base.entity.AreaTree;
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
@FeignClient(value = "base-service", url = "http://localhost:2226/base-service/area")
public interface AreaMicroApi extends ICRUDMicroApi<Area>
{
    @RequestMapping(value = "/makeESDb", method = RequestMethod.GET)
    public Response<Area> makeESDb() throws Exception;

    @RequestMapping(value = "/batchSaveToES", method = RequestMethod.POST)
    public Response<Area> batchSaveToES(@RequestBody QueryTree queryTree);

    @RequestMapping(value = "/saveToES", method = RequestMethod.POST)
    public Response<Area> saveToES(@RequestBody Area entity) throws IOException;

    @RequestMapping(value = "/deleteFromES", method = RequestMethod.POST)
    public Response<Area> removeFromES(@RequestBody Area entity);

    @RequestMapping(value = "/deleteFromES/{id}", method = RequestMethod.GET)
    public Response<Area> removeFromES(@PathVariable(value = "id") String id);

    @RequestMapping(value = "/getAreaTree", method = RequestMethod.POST)
    public Response<AreaTree> getAreaTree(@RequestBody QueryTree queryTree);

    @RequestMapping(value = "/getList", method = RequestMethod.POST)
    public Response<Area> getList(@RequestBody QueryTree queryTree);

    @RequestMapping(value = "/getUsers/{areaid}", method = RequestMethod.GET)
    public Response<UserLoginInfo> getUsers(@PathVariable(value = "areaid") String areaid);

    @RequestMapping(value = "/getAreaRelation", method = RequestMethod.POST)
    public Response<Boolean> getAreaRelation(@RequestBody Map<String, Object> param);

    @RequestMapping(value = "/getAreasByParent/{parentid}", method = RequestMethod.GET)
    public Response<Area> getAreasByParent(@PathVariable (value = "parentid") String parentid);

    @RequestMapping(value = "/getParentAreaId/{areaid}", method = RequestMethod.GET)
    public Response<Area> getParentAreaId(@PathVariable(value = "areaid") String areaid);

    @RequestMapping(value = "/getAreaInfoList", method = RequestMethod.GET)
    public Response<Area> getAreaInfoList();
}
