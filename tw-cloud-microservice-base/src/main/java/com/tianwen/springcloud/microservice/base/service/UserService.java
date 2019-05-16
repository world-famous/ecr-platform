package com.tianwen.springcloud.microservice.base.service;

import com.github.pagehelper.Page;
import com.tianwen.springcloud.commonapi.base.response.Response;
import com.tianwen.springcloud.commonapi.log.SystemServiceLog;
import com.tianwen.springcloud.commonapi.query.OrderMethod;
import com.tianwen.springcloud.commonapi.query.Pagination;
import com.tianwen.springcloud.commonapi.query.QueryTree;
import com.tianwen.springcloud.datasource.base.BaseService;
import com.tianwen.springcloud.datasource.util.QueryUtils;
import com.tianwen.springcloud.microservice.base.dao.UserMapper;
import com.tianwen.springcloud.microservice.base.entity.UserLoginInfo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class UserService extends BaseService<UserLoginInfo>
{
    @Autowired
    private UserMapper userMapper;

    @SystemServiceLog(description = "")
    public Response<UserLoginInfo> search(QueryTree queryTree)
    {
        // PageHelper
        Pagination pagination = queryTree.getPagination();
        Map<String, Object> map = QueryUtils.queryTree2Map(queryTree);
        fixParam(map);
        List<OrderMethod> orderMethods = queryTree.getOrderMethods();

        if (!CollectionUtils.isEmpty(orderMethods)) {
            for (OrderMethod method : orderMethods) {
                if (StringUtils.equals(method.getField(), "OrderByVotetimes")) {
                    map.put("OrderByVotetimes", "");
                } else if (StringUtils.equals(method.getField(), "OrderByRecentUpload")) {
                    map.put("OrderByRecentUpload", "");
                }
            }
        }

        Long count = userMapper.countUserLoginInfo(map);
        map.put("start", pagination.getStart());
        map.put("numPerPage", pagination.getNumPerPage());
        List<UserLoginInfo> queryList = userMapper.queryUserLoginInfoForList(map);
        Page<UserLoginInfo> result = new Page<UserLoginInfo>(pagination.getPageNo(), pagination.getNumPerPage());
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
    
    @Override
    public String getKey(UserLoginInfo t)
    {
        return t.getUserId();
    }

    public String getLastId() {
        return userMapper.getLastId();
    }

    public UserLoginInfo getUserByLoginName(String loginname) {
        return userMapper.getUserByLoginName(loginname);
    }

    public UserLoginInfo getUserByRealName(String realname) {
        return userMapper.getUserByRealName(realname);
    }

    public UserLoginInfo getUserByToken(String token) {
        UserLoginInfo userLoginInfo = userMapper.getUserByToken(token);
        return userLoginInfo;
    }

    public UserLoginInfo getUserByRefreshToken(String refresh_token) {
        return userMapper.getUserByRefreshToken(refresh_token);
    }

    public List<UserLoginInfo> getByOrg(String orgid) {
        return userMapper.getByOrg(orgid);
    }

    public List<String> getByArea(String areaid) {
        Map<String, String> map = new HashMap<>();
        map.put("areaid", areaid);
        return userMapper.getByArea(map);
    }

    public List<UserLoginInfo> getList() { return userMapper.getList(); }

    public Response<String> getUserIdsByQueryTree(QueryTree queryTree) {
        Pagination pagination = queryTree.getPagination();
        Map<String, Object> map = QueryUtils.queryTree2Map(queryTree);
        map.put("start", pagination.getStart());
        map.put("numPerPage", pagination.getNumPerPage());
        List<String> queryList = userMapper.getUserIdsByQueryTree(map);
        if (CollectionUtils.isEmpty(queryList)) {
            queryList.add("");
        }
        queryList.add("");
        return new Response<>(queryList);
    }

    //author: han
    public Response<String> getUserIdsByQueryTreeExtra(QueryTree queryTree) {
        Map<String, Object> map = QueryUtils.queryTree2Map(queryTree);
        List<String> queryList = userMapper.getUserIdsByQueryTreeExtra(map);
        return new Response<>(queryList);
    }
    //end han

    public UserLoginInfo getById(String userid) {
        return userMapper.getById(userid);
    }

    public void deleteUserRole(String userid) {
        userMapper.deleteUserRole(userid);
    }

    public void insertUserRole(Map<String, Object> userRole) {
        userMapper.insertUserRole(userRole);
    }

    public Response<UserLoginInfo> getByName(String username) {
        Map<String, String> map = new HashMap<>();
        map.put("username", username);
        UserLoginInfo userLoginInfo = userMapper.getByName(map);
        return new Response<>(userLoginInfo);
    }

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

}
