package com.tianwen.springcloud.microservice.base.dao;

import com.tianwen.springcloud.datasource.mapper.MyMapper;
import com.tianwen.springcloud.microservice.base.entity.ScoreRule;
import com.tianwen.springcloud.microservice.base.entity.UserLoginInfo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface ScoreruleMapper extends MyMapper<ScoreRule>
{
    /**
     *
     * @param map
     * @return
     */
    List<ScoreRule> queryForList(Map<String, Object> map);

    ScoreRule getByScoreRule(ScoreRule scoreRule);

    /**
     * exchange rate
     * @return
     */
    Integer getExchangeRate();
}
