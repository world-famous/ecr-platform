package com.tianwen.springcloud.microservice.base.dao;

import com.tianwen.springcloud.datasource.mapper.MyMapper;
import com.tianwen.springcloud.microservice.base.entity.DictItem;
import com.tianwen.springcloud.microservice.base.entity.School;
import com.tianwen.springcloud.microservice.base.entity.Synonym;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface SynonymMapper extends MyMapper<Synonym>
{
    List<Synonym> querySynonymForList(Map<String, Object> param);

    List<Synonym> getSynonymByOrginal(Map<String, Object> param);

    List<Synonym> getSynonymBySynonym(Map<String, Object> param);

    List<Synonym> getSynonymBySearchkey(Map<String, Object> param);

    Long getCount(Map<String, Object> map);

    List<String> getOriginals(Map<String, Object> orgParam);

    List<String> getSynonyms(Map<String, Object> synParam);
}
