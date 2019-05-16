package com.tianwen.springcloud.microservice.base.controller;

import com.tianwen.springcloud.commonapi.base.response.Response;
import com.tianwen.springcloud.commonapi.query.QueryTree;
import com.tianwen.springcloud.datasource.base.AbstractCRUDController;
import com.tianwen.springcloud.microservice.base.api.SchoolMicroApi;
import com.tianwen.springcloud.microservice.base.entity.DictItem;
import com.tianwen.springcloud.microservice.base.entity.School;
import com.tianwen.springcloud.microservice.base.entity.SchoolType;
import com.tianwen.springcloud.microservice.base.service.OrganizationService;
import com.tianwen.springcloud.microservice.base.service.SchoolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/school")
public class SchoolController extends AbstractCRUDController<School> implements SchoolMicroApi
{
        @Autowired
        private SchoolService schoolService;

        @Autowired
        private OrganizationService orgService;

        @Override
        public Response<SchoolType> getSchoolTypeList() {
                return new Response<SchoolType>( schoolService.getSchoolTypeList() );
        }

        @Override
        public Response<School> getAllSchoolList(@RequestBody QueryTree queryTree) {
                return schoolService.getAllSchoolList(queryTree);
        }

        @Override
        public Response<School> getSchoolList(@RequestBody QueryTree queryTree) {
                return schoolService.getSchoolList(queryTree);
        }

        @Override
        public Response<School> getListByAreaAndConType(@RequestBody QueryTree queryTree) {
                return schoolService.getListByAreaAndConType(queryTree);
        }

        @Override
        public Response<DictItem> getSchoolSectionBySchoolid(@PathVariable(value = "schoolid") String schoolid) {
                return schoolService.getSchoolSectionBySchoolid(schoolid);
        }

        @Override
        public Response<Integer> getOrdernoByOrgid(@PathVariable(value = "userid") String userid) {
                Integer integer = get(userid).getResponseEntity().getOrderno();
                return new Response<>(integer);
        }

        @Override
        public Response<School> setOrdernoByOrgid(@RequestBody School school) {
//Author : GOD 2019-2-13
                if(schoolService.isValidSchoolOrderno(school.getOrderno()) == false) {
                        Response<School> resp = new Response<>(school);
                        resp.getServerResult().setResultCode(Integer.toString(401));
                        return resp;
                } else{
                        return update(school);
                }
//Author : GOD 2019-2-13
        }

        @Override
        public void validate(MethodType methodType, Object p) { }
}
