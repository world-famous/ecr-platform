package com.tianwen.springcloud.microservice.base.dao;

import com.tianwen.springcloud.datasource.mapper.MyMapper;
import com.tianwen.springcloud.microservice.base.entity.DictItem;
import com.tianwen.springcloud.microservice.base.entity.Knowledge;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface KnowledgeMapper extends MyMapper<Knowledge> {

    /**
     *
     *
     * @param map : getList condition
     * @return List<KnowledgeMapper> : getList result
     * @see
     */
    List<Knowledge> queryForList(Map<String, Object> mapList);

    List<DictItem> getSchoolSectionList(Map<String, Object> mapList);

    List<DictItem> getSubjectList(Map<String, Object> mapList);

    Long count(Map<String, Object> mapList);

    String getMaxSequence(String parentknowledgeid);
}
