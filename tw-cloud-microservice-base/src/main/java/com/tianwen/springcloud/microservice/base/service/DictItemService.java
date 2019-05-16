package com.tianwen.springcloud.microservice.base.service;

import com.github.pagehelper.Page;
import com.tianwen.springcloud.commonapi.base.response.Response;
import com.tianwen.springcloud.commonapi.query.Pagination;
import com.tianwen.springcloud.commonapi.query.QueryTree;
import com.tianwen.springcloud.datasource.base.BaseService;
import com.tianwen.springcloud.datasource.util.QueryUtils;
import com.tianwen.springcloud.microservice.base.dao.AreaMapper;
import com.tianwen.springcloud.microservice.base.dao.DictItemMapper;
import com.tianwen.springcloud.microservice.base.entity.Area;
import com.tianwen.springcloud.microservice.base.entity.DictItem;
import com.tianwen.springcloud.microservice.base.entity.ESearchInfo;
import com.tianwen.springcloud.microservice.base.entity.Organization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class DictItemService extends BaseService<DictItem> {

    @Autowired
    private DictItemMapper dictItemMapper;

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

    public Response<DictItem> search(QueryTree queryTree)
    {
        // PageHelper
        Pagination pagination = queryTree.getPagination();
        Map<String, Object> map = QueryUtils.queryTree2Map(queryTree);
        int count = dictItemMapper.count(map);
        map.put("start", pagination.getStart());
        map.put("numPerPage", pagination.getNumPerPage());
        List<DictItem> queryList = dictItemMapper.queryDictItemForList(map);
        Page<DictItem> result = new Page<DictItem>(pagination.getPageNo(), pagination.getNumPerPage());
        result.addAll(queryList);
        result.setTotal(count);
        return new Response<DictItem>(result);
    }

    public int getCount(QueryTree queryTree)
    {
        Map<String, Object> map = QueryUtils.queryTree2Map(queryTree);
        int count = dictItemMapper.count(map);
        return count;
    }

    public String getMaxId()
    {
        return dictItemMapper.getMaxId();
    }

    public void doUpdate(DictItem entity) {
        dictItemMapper.doUpdate(entity);
    }

    public void updateType(String oldid, String dicttypeid) {
        if (StringUtils.isEmpty(oldid) || StringUtils.isEmpty(dicttypeid))
            return;

        Map<String, Object> map = new HashMap<>();
        map.put("oldtypeid", oldid);
        map.put("newtypeid", dicttypeid);
        dictItemMapper.updateType(map);
    }

    public void doRemove(String dictid) {
        dictItemMapper.doRemove(dictid);
    }

    public Response<DictItem> getByDictInfo(DictItem dictItem) {
        return new Response<>(dictItemMapper.getByDictInfo(dictItem));
    }

    public ESearchInfo getESearchInfo(Map<String, Object> param) {
        return dictItemMapper.getESearchInfo(param);
    }
}
