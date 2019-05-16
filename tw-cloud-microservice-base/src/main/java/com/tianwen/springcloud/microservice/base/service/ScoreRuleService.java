package com.tianwen.springcloud.microservice.base.service;

import com.github.pagehelper.Page;
import com.tianwen.springcloud.commonapi.base.response.Response;
import com.tianwen.springcloud.commonapi.log.SystemServiceLog;
import com.tianwen.springcloud.commonapi.query.Pagination;
import com.tianwen.springcloud.commonapi.query.QueryTree;
import com.tianwen.springcloud.datasource.base.BaseService;
import com.tianwen.springcloud.datasource.util.QueryUtils;
import com.tianwen.springcloud.microservice.base.dao.ScoreruleMapper;
import com.tianwen.springcloud.microservice.base.entity.ScoreRule;
import com.tianwen.springcloud.microservice.base.entity.Teacher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.Map;


@Service
public class ScoreRuleService extends BaseService<ScoreRule>
{
    @Autowired
    private ScoreruleMapper scoreruleMapper;

    @SystemServiceLog(description = "scorerulelist")
    public Response<ScoreRule> getList(QueryTree queryTree)
    {
        Pagination pagination = queryTree.getPagination();
        Map<String, Object> map = QueryUtils.queryTree2Map(queryTree);
        if (pagination != null) {
            queryTree.setPagination(null);
        }
        Example example = QueryUtils.queryTree2Example(queryTree, ScoreRule.class);
        Integer count = selectByExample(example).size();
        List<ScoreRule> queryList = scoreruleMapper.queryForList(map);

        int pageSize, pageNo;

        try {
            pageSize = pagination.getNumPerPage();
        }
        catch (NullPointerException e) {
            pageSize = 200;
        }

        try {
            pageNo = pagination.getPageNo();
        }
        catch (NullPointerException e) {
            pageNo = 1;
        }

        Page<ScoreRule> result = new Page<>(pageNo, pageSize);
        result.addAll(queryList);
        result.setTotal(count);
        return new Response<>(result);
    }

    public Response<ScoreRule> getByScoreRule(ScoreRule scoreRule) {
        return new Response<>(scoreruleMapper.getByScoreRule(scoreRule));
    }

    public Response<Integer> getExchangeRate() {
        return new Response(scoreruleMapper.getExchangeRate());
    }
}
