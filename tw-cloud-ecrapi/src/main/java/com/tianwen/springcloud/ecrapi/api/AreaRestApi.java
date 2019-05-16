package com.tianwen.springcloud.ecrapi.api;

import com.tianwen.springcloud.commonapi.base.response.Response;
import com.tianwen.springcloud.commonapi.query.QueryTree;
import com.tianwen.springcloud.microservice.base.entity.Area;
import com.tianwen.springcloud.microservice.base.entity.AreaTree;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Map;

public interface AreaRestApi{
    @RequestMapping(value = "/getAreaList", method = RequestMethod.POST)
    public Response<AreaTree> getAreaList(@RequestBody QueryTree queryTree);

    @RequestMapping(value = "/getAreasByParent/{parentareaid}", method = RequestMethod.GET)
    public Response<Area> getAreasByParent(@PathVariable(value = "parentareaid") String parentareaid);

    @RequestMapping(value = "/getParentAreaId/{areaid}", method = RequestMethod.GET)
    public Response<Area> getParentAreaId(@PathVariable(value = "areaid") String areaid);

    @RequestMapping(value = "/getAreaByUser/{userid}", method = RequestMethod.GET)
    public Response<Map<String, Object>> getAreaByUser(@PathVariable(value = "userid")String userid);
}