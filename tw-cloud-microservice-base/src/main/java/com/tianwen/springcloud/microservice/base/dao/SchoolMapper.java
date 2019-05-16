package com.tianwen.springcloud.microservice.base.dao;

import com.tianwen.springcloud.datasource.mapper.MyMapper;
import com.tianwen.springcloud.microservice.base.entity.DictItem;
import com.tianwen.springcloud.microservice.base.entity.School;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface SchoolMapper extends MyMapper<School>
{
    List<School> getList(Map<String, Object> param);

    int getCount(Map<String, Object> map);

    List<School> getSchoolList(Map<String, Object> map);

    List<DictItem> getSchoolSectionBySchoolid(Map<String, String> map);

    Long getCountByAreaAndConType(Map<String, Object> map);

    List<School> queryListByAreaAndConType(Map<String, Object> map);
    //Author : GOD 2019-2-13
    Long getSchoolCountByOrderno(Map<String, Object> map);
    //Author : GOD 2019-2-13
}
