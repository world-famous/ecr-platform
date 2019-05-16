package com.tianwen.springcloud.microservice.base.service;

import com.github.pagehelper.Page;
import com.tianwen.springcloud.commonapi.base.response.Response;
import com.tianwen.springcloud.commonapi.log.SystemServiceLog;
import com.tianwen.springcloud.commonapi.query.Pagination;
import com.tianwen.springcloud.commonapi.query.QueryTree;
import com.tianwen.springcloud.datasource.base.BaseService;
import com.tianwen.springcloud.datasource.util.QueryUtils;
import com.tianwen.springcloud.microservice.base.dao.SchoolTypeMapper;
import com.tianwen.springcloud.microservice.base.entity.SchoolType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.Map;

@Service
public class SchoolTypeService extends BaseService<SchoolType>
{
    @Autowired
    private SchoolTypeMapper schoolTypeMapper;

    @SystemServiceLog(description = "")
    public Response<SchoolType> search(QueryTree queryTree)
    {
        if (queryTree.isSingleTable())
        {
            Example example = QueryUtils.queryTree2Example(queryTree, SchoolType.class);
            return new Response<SchoolType>(super.selectByExample(example));
        }

        // PageHelper
        Pagination pagination = queryTree.getPagination();
        if (pagination != null)
        {
            queryTree.setPagination(pagination);
        }
        Map<String, Object> map = QueryUtils.queryTree2Map(queryTree);
        Long count = schoolTypeMapper.getCount(map);

        int start, pageSize, pageNo;

        try {
            start = pagination.getStart();
        }
        catch (NullPointerException e) {
            start = 0;
        }

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

        map.put("start", start);
        map.put("numPerPage", pageSize);
        List<SchoolType> queryList = schoolTypeMapper.getList(map);
        Page<SchoolType> result = new Page<SchoolType>(pageNo, pageSize);
        result.addAll(queryList);
        result.setTotal(count);
        return new Response<SchoolType>(result);
    }

    public void connectSchoolType(Map<String, String> map) {
        if (schoolTypeMapper.isconnectSchoolType(map) == 0)
            schoolTypeMapper.connectSchoolType(map);
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
