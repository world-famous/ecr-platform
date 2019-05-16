package com.tianwen.springcloud.microservice.base.dao;

import com.tianwen.springcloud.datasource.mapper.MyMapper;
import com.tianwen.springcloud.microservice.base.entity.Area;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface AreaMapper extends MyMapper<Area> {

    /**
     *
     *
     * @param map : getList condition
     * @return List<Area> : getList result
     * @see
     */
    List<Area> queryAreaForList(Map<String, Object> mapList);

    Long getCount(Map<String, Object> mapList);

    List<Area> getChildrenArea(String parentareaid);

    List<Area> getAreaByParent(String parentid);

    Area getParentAreaId(String areaid);

    List<String> getAllChildren(String areaid);
}
