package com.tianwen.springcloud.ecrapi.controller;

import com.tianwen.springcloud.commonapi.base.response.Response;
import com.tianwen.springcloud.commonapi.query.OrderMethod;
import com.tianwen.springcloud.commonapi.query.QueryCondition;
import com.tianwen.springcloud.commonapi.query.QueryTree;
import com.tianwen.springcloud.ecrapi.api.RewardRestApi;
import com.tianwen.springcloud.ecrapi.constant.ICommonConstants;
import com.tianwen.springcloud.ecrapi.constant.IErrorMessageConstants;
import com.tianwen.springcloud.microservice.activity.constant.IActivityMicroConstants;
import com.tianwen.springcloud.microservice.activity.entity.Activity;
import com.tianwen.springcloud.microservice.base.entity.UserLoginInfo;
import com.tianwen.springcloud.microservice.operation.constant.IOperationConstants;
import com.tianwen.springcloud.microservice.operation.entity.Integral;
import com.tianwen.springcloud.microservice.operation.entity.Member;
import com.tianwen.springcloud.microservice.resource.constant.IResourceMicroConstants;
import com.tianwen.springcloud.microservice.resource.entity.ExportMan;
import com.tianwen.springcloud.microservice.resource.entity.Resource;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping(value = "/reward")
public class RewardRestController extends BaseRestController implements RewardRestApi
{
    @Override
    public Response<Activity> get(@PathVariable(value = "id") String id) {
        return activityMicroApi.get(id);
    }

    @Override
    public Response addRewardActivity(@RequestBody Activity rewardActivity, @RequestHeader(value = "token") String token) {
        Response<UserLoginInfo> userResponse = getUserByToken(token);
        UserLoginInfo user = userResponse.getResponseEntity();
        if (user == null) return userResponse;

        Integer userScore = memberMicroApi.getMyScore(user.getUserId()).getResponseEntity();
        Integer bonus = rewardActivity.getBonuspoints();
        if (bonus != null && bonus > 0) {
            if (userScore < bonus) {
                return new Response(IErrorMessageConstants.ERR_SCORE_NOT_ENOUGH, IErrorMessageConstants.ERR_MSG_SCORE_NOT_ENOUGH);
            }
        }

        rewardActivity.setActivitytype(IActivityMicroConstants.ACTIVITY_TYPE_REWARD);
        rewardActivity.setCreator(userMicroApi.getByToken(token).getResponseEntity().getUserId());
        rewardActivity =  activityMicroApi.add(rewardActivity).getResponseEntity();
        if (rewardActivity == null) {
            return new Response(IErrorMessageConstants.ERR_PARAMETER_INVALID, IErrorMessageConstants.ERR_MSG_DUPLICATE_ACTIVITY_THEME);
        }

        Member userMember = memberMicroApi.getByUserId(user.getUserId()).getResponseEntity();
        if (userMember != null) {
            userMember.setUserid(user.getUserId());
            userMember.setFrozenintegral(userMember.getFrozenintegral() + rewardActivity.getBonuspoints());

            Integer updateScore = userScore - bonus;
            Integral integral = new Integral();
            integral.setScoretype(IOperationConstants.SCORE_TYPE_ACTIVITY);
            integral.setUserid(user.getUserId());
            integral.setUserintegralvalue(updateScore);
            integral.setIncometype(IOperationConstants.INCOME_TYPE_USE);
            integral.setIntegralvalue(bonus);
            integral.setOperationtype(IOperationConstants.OPERATION_CREATE_REWARD);
            integral.setObjectid(rewardActivity.getActivityid());
            integralMicroApi.add(integral);

            userMember.setUseintegral(updateScore);
            memberMicroApi.update(userMember);
        }
        return new Response(rewardActivity);
    }

    @Override
    public Response<Activity> delete(@PathVariable(value = "id")String id, @RequestHeader(value = "token") String token) {
        logOptionEntity(id, id, ICommonConstants.OPTION_OPTIONTYPE_REWARD_DELETE, token);
        return  activityMicroApi.delete(id);
    }

    @Override
    public Response<Activity> batchRemoveActivity(@RequestBody String[] ids, @RequestHeader(value = "token") String token) {
        for (String id : ids){
            logOptionEntity(id, id, ICommonConstants.OPTION_OPTIONTYPE_REWARD_DELETE, token);
        }
        return activityMicroApi.batchRemove(ids);
    }

    @Override
    public Response<Activity> endRewardActivity(@PathVariable(value = "activityid") String activityid){
        Activity activity = activityMicroApi.get(activityid).getResponseEntity();
        if (activity != null && StringUtils.equals(activity.getStatus(), IActivityMicroConstants.ACTIVITY_STATUS_STARTED)) {
            String creatorid = activity.getCreator();
            if (!StringUtils.isEmpty(creatorid)) {
                UserLoginInfo userLoginInfo = userMicroApi.get(creatorid).getResponseEntity();
                if (userLoginInfo != null) {
                    doAction(ICommonConstants.ACTION_RETURN_FROZEN_SCORE, null, userLoginInfo, activity.getBonuspoints());
                }
            }
        }
        return activityMicroApi.end(activityid);
    }

    @Override
    public Response <Activity> editRewardActivity(@RequestBody Activity rewardActivity, @RequestHeader(value = "token") String token) {
        rewardActivity.setCreator(userMicroApi.getByToken(token).getResponseEntity().getUserId());
        logOptionEntity(rewardActivity.getActivityid(), rewardActivity.getActivityid(), ICommonConstants.OPTION_OPTIONTYPE_REWARD_MODIFY, token);
        return activityMicroApi.edit(rewardActivity);
    }

    @Override
    public Response <Activity> getRewardActivityList(@RequestBody QueryTree queryTree) {
        queryTree.getConditions().add(new QueryCondition("activitytype", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL,  IActivityMicroConstants.ACTIVITY_TYPE_REWARD));
        Response<Activity> response = activityMicroApi.getList(queryTree);
        validateActivityList(response);
        return response;
    }

    @Override
    public Response<Activity> getRecentRewardList(@RequestBody QueryTree queryTree) {
        QueryCondition queryCondition = new QueryCondition("activitytype", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL,  IActivityMicroConstants.ACTIVITY_TYPE_REWARD);
        if (queryTree.getConditions() == null)
            queryTree.setConditions(new ArrayList<>());
        queryTree.addCondition(queryCondition);

        OrderMethod orderMethod = new OrderMethod("orderBycreatetime", OrderMethod.Method.DESC);
        if (queryTree.getOrderMethods() == null)
            queryTree.setOrderMethods(new ArrayList<>());
        queryTree.orderBy(orderMethod);

        Response<Activity> response = activityMicroApi.getList(queryTree);
        validateActivityList(response);
        return response;
    }

    @Override
    public Response<Activity> getMyRewardList(@RequestBody QueryTree queryTree, @RequestHeader(value = "token") String token) {
        UserLoginInfo userLoginInfo = getUserByToken(token).getResponseEntity();
        queryTree.getConditions().add(new QueryCondition("creator", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, userLoginInfo.getUserId()));
        queryTree.getConditions().add(new QueryCondition("activitytype", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL,  IActivityMicroConstants.ACTIVITY_TYPE_REWARD));

        OrderMethod orderMethod = new OrderMethod("orderBycreatetime", OrderMethod.Method.DESC);
        if (queryTree.getOrderMethods() == null)
            queryTree.setOrderMethods(new ArrayList<>());
        queryTree.getOrderMethods().add(orderMethod);

        Response<Activity> response = activityMicroApi.getList(queryTree);
        validateActivityList(response);
        return response;
    }

    @Override
    public Response setGoodAnswer(@PathVariable(value = "contentid") String contentid, @RequestHeader(value = "token") String token) {
        Response<UserLoginInfo> operatorResponse = getUserByToken(token);
        UserLoginInfo operator = operatorResponse.getResponseEntity();
        if (operator == null) return operatorResponse;

        Resource resource = new Resource();
        resource.setContentid(contentid);
        resource.setIsanswer(IResourceMicroConstants.RES_SOURCE_ANSWER);
        resourceMicroApi.update(resource);

        QueryTree queryTree = new QueryTree();
        queryTree.getConditions().add(new QueryCondition("contentid", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, contentid));
        resource = resourceMicroApi.getByQueryTree(queryTree).getResponseEntity();
        String activityid = resource.getActivityid();
        if (!StringUtils.isEmpty(activityid)) {
            Activity activity = activityMicroApi.get(activityid).getResponseEntity();
            if (activity != null) {
                Integer bonus = activity.getBonuspoints();
                UserLoginInfo resourceCreator = userMicroApi.get(resource.getCreator()).getResponseEntity();
                if (bonus > 0 && resourceCreator != null) {
                    doAction(ICommonConstants.ACTION_JOIN_REWARD, resource, resourceCreator, bonus);
                }
                Activity example = new Activity();
                example.setActivityid(activityid);
                example.setStatus(IActivityMicroConstants.ACTIVITY_STATUS_ANSWERED);
                activity.setAnsweruserid(resource.getCreator());
                activityMicroApi.update(example);
            }
        }
        return new Response(resource);
    }

    @Override
    public Response<ExportMan> getRewardExportManList(@RequestBody QueryTree queryTree) {
        queryTree.getConditions().add(new QueryCondition("sourceid", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, IResourceMicroConstants.RES_SOURCE_REWARD));
        return getExportManList(queryTree);
    }

    @Override
    public Response<String> getManyResourceTypeList() {
        return resourceMicroApi.getActivityCountInfo(IResourceMicroConstants.RES_SOURCE_REWARD);
    }

    @Override
    public Response getIJoinedRewardList(@RequestBody QueryTree queryTree, @RequestHeader(value = "token") String token) {
        Response<UserLoginInfo> loginInfoResponse = getUserByToken(token);
        UserLoginInfo userLoginInfo = loginInfoResponse.getResponseEntity();
        if (userLoginInfo == null)
            return new Response(IErrorMessageConstants.ERR_MSG_TOKEN_NOT_CORRECT);

        Map<String, String> map = new HashMap<>();
        map.put("activitytype", IActivityMicroConstants.ACTIVITY_TYPE_REWARD);
        map.put("userid", userLoginInfo.getUserId());
        List<String> activityids = resourceMicroApi.getActivitIds(map).getPageInfo().getList();

        queryTree.getConditions().add(new QueryCondition("activityids", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, activityids));
        Response<Activity> activityResponse = activityMicroApi.getList(queryTree);
        validateActivityList(activityResponse);
        return activityResponse;
    }

    @Override
    public Response<Resource> getIJoinedResourceList(@RequestBody QueryTree queryTree, @RequestHeader(value = "token") String token) {
        UserLoginInfo userLoginInfo = getUserByToken(token).getResponseEntity();
        QueryCondition queryCondition = queryTree.getQueryCondition("status");
        if (queryCondition != null)
            queryTree.getConditions().remove(queryCondition);

        queryTree.getConditions().add(new QueryCondition("creator", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, userLoginInfo.getUserId()));
        queryTree.getConditions().add(new QueryCondition("isgoods", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, IResourceMicroConstants.RES_SOURCE_GOODS));
        Response<Resource>  resourceResponse = resourceMicroApi.getList(queryTree);
        List<Resource> resources = resourceResponse.getPageInfo().getList();
        if (!CollectionUtils.isEmpty(resources)) {
            for (Resource resource : resources) {
                validateResource(resource);
            }
        }
        return  resourceResponse;
    }

    @Override
    public Response<Resource> getICreatedResourceList(@RequestBody QueryTree queryTree, @RequestHeader(value = "token") String token) {
        UserLoginInfo userLoginInfo = getUserByToken(token).getResponseEntity();
        queryTree.getConditions().add(new QueryCondition("activitytype", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, IActivityMicroConstants.ACTIVITY_TYPE_REWARD));
        queryTree.getConditions().add(new QueryCondition("creator", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, userLoginInfo.getUserId()));
        QueryCondition idCondition = queryTree.getQueryCondition("activityid");
        if (idCondition == null) {
            List<String> activityids = activityMicroApi.getIdsByQueryTree(queryTree).getPageInfo().getList();
            queryTree.getConditions().add(new QueryCondition("activityids", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, activityids));
        }

        QueryCondition queryCondition = queryTree.getQueryCondition("status");
        if (queryCondition != null)
            queryTree.getConditions().remove(queryCondition);

        queryCondition = queryTree.getQueryCondition("creator");
        if (queryCondition != null)
            queryTree.getConditions().remove(queryCondition);

        queryTree.getConditions().add(new QueryCondition("isgoods", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, IResourceMicroConstants.RES_SOURCE_GOODS));
        Response<Resource>  resourceResponse = resourceMicroApi.getList(queryTree);
        List<Resource> resources = resourceResponse.getPageInfo().getList();
        if (!CollectionUtils.isEmpty(resources)) {
            for (Resource resource : resources) {
                validateResource(resource);
            }
        }
        return  resourceResponse;
    }

    @Override
    public Response<Resource> getActivityAnswerList(@RequestBody QueryTree queryTree) {
        queryTree.getConditions().add(new QueryCondition("activitytype", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, IActivityMicroConstants.ACTIVITY_TYPE_REWARD));
        queryTree.getConditions().add(new QueryCondition("isanswer", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, IResourceMicroConstants.RES_SOURCE_ANSWER));
        queryTree.getConditions().add(new QueryCondition("status", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, IResourceMicroConstants.RES_STATUS_PASS));
        QueryCondition nameCondition = queryTree.getQueryCondition("realname");
        if (nameCondition != null) {
            QueryTree queryTree1 = new QueryTree();
            queryTree1.getConditions().add(nameCondition);
            List<String> userids = userMicroApi.getUserIdsByQueryTree(queryTree1).getPageInfo().getList();
            queryTree.getConditions().add(new QueryCondition("creatorids", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, userids));
        }
        Response<Resource>  resourceResponse = resourceMicroApi.getList(queryTree);
        List<Resource> resources = resourceResponse.getPageInfo().getList();
        if (!CollectionUtils.isEmpty(resources)) {
            for (Resource resource : resources) {
                validateResource(resource);
            }
        }
        return  resourceResponse;
    }

    @Override
    public Response<Resource> getRewardActivityResourceList(@RequestBody QueryTree queryTree) {
        queryTree.getConditions().add(new QueryCondition("activitytype", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, IActivityMicroConstants.ACTIVITY_TYPE_REWARD));
        queryTree.getConditions().add(new QueryCondition("isgoods", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, IResourceMicroConstants.RES_SOURCE_GOODS));
        Response<Resource> resourceResponse = resourceMicroApi.activityResourceList(queryTree);
        List<Resource> resultList = resourceResponse.getPageInfo().getList();
        if (!CollectionUtils.isEmpty(resultList)) {
            for (Resource resource : resultList) {
                validateResource(resource);
            }
        }
        return resourceResponse;
    }

    @Override
    public Response<Map<String, Long>> getRewardCountInfo(@RequestHeader(value = "token") String token) {
        UserLoginInfo user = getUserByToken(token).getResponseEntity();
        Map<String, Long> result = new HashMap<>();
        result.put("created", activityMicroApi.getCountByCreator(user.getUserId()).getResponseEntity());
        result.put("joined", resourceMicroApi.getCountByCreator(user.getUserId()).getResponseEntity());
        return new Response<>(result);
    }

}
