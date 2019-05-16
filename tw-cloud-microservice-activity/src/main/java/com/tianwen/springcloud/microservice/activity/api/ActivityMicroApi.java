package com.tianwen.springcloud.microservice.activity.api;

import com.tianwen.springcloud.commonapi.base.ICRUDMicroApi;
import com.tianwen.springcloud.commonapi.base.response.Response;
import com.tianwen.springcloud.commonapi.query.QueryTree;
import com.tianwen.springcloud.microservice.activity.entity.Activity;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@FeignClient(value = "activity-service", url = "http://localhost:2227/activity-service/activity")
public interface ActivityMicroApi extends ICRUDMicroApi<Activity>
{
    @RequestMapping(value = "/getList", method = RequestMethod.POST)
    public Response<Activity> getList(@RequestBody QueryTree queryTree);

    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public Response<Activity> edit(@RequestBody Activity collectionActivity);

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Response<Activity> get(@PathVariable("id") String id);

    @RequestMapping(value = "/end/{activityid}", method = RequestMethod.GET)
    public Response<Activity> end(@PathVariable("activityid") String activityid);

    @RequestMapping(value = "/batchRemove", method = RequestMethod.POST)
    public Response<Activity> batchRemove(@RequestBody String[] activityIds);

    @RequestMapping(value = "/regetPageInfoWithList", method = RequestMethod.POST)
    public Response<Activity> regetPageInfoWithList(@RequestBody Map<String, Object> param);

    @RequestMapping(value = "/getActivityCreatorIds", method = RequestMethod.POST)
    public Response<String> getActivityCreatorIds(@RequestBody QueryTree queryTree);

    @RequestMapping(value = "/getIdsByQueryTree", method = RequestMethod.POST)
    public Response<String> getIdsByQueryTree(@RequestBody QueryTree queryTree);

    //author:han 韩哲国
    @RequestMapping(value = "/getIdsByQueryTreeExtra", method = RequestMethod.POST)
    public Response<String> getIdsByQueryTreeExtra(@RequestBody QueryTree queryTree);
    //end han

    @RequestMapping(value = "/getCountByCreator/{userid}", method = RequestMethod.GET)
    Response<Long> getCountByCreator(@PathVariable(value = "userid") String userid);
}
