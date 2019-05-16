package com.tianwen.springcloud.ecrapi.api;

import com.tianwen.springcloud.commonapi.base.response.Response;
import com.tianwen.springcloud.commonapi.query.QueryTree;
import com.tianwen.springcloud.microservice.base.entity.ScoreRule;
import com.tianwen.springcloud.microservice.operation.entity.Integral;
import com.tianwen.springcloud.microservice.operation.entity.Member;
import com.tianwen.springcloud.microservice.operation.entity.ScoreExportMan;
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

public interface ScoreRestApi
{
    @RequestMapping(value = "/getScoreRule", method = RequestMethod.POST)
    public Response getScoreRule(@RequestBody QueryTree queryTree);

    @RequestMapping(value = "/setScoreRule", method = RequestMethod.POST)
    public Response<ScoreRule> setScoreRule(@RequestBody ScoreRule scoreRuleInfo);

    @RequestMapping(value = "/getMyScore", method = RequestMethod.GET)
    public Response<Integer> getMyScore(@RequestHeader(value = "token") String token);

    @RequestMapping(value = "/getUserScore/{userid}", method = RequestMethod.GET)
    Response getUserScore(@PathVariable(value = "userid") String userid);

    @RequestMapping(value = "/getScoreDetail", method = RequestMethod.POST)
    public Response<Integral> getScoreDetail(@RequestBody QueryTree queryTree, @RequestHeader(value = "token") String token);

    @RequestMapping(value = "/getScoreDetailByUserId", method = RequestMethod.POST)
    Response getScoreDetailByUserId(@RequestBody QueryTree queryTree);

    @RequestMapping(value = "/getAccountScoreList", method=RequestMethod.POST)
    public Response<Member> getAccountScoreList(@RequestBody QueryTree queryTree);

    @RequestMapping(value = "/batchSetAccountScore", method = RequestMethod.POST)
    public Response<Member> batchSetAccountScore(@RequestBody Map<String, Object> accountScoreInfo, @RequestHeader(value = "token") String token);

    @RequestMapping(value = "/getAccountScoreLog", method = RequestMethod.POST)
    public Response<Integral> getAccountScoreLog(@RequestBody QueryTree queryTree);

    @RequestMapping(value = "/getTop10ScoreUser", method = RequestMethod.GET)
    public Response<ScoreExportMan> getTop10ScoreUser();

    @RequestMapping(value = "/getScoreStatistic", method = RequestMethod.GET)
    public Response getScoreStatistic(@RequestHeader(value = "token") String token);

    @RequestMapping(value = "/getScoreTotalIntegralInfo", method = RequestMethod.GET)
    public Response getScoreTotalIntegralInfo();

    @RequestMapping(value = "/getExchangeRate", method = RequestMethod.GET)
    Response getExchangeRate();
}
