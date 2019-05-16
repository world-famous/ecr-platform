package com.tianwen.springcloud.ecrapi.api;

import com.tianwen.springcloud.commonapi.base.response.Response;
import com.tianwen.springcloud.commonapi.query.QueryTree;
import com.tianwen.springcloud.microservice.base.entity.Config;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

public interface ConfigRestApi {
    @RequestMapping(value = "/addConfig", method = RequestMethod.POST)
    public Response<Config> addConfig(@RequestBody Config entity);

    @RequestMapping(value = "/updateConfig", method = RequestMethod.POST)
    public Response<Config> updateConfig(@RequestBody Config entity);

    @RequestMapping(value = "/getConfigList", method = RequestMethod.POST)
    public Response<Config> getConfigList(@RequestBody QueryTree queryTree);

    @RequestMapping(value = "/removeConfig/{id}", method = RequestMethod.POST)
    public Response<Config> removeConfig(@PathVariable(value = "id") String id);

    @RequestMapping(value = "/getBadWord", method = RequestMethod.GET)
    public Response<Config> getBadWord();

    @RequestMapping(value = "/addBadWord", method = RequestMethod.POST)
    public Response<Config> addBadWord(@RequestBody Config entity);
}