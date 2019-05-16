package com.tianwen.springcloud.ecrapi.api;

import com.tianwen.springcloud.commonapi.base.response.Response;
import com.tianwen.springcloud.commonapi.query.QueryTree;
import com.tianwen.springcloud.microservice.base.entity.*;
import com.tianwen.springcloud.microservice.bussinessassist.entity.Good;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

public interface SchoolApi {
    @RequestMapping(value = "/getGoodStatus", method = RequestMethod.POST)
    Response getGoodStatus(@RequestBody QueryTree queryTree, @RequestHeader(value = "token") String token);

    @RequestMapping(value = "/exportToExcel", method = RequestMethod.POST)
    Response exportToExcel(@RequestBody QueryTree queryTree, @RequestHeader(value = "token") String token) throws Exception;

    @RequestMapping(value = "/batchRegisterSchoolList", method = RequestMethod.POST)
    public Response<School> batchRegisterSchoolList(@RequestBody List<School> schoolEntityList, @RequestHeader(value = "token") String token);

    @RequestMapping(value = "/getSchoolTypeList", method = RequestMethod.GET)
    public Response<SchoolType> getSchoolTypeList();

    @RequestMapping(value = "/getSchoolList", method = RequestMethod.POST)
    public Response<School> getSchoolList(@RequestBody QueryTree queryTree);

    @RequestMapping(value = "/batchDeleteSchool", method = RequestMethod.POST)
    public Response<School> batchDeleteSchool(@RequestBody List<String> orgidList);

    @RequestMapping(value = "/batchSetSchoolNamed", method = RequestMethod.POST)
    public Response<School> batchSetSchoolNamed(@RequestBody List<School> schoolEntityList, @RequestHeader(value = "token")String token);

    @RequestMapping(value = "/getOrdernoByOrgid", method = RequestMethod.GET)
    public Response<Integer> getOrdernoByOrgid(@PathVariable (value = "userid") String userid);

    @RequestMapping(value = "/setOrdernoByOrgid", method = RequestMethod.POST)
    public Response<School> setOrdernoByOrgid(@RequestBody School school);

    @RequestMapping(value = "/batchSetArea2School", method = RequestMethod.POST)
    public Response<Organization> batchSetArea2School(@RequestBody  List<Organization> schoolEntityList, @RequestHeader(value = "token") String token);

    @RequestMapping(value = "/getNamedSchoolInfoListByAreaId", method = RequestMethod.POST)
    Response<Map<String, Object>> getNamedSchoolInfoListByAreaId(@RequestBody QueryTree queryTree, @RequestHeader(value = "token") String token);

    @RequestMapping(value = "/getGoodsStatisticsBySchoolId/{orgid}", method = RequestMethod.GET)
    Response getGoodsStatisticsBySchoolId(@PathVariable(value = "orgid") String orgid, @RequestHeader(value = "token") String token);

    @RequestMapping(value = "/getGoodsList", method = RequestMethod.POST)
    Response<Good> getGoodsList(@RequestBody QueryTree queryTree, @RequestHeader(value = "token") String token);

    @RequestMapping(value = "/getSchoolSectionBySchoolid/{schoolid}", method = RequestMethod.GET)
    Response<DictItem> getSchoolSectionBySchoolid(@PathVariable(value = "schoolid") String schoolid);
}
