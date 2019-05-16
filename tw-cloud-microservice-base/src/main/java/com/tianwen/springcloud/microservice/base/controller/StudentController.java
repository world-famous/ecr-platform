package com.tianwen.springcloud.microservice.base.controller;

import com.tianwen.springcloud.commonapi.base.response.Response;
import com.tianwen.springcloud.commonapi.query.QueryTree;
import com.tianwen.springcloud.datasource.base.AbstractCRUDController;
import com.tianwen.springcloud.microservice.base.api.StudentMicroApi;
import com.tianwen.springcloud.microservice.base.api.TeacherMicroApi;
import com.tianwen.springcloud.microservice.base.entity.Student;
import com.tianwen.springcloud.microservice.base.entity.Teacher;
import com.tianwen.springcloud.microservice.base.service.StudentService;
import com.tianwen.springcloud.microservice.base.service.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/student")
public class StudentController extends AbstractCRUDController<Student> implements StudentMicroApi
{
    @Autowired
    private StudentService studentService;

    @Override
    public Response<Student> getStudentList(@RequestBody QueryTree queryTree) {

        return  studentService.search(queryTree);
    }

    @Override
    public void validate(MethodType methodType, Object p) {}
}
