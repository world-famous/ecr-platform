package com.tianwen.springcloud.microservice.base.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.tianwen.springcloud.commonapi.base.response.Response;
import com.tianwen.springcloud.commonapi.log.SystemServiceLog;
import com.tianwen.springcloud.commonapi.query.Pagination;
import com.tianwen.springcloud.commonapi.query.QueryTree;
import com.tianwen.springcloud.datasource.base.BaseService;
import com.tianwen.springcloud.datasource.util.QueryUtils;
import com.tianwen.springcloud.microservice.base.constant.IBaseMicroConstants;
import com.tianwen.springcloud.microservice.base.dao.ThemeMapper;
import com.tianwen.springcloud.microservice.base.entity.Theme;
import org.apache.http.client.methods.HttpPost;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by gems on 2018.12.19.
 */
@Service
public class ThemeService extends BaseService<Theme> {
    @Autowired
    private ThemeMapper themeMapper;

    @Autowired
    private ESearchService eSearchService;

    @Value("${ESEARCH_SERVER}")
    protected String esearchServer;

    @SystemServiceLog(description = "base/themeservice/getList")
    public Response<Theme> search(QueryTree queryTree)
    {

        // PageHelper
        Pagination pagination = queryTree.getPagination();
        if (pagination == null)
            pagination = new Pagination();
        Map<String, Object> map = QueryUtils.queryTree2Map(queryTree);

        if (map.get("status") == null)
            map.put("status", IBaseMicroConstants.THEME_STATUS_VALID);


        Long count = themeMapper.getCount(map);

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

        List<Theme> queryList = themeMapper.getList(map);

        try{
            pageNo = pagination.getPageNo();
        }
        catch (NullPointerException e){
            pageNo = 1;
        }

        Page<Theme> result = new Page<Theme>(pageNo, pageSize);
        result.addAll(queryList);
        result.setTotal(count);

        return new Response<Theme>(result);
    }

    @SystemServiceLog(description = "base/themeservice/getListAndChildCount")
    public Response<Theme> getListAndChildCount(QueryTree queryTree)
    {

        // PageHelper
        Pagination pagination = queryTree.getPagination();
        if (pagination == null)
            pagination = new Pagination();
        Map<String, Object> map = QueryUtils.queryTree2Map(queryTree);

        if (map.get("status") == null)
            map.put("status", IBaseMicroConstants.THEME_STATUS_VALID);


        Long count = themeMapper.getCount(map);

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

        List<Theme> queryList = themeMapper.getListAndChildCount(map);

        try{
            pageNo = pagination.getPageNo();
        }
        catch (NullPointerException e){
            pageNo = 1;
        }

        Page<Theme> result = new Page<Theme>(pageNo, pageSize);
        result.addAll(queryList);
        result.setTotal(count);

        return new Response<Theme>(result);
    }

    @SystemServiceLog(description = "base/themeservice/getMyAllThemes")
    public List<String> getMyAllThemes(String themeid){
        List<String> ids = themeMapper.getMyAllThemes(themeid);
        ids.add(themeid);
        return ids;
    }
}
