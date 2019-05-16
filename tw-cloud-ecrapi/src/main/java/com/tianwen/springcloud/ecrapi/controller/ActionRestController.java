package com.tianwen.springcloud.ecrapi.controller;

import com.tianwen.springcloud.commonapi.base.response.Response;
import com.tianwen.springcloud.commonapi.log.SystemControllerLog;
import com.tianwen.springcloud.commonapi.query.QueryTree;
import com.tianwen.springcloud.ecrapi.api.ActionRestApi;
import com.tianwen.springcloud.microservice.base.entity.UserLoginInfo;
import com.tianwen.springcloud.microservice.bussinessassist.api.ActionMicroApi;
import com.tianwen.springcloud.microservice.bussinessassist.entity.Action;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Timestamp;
import java.util.List;

@RestController
@RequestMapping(value = "/action")
public  class ActionRestController extends BaseRestController implements ActionRestApi {
    @Autowired
    private ActionMicroApi actionMicroApi;

    @Autowired
    private HttpServletRequest request;

    @Override
    @ApiOperation(value = "获取用户行为列表", notes = "获取用户行为列表")
    @ApiImplicitParam(name = "querytree", value = "搜索条件树", required = true, dataType = "Querytree", paramType = "body")
    @SystemControllerLog(description = "获取用户行为列表")
    public Response<Action> getActionList(@RequestBody QueryTree queryTree) {
        return actionMicroApi.getList(queryTree);
    }

    @Override
    @ApiOperation(value = "根据id列表删除用户行为列表", notes = "根据id列表删除用户行为列表")
    @ApiImplicitParam(name = "id列表", value = "用户行为id列表", required = true, dataType = "List<String>", paramType = "body")
    @SystemControllerLog(description = "根据id列表删除用户行为列表")
    public Response<Action> batchActionDelete(@RequestBody List<String> actionids) {
        for (String actionid : actionids) {
            actionMicroApi.delete(actionid);
        }
        return new Response<>();
    }

    @Override
    public Response inputUserAction(@RequestBody Action action, @RequestHeader(value = "token")String token) {
        Timestamp starttime = new Timestamp(System.currentTimeMillis());
        Response<UserLoginInfo> userResponse = getUserByToken(token);
        UserLoginInfo userLoginInfo = userResponse.getResponseEntity();
        if (userLoginInfo == null) return userResponse;

        List<Action>actionListByUser = actionMicroApi.getActionListByUser(userLoginInfo.getUserId()).getPageInfo().getList();
        Action beforeAction = null;
        if (!actionListByUser.isEmpty())
             beforeAction = actionListByUser.get(0);

        String remoteAddr = null;
        if (request != null)
            remoteAddr = request.getRemoteAddr();

        if (beforeAction == null) {
            beforeAction = new Action();

            beforeAction.setIpaddress(remoteAddr);
            beforeAction.setActiontype(action.getActiontype());
            beforeAction.setStarttime(starttime);
            beforeAction.setUserid(userLoginInfo.getUserId());
            beforeAction.setContentid(action.getContentid());
            Timestamp endtime = new Timestamp(System.currentTimeMillis());
            beforeAction.setEndtime(endtime);
            actionMicroApi.add(beforeAction);
            return new Response<>(beforeAction);
        }

        if (StringUtils.equals(beforeAction.getUserid(),userLoginInfo.getUserId()) &&
            StringUtils.equals(beforeAction.getActiontype(), action.getActiontype()) &&
            StringUtils.equals(beforeAction.getContentid(), action.getContentid()) &&
            compareTwoTimeStamps(starttime , beforeAction.getEndtime()) <= 1 ) {
            beforeAction.setEndtime(starttime);
            actionMicroApi.update(beforeAction);
            return new Response<>(beforeAction);
        }

        action.setUserid(userLoginInfo.getUserId());
        action.setIpaddress(remoteAddr);
        action.setStarttime(starttime);
        Timestamp endtime = new Timestamp(System.currentTimeMillis());
        action.setEndtime(endtime);
        actionMicroApi.add(action);
        return new Response<>(action);
    }

    public static long compareTwoTimeStamps(Timestamp currentTime, Timestamp oldTime) {
        long milliseconds1 = oldTime.getTime();
        long milliseconds2 = currentTime.getTime();

        long diff = milliseconds2 - milliseconds1;
        long diffMinutes = diff / (60 * 1000);

        return diffMinutes;
    }
}
