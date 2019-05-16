package com.tianwen.springcloud.ecrapi.controller;

import com.tianwen.springcloud.commonapi.base.response.Response;
import com.tianwen.springcloud.commonapi.query.QueryTree;
import com.tianwen.springcloud.ecrapi.api.TeachingRestApi;
import com.tianwen.springcloud.microservice.base.entity.Teaching;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/teaching")
public class TeachingRestController extends BaseRestController implements TeachingRestApi {
    @Override
    public Response<Teaching> getTeachingList(@RequestBody QueryTree queryTree) {
        return teachingMicroApi.getTeachingList(queryTree);
    }

    @Override
    public Response<Teaching> insertTeaching(@RequestBody Teaching entity) {
        return teachingMicroApi.insertTeaching(entity);
    }

    @Override
    public Response<Teaching> removeTeaching(@RequestBody Teaching entity) {
        return teachingMicroApi.removeTeaching(entity);
    }
}
