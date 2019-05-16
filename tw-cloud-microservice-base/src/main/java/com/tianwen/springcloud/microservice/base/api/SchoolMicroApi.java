package com.tianwen.springcloud.microservice.base.api;

import com.tianwen.springcloud.commonapi.base.ICRUDMicroApi;
import com.tianwen.springcloud.commonapi.base.response.Response;
import com.tianwen.springcloud.commonapi.query.QueryTree;
import com.tianwen.springcloud.microservice.base.entity.DictItem;
import com.tianwen.springcloud.microservice.base.entity.School;
import com.tianwen.springcloud.microservice.base.entity.SchoolType;
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
@FeignClient(value = "base-service", url = "http://localhost:2226/base-service/school")
public interface SchoolMicroApi extends ICRUDMicroApi<School>{

    @RequestMapping(value = "/getSchoolTypeList", method = RequestMethod.GET)
    public Response<SchoolType> getSchoolTypeList();

    @RequestMapping(value = "/getAllSchoolList", method = RequestMethod.POST)
    public Response<School> getAllSchoolList(@RequestBody QueryTree queryTree);

    @RequestMapping(value = "/getSchoolList", method = RequestMethod.POST)
    public Response<School> getSchoolList(@RequestBody QueryTree queryTree);

    @RequestMapping(value = "/getListByAreaAndConType", method = RequestMethod.POST)
    public Response<School> getListByAreaAndConType(@RequestBody QueryTree queryTree);

    @RequestMapping(value = "/getSchoolSectionBySchoolid/{schoolid}",method = RequestMethod.GET)
    public Response<DictItem>getSchoolSectionBySchoolid(@PathVariable (value = "schoolid")String schoolid);

    @RequestMapping(value = "/getOrdernoByOrgid/{orgid}", method = RequestMethod.GET)
    public Response<Integer> getOrdernoByOrgid(@PathVariable (value = "orgid") String orgid);

    @RequestMapping(value = "/setOrdernoByOrgid", method = RequestMethod.POST)
    public Response<School> setOrdernoByOrgid(@RequestBody School school);
}
