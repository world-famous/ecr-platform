package com.tianwen.springcloud.microservice.base.controller;

import com.tianwen.springcloud.commonapi.base.response.Response;
import com.tianwen.springcloud.commonapi.constant.IStateCode;
import com.tianwen.springcloud.commonapi.exception.ParameterException;
import com.tianwen.springcloud.commonapi.query.QueryTree;
import com.tianwen.springcloud.datasource.base.AbstractCRUDController;
import com.tianwen.springcloud.microservice.base.api.ScoreRuleMicroApi;
import com.tianwen.springcloud.microservice.base.entity.ScoreRule;
import com.tianwen.springcloud.microservice.base.service.ScoreRuleService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping(value = "/scorerule")
public class ScoreRuleController extends AbstractCRUDController<ScoreRule> implements ScoreRuleMicroApi
{
    @Autowired
    private ScoreRuleService scoreRuleService;

    @Override
    public Response<ScoreRule> getList(@RequestBody QueryTree querytree) {
        return scoreRuleService.getList(querytree);
    }

    @Override
    public Response<ScoreRule> edit(@RequestBody ScoreRule scoreRule) {
        if (scoreRule == null) {
            throw new ParameterException(IStateCode.PARAMETER_IS_EMPTY, "请求体为空");
        }
        scoreRuleService.updateNotNull(scoreRule);
        return new Response<> (scoreRule);
    }

    @Override
    public Response<Integer> getExchangeRate() {
        return scoreRuleService.getExchangeRate();
    }

    @Override
    public Response<ScoreRule> getByScoreRule(@RequestBody ScoreRule scoreRule) {
        return scoreRuleService.getByScoreRule(scoreRule);
    }

    @Override
    public void validate(MethodType methodType, Object p) {}
}
