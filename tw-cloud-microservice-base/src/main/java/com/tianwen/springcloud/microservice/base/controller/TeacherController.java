package com.tianwen.springcloud.microservice.base.controller;

import com.tianwen.springcloud.commonapi.base.response.Response;
import com.tianwen.springcloud.commonapi.query.QueryCondition;
import com.tianwen.springcloud.commonapi.query.QueryTree;
import com.tianwen.springcloud.datasource.base.AbstractCRUDController;
import com.tianwen.springcloud.microservice.base.api.TeacherMicroApi;
import com.tianwen.springcloud.microservice.base.entity.Teacher;
import com.tianwen.springcloud.microservice.base.service.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/teacher")
public class TeacherController extends AbstractCRUDController<Teacher> implements TeacherMicroApi
{
    @Autowired
    private TeacherService teacherService;

    @Override
    public Response<Teacher> getTeacherList(@RequestBody QueryTree queryTree) {
        return  teacherService.getTeacherList(queryTree) ;
    }

    @Override
    public Response<Teacher> getByUserid(@PathVariable(value = "userid") String userid) {
        QueryTree queryTree = new QueryTree();
        queryTree.getConditions().add(new QueryCondition("userid", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, userid));
        List<Teacher> teacherList = teacherService.getTeacherList(queryTree).getPageInfo().getList();
        Teacher teacher = null;
        if (!CollectionUtils.isEmpty(teacherList))
            teacher = teacherList.get(0);

        return new Response<>(teacher);
    }

    @Override
    public Response<String> getTeacherIds(@RequestBody QueryTree queryTree) {
        return teacherService.getTeacherIds(queryTree);
    }

    @Override
    public Response<Teacher> getNamedTeacherList(@RequestBody QueryTree queryTree) {
        return teacherService.getNamedTeacherList(queryTree);
    }

    @Override
    public Response<Integer> getOrdernoByUserid(@PathVariable(value = "userid") String userid) {
        Integer integer = get(userid).getResponseEntity().getOrderno();
        return new Response<>(integer);
    }
//    Author : GOD 2019-2-15 Bug ID: #778
    @Override
    public Response<Teacher> setOrdernoByUserid(@RequestBody Teacher teacher) {
        if(teacherService.isValidTeacherOrderno(teacher.getOrderno()) == false) {
            Response<Teacher> resp = new Response<>(teacher);
            resp.getServerResult().setResultCode(Integer.toString(401));
            return resp;
        } else{
            return update(teacher);
        }
    }
//    Author : GOD 2019-2-15 Bug ID: #778
    @Override
    public void validate(MethodType methodType, Object p) {}
}
