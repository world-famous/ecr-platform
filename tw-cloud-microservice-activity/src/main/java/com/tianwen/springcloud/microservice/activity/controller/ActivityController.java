package com.tianwen.springcloud.microservice.activity.controller;

import com.github.pagehelper.Page;
import com.tianwen.springcloud.commonapi.base.response.Response;
import com.tianwen.springcloud.commonapi.constant.IStateCode;
import com.tianwen.springcloud.commonapi.exception.ParameterException;
import com.tianwen.springcloud.commonapi.log.SystemControllerLog;
import com.tianwen.springcloud.commonapi.query.QueryCondition;
import com.tianwen.springcloud.commonapi.query.QueryCondition.Operator;
import com.tianwen.springcloud.commonapi.query.QueryTree;
import com.tianwen.springcloud.datasource.base.AbstractCRUDController;
import com.tianwen.springcloud.datasource.util.QueryUtils;
import com.tianwen.springcloud.microservice.activity.api.ActivityMicroApi;
import com.tianwen.springcloud.microservice.activity.constant.IActivityMicroConstants;
import com.tianwen.springcloud.microservice.activity.entity.Activity;
import com.tianwen.springcloud.microservice.activity.service.ActivityService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/activity")
public class ActivityController extends AbstractCRUDController<Activity> implements ActivityMicroApi
{
    @Autowired
    private ActivityService activityService;

    @Override
    @ApiOperation(value = "获取活动列表", notes = "获取活动列表")
    @ApiImplicitParam(name = "queryTree", value = "搜索条件树", required = true, dataType = "QueryTree", paramType = "body")
    @SystemControllerLog(description = "获取活动列表")
    public Response<Activity> getList(@RequestBody QueryTree queryTree) {
        List<Activity> activityList = activityService.search(queryTree).getPageInfo().getList();
        if (!CollectionUtils.isEmpty(activityList)) {
            for (Activity activity : activityList) {
                String status = activityService.getActivityStatus(activity.getActivityid());
                activity.setStatus(StringUtils.equals(status, IActivityMicroConstants.ACTIVITY_STATUS_END)?IActivityMicroConstants.ACTIVITY_STATUS_END:activity.getStatus());
            }
        }
        return new Response<>(activityList);
    }

    @Override
    @ApiOperation(value = "获取活动列表", notes = "获取活动列表")
    @ApiImplicitParam(name = "queryTree", value = "搜索条件树", required = true, dataType = "QueryTree", paramType = "body")
    @SystemControllerLog(description = "获取活动列表")
    public Response<Activity> get(@PathVariable("id") String id) {
        Activity activity = activityService.selectByKey(id);
        if (activity != null) {
            String format = activity.getFormat();
            if (format != null && !format.isEmpty()) {
                activity.setFormats(format.split(","));
            }

            String[] accordings = StringUtils.split(activity.getAccording(), ",");
            List<String> accList = org.springframework.util.CollectionUtils.arrayToList(accordings);
            activity.setAccordings(accList);

            String status = activityService.getActivityStatus(activity.getActivityid());
            activity.setStatus(StringUtils.equals(status, IActivityMicroConstants.ACTIVITY_STATUS_END)?IActivityMicroConstants.ACTIVITY_STATUS_END:activity.getStatus());
        }


        return new Response<>(activity);
    }

    @Override
    @ApiOperation(value = "资源结束", notes = "资源结束")
    @ApiImplicitParam(name = "activityid", value = "资源ID", required = true, dataType = "String", paramType = "body")
    @SystemControllerLog(description = "资源结束")
    public Response<Activity> end(@PathVariable String activityid) {
        Activity activity = new Activity();
        activity.setActivityid(activityid);
        activity.setStatus(IActivityMicroConstants.ACTIVITY_STATUS_END);
        super.update(activity);
        return new Response<>(activity);
    }

    @Override
    @ApiOperation(value = "资源批量删除", notes = "资源批量删除")
    @ApiImplicitParam(name = "activityIds", value = "资源ID", required = true, dataType = "String[]", paramType = "body")
    @SystemControllerLog(description = "资源批量删除")
    public Response<Activity> batchRemove(@RequestBody String[] activityIds) {
        for (String activityid:activityIds) {
            activityService.deleteByPrimaryKey(activityid);
        }
        return new Response<>();
    }

    @Override
    public Response<Activity> regetPageInfoWithList(@RequestBody Map<String, Object> map) {
        List<Activity> activityList = (List<Activity>) map.get("list");
        int start = 0, pageSize = (int) map.get("numperpage"), pageno = (int) map.get("pageno");
        int count = activityList.size();
        if (start > activityList.size())
            activityList = new ArrayList<Activity>();
        else
            activityList = activityList.subList(start ,start + pageSize <= activityList.size() ? start + pageSize : activityList.size());
        Page<Activity> result = new Page<Activity>(pageno, pageSize);
        result.addAll(activityList);
        result.setTotal(count);
        return new Response<>(result);
    }

    @Override
    @ApiOperation(value = "", notes = "")
    @ApiImplicitParam(name = "", value = "", required = true, dataType = "", paramType = "")
    @SystemControllerLog(description = "")
    public Response<String> getActivityCreatorIds(@RequestBody QueryTree queryTree) {
        return activityService.getActivityCreatorIds(queryTree);
    }

    @Override
    @ApiOperation(value = "", notes = "")
    @ApiImplicitParam(name = "", value = "", required = true, dataType = "", paramType = "")
    @SystemControllerLog(description = "")
    public Response<String> getIdsByQueryTree(@RequestBody QueryTree queryTree) {
        return activityService.getIdsByQueryTree(queryTree);
    }

    //author:han 韩哲国
    @Override
    @ApiOperation(value = "", notes = "")
    @ApiImplicitParam(name = "", value = "", required = true, dataType = "", paramType = "")
    @SystemControllerLog(description = "")
    public Response<String> getIdsByQueryTreeExtra(@RequestBody QueryTree queryTree) {
        return activityService.getIdsByQueryTreeExtra(queryTree);
    }
    //end han

    @Override
    @ApiOperation(value = "", notes = "")
    @ApiImplicitParam(name = "", value = "", required = true, dataType = "", paramType = "")
    @SystemControllerLog(description = "")
    public Response<Long> getCountByCreator(@PathVariable(value = "userid") String userid) {
        return activityService.getCountByCreator(userid);
    }

    @Override
    @ApiOperation(value = "修改", notes = "修改")
    @ApiImplicitParam(name = "collectionActivity", value = "信息", required = true, dataType = "Activity", paramType = "body")
    @SystemControllerLog(description = "修改")
    public Response<Activity> edit(@RequestBody Activity collectionActivity) {
        Timestamp curtime = new Timestamp(System.currentTimeMillis());
        collectionActivity.setLastmodifytime(curtime);
        setFormatWithList(collectionActivity);

        super.update(collectionActivity);
        return new Response<>(collectionActivity);
    }

    private void setFormatWithList(Activity activity) {
        String[] formats = activity.getFormats();
        if (formats != null) {
            String format = "";
            StringBuilder stringBuilder = new StringBuilder();
            for (String strFormat : formats) {
                stringBuilder.append(strFormat+",");
            }
            format = stringBuilder.substring(0, stringBuilder.lastIndexOf(","));
            activity.setFormat(format);
        }
    }

    private void validateAdd(Activity activity) {
        Timestamp curtime = new Timestamp(System.currentTimeMillis());
        activity.setCreatetime(curtime);
        activity.setStatus(IActivityMicroConstants.ACTIVITY_STATUS_STARTED);
        if (StringUtils.isEmpty(activity.getIsanonymity()))
            activity.setIsanonymity(IActivityMicroConstants.ACTIVITY_NOT_ANONYMITY);
        activity.setLastmodifytime(curtime);
        setFormatWithList(activity);

        // 校验重复
        QueryTree queryTree = new QueryTree();
        queryTree.where("activityname", Operator.EQUAL, activity.getActivityname());
        queryTree.addCondition(new QueryCondition("activitytype", QueryCondition.Prepender.AND, Operator.EQUAL, activity.getActivitytype()));
        Example example = QueryUtils.queryTree2Example(queryTree, Activity.class);
        List<Activity> existList = activityService.selectByExample(example);
        if (CollectionUtils.isNotEmpty(existList))
        {
            throw new ParameterException(IStateCode.PARAMETER_IS_INVALID, "活动名重复");
        }
    }

    private void validateUpdate(Activity activity){
        Activity entity = activityService.selectByKey(activity.getActivityid());
        activity.setCreatetime(entity.getCreatetime());
    }

    @Override
    public void validate(MethodType methodType, Object p) {
        switch (methodType)
        {
            case ADD:
                Activity entity = (Activity) p;
                validateAdd(entity);
                break;
            case DELETE:
                break;
            case GET:
                break;
            case SEARCH:
                break;
            case UPDATE:
                Activity object = (Activity) p;
                validateUpdate(object);
                break;
            case BATCHADD:
                break;
            case BATCHDELETEBYENTITY:
                break;
            case BATCHUPDATE:
                break;
            case DELETEBYENTITY:
                break;
            case GETBYENTITY:
                break;
            default:
                break;
        }
    }

}
