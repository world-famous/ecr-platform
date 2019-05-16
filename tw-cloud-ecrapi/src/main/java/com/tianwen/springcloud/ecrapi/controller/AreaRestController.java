package com.tianwen.springcloud.ecrapi.controller;

import com.tianwen.springcloud.commonapi.base.response.Response;
import com.tianwen.springcloud.commonapi.log.SystemControllerLog;
import com.tianwen.springcloud.commonapi.query.QueryTree;
import com.tianwen.springcloud.ecrapi.api.AreaRestApi;
import com.tianwen.springcloud.microservice.base.entity.Area;
import com.tianwen.springcloud.microservice.base.entity.AreaTree;
import com.tianwen.springcloud.microservice.base.entity.Organization;
import com.tianwen.springcloud.microservice.base.entity.UserLoginInfo;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(value = "/area")
public  class AreaRestController extends BaseRestController implements AreaRestApi {
    @Override
    @ApiOperation(value = "获取区域列表", notes = "获取区域列表")
    @ApiImplicitParam(name = "queryTree", value = "搜索条件树", required = true, dataType = "Querytree", paramType = "body")
    @SystemControllerLog(description = "获取区域列表")
    public Response<AreaTree> getAreaList(@RequestBody QueryTree queryTree) {
        return areaMicroApi.getAreaTree(queryTree);
    }

    @Override
    @ApiOperation(value = "根据父母id获取区域列表", notes = "根据父母id获取区域列表")
    @ApiImplicitParam(name = "id", value = "父母id", required = true, dataType = "String", paramType = "path")
    @SystemControllerLog(description = "根据父母id获取区域列表")
    public Response<Area> getAreasByParent(@PathVariable(value = "parentareaid") String parentareaid) {

        return areaMicroApi.getAreasByParent(parentareaid);
    }

    @Override
    @ApiOperation(value = "根据父母id获取区域列表", notes = "根据父母id获取区域列表")
    @ApiImplicitParam(name = "id", value = "父母id", required = true, dataType = "String", paramType = "path")
    @SystemControllerLog(description = "根据父母id获取区域列表")
    public Response <Area> getParentAreaId(@PathVariable(value = "areaid") String areaid){

        return areaMicroApi.getParentAreaId(areaid);
    }

    @Override
    @ApiOperation(value = "根据用户id获取区域情报", notes = "根据用户id获取区域情报")
    @ApiImplicitParam(name = "id", value = "用户id", required = true, dataType = "String", paramType = "path")
    @SystemControllerLog(description = "根据用户id获取区域情报")
    public Response<Map<String, Object>> getAreaByUser(@PathVariable(value = "userid") String userid) {
        Map<String, Object> map = new HashMap<>();
        UserLoginInfo userLoginInfo = userMicroApi.get(userid).getResponseEntity();
        Organization organization = organizationMicroApi.get(userLoginInfo.getOrgId()).getResponseEntity();
        map.put("orgname", organization.getOrgname());
        Area district = areaMicroApi.get(organization.getAreaid()).getResponseEntity();
        map.put("district", district.getAreaname());
        Area city = areaMicroApi.get(district.getParentareaid()).getResponseEntity();
        map.put("city", city.getAreaname());
        Area castle = areaMicroApi.get(city.getParentareaid()).getResponseEntity();
        map.put("castle", castle.getAreaname());
        return new Response<>(map);
    }
}
