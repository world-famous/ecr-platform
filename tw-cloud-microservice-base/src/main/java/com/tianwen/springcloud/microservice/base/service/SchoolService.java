package com.tianwen.springcloud.microservice.base.service;

import com.github.pagehelper.Page;
import com.tianwen.springcloud.commonapi.base.response.Response;
import com.tianwen.springcloud.commonapi.query.Pagination;
import com.tianwen.springcloud.commonapi.query.QueryTree;
import com.tianwen.springcloud.datasource.base.BaseService;
import com.tianwen.springcloud.datasource.util.QueryUtils;
import com.tianwen.springcloud.microservice.base.dao.SchoolMapper;
import com.tianwen.springcloud.microservice.base.dao.SchoolTypeMapper;
import com.tianwen.springcloud.microservice.base.entity.DictItem;
import com.tianwen.springcloud.microservice.base.entity.School;
import com.tianwen.springcloud.microservice.base.entity.SchoolType;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.sql.Timestamp;
import java.util.*;

@Service
public class SchoolService extends BaseService<School>
{
    @Autowired
    private SchoolMapper schoolMapper;

    @Autowired
    private SchoolTypeMapper schoolTypeMapper;

    public void fixParam(Map<String, Object> map) {
        {
            Object begin = map.get("begin_time");
            if (begin != null) {
                map.remove("begin_time");
                if (!begin.toString().isEmpty()) {
                    Timestamp beginDate = Timestamp.valueOf(begin.toString() + " 00:00:00");
                    map.remove("begin_time");
                    map.put("begin_time", beginDate);
                }
            }
        }

        {
            Object end = map.get("end_time");
            if (end != null) {
                map.remove("end_time");
                if (!end.toString().isEmpty()) {
                    Date endDate = Timestamp.valueOf(end.toString() + " 23:59:59");
                    map.remove("end_time");
                    map.put("end_time", endDate);
                }
            }
        }
    }

    public Response<School> search(QueryTree queryTree)
    {
        if (queryTree.isSingleTable())
        {
            Example example = QueryUtils.queryTree2Example(queryTree, School.class);
            return new Response<School>(super.selectByExample(example));
        }

        // PageHelper
        Pagination pagination = queryTree.getPagination();
        if (pagination != null)
        {
            queryTree.setPagination(pagination);
        }
        Map<String, Object> map = QueryUtils.queryTree2Map(queryTree);
        fixParam(map);
        List<School> queryList = schoolMapper.getList(map), pageList;
        int count = queryList.size();

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

        if (start > queryList.size())
            pageList = new ArrayList<School>();
        else
            pageList = queryList.subList(start ,start + pageSize <= queryList.size() ? start + pageSize : queryList.size());

        Page<School> result = new Page<School>(pageNo, pageSize);
        result.addAll(pageList);
        result.setTotal(count);
        return new Response<School>(result);
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

    public List<SchoolType> getSchoolTypeList() {
        return schoolTypeMapper.getSchoolTypeList();
    }

    public Response<School> getAllSchoolList(QueryTree queryTree) {
        // PageHelper
        Pagination pagination = queryTree.getPagination();
        if (pagination != null)
        {
            queryTree.setPagination(pagination);
        }
        Map<String, Object> map = QueryUtils.queryTree2Map(queryTree);
        fixParam(map);
        List<School> rawData = schoolMapper.getSchoolList(map), pageList, queryList;

        int i=0;
        for (School query : rawData){
            for (int j=0;j<i;j++){
                if (query.getOrgid().equals(rawData.get(j).getOrgid()) ){
                    String schooltypeid = rawData.get(j).getSchooltypeid();
                    String schooltypename = rawData.get(j).getSchooltypename();
                    if (!StringUtils.isEmpty(schooltypename)) {
                        rawData.get(j).setSchooltypeid(schooltypeid + "," + query.getSchooltypeid());
                        rawData.get(j).setSchooltypename(schooltypename + "," + query.getSchooltypename());
                    }
                    query.setOrgid("");
                }
            }
            i++;
        }

        pageList = new ArrayList<>();
        for (School data : rawData)
            if (!data.getOrgid().isEmpty())
                pageList.add(data);

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

        Page<School> result = new Page<School>(pageNo, pageSize);
        result.addAll(pageList);
        result.setTotal(pageList.size());
        return new Response<School>(result);
    }

    public Response<School> getSchoolList(QueryTree queryTree) {
        // PageHelper
        Pagination pagination = queryTree.getPagination();
        if (pagination != null)
        {
            queryTree.setPagination(pagination);
        }
        Map<String, Object> map = QueryUtils.queryTree2Map(queryTree);
        fixParam(map);
        List<School> rawData = schoolMapper.getSchoolList(map), pageList, queryList;

        int i=0;
        for (School query : rawData){
            for (int j=0;j<i;j++){
                if (query.getOrgid().equals(rawData.get(j).getOrgid()) ){
                    String schooltypeid = rawData.get(j).getSchooltypeid();
                    String schooltypename = rawData.get(j).getSchooltypename();
                    if (!StringUtils.isEmpty(schooltypename)) {
                        rawData.get(j).setSchooltypeid(schooltypeid + "," + query.getSchooltypeid());
                        rawData.get(j).setSchooltypename(schooltypename + "," + query.getSchooltypename());
                    }
                    query.setOrgid("");
                }
            }
            i++;
        }

        queryList = new ArrayList<>();
        for (School data : rawData)
            if (!data.getOrgid().isEmpty())
                queryList.add(data);

        int count = queryList.size();

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

        if (start > queryList.size())
            pageList = new ArrayList<School>();
        else
            pageList = queryList.subList(start ,start + pageSize <= queryList.size() ? start + pageSize : queryList.size());

        Page<School> result = new Page<School>(pageNo, pageSize);
        result.addAll(pageList);
        result.setTotal(count);
        return new Response<School>(result);
    }


    public Response<DictItem> getSchoolSectionBySchoolid(String schoolid) {
        Map<String, String> map = new HashMap<>();
        map.put("schoolid", schoolid);
        return new Response<>(schoolMapper.getSchoolSectionBySchoolid(map));
    }

    public Response<School> getListByAreaAndConType(QueryTree queryTree) {
        Map<String, Object> map = QueryUtils.queryTree2Map(queryTree);
        fixParam(map);
        Pagination pagination = queryTree.getPagination();
        Long count = schoolMapper.getCountByAreaAndConType(map);
        map.put("start", pagination.getStart());
        map.put("numPerPage", pagination.getNumPerPage());
        List<School> schools = schoolMapper.queryListByAreaAndConType(map);

        Response<School> response = new Response<>(schools);
        response.getPageInfo().setTotal(count);
        return response;
    }
    //Author : GOD 2019-2-13
    public boolean isValidSchoolOrderno(Integer orderNo) {
        Map<String, Object> map = new HashMap<>();
        map.put("orderno", orderNo);
        Long count = schoolMapper.getSchoolCountByOrderno(map);
        if ( count>0 )
            return false;
        else
            return true;
    }
    //Author : GOD 2019-2-13
}
