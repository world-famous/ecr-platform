package com.tianwen.springcloud.microservice.base.api;

import com.tianwen.springcloud.commonapi.base.ICRUDMicroApi;
import com.tianwen.springcloud.commonapi.base.response.Response;
import com.tianwen.springcloud.commonapi.query.QueryTree;
import com.tianwen.springcloud.microservice.base.entity.Theme;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by gems on 2018.12.19.
 */
@FeignClient(value = "base-service", url = "http://localhost:2226/base-service/theme")
public interface ThemeMicroApi extends ICRUDMicroApi<Theme>{
    @RequestMapping(value = "/makeESDb", method = RequestMethod.GET)
    public Response<Theme> makeESDb();

    @RequestMapping(value = "/batchSaveToES", method = RequestMethod.POST)
    public Response<Theme> batchSaveToES(@RequestBody QueryTree queryTree);

    @RequestMapping(value = "/saveToES", method = RequestMethod.POST)
    public Response<Theme> saveToES(@RequestBody Theme entity);

    @RequestMapping(value = "/deleteFromES", method = RequestMethod.POST)
    public Response<Theme> removeFromES(@RequestBody Theme entity);

    @RequestMapping(value = "/deleteFromES/{id}", method = RequestMethod.GET)
    public Response<Theme> removeFromES(@PathVariable(value = "id") String id);

    @RequestMapping(value = "/insert", method = RequestMethod.POST)
    public Response<Theme> insert(@RequestBody Theme entity);

    @RequestMapping(value = "/modify", method = RequestMethod.POST)
    public Response<Theme> modify(@RequestBody Theme entity);

    @RequestMapping(value = "/remove/{id}", method = RequestMethod.GET)
    public Response<Theme> remove(@PathVariable(value = "id") String id);

    @RequestMapping(value = "/getList", method = RequestMethod.POST)
    public Response<Theme> getList(@RequestBody QueryTree queryTree);

    // Author : GOD
    // Date Start : 2019-1-31 3 PM
    // Reason : 系统管理 专题管理 Page Loading Speed, Select First Theme as Default

    @RequestMapping(value = "/getListAndChildCount", method = RequestMethod.POST)
    public Response<Theme> getListAndChildCount(@RequestBody QueryTree queryTree);

    // Author : GOD
    // Date End : 2019-2-1 6 PM
    // Reason : 系统管理 专题管理 Page Loading Speed, Select First Theme as Default

    @RequestMapping(value = "/getMyAllThemes/{parent}", method = RequestMethod.GET)
    public Response<String> getMyAllThemes(@PathVariable(value = "parent") String parent);

    @RequestMapping(value = "/get/{themeid}", method = RequestMethod.GET)
    Response<Theme> get(@PathVariable(value = "themeid") String themeid);
}
