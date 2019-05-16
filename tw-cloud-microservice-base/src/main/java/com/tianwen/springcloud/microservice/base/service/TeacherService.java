package com.tianwen.springcloud.microservice.base.service;

import com.github.pagehelper.Page;
import com.tianwen.springcloud.commonapi.base.response.Response;
import com.tianwen.springcloud.commonapi.log.SystemServiceLog;
import com.tianwen.springcloud.commonapi.query.OrderMethod;
import com.tianwen.springcloud.commonapi.query.Pagination;
import com.tianwen.springcloud.commonapi.query.QueryTree;
import com.tianwen.springcloud.datasource.base.BaseService;
import com.tianwen.springcloud.datasource.util.QueryUtils;
import com.tianwen.springcloud.microservice.base.dao.TeacherMapper;
import com.tianwen.springcloud.microservice.base.entity.Teacher;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.sql.Timestamp;
import java.util.*;

@Service
public class TeacherService extends BaseService<Teacher>
{
    @Autowired
    private TeacherMapper teacherMapper;

    @SystemServiceLog(description = "")
    public Response<Teacher> search(QueryTree queryTree)
    {
        // PageHelper
        Pagination pagination = queryTree.getPagination();
        if (pagination != null)
        {
            queryTree.setPagination(pagination);
        }
        Map<String, Object> map = QueryUtils.queryTree2Map(queryTree);
        int count = teacherMapper.getCount(map);

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
        List<Teacher> queryList = teacherMapper.getList(map);
        Page<Teacher> result = new Page<Teacher>(pageNo, pageSize);
        result.addAll(queryList);
        result.setTotal(count);
        return new Response<>(result);
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

    public Response<Teacher> getTeacherList(QueryTree queryTree) {
        // PageHelper
        Pagination pagination = queryTree.getPagination();
        if (pagination != null)
        {
            queryTree.setPagination(pagination);
        }
        Map<String, Object> map = QueryUtils.queryTree2Map(queryTree);
        List<Teacher> queryList = teacherMapper.getTeacherList(map), pageList;
        List<Teacher> querySubList = teacherMapper.getTeacherSubList(map);
        for(Teacher query : querySubList){
            queryList.add(query);
        }

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
            pageList = new ArrayList<Teacher>();
        else
            pageList = queryList.subList(start ,start + pageSize <= queryList.size() ? start + pageSize : queryList.size());

        Page<Teacher> result = new Page<Teacher>(pageNo, pageSize);
        result.addAll(pageList);
        result.setTotal(count);
        return new Response<Teacher>(result);
    }

    public Response<String> getTeacherIds(QueryTree queryTree) {
        Map<String, Object> map = QueryUtils.queryTree2Map(queryTree);
        List<String> userids = teacherMapper.getTeacherIds(map);
        if (userids != null) {
            userids.add("");
            userids.add("");
        }
        return new Response<>(userids);
    }

    public Response<Teacher> getNamedTeacherList(QueryTree queryTree) {
        // PageHelper
        Pagination pagination = queryTree.getPagination();
        if (pagination != null)
        {
            queryTree.setPagination(pagination);
        }
        Map<String, Object> map = QueryUtils.queryTree2Map(queryTree);
        map.put("sharerange", "1");
        List<Teacher> resultList = new ArrayList<>();
        List<Teacher> queryList = teacherMapper.getTeacherList(map), pageList;
        List<Teacher> querySubList = teacherMapper.getTeacherSubList(map);
        for(Teacher query : queryList){
            resultList.add(query);
        }
        for(Teacher query : querySubList){
            resultList.add(query);
        }
        queryList.clear();
        querySubList.clear();

        Object object;
        String userareaid = null;
        object = map.get("userareaid");
        if (object != null)
            userareaid = object.toString();

        if (!StringUtils.isEmpty(userareaid)) {
            String[] userareaids = userareaid.split(",");
            String areaid = null;
            object = map.get("areaid");
            try {
                areaid = object.toString();
            }
            catch (NullPointerException e) {
                areaid = null;
            }

            if(userareaids.length == 2 && (StringUtils.equals(areaid,userareaids[1]) || StringUtils.isEmpty(areaid))){
                map.replace("sharerange","1","2");
                if (StringUtils.isEmpty(areaid))
                    map.put("areaid",userareaids[1]);
                queryList= teacherMapper.getTeacherList(map);
                querySubList = teacherMapper.getTeacherSubList(map);
                for(Teacher query : queryList){
                    resultList.add(query);
                }
                for(Teacher query : querySubList){
                    resultList.add(query);
                }
                queryList.clear();
                querySubList.clear();

                map.replace("sharerange","2", "3");
                map.replace("areaid", userareaids[0]);
                queryList= teacherMapper.getTeacherList(map);
                querySubList = teacherMapper.getTeacherSubList(map);
                for(Teacher query : queryList){
                    resultList.add(query);
                }
                for(Teacher query : querySubList){
                    resultList.add(query);
                }
                queryList.clear();
                querySubList.clear();
            }
            else if (userareaids.length == 1 && (StringUtils.equals(areaid,userareaids[0]) || StringUtils.isEmpty(areaid))){
                map.replace("sharerange","1","2");
                if (StringUtils.isEmpty(areaid))
                    map.put("areaid",userareaids[0]);
                queryList= teacherMapper.getTeacherList(map);
                querySubList = teacherMapper.getTeacherSubList(map);
                for(Teacher query : queryList){
                    resultList.add(query);
                }
                for(Teacher query : querySubList){
                    resultList.add(query);
                }
                queryList.clear();
                querySubList.clear();
            }
        }
        int count = resultList.size();

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

        if (start > resultList.size())
            pageList = new ArrayList<Teacher>();
        else
            pageList = resultList.subList(start ,start + pageSize <= resultList.size() ? start + pageSize : resultList.size());

        Page<Teacher> result = new Page<Teacher>(pageNo, pageSize);
        result.addAll(pageList);
        result.setTotal(count);
        return new Response<Teacher>(result);
    }
//    Author : GOD 2019-2-15 Bug ID: #778
    public boolean isValidTeacherOrderno(Integer orderNo) {
        Map<String, Object> map = new HashMap<>();
        map.put("orderno", orderNo);
        Long count = teacherMapper.getTeacherCountByOrderno(map);
        if ( count>0 )
            return false;
        else
            return true;
    }
//    Author : GOD 2019-2-15 Bug ID: #778
}
