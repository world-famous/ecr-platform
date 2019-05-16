package com.tianwen.springcloud.ecrapi.api;

import com.tianwen.springcloud.commonapi.base.response.Response;
import com.tianwen.springcloud.commonapi.query.QueryTree;
import com.tianwen.springcloud.microservice.base.entity.Teaching;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

public interface TeachingRestApi {
    @RequestMapping(value = "/getTeachingList", method = RequestMethod.POST)
    public Response<Teaching> getTeachingList(@RequestBody QueryTree queryTree);

    @RequestMapping(value = "/insertTeaching", method = RequestMethod.POST)
    public Response<Teaching> insertTeaching(@RequestBody Teaching entity);

    @RequestMapping(value = "/removeTeaching", method = RequestMethod.POST)
    public Response<Teaching> removeTeaching(@RequestBody Teaching entity);
}