package com.tianwen.springcloud.microservice.base.service;

import com.github.pagehelper.Page;
import com.tianwen.springcloud.commonapi.base.response.Response;
import com.tianwen.springcloud.commonapi.log.SystemServiceLog;
import com.tianwen.springcloud.commonapi.query.Pagination;
import com.tianwen.springcloud.commonapi.query.QueryTree;
import com.tianwen.springcloud.datasource.base.BaseService;
import com.tianwen.springcloud.datasource.util.QueryUtils;
import com.tianwen.springcloud.microservice.base.dao.OrganizationMapper;
import com.tianwen.springcloud.microservice.base.entity.Organization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.Map;

@Service
public class OrganizationService extends BaseService<Organization>
{
    @Autowired
    private OrganizationMapper organizationMapper;

    @SystemServiceLog(description = "")
    public Response<Organization> search(QueryTree queryTree)
    {
        if (queryTree.isSingleTable())
        {
            Example example = QueryUtils.queryTree2Example(queryTree, Organization.class);
            return new Response<Organization>(super.selectByExample(example));
        }

        // PageHelper
        Pagination pagination = queryTree.getPagination();
        if (pagination != null)
        {
            queryTree.setPagination(pagination);
        }
        Map<String, Object> map = QueryUtils.queryTree2Map(queryTree);
        Long count = organizationMapper.getCount(map);
        Integer start, pageSize, pageNo;

        try{
            start = pagination.getStart();
        }
        catch (NullPointerException e) {
            start = 0;
        }
        map.put("start", start);

        try{
            pageSize = pagination.getNumPerPage();
        }
        catch (NullPointerException e) {
            pageSize = 10;
        }
        map.put("numPerPage", pageSize);

        List<Organization> queryList = organizationMapper .getList(map);

        try{
            pageNo = pagination.getPageNo();
        }
        catch (NullPointerException e){
            pageNo = 1;
        }

        Page<Organization> result = new Page<Organization>(pageNo, pageSize);
        result.addAll(queryList);
        result.setTotal(count);
        return new Response<Organization>(result);
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
