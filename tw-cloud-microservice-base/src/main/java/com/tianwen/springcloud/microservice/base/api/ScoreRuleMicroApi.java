package com.tianwen.springcloud.microservice.base.api;

import com.tianwen.springcloud.commonapi.base.ICRUDMicroApi;
import com.tianwen.springcloud.commonapi.base.response.Response;
import com.tianwen.springcloud.commonapi.query.QueryTree;
import com.tianwen.springcloud.microservice.base.entity.ScoreRule;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Map;

/**
 * 用户相关对外接口
 * 
 * @author wangbin
 * @version [版本号, 2017年5月11日]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */
@FeignClient(value = "base-service", url = "http://localhost:2226/base-service/scorerule")
public interface ScoreRuleMicroApi extends ICRUDMicroApi<ScoreRule>
{
    @RequestMapping(value = "/getList", method = RequestMethod.POST)
    public Response<ScoreRule> getList(@RequestBody QueryTree queryTree);

    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public Response<ScoreRule> edit(@RequestBody ScoreRule scoreRule);

    @RequestMapping(value = "/getExchangeRate", method = RequestMethod.GET)
    Response<Integer> getExchangeRate();

    @RequestMapping(value = "/getByScoreRule", method = RequestMethod.POST)
    public Response<ScoreRule> getByScoreRule(@RequestBody ScoreRule scoreRule);
}
