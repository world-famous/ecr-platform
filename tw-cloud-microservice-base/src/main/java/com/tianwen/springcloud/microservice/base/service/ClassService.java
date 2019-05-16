package com.tianwen.springcloud.microservice.base.service;

import com.github.pagehelper.Page;
import com.tianwen.springcloud.commonapi.base.response.Response;
import com.tianwen.springcloud.commonapi.log.SystemServiceLog;
import com.tianwen.springcloud.commonapi.query.Pagination;
import com.tianwen.springcloud.commonapi.query.QueryTree;
import com.tianwen.springcloud.datasource.base.BaseService;
import com.tianwen.springcloud.datasource.util.QueryUtils;
import com.tianwen.springcloud.microservice.base.dao.ClassMapper;
import com.tianwen.springcloud.microservice.base.entity.ClassInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ClassService extends BaseService<ClassInfo>
{
    @Autowired
    private ClassMapper classMapper;

    @SystemServiceLog(description = "")
    public Response<ClassInfo> search(QueryTree queryTree)
    {
        // PageHelper
        Pagination pagination = queryTree.getPagination();
        Map<String, Object> map = QueryUtils.queryTree2Map(queryTree);
        int count = classMapper.getCount(map);
        map.put("start", pagination.getStart());
        map.put("numPerPage", pagination.getNumPerPage());
        List<ClassInfo> queryList = classMapper.getList(map);
        Page<ClassInfo> result = new Page<ClassInfo>(pagination.getPageNo(), pagination.getNumPerPage());
        result.addAll(queryList);
        result.setTotal(count);
        return new Response<ClassInfo>(result);
    }

    public ClassInfo getClassById(Map<String, Object> param)
    {
        return classMapper.getClassById(param);
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
