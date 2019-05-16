package com.tianwen.springcloud.microservice.base.dao;

import com.tianwen.springcloud.datasource.mapper.MyMapper;
import com.tianwen.springcloud.microservice.base.entity.Student;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface StudentMapper extends MyMapper<Student>
{
    List<Student> getList(Map<String, Object> param);

    int getCount(Map<String, Object> map);

    List<Student> getTeacherList(Map<String, Object> param);
}
