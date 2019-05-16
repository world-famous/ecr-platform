package com.tianwen.springcloud.microservice.base.service;

import com.github.pagehelper.Page;
import com.tianwen.springcloud.commonapi.base.response.Response;
import com.tianwen.springcloud.commonapi.query.Pagination;
import com.tianwen.springcloud.commonapi.query.QueryTree;
import com.tianwen.springcloud.datasource.base.BaseService;
import com.tianwen.springcloud.datasource.util.QueryUtils;
import com.tianwen.springcloud.microservice.base.dao.KnowledgeMapper;
import com.tianwen.springcloud.microservice.base.entity.Area;
import com.tianwen.springcloud.microservice.base.entity.DictItem;
import com.tianwen.springcloud.microservice.base.entity.Knowledge;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class KnowledgeService extends BaseService<Knowledge> {
    @Autowired
    private KnowledgeMapper knowledgeMapper;

    public Response<Knowledge> search(QueryTree queryTree) {
        Pagination pagination = queryTree.getPagination();

        Map<String, Object> map = QueryUtils.queryTree2Map(queryTree);

        Long count = knowledgeMapper.count(map);

        map.put("start", pagination.getStart());
        map.put("numPerPage", pagination.getNumPerPage());

        List<Knowledge> queryList = knowledgeMapper.queryForList(map);

        Page<Knowledge> page = new Page<>(pagination.getPageNo(), pagination.getNumPerPage());
        page.addAll(queryList);
        page.setTotal(count);

        return new Response<>(page);
    }

    public List<DictItem> getSchoolSectionList(QueryTree queryTree)
    {
        Map<String, Object> map = QueryUtils.queryTree2Map(queryTree);

        return knowledgeMapper.getSchoolSectionList(map);
    }

    public List<DictItem> getSubjectList(QueryTree queryTree)
    {
        Map<String, Object> map = QueryUtils.queryTree2Map(queryTree);

        return knowledgeMapper.getSubjectList(map);
    }

    public String getMaxSequence(String parentknowledgeid)
    {
        return knowledgeMapper.getMaxSequence(parentknowledgeid);
    }
}
