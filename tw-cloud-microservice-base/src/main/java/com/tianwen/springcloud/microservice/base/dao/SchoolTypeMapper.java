package com.tianwen.springcloud.microservice.base.dao;

import com.tianwen.springcloud.datasource.mapper.MyMapper;
import com.tianwen.springcloud.microservice.base.entity.SchoolType;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface SchoolTypeMapper extends MyMapper<SchoolType>
{
    List<SchoolType> getList(Map<String, Object> param);

    Long getCount(Map<String, Object> map);

    void connectSchoolType(Map<String, String> map);

    List<SchoolType> getSchoolTypeList();

    Integer isconnectSchoolType(Map<String, String> schoolid);
}
