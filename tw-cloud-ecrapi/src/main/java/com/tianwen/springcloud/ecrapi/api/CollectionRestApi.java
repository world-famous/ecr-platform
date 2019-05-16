package com.tianwen.springcloud.ecrapi.api;

import com.tianwen.springcloud.commonapi.base.response.Response;
import com.tianwen.springcloud.commonapi.query.QueryTree;
import com.tianwen.springcloud.microservice.activity.entity.Activity;
import com.tianwen.springcloud.microservice.resource.entity.ExportMan;
import com.tianwen.springcloud.microservice.resource.entity.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * ECRApi对外接口
 * 
 * @author wangbin
 * @version [版本号, 2017年5月11日]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */

public interface CollectionRestApi
{
    @RequestMapping(value = "/{activityid}", method = RequestMethod.GET)
    public Response<Activity> get(@PathVariable(value = "activityid") String activity);

    @RequestMapping(value = "/getCollectionCountInfo/{activityid}", method = RequestMethod.GET)
    public Response<Map<String, Object>> getCollectionCountInfo(@PathVariable(value = "activityid") String activityid);

    @RequestMapping(value = "/addCollectionActivity", method = RequestMethod.POST)
    public Response addCollectionActivity(@RequestBody Activity collectionActivity, @RequestHeader(value = "token") String token);

    @RequestMapping(value = "/delete/{id}", method = RequestMethod.GET)
    public Response<Activity> delete(@PathVariable(value = "id")String id, @RequestHeader(value = "token") String token);

    @RequestMapping(value = "/batchRemoveActivity", method = RequestMethod.POST)
    public Response<Activity> batchRemoveActivity(@RequestBody String[] ids, @RequestHeader(value = "token") String token);

    @RequestMapping(value = "/endCollectionActivity/{activityid}", method = RequestMethod.GET)
    public Response<Activity> endCollectionActivity(@PathVariable(value = "activityid") String id);

    @RequestMapping(value = "/editCollectionActivity", method = RequestMethod.POST)
    public Response <Activity> editCollectionActivity(@RequestBody Activity collectionActivity, @RequestHeader(value = "token") String token);

    @RequestMapping(value = "/getCollectionActivityList", method = RequestMethod.POST)
    public Response <Activity> getCollectionActivityList(@RequestBody QueryTree queryTree);

    @RequestMapping(value = "/getRecentCollectionList", method = RequestMethod.POST)
    public Response<Activity> getRecentCollectionList(@RequestBody QueryTree queryTree);

    @RequestMapping(value = "/getMyCollectionList", method = RequestMethod.POST)
    public Response<Activity> getMyCollectionList(@RequestBody QueryTree queryTree, @RequestHeader(value = "token") String token);

    @RequestMapping(value = "/getCollectionExportManList", method = RequestMethod.POST)
    public Response<ExportMan> getCollectionExportManList(@RequestBody QueryTree queryTree);

    @RequestMapping(value = "/getManyResourceTypeList", method = RequestMethod.GET)
    public Response<String> getManyResourceTypeList();

    @RequestMapping(value = "/getCollectionActivityResourceList", method = RequestMethod.POST)
    public Response<Resource> getCollectionActivityResourceList(@RequestBody QueryTree queryTree);

}
