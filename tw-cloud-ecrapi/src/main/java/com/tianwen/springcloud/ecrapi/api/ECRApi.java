package com.tianwen.springcloud.ecrapi.api;

import com.alibaba.fastjson.JSONObject;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * ECRApi对外接口
 * 
 * @author wangbin
 * @version [版本号, 2017年5月11日]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */

public interface ECRApi
{
    @RequestMapping(value = "/queryTeachmaterialList", method = RequestMethod.POST)
    public JSONObject queryTeachmaterialList(@RequestBody JSONObject param);

    @RequestMapping(value = "/queryTeachmaterialCatalog", method = RequestMethod.POST)
    public JSONObject queryTeachmaterialCatalog(@RequestBody JSONObject param);

    @RequestMapping(value = "/querySubjectnaviInfo", method = RequestMethod.POST)
    public JSONObject querySubjectnaviInfo(@RequestBody JSONObject param);

    @RequestMapping(value = "/uploadResources", method = RequestMethod.POST)
    public JSONObject uploadResources(@RequestBody JSONObject param);

    @RequestMapping(value = "/uploadResourcesfile", method = RequestMethod.POST)
    public JSONObject uploadResourcesfile(@RequestBody JSONObject param);

    @RequestMapping(value = "/delResources", method = RequestMethod.POST)
    public JSONObject delResources(@RequestBody JSONObject param);

    @RequestMapping(value = "/searchResourcesList", method = RequestMethod.POST)
    public JSONObject searchResourcesList(@RequestBody JSONObject param);

    @RequestMapping(value = "/getResourcesInfo", method = RequestMethod.POST)
    public JSONObject getResourcesInfo(@RequestBody JSONObject param);

    @RequestMapping(value = "/updateResources", method = RequestMethod.POST)
    public JSONObject updateResources(@RequestBody JSONObject param);

    @RequestMapping(value = "/updateResourcesfile", method = RequestMethod.POST)
    public JSONObject updateResourcesfile(@RequestBody JSONObject param);

}
