package com.tianwen.springcloud.microservice.base.dao;

import com.tianwen.springcloud.datasource.mapper.MyMapper;
import com.tianwen.springcloud.microservice.base.entity.Area;
import com.tianwen.springcloud.microservice.base.entity.Role;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface RoleMapper extends MyMapper<Role> {

    /**
     *
     *
     * @param map : getList condition
     * @return List<Area> : getList result
     * @see
     */
    List<Role> queryRoleForList(Map<String, Object> mapList);
}
