package com.tianwen.springcloud.ecrapi.controller;

import com.tianwen.springcloud.commonapi.base.response.Response;
import com.tianwen.springcloud.commonapi.query.OrderMethod;
import com.tianwen.springcloud.commonapi.query.QueryCondition;
import com.tianwen.springcloud.commonapi.query.QueryTree;
import com.tianwen.springcloud.ecrapi.api.MessageRestApi;
import com.tianwen.springcloud.ecrapi.constant.ICommonConstants;
import com.tianwen.springcloud.ecrapi.constant.IErrorMessageConstants;
import com.tianwen.springcloud.microservice.base.entity.UserLoginInfo;
import com.tianwen.springcloud.microservice.bussinessassist.api.MessageMicroApi;
import com.tianwen.springcloud.microservice.bussinessassist.entity.Message;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/message")
public  class MessageRestController extends BaseRestController implements MessageRestApi {
    @Autowired
    private MessageMicroApi messageMicroApi;

    @Override
    public Response getMessageListByUser(@RequestBody QueryTree queryTree, @RequestHeader(value = "token") String token) {
        Response<UserLoginInfo> loginInfoResponse = getUserByToken(token);
        String resultCode = loginInfoResponse.getServerResult().getResultCode();
        if (!StringUtils.equals(resultCode, ICommonConstants.RESPONSE_RESULT_SUCCESS)) return loginInfoResponse;

        UserLoginInfo user = loginInfoResponse.getResponseEntity();
        queryTree.addCondition(new QueryCondition("userid", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, user.getUserId()));

        List<OrderMethod> orderMethods = new ArrayList<>();
        orderMethods.add(new OrderMethod("receivetime", OrderMethod.Method.DESC));
        queryTree.setOrderMethods(orderMethods);

        return messageMicroApi.search(queryTree);
    }

    @Override
    public Response setMessageRead(@PathVariable(value = "newsid") String newsid) {
        Message message = messageMicroApi.get(newsid).getResponseEntity();
        if (message == null)
            return new Response(IErrorMessageConstants.ERR_CODE_NEWS_IS_NOT_EXISTED,IErrorMessageConstants.ERR_MSG_NEWS_IS_NOT_EXISTED);

        message.setStatus("1");
        return messageMicroApi.update(message);
    }

    @Override
    public Response<Message> insertMessage(@RequestBody Message message, @RequestHeader(value = "token") String token) {
        UserLoginInfo user = getUserByToken(token).getResponseEntity();
        message.setUserid(user.getUserId());
        message.setReceivetime(new Timestamp(System.currentTimeMillis()));
        message.setStatus("0");
        return null;
    }
}
