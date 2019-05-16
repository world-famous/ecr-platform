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

public interface RewardRestApi
{
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Response<Activity> get(@PathVariable(value = "id") String id);

    @RequestMapping(value = "/addRewardActivity", method = RequestMethod.POST)
    public Response<Activity> addRewardActivity(@RequestBody Activity rewardActivity, @RequestHeader(value = "token") String token);

    @RequestMapping(value = "/delete/{id}", method = RequestMethod.GET)
    public Response<Activity> delete(@PathVariable(value = "id") String id, @RequestHeader(value = "token") String token);

    @RequestMapping(value = "/batchRemoveActivity", method = RequestMethod.POST)
    public Response<Activity> batchRemoveActivity(@RequestBody String[] ids, @RequestHeader(value = "token") String token);

    @RequestMapping(value = "/endRewardActivity/{activityid}", method = RequestMethod.GET)
    public Response<Activity> endRewardActivity(@PathVariable(value = "activityid") String id);

    @RequestMapping(value = "/editRewardActivity", method = RequestMethod.POST)
    public Response <Activity> editRewardActivity(@RequestBody Activity rewardActivity, @RequestHeader(value = "token") String token);

    @RequestMapping(value = "/getRewardActivityList", method = RequestMethod.POST)
    public Response <Activity> getRewardActivityList(@RequestBody QueryTree queryTree);

    @RequestMapping(value = "/getRecentRewardList", method = RequestMethod.POST)
    public Response<Activity> getRecentRewardList(@RequestBody QueryTree queryTree);

    @RequestMapping(value = "/getMyRewardList", method = RequestMethod.POST)
    public Response<Activity> getMyRewardList(@RequestBody QueryTree queryTree, @RequestHeader(value = "token") String token);

    @RequestMapping(value = "/setGoodAnswer/{contentid}", method = RequestMethod.GET)
    Response setGoodAnswer(@PathVariable(value = "contentid") String contentid, @RequestHeader(value = "token") String token);

    @RequestMapping(value = "/getRewardExportManList", method = RequestMethod.POST)
    public Response<ExportMan> getRewardExportManList(@RequestBody QueryTree queryTree);

    @RequestMapping(value = "/getManyResourceTypeList", method = RequestMethod.GET)
    public Response<String> getManyResourceTypeList();

    @RequestMapping(value = "/getIJoinedRewardList", method = RequestMethod.POST)
    Response<Resource> getIJoinedRewardList(@RequestBody QueryTree queryTree, @RequestHeader(value = "token") String token);

    @RequestMapping(value = "/getIJoinedResourceList", method = RequestMethod.POST)
    Response<Resource> getIJoinedResourceList(@RequestBody QueryTree queryTree, @RequestHeader(value = "token") String token);

    @RequestMapping(value = "/getActivityAnswerList", method = RequestMethod.POST)
    Response<Resource> getActivityAnswerList(@RequestBody QueryTree queryTree);

    @RequestMapping(value = "/getICreatedResourceList", method = RequestMethod.POST)
    Response<Resource> getICreatedResourceList(@RequestBody QueryTree queryTree, @RequestHeader(value = "token") String token);

    @RequestMapping(value = "/getRewardActivityResourceList", method = RequestMethod.POST)
    public Response<Resource> getRewardActivityResourceList(@RequestBody QueryTree queryTree);

    @RequestMapping(value = "/getRewardCountInfo", method = RequestMethod.GET)
    public Response<Map<String, Long>> getRewardCountInfo(@RequestHeader(value = "token") String token);
}
