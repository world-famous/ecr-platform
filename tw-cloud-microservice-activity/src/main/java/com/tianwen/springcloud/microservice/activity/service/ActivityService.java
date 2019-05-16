package com.tianwen.springcloud.microservice.activity.service;

import com.github.pagehelper.Page;
import com.tianwen.springcloud.commonapi.base.response.Response;
import com.tianwen.springcloud.commonapi.query.OrderMethod;
import com.tianwen.springcloud.commonapi.query.Pagination;
import com.tianwen.springcloud.commonapi.query.QueryTree;
import com.tianwen.springcloud.datasource.base.BaseService;
import com.tianwen.springcloud.datasource.util.QueryUtils;
import com.tianwen.springcloud.microservice.activity.dao.ActivityMapper;
import com.tianwen.springcloud.microservice.activity.entity.Activity;
import org.springframework.util.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ActivityService extends BaseService<Activity> {

    @Autowired
    private ActivityMapper activityMapper;

    public Response<Activity> search(QueryTree queryTree) {
        Pagination pagination = queryTree.getPagination();

        Map<String, Object> map = QueryUtils.queryTree2Map(queryTree);
        Long count = activityMapper.countForActivityList(map);

        List<OrderMethod> ordermethods = queryTree.getOrderMethods();

        if (!CollectionUtils.isEmpty(ordermethods)) {
            if (ordermethods.get(0).getField().equals("orderBycreatetime"))
                map.put("orderBycreatetime", "");
        } else {
            map.put("orderBycreatetime", "");
        }

        map.put("start", pagination.getStart());
        map.put("numPerPage", pagination.getNumPerPage());
        List<Activity> queryList = activityMapper.queryActivityForList(map);
        Page<Activity> page = new Page<>(pagination.getPageNo(), pagination.getNumPerPage());
        page.setTotal(count);
        page.addAll(queryList);

        for (Activity item : queryList)
        {
            List<String> accordings = CollectionUtils.arrayToList(StringUtils.split(item.getAccording(), ","));
            item.setAccordings(accordings);
        }

        return new Response<>(page);
    }

    public Response<String> getActivityCreatorIds(QueryTree queryTree) {
        Map<String, Object> map = QueryUtils.queryTree2Map(queryTree);
        List<String> userids = activityMapper.getActivityCreatorIds(map);
        if (userids != null) {
            userids.add("");
            userids.add("");
        }
        return new Response<>(userids);
    }

    public Response<String> getIdsByQueryTree(QueryTree queryTree) {
        Map<String, Object> map = QueryUtils.queryTree2Map(queryTree);
        List<String> activityids = activityMapper.getIds(map);
        if (activityids != null) {
            activityids.add("");
            activityids.add("");
        }
        return new Response<>(activityids);
    }

    //author:han 韩哲国
    public Response<String> getIdsByQueryTreeExtra(QueryTree queryTree) {
        Map<String, Object> map = QueryUtils.queryTree2Map(queryTree);
        List<String> activityids = activityMapper.getIdsExtra(map);
        if (activityids != null) {
            activityids.add("");
            activityids.add("");
        }
        return new Response<>(activityids);
    }
    //end han

    public Response<Long> getCountByCreator(String userid) {
        Map<String, String> map = new HashMap<>();
        map.put("creator", userid);
        return new Response<>(activityMapper.getCountByCreator(map));
    }

    public String getActivityStatus(String activityid) {
       return activityMapper.getActivityStatus(activityid);
    }
}
