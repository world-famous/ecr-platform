package com.tianwen.springcloud.microservice.base.service;

import com.github.pagehelper.Page;
import com.tianwen.springcloud.commonapi.base.response.Response;
import com.tianwen.springcloud.commonapi.query.Pagination;
import com.tianwen.springcloud.commonapi.query.QueryTree;
import com.tianwen.springcloud.datasource.base.BaseService;
import com.tianwen.springcloud.datasource.util.QueryUtils;
import com.tianwen.springcloud.microservice.base.dao.DictItemMapper;
import com.tianwen.springcloud.microservice.base.dao.DictTypeMapper;
import com.tianwen.springcloud.microservice.base.entity.DictItem;
import com.tianwen.springcloud.microservice.base.entity.DictType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class DictTypeService extends BaseService<DictType> {

    @Autowired
    private DictTypeMapper dictTypeMapper;

    public String increment(String s)
    {
        if (s == null)
            s = "000000000000";

        Matcher m = Pattern.compile("\\d+").matcher(s);
        if (!m.find())
            throw new NumberFormatException();
        String num = m.group();
        long inc = Long.parseLong(num) + 1;
        String incStr = String.format("%0" + num.length() + "d", inc);
        return  m.replaceFirst(incStr);
    }

    public Response<DictType> search(QueryTree queryTree)
    {
        // PageHelper
        Pagination pagination = queryTree.getPagination();
        Map<String, Object> map = QueryUtils.queryTree2Map(queryTree);
        int count = dictTypeMapper.count(map);
        map.put("start", pagination.getStart());
        map.put("numPerPage", pagination.getNumPerPage());
        List<DictType> queryList = dictTypeMapper.queryDictTypeForList(map);
        Page<DictType> result = new Page<DictType>(pagination.getPageNo(), pagination.getNumPerPage());
        result.addAll(queryList);
        result.setTotal(count);
        return new Response<DictType>(result);
    }

    public Integer getCount(QueryTree queryTree)
    {
        Map<String, Object> map = QueryUtils.queryTree2Map(queryTree);
        Integer count = dictTypeMapper.count(map);
        return count;
    }

    public void doUpdate(DictType entity) {
        dictTypeMapper.doUpdate(entity);
    }

    public Integer getMaxSortNo(String parenttypeid) {
        return dictTypeMapper.getMaxSortNo(parenttypeid);
    }

    public Integer isExistById(String id){
        return dictTypeMapper.isExistById(id);
    }

    public void doRemove(String dicttypeid) {
        dictTypeMapper.doRemove(dicttypeid);
    }
}
