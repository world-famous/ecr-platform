package com.tianwen.springcloud.ecrapi.controller;

import com.tianwen.springcloud.commonapi.base.response.Response;
import com.tianwen.springcloud.commonapi.query.QueryCondition;
import com.tianwen.springcloud.commonapi.query.QueryTree;
import com.tianwen.springcloud.ecrapi.api.ScoreRestApi;
import com.tianwen.springcloud.ecrapi.constant.ICommonConstants;
import com.tianwen.springcloud.ecrapi.constant.IErrorMessageConstants;
import com.tianwen.springcloud.microservice.base.entity.Area;
import com.tianwen.springcloud.microservice.base.entity.ScoreRule;
import com.tianwen.springcloud.microservice.base.entity.UserLoginInfo;
import com.tianwen.springcloud.microservice.operation.entity.Integral;
import com.tianwen.springcloud.microservice.operation.entity.IntegralInfo;
import com.tianwen.springcloud.microservice.operation.entity.Member;
import com.tianwen.springcloud.microservice.operation.entity.ScoreExportMan;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping(value = "/score")
public class ScoreRestController extends BaseRestController implements ScoreRestApi
{
    @Override
    public Response getScoreRule(@RequestBody QueryTree queryTree) {
        return scoreRuleMicroApi.getList(queryTree);
    }

    @Override
    public Response<ScoreRule> setScoreRule(@RequestBody ScoreRule scoreRuleInfo) {
        ScoreRule scoreRule = new ScoreRule();
        scoreRule.setRuleid(scoreRuleInfo.getRuleid());
        scoreRule.setScore(scoreRuleInfo.getScore());
        return scoreRuleMicroApi.edit(scoreRule);
    }

    @Override
    public Response getMyScore(@RequestHeader(value = "token") String token) {
        Response<UserLoginInfo> userResponse = getUserByToken(token);
        UserLoginInfo userLoginInfo = userResponse.getResponseEntity();
        if (userLoginInfo == null) return userResponse;

        Integer memberScore = memberMicroApi.getMyScore(userLoginInfo.getUserId()).getResponseEntity();
        return new Response<>(memberScore);
    }

    @Override
    public Response getUserScore(@PathVariable(value = "userid") String userid) {
        Map<String, Object> result = new HashMap<>();
        UserLoginInfo userLoginInfo = userMicroApi.getUserInfo(userid).getResponseEntity();
        if (userLoginInfo == null) {
            Response response = new Response();
            response.getServerResult().setResultCode(ICommonConstants.ERROR_CODE_USER_NOT_EXIST);
            response.getServerResult().setResultMessage(IErrorMessageConstants.ERR_MSG_USER_NOT_EXIST);
            return  response;
        }
        Integer memberScore = memberMicroApi.getMyScore(userid).getResponseEntity();

        result.put("loginname", userLoginInfo.getLoginName());
        result.put("realname", userLoginInfo.getRealName());
        result.put("score", memberScore);

        return new Response<>(result);
    }

    @Override
    public Response<Integral> getScoreDetail(@RequestBody QueryTree queryTree, @RequestHeader(value = "token") String token) {
        UserLoginInfo loginInfo = getUserByToken(token).getResponseEntity();
        queryTree.addCondition(new QueryCondition("userid", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, loginInfo.getUserId()));
        return integralMicroApi.getList(queryTree);
    }

    @Override
    public Response getScoreDetailByUserId(@RequestBody QueryTree queryTree) {
        return integralMicroApi.getList(queryTree);
    }

    @Override
    public Response<Member> getAccountScoreList(@RequestBody QueryTree queryTree) {
        if (!CollectionUtils.isEmpty(queryTree.getConditions())) {
            List<String> userids = userMicroApi.getUserIdsByQueryTree(queryTree).getPageInfo().getList();
            queryTree.getConditions().add(new QueryCondition("userids", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, userids));
        }
        Response response = memberMicroApi.getList(queryTree);
        List<Member> members = response.getPageInfo().getList();
        if (!CollectionUtils.isEmpty(members)) {
            for (Member member : members) {
                String userid = member.getUserid();
                UserLoginInfo userLoginInfo = userMicroApi.get(userid).getResponseEntity();
                if (userLoginInfo != null) {
                    member.setRealname(userLoginInfo.getRealName());
                    member.setLoginname(userLoginInfo.getLoginName());
                }
            }
        }

        return response;
    }

    @Override
    public Response<Member> batchSetAccountScore(@RequestBody Map<String, Object> accountScoreInfo, @RequestHeader(value = "token") String token) {
        List<String> memberids = (List<String>) accountScoreInfo.get("memberids");
        Integer score = (Integer) accountScoreInfo.get("score");

        List<Member> members = new ArrayList<>();
        if (!CollectionUtils.isEmpty(memberids)) {
            for (String memberid : memberids) {
                Member member = new Member();
                member.setMemberid(memberid);
                member.setUseintegral(score);
                memberMicroApi.update(member);
                members.add(member);
            }
        }
        return new Response<>(members);
    }

    @Override
    public Response<Integral> getAccountScoreLog(@RequestBody QueryTree queryTree) {
        if (!CollectionUtils.isEmpty(queryTree.getConditions())) {
            List<String> userids = userMicroApi.getUserIdsByQueryTree(queryTree).getPageInfo().getList();
            queryTree.getConditions().add(new QueryCondition("userids", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, userids));
        }
        Response response = integralMicroApi.getList(queryTree);
        List<Integral> integrals = response.getPageInfo().getList();
        if (!CollectionUtils.isEmpty(integrals)) {
            for (Integral integral : integrals) {
                UserLoginInfo userLoginInfo = userMicroApi.get(integral.getUserid()).getResponseEntity();
                if (userLoginInfo != null) {
                    integral.setRealname(userLoginInfo.getRealName());
                    integral.setLoginname(userLoginInfo.getLoginName());
                }
            }
        }
        return response;
    }

    @Override
    public Response<ScoreExportMan> getTop10ScoreUser() {
        Response response = integralMicroApi.getExportManList();
        List<ScoreExportMan> exportMans = response.getPageInfo().getList();
        if (!CollectionUtils.isEmpty(exportMans)) {
            for (ScoreExportMan exportMan : exportMans) {
                String userid = exportMan.getUserid();
                UserLoginInfo userLoginInfo = userMicroApi.get(userid).getResponseEntity();
                if (userLoginInfo != null) {
                    exportMan.setUsername(userLoginInfo.getRealName());
                }
            }
        }
        return response;
    }

    @Override
    public Response getScoreStatistic(@RequestHeader(value = "token") String token) {
        List<Area> areas = areaMicroApi.getAreaInfoList().getPageInfo().getList();
        return  integralMicroApi.getScoreStatistics(parseToAreaInfoList(areas));
    }

    @Override
    public Response getScoreTotalIntegralInfo() {
        Map<String, Object> result = new HashMap<>();
        QueryTree queryTree = new QueryTree();

        //today total integral info
        QueryCondition todayCondition = new QueryCondition("today", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, "");
        queryTree.addCondition(todayCondition);
        IntegralInfo today = integralMicroApi.getTodayIntegralInfo(queryTree).getResponseEntity();
        result.put("today", today);
        queryTree.getConditions().remove(todayCondition);

        // week total integral info
        QueryCondition weekCondition = new QueryCondition("week", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, "");
        queryTree.addCondition(weekCondition);
        IntegralInfo week = integralMicroApi.getTodayIntegralInfo(queryTree).getResponseEntity();
        result.put("week", week);
        queryTree.getConditions().remove(weekCondition);

        // week total integral info
        QueryCondition monthCondition = new QueryCondition("month", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, "");
        queryTree.addCondition(monthCondition);
        IntegralInfo month = integralMicroApi.getTodayIntegralInfo(queryTree).getResponseEntity();
        result.put("month", month);

        //total useable integral
        result.put("useabletotal", memberMicroApi.getUseableTotalScore(new QueryTree()).getResponseEntity());

        return new Response(result);
    }

    @Override
    public Response getExchangeRate() {
        return scoreRuleMicroApi.getExchangeRate();
    }
}
