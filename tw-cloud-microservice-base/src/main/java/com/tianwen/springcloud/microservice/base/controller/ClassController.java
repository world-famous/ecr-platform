package com.tianwen.springcloud.microservice.base.controller;

import com.sun.javafx.collections.MappingChange;
import com.tianwen.springcloud.commonapi.base.response.Response;
import com.tianwen.springcloud.commonapi.query.QueryTree;
import com.tianwen.springcloud.datasource.base.AbstractCRUDController;
import com.tianwen.springcloud.microservice.base.api.ClassMicroApi;
import com.tianwen.springcloud.microservice.base.constant.IBaseMicroConstants;
import com.tianwen.springcloud.microservice.base.entity.ClassInfo;
import com.tianwen.springcloud.microservice.base.service.ClassService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(value = "/class")
public class ClassController extends AbstractCRUDController<ClassInfo> implements ClassMicroApi
{
    @Autowired
    private ClassService classService;

    @Override
    public Response<ClassInfo> getClassList(@RequestBody QueryTree queryTree) {
        return classService.search(queryTree);
    }

    @Override
    public Response<ClassInfo> getClassById(@PathVariable(value = "id") String id) {
        Map<String, Object> param = new HashMap<>();
        param.put("classid", id);
        param.put("lang", IBaseMicroConstants.zh_CN);
        ClassInfo classInfo = classService.getClassById(param);
        return new Response<>(classInfo);
    }

    @Override
    public Response<ClassInfo> insertClass(@RequestBody ClassInfo entity) {
        classService.save(entity);
        return new Response<>(entity);
    }

    @Override
    public Response<ClassInfo> removeClass(@PathVariable(value = "classid") String classid) {
        ClassInfo entity = classService.selectByKey(classid);
        classService.deleteByPrimaryKey(entity);
        return new Response<>(entity);
    }

    @Override
    public void validate(MethodType methodType, Object p) {}
}
