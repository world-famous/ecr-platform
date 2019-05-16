package com.tianwen.springcloud.microservice.base.service;

import com.github.pagehelper.Page;
import com.tianwen.springcloud.commonapi.base.response.Response;
import com.tianwen.springcloud.commonapi.query.Pagination;
import com.tianwen.springcloud.commonapi.query.QueryTree;
import com.tianwen.springcloud.datasource.base.BaseService;
import com.tianwen.springcloud.datasource.util.QueryUtils;
import com.tianwen.springcloud.microservice.base.dao.AreaMapper;
import com.tianwen.springcloud.microservice.base.entity.Area;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AreaService extends BaseService<Area> {

    @Autowired
    private AreaMapper areaMapper;

    public Response<Area> search(QueryTree queryTree) {
        Pagination pagination = queryTree.getPagination();

        Map<String, Object> map = QueryUtils.queryTree2Map(queryTree);

        Long count = areaMapper.getCount(map);
        map.put("start", pagination.getStart());
        map.put("numPerPage", pagination.getNumPerPage());
        List<Area> queryList = areaMapper.queryAreaForList(map);

        Page<Area> page = new Page<>(pagination.getPageNo(), pagination.getNumPerPage());
        page.setTotal(count);
        page.addAll(queryList);

        return new Response<>(page);
    }

    public boolean isAncestor(String bigAreaId, String smallAreaId)
    {
        Area area = selectByKey(smallAreaId);
        while (area != null)
        {
            if (area.getAreaid().equals(bigAreaId))
                return true;

            area = selectByKey(area.getParentareaid());
        }

        return false;
    }


    public List<Area> getAllData(QueryTree queryTree) {
        Map<String, Object> map = QueryUtils.queryTree2Map(queryTree);

        List<Area> queryList = areaMapper.queryAreaForList(map);

        return queryList;
    }

    public List<Area> getChildrenArea(String parentareaid) {
        return areaMapper.getChildrenArea(parentareaid);
    }

    public Response<Area> getAreaByParent(String parentid) {
        return new Response<>(areaMapper.getAreaByParent(parentid));
    }

    public Response<Area> getParentAreaId(String areaid) {

        List<Area> areas = new ArrayList<>();
        Area temp = areaMapper.selectByPrimaryKey(areaid);
        int i = 0;
        areas.add(temp);
        boolean flag = true;
        while (flag && i < 10){
            temp = areaMapper.selectByPrimaryKey(temp.getParentareaid());
            areas.add(temp);
            if(temp.getParentareaid().equals("-1")){
                flag = false;
            }
            i++;
        }
        return new Response<>(areas);
    }

    public List<String> getAllChildrens(String areaid) {
        return areaMapper.getAllChildren(areaid);
    }
}
