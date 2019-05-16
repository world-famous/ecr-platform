package com.tianwen.springcloud.ecrapi.controller;

import com.tianwen.springcloud.commonapi.base.response.Response;
import com.tianwen.springcloud.commonapi.query.QueryTree;
import com.tianwen.springcloud.ecrapi.api.ClassRestApi;
import com.tianwen.springcloud.microservice.base.entity.ClassInfo;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/class")
public  class ClassRestController extends BaseRestController implements ClassRestApi {
    @Override
    public Response<ClassInfo> getClassList(@RequestBody QueryTree queryTree) {
        return classMicroApi.getClassList(queryTree);
    }

    @Override
    public Response<ClassInfo> insertClass(@RequestBody ClassInfo entity) {
        return classMicroApi.insertClass(entity);
    }

    @Override
    public Response<ClassInfo> removeClass(@PathVariable(value = "classid") String classid) {
        return classMicroApi.removeClass(classid);
    }
}
