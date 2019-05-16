package com.tianwen.springcloud.microservice.base.dao;

import com.tianwen.springcloud.datasource.mapper.MyMapper;
import com.tianwen.springcloud.microservice.base.entity.Theme;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface ThemeMapper extends MyMapper<Theme> {

    List<Theme> getList(Map<String, Object> param);
    // Author : GOD
    // Date Start : 2019-1-31 3 PM
    // Reason : 系统管理 专题管理 Page Loading Speed, Select First Theme as Default
    List<Theme> getListAndChildCount(Map<String, Object> param);
    // Author : GOD
    // Date End : 2019-2-1 6 PM
    // Reason : 系统管理 专题管理 Page Loading Speed, Select First Theme as Default
    Long getCount(Map<String, Object> map);

    List<String> getMyAllThemes(String parent);
}
