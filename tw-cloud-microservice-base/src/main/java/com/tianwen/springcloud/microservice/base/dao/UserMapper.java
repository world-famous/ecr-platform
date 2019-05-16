package com.tianwen.springcloud.microservice.base.dao;

import java.util.List;
import java.util.Map;

import com.tianwen.springcloud.commonapi.base.response.Response;
import org.apache.catalina.User;
import org.apache.ibatis.annotations.Mapper;

import com.tianwen.springcloud.datasource.mapper.MyMapper;
import com.tianwen.springcloud.microservice.base.entity.UserLoginInfo;

/**
 * ?�户?�关DAO
 * 
 * @author wangbin
 * @version [?�本?? 2017�?????
 * @see [?�关�??�法]
 * @since [产品/模块?�本]
 */
@Mapper
public interface UserMapper extends MyMapper<UserLoginInfo>
{

    void batchInsertUser2Role(List<Map<String, Object>> list);
    
    List<UserLoginInfo> queryUserLoginInfoForList(Map<String, Object> map);

    Long countUserLoginInfo(Map<String, Object> map);

    List<UserLoginInfo> queryUserWithRoleForList(Map<String, Object> map);

    Integer queryUserWithRoleCount(Map<String, Object> map);

    /**
     * find the biggest user id to decide next user id
     *
     * @param
     * @return String the biggest user id
     * @see
     */
    String getLastId();

    /**
     * find a user by his/her loginname
     *
     * @param String loginname
     * @return UserLoginInfo user login information
     * @see
     */
    UserLoginInfo getUserByLoginName(String loginname);

    /**
     * find a user by his/her token
     *
     * @param token
     * @return UserLoginInfo user login information
     * @see
     */
    UserLoginInfo getUserByToken(String token);

    /**
     * find a user by his/her refresh token
     *
     * @param
     * @return UserLoginInfo user login information
     * @see
     */
    UserLoginInfo getUserByRefreshToken(String refresh_token);

    /**
     *
     * @param auditorname
     * @return
     */
    UserLoginInfo getUserByRealName(String auditorname);

    /**
     *
     * @param token
     * @return
     */
    UserLoginInfo getManagerUserByToken(String token);

    /**
     *
     * @param orgid
     * @return
     */
    List<UserLoginInfo> getByOrg(String orgid);

    /**
     *
     * @param map
     * @return
     */
    List<String> getByArea(Map<String, String> map);

    /**
     *
     * @return
     */
    List<UserLoginInfo> getList();

    /**
     *
     * @param map
     * @return
     */
    List<String> getUserIdsByQueryTree(Map<String, Object> map);

    //author:han
    /**
     *
     * @param map
     * @return
     */
    List<String> getUserIdsByQueryTreeExtra(Map<String, Object> map);
    //end han

    /**
     *
     * @param userid
     * @return
     */
    String getRoleType(String userid);

    /**
     * get userlogin info by userid
     * @param userid
     * @return
     */
    UserLoginInfo getById(String userid);

    void deleteUserRole(String userid);

    void insertUserRole(Map<String, Object> userRole);

    /**
     * select user by name
     * @param map
     * @return
     */
    UserLoginInfo getByName(Map<String, String> map);

    Integer getLoginedUserCount(Map<String, Object> param);
}
