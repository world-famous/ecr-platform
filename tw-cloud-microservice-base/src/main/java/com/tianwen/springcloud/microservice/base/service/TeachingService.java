package com.tianwen.springcloud.microservice.base.service;

import com.github.pagehelper.Page;
import com.tianwen.springcloud.commonapi.base.response.Response;
import com.tianwen.springcloud.commonapi.log.SystemServiceLog;
import com.tianwen.springcloud.commonapi.query.Pagination;
import com.tianwen.springcloud.commonapi.query.QueryTree;
import com.tianwen.springcloud.datasource.base.BaseService;
import com.tianwen.springcloud.datasource.util.QueryUtils;
import com.tianwen.springcloud.microservice.base.constant.IBaseMicroConstants;
import com.tianwen.springcloud.microservice.base.dao.TeachingMapper;
import com.tianwen.springcloud.microservice.base.entity.Teaching;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class TeachingService extends BaseService<Teaching>
{
    @Autowired
    private TeachingMapper teachingMapper;

    @SystemServiceLog(description = "")
    public Response<Teaching> search(QueryTree queryTree)
    {
        // PageHelper
        Pagination pagination = queryTree.getPagination();
        Map<String, Object> map = QueryUtils.queryTree2Map(queryTree);
        if (map.get("lang") == null)
            map.put("lang", IBaseMicroConstants.zh_CN);
        int count = teachingMapper.getCount(map);
        map.put("start", pagination.getStart());
        map.put("numPerPage", pagination.getNumPerPage());
        List<Teaching> queryList = teachingMapper.getList(map);
        Page<Teaching> result = new Page<Teaching>(pagination.getPageNo(), pagination.getNumPerPage());
        result.addAll(queryList);
        result.setTotal(count);
        return new Response<Teaching>(result);
    }
    
    @Override
    public boolean cacheable()
    {
        return false;
    }
    
    @Override
    public boolean listCacheable()
    {
        return false;
    }
}
