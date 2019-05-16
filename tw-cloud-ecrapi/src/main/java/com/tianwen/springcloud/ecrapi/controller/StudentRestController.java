package com.tianwen.springcloud.ecrapi.controller;

import com.tianwen.springcloud.commonapi.base.response.Response;
import com.tianwen.springcloud.commonapi.query.QueryTree;
import com.tianwen.springcloud.ecrapi.api.StudentRestApi;
import com.tianwen.springcloud.microservice.base.entity.Organization;
import com.tianwen.springcloud.microservice.base.entity.Student;
import com.tianwen.springcloud.microservice.base.entity.UserLoginInfo;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping(value = "/student")
public class StudentRestController extends BaseRestController implements StudentRestApi
{
    @Override
    public Response<Student> batchRegisterStudent(@RequestBody List<Student> studentEntityList) {

        for(Student student:studentEntityList){
            UserLoginInfo userEntity = new UserLoginInfo();
            Organization orgEntity = new Organization();

            userEntity.setUserId(student.getUserid());
            userEntity.setLoginName(student.getLoginname());
            userEntity.setRealName(student.getRealname());
            userEntity.setSex(student.getSex());
            //userEntity.setBirthday(student.getBirthday());
            userEntity.setStatus(student.getStatus());
            userEntity.setOrgId(student.getOrgid());
            userEntity.setLastModifyTime(student.getLastmodifytime());
            userEntity.setCreateTime(student.getCreatetime());

            orgEntity.setOrgid(student.getOrgid());
            orgEntity.setOrgname(student.getOrgname());

            if (userMicroApi.get(userEntity.getUserId()).getResponseEntity() != null)
                userMicroApi.update(userEntity);
            else
                userMicroApi.insert(userEntity);

            if (organizationMicroApi.get(student.getOrgid()).getResponseEntity() != null)
                organizationMicroApi.update(orgEntity);
            else
                organizationMicroApi.add(orgEntity);

            if(studentMicroApi.get(student.getUserid()).getResponseEntity() != null) {
                studentMicroApi.update(student);
            }
            else
                studentMicroApi.add(student);
        }
        return new Response<Student>(studentEntityList);
    }

    @Override
    public Response<Student> getStudentList(@RequestBody QueryTree queryTree) {
        return studentMicroApi.getStudentList(queryTree);
    }

    @Override
    public Response<Student> batchDeleteStudent(@RequestBody List<String> orgidList) {
        for(String orgid:orgidList){
            studentMicroApi.delete(orgid);
        }
        return new Response<>();
    }

    @Override
    public Response<Organization> batchSetArea2Student(@RequestBody List<Organization> studentEntityList, @RequestHeader(value = "token") String token) {
        return organizationMicroApi.batchUpdate(studentEntityList);
    }

    @Override
    public Response<Student> batchSetSharerange2Student(@RequestBody List<Student> studentList) {
        return studentMicroApi.batchUpdate(studentList);
    }
}
