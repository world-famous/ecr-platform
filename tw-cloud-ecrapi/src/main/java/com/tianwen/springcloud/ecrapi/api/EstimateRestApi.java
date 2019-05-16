package com.tianwen.springcloud.ecrapi.api;

import com.tianwen.springcloud.commonapi.base.response.Response;
import com.tianwen.springcloud.commonapi.query.QueryTree;
import com.tianwen.springcloud.microservice.activity.entity.Activity;
import com.tianwen.springcloud.microservice.resource.entity.ExportMan;
import org.springframework.web.bind.annotation.*;

/**
 * ECRApi对外接口
 * 
 * @author wangbin
 * @version [版本号, 2017年5月11日]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */

public interface EstimateRestApi
{
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Response<Activity> get(@PathVariable(value = "id") String id);

    @RequestMapping(value = "/addEstimateActivity", method = RequestMethod.POST)
    public Response<Activity> addEstimateActivity(@RequestBody Activity estimateActivity, @RequestHeader(value = "token") String token);

    @RequestMapping(value = "/delete/{id}", method = RequestMethod.GET)
    public Response<Activity> delete(@PathVariable(value = "id") String id);

    @RequestMapping(value = "/batchRemoveActivity", method = RequestMethod.POST)
    public Response<Activity> batchRemoveActivity(@RequestBody String[] ids);

    @RequestMapping(value = "/endEstimateActivity/{activityid}", method = RequestMethod.GET)
    public Response<Activity> endEstimateActivity(@PathVariable(value = "activityid") String id);

    @RequestMapping(value = "/editEstimateActivity", method = RequestMethod.POST)
    public Response <Activity> editEstimateActivity(@RequestBody Activity estimateActivity, @RequestHeader(value = "token") String token);

    @RequestMapping(value = "/getEstimateActivityList", method = RequestMethod.POST)
    public Response <Activity> getEstimateActivityList(@RequestBody QueryTree queryTree);

    @RequestMapping(value = "/getRecentEstimateList", method = RequestMethod.POST)
    public Response<Activity> getRecentEstimateList(@RequestBody QueryTree queryTree);

    @RequestMapping(value = "/getMyEstimateList", method = RequestMethod.POST)
    public Response<Activity> getMyEstimateList(@RequestBody QueryTree queryTree, @RequestHeader(value = "token") String token);

    @RequestMapping(value = "/voteTeacher/{userid}", method = RequestMethod.GET)
    public Response<Integer> voteTeacher(@PathVariable(value = "userid") String userid, @RequestHeader(value = "token") String token);

    @RequestMapping(value = "/getEstimateTeacherList", method = RequestMethod.POST)
    public Response getEstimateTeacherList(@RequestBody QueryTree queryTree);

    @RequestMapping(value = "/getEstimateExporterList", method = RequestMethod.POST)
    public Response<ExportMan> getEstimateExporterList(@RequestBody QueryTree queryTree);

    @RequestMapping(value = "/exportTeacherDetail", method = RequestMethod.POST)
    Response exportTeacherDetail(@RequestBody QueryTree queryTree) throws Exception;

}
