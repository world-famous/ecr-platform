package com.tianwen.springcloud.microservice.base.dao;

import com.tianwen.springcloud.datasource.mapper.MyMapper;
import com.tianwen.springcloud.microservice.base.entity.Teacher;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface TeacherMapper extends MyMapper<Teacher>
{
    List<Teacher> getList(Map<String, Object> param);

    int getCount(Map<String, Object> map);

    List<Teacher> getTeacherList(Map<String, Object> param);
    List<Teacher>getTeacherSubList(Map<String, Object>param);

    List<String> getTeacherIds(Map<String, Object> map);
//    Author : GOD 2019-2-15 Bug ID: #778
    Long getTeacherCountByOrderno(Map<String, Object> map);
//    Author : GOD 2019-2-15 Bug ID: #778
}
