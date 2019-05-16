package com.tianwen.springcloud.microservice.base.controller;

import com.tianwen.springcloud.commonapi.base.response.Response;
import com.tianwen.springcloud.commonapi.query.QueryTree;
import com.tianwen.springcloud.datasource.base.AbstractCRUDController;
import com.tianwen.springcloud.microservice.base.api.TeachingMicroApi;
import com.tianwen.springcloud.microservice.base.entity.Teaching;
import com.tianwen.springcloud.microservice.base.service.TeachingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/teaching")
public class TeachingController extends AbstractCRUDController<Teaching> implements TeachingMicroApi
{
    @Autowired
    private TeachingService teachingService;

    @Override
    public Response<Teaching> getTeachingList(@RequestBody QueryTree queryTree) {
        return  teachingService.search(queryTree);
    }

    @Override
    public Response<Teaching> insertTeaching(@RequestBody Teaching entity) {
        teachingService.save(entity);
        return new Response<>(entity);
    }

    @Override
    public Response<Teaching> removeTeaching(@RequestBody Teaching entity) {
        teachingService.delete(entity);
        return new Response<>(entity);
    }

    @Override
    public void validate(MethodType methodType, Object p) {}
}
