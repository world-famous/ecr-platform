package com.tianwen.springcloud.ecrapi.api;

import com.tianwen.springcloud.commonapi.base.response.Response;
import com.tianwen.springcloud.commonapi.query.QueryTree;
import com.tianwen.springcloud.microservice.base.entity.Organization;
import com.tianwen.springcloud.microservice.base.entity.Teacher;
import org.springframework.web.bind.annotation.*;

import java.util.List;

public interface TeacherApi {

    @RequestMapping(value = "/batchRegisterTeacher", method = RequestMethod.POST)
    public Response<Teacher> batchRegisterTeacher(@RequestBody List<Teacher> teacherEntityList);

    @RequestMapping(value = "/getTeacherList", method = RequestMethod.POST)
    public Response<Teacher> getTeacherList(@RequestBody QueryTree queryTree);

    @RequestMapping(value = "/getNamedTeacherList", method = RequestMethod.POST)
    public Response<Teacher> getNamedTeacherList(@RequestBody QueryTree queryTree, @RequestHeader(value = "token")String token);

    @RequestMapping(value = "/batchDeleteTeacher", method = RequestMethod.POST)
    public Response<Teacher> batchDeleteTeacher(@RequestBody List<String> orgidList);

    @RequestMapping(value = "/batchSetTeacherNamed", method = RequestMethod.POST)
    public Response<Teacher> batchSetTeacherNamed(@RequestBody List<Teacher> teacherEntityList);

    @RequestMapping(value = "/batchSetArea2Teacher", method = RequestMethod.POST)
    public Response<Organization> batchSetArea2Teacher(@RequestBody  List<Organization> teacherEntityList, @RequestHeader(value = "token") String token);

   @RequestMapping(value = "/getOrdernoByUserid", method = RequestMethod.GET)
    public Response<Integer> getOrdernoByUserid(@PathVariable (value = "userid") String userid);

    @RequestMapping(value = "/setOrdernoByUserid", method = RequestMethod.POST)
    public Response<Teacher> setOrdernoByUserid(@RequestBody Teacher teacher);

    @RequestMapping(value = "/batchSetSharerange2Teacher", method = RequestMethod.POST)
    public Response<Teacher> batchSetSharerange2Teacher(@RequestBody List<Teacher> teacherList);

    @RequestMapping(value = "/getTeacherDetail/{userid}", method = RequestMethod.GET)
    public Response<Teacher> getTeacherDetail(@PathVariable(value = "userid") String userid);

    @RequestMapping(value = "/getByUserId/{userid}", method = RequestMethod.GET)
    public Response<Teacher> getByUserId(@PathVariable(value = "userid") String userid);
}
