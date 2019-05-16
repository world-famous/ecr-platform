package com.tianwen.springcloud.microservice.base.dao;

import com.tianwen.springcloud.datasource.mapper.MyMapper;
import com.tianwen.springcloud.microservice.base.entity.ClassInfo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface ClassMapper extends MyMapper<ClassInfo>
{
    List<ClassInfo> getList(Map<String, Object> param);

    ClassInfo getClassById(Map<String, Object> classid);

    int getCount(Map<String, Object> map);
}
