package com.tianwen.springcloud.microservice.base.api;

import com.tianwen.springcloud.commonapi.base.ICRUDMicroApi;
import com.tianwen.springcloud.commonapi.base.response.Response;
import com.tianwen.springcloud.commonapi.query.QueryTree;
import com.tianwen.springcloud.microservice.base.entity.*;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Map;

/**
 * 用户相关对外接口
 * 
 * @author wangbin
 * @version [版本号, 2017年5月11日]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */
@FeignClient(value = "base-service", url = "http://localhost:2226/base-service/knowledge")
public interface KnowledgeMicroApi extends ICRUDMicroApi<Knowledge>
{
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public Response<Knowledge> update(@RequestBody Knowledge entity);

    @RequestMapping(value = "/delete/{id}", method = RequestMethod.GET)
    public Response<Knowledge> delete(@PathVariable(value = "id") String id);

    @RequestMapping(value = "/getTree", method = RequestMethod.POST)
    public Response<KnowledgeTree> getTree(@RequestBody QueryTree queryTree);

    @RequestMapping(value = "/getList", method = RequestMethod.POST)
    public Response<Knowledge> getList(@RequestBody QueryTree queryTree);

    @RequestMapping(value = "/moveKnowledge", method = RequestMethod.POST)
    public Response moveKnowledge(@RequestBody Map<String, Object> param);

    @RequestMapping(value = "/getSchoolSectionList", method = RequestMethod.POST)
    public Response getSchoolSectionList(@RequestBody QueryTree queryTree);

    @RequestMapping(value = "/getSubjectList", method = RequestMethod.POST)
    public Response getSubjectList(@RequestBody QueryTree queryTree);
}
