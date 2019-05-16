package com.tianwen.springcloud.ecrapi.api;

import com.tianwen.springcloud.commonapi.base.response.Response;
import com.tianwen.springcloud.commonapi.query.QueryTree;
import com.tianwen.springcloud.microservice.base.entity.ClassInfo;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

public interface ClassRestApi {
    @RequestMapping(value = "/getClassList", method = RequestMethod.POST)
    public Response<ClassInfo> getClassList(@RequestBody QueryTree queryTree);

    @RequestMapping(value = "/insertClass", method = RequestMethod.POST)
    public Response<ClassInfo> insertClass(@RequestBody ClassInfo entity);

    @RequestMapping(value = "/removeClass/{classid}", method = RequestMethod.GET)
    public Response<ClassInfo> removeClass(@PathVariable(value = "classid") String classid);
}