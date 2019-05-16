package com.tianwen.springcloud.microservice.base.dao;

import com.tianwen.springcloud.datasource.mapper.MyMapper;
import com.tianwen.springcloud.microservice.base.entity.Area;
import com.tianwen.springcloud.microservice.base.entity.DictItem;
import com.tianwen.springcloud.microservice.base.entity.ESearchInfo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface DictItemMapper extends MyMapper<DictItem> {

    /**
     *
     *
     * @param map : getList condition
     * @return List<Area> : getList result
     * @see
     */
    List<DictItem> queryDictItemForList(Map<String, Object> map);

    int count(Map<String, Object> map);

    String getMaxId();

    void doUpdate(DictItem entity);

    void updateType(Map<String, Object> map);

    void doRemove(String dictid);

    DictItem getByDictInfo(DictItem dictItem);

    ESearchInfo getESearchInfo(Map<String, Object> param);
}
