package com.tianwen.springcloud.ecrapi.controller;

import com.tianwen.springcloud.commonapi.base.response.Response;
import com.tianwen.springcloud.commonapi.query.QueryCondition;
import com.tianwen.springcloud.commonapi.query.QueryTree;
import com.tianwen.springcloud.ecrapi.api.AdvertRestApi;
import com.tianwen.springcloud.ecrapi.constant.ICommonConstants;
import com.tianwen.springcloud.ecrapi.constant.IErrorMessageConstants;
import com.tianwen.springcloud.ecrapi.util.SensitiveWordFilter;
import com.tianwen.springcloud.microservice.base.entity.UserLoginInfo;
import com.tianwen.springcloud.microservice.bussinessassist.api.AdvertMicroApi;
import com.tianwen.springcloud.microservice.bussinessassist.entity.Advert;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/advret")
public class AdvertRestController extends BaseRestController implements AdvertRestApi {
    @Autowired
    private AdvertMicroApi advertMicroApi;

    @Override
    public Response getAdvretList(@RequestBody QueryTree queryTree, @RequestHeader(value = "token") String token) {
        return advertMicroApi.getList(queryTree);
    }

    @Override
    public Response getAdvretCount(@RequestBody QueryTree queryTree, @RequestHeader(value = "token") String token) {
        return advertMicroApi.getCount(queryTree);
    }

    @Override
    public Response insertAdvret(@RequestBody Advert entity, @RequestHeader(value = "token") String token) {
        /**
         * @author: jong
         * @date: 2019-02-08,11
         * @comment: check sensitive words
         */
        SensitiveWordFilter swFilter = new SensitiveWordFilter();
        entity.setName(swFilter.process(entity.getName()));
        /*if (swFilter.getSensitiveWordList().size() > 0) {
            return new Response<>(IErrorMessageConstants.ERR_CODE_SENSITIVE_WORD_DETECTED,
                    IErrorMessageConstants.ERR_MSG_SENSITIVE_WORD_DETECTED + ": " + swFilter.getSensitiveWordList().get(0));
        }*/
        /*
         * ----------------------------------------------------------------------
         */

        QueryTree queryTree = new QueryTree();
        queryTree.addCondition(new QueryCondition("sequence", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, entity.getSequence()));
        List<Advert> exists = advertMicroApi.search(queryTree).getPageInfo().getList();

        if (!CollectionUtils.isEmpty(exists))
            return new Response(IErrorMessageConstants.ERR_CODE_SAME_ITEM_EXIST, IErrorMessageConstants.ERR_MSG_SAME_ITEM_EXIST);

        Response<UserLoginInfo> loginInfoResponse = getUserByToken(token);
        String resultCode = loginInfoResponse.getServerResult().getResultCode();
        if (!StringUtils.equals(resultCode, ICommonConstants.RESPONSE_RESULT_SUCCESS)) return loginInfoResponse;

        UserLoginInfo user = loginInfoResponse.getResponseEntity();
        entity.setCreator(user.getUserId());
        entity.setCreatetime(new Timestamp(System.currentTimeMillis()));
        return advertMicroApi.insert(entity);
    }

    @Override
    public Response modifyAdvret(@RequestBody Advert entity, @RequestHeader(value = "token") String token) {
        QueryTree queryTree = new QueryTree();
        queryTree.addCondition(new QueryCondition("sequence", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, entity.getSequence()));
        List<Advert> exists = advertMicroApi.search(queryTree).getPageInfo().getList();

        // Check if modifying entity has the same sequence
        if (!CollectionUtils.isEmpty(exists) && !exists.get(0).getAdvertid().equalsIgnoreCase(entity.getAdvertid()))
            return new Response(IErrorMessageConstants.ERR_CODE_SAME_ITEM_EXIST, IErrorMessageConstants.ERR_MSG_SAME_ITEM_EXIST);

        Response<UserLoginInfo> loginInfoResponse = getUserByToken(token);
        String resultCode = loginInfoResponse.getServerResult().getResultCode();
        if (!StringUtils.equals(resultCode, ICommonConstants.RESPONSE_RESULT_SUCCESS)) return loginInfoResponse;
        return advertMicroApi.modify(entity);
    }

    @Override
    public Response setAdvretStatus(@RequestBody Map<String, Object> param, @RequestHeader(value = "token") String token) {
         Response<UserLoginInfo> loginInfoResponse = getUserByToken(token);
        String resultCode = loginInfoResponse.getServerResult().getResultCode();
        if (!StringUtils.equals(resultCode, ICommonConstants.RESPONSE_RESULT_SUCCESS)) return loginInfoResponse;
        Advert entity = new Advert();
        if (param.get("advertid") != null)
            entity.setAdvertid(param.get("advertid").toString());
        if (param.get("status") != null)
            entity.setStatus(param.get("status").toString());
         if (param.get("sequence") != null) {
             entity.setSequence(param.get("sequence").toString());
         }
        return modifyAdvret(entity, token);
    }

    @Override
    public Response removeAdvret(@PathVariable(value = "advertid") String advertid, @RequestHeader(value = "token") String token) {
        Response<UserLoginInfo> loginInfoResponse = getUserByToken(token);
        String resultCode = loginInfoResponse.getServerResult().getResultCode();
        if (!StringUtils.equals(resultCode, ICommonConstants.RESPONSE_RESULT_SUCCESS)) return loginInfoResponse;
        return advertMicroApi.remove(advertid);
    }
}
