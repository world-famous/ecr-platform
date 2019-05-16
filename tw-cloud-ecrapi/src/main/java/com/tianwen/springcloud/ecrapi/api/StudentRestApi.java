package com.tianwen.springcloud.ecrapi.api;

import com.tianwen.springcloud.commonapi.base.response.Response;
import com.tianwen.springcloud.commonapi.query.QueryTree;
import com.tianwen.springcloud.microservice.base.entity.Organization;
import com.tianwen.springcloud.microservice.base.entity.Student;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

public interface StudentRestApi {

    @RequestMapping(value = "/batchRegisterStudent", method = RequestMethod.POST)
    public Response<Student> batchRegisterStudent(@RequestBody List<Student> studentEntityList);

    @RequestMapping(value = "/getStudentList", method = RequestMethod.POST)
    public Response<Student> getStudentList(@RequestBody QueryTree queryTree);

    @RequestMapping(value = "/batchDeleteStudent", method = RequestMethod.POST)
    public Response<Student> batchDeleteStudent(@RequestBody List<String> orgidList);

    @RequestMapping(value = "/batchSetArea2Student", method = RequestMethod.POST)
    public Response<Organization> batchSetArea2Student(@RequestBody List<Organization> studentEntityList, @RequestHeader(value = "token") String token);

    @RequestMapping(value = "/batchSetSharerange2Student", method = RequestMethod.POST)
    public Response<Student> batchSetSharerange2Student(@RequestBody List<Student> studentList);
}
