package com.tianwen.springcloud.microservice.base.dao;

import java.util.List;
import java.util.Map;

import com.tianwen.springcloud.microservice.base.entity.*;
import org.apache.ibatis.annotations.Mapper;
import com.tianwen.springcloud.datasource.mapper.MyMapper;

@Mapper
public interface NavigationMapper extends MyMapper<Navigation>
{
    List<Navigation> getList(Map<String, Object> param);

    List<DictItem> getSchoolSectionList(Navigation param);
    List<DictItem> getSubjectList(Navigation param);
    List<DictItem> getGradeList(Navigation param);
    List<DictItem> getBookModelList(Navigation param);
    List<DictItem> getEditionTypeList(Navigation param);

    List<DictItem> getNaviSchoolSectionList(Navigation param);
    List<DictItem> getNaviSubjectList(Navigation param);
    List<DictItem> getNaviGradeList(Navigation param);
    List<DictItem> getNaviBookModelList(Navigation param);
    List<DictItem> getNaviEditionTypeList(Navigation param);
    List<DictItem> getAllDicItems(Navigation param);
    List<Navigation> getAllSubjectNavi(Navigation param);

    List<DictItem> getCatalogSchoolSectionList(Navigation param);
    List<DictItem> getCatalogSubjectList(Navigation param);
    List<DictItem> getCatalogGradeList(Navigation param);
    List<DictItem> getCatalogBookModelList(Navigation param);
    List<DictItem> getCatalogEditionTypeList(Navigation param);

    int getCount(Map<String, Object> map);

    Navigation getByExample(Navigation param);

    Navigation getByNaviId(String naviid);

    List<Navigation> getCatalogList(Navigation param);

    void remove(Navigation entity);
}
