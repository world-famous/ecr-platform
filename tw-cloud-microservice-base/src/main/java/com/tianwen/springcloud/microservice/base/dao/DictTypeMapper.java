package com.tianwen.springcloud.microservice.base.dao;

import com.tianwen.springcloud.datasource.mapper.MyMapper;
import com.tianwen.springcloud.microservice.base.entity.DictItem;
import com.tianwen.springcloud.microservice.base.entity.DictType;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface DictTypeMapper extends MyMapper<DictType> {

    /**
     *
     *
     * @param map : getList condition
     * @return List<Area> : getList result
     * @see
     */
    List<DictType> queryDictTypeForList(Map<String, Object> map);

    int count(Map<String, Object> map);

    void doUpdate(DictType entity);

    Integer getMaxSortNo(String parenttypeid);
    Integer isExistById(String id);
    void doRemove(String dicttypeid);
}
