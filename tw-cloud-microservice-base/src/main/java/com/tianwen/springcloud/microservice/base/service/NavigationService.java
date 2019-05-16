package com.tianwen.springcloud.microservice.base.service;

import com.github.pagehelper.Page;
import com.tianwen.springcloud.commonapi.base.IService;
import com.tianwen.springcloud.commonapi.base.response.Response;
import com.tianwen.springcloud.commonapi.constant.IStateCode;
import com.tianwen.springcloud.commonapi.log.SystemServiceLog;
import com.tianwen.springcloud.commonapi.query.Pagination;
import com.tianwen.springcloud.commonapi.query.QueryTree;
import com.tianwen.springcloud.datasource.base.BaseService;
import com.tianwen.springcloud.datasource.util.QueryUtils;
import com.tianwen.springcloud.microservice.base.constant.IBaseMicroConstants;
import com.tianwen.springcloud.microservice.base.dao.NavigationMapper;
import com.tianwen.springcloud.microservice.base.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.*;

@Service
public class NavigationService extends BaseService<Navigation>
{
    @Autowired
    private NavigationMapper navigationMapper;

    public void fixNavigationParam(Navigation navigation)
    {
        if (!StringUtils.isEmpty(navigation.getSchoolsectionid()))
            navigation.setSchoolsectionid("," + navigation.getSchoolsectionid() + ",");
        if (!StringUtils.isEmpty(navigation.getSubjectid()))
            navigation.setSubjectid("," + navigation.getSubjectid() + ",");
        if (!StringUtils.isEmpty(navigation.getGradeid()))
            navigation.setGradeid("," + navigation.getGradeid() + ",");
        if (!StringUtils.isEmpty(navigation.getEditiontypeid()))
            navigation.setEditiontypeid("," + navigation.getEditiontypeid() + ",");
        if (!StringUtils.isEmpty(navigation.getBookmodelid()))
            navigation.setBookmodelid("," + navigation.getBookmodelid() + ",");
    }

    public void fixNavigationParam(Map<String, Object> navigation)
    {
        if (navigation.get("schoolsectionid") != null && !navigation.get("schoolsectionid").toString().isEmpty())
            navigation.put("schoolsectionid", "," + navigation.get("schoolsectionid") + ",");
        if (navigation.get("subjectid") != null && !navigation.get("subjectid").toString().isEmpty())
            navigation.put("subjectid", "," + navigation.get("subjectid") + ",");
        if (navigation.get("gradeid") != null && !navigation.get("gradeid").toString().isEmpty())
            navigation.put("gradeid", "," + navigation.get("gradeid") + ",");
        if (navigation.get("editiontypeid") != null && !navigation.get("editiontypeid").toString().isEmpty())
            navigation.put("editiontypeid", "," + navigation.get("editiontypeid") + ",");
        if (navigation.get("bookmodelid") != null && !navigation.get("bookmodelid").toString().isEmpty())
            navigation.put("bookmodelid", "," + navigation.get("bookmodelid") + ",");
    }

    @SystemServiceLog(description = "")
    public Response<Navigation> search(QueryTree queryTree)
    {
        // PageHelper
        Pagination pagination = queryTree.getPagination();
        Map<String, Object> map = QueryUtils.queryTree2Map(queryTree);
        if (map.get("lang") == null)
            map.put("lang", IBaseMicroConstants.zh_CN);
        fixNavigationParam(map);
        int count = navigationMapper.getCount(map);
        map.put("start", pagination.getStart());
        map.put("numPerPage", pagination.getNumPerPage());
        List<Navigation> queryList = navigationMapper.getList(map);
        Page<Navigation> result = new Page<Navigation>(pagination.getPageNo(), pagination.getNumPerPage());
        result.addAll(queryList);
        result.setTotal(count);
        return new Response<Navigation>(result);
    }
    
    @Override
    public boolean cacheable()
    {
        return false;
    }
    
    @Override
    public boolean listCacheable()
    {
        return false;
    }

    @SystemServiceLog(description = "SchoolSectionList")
    public List<DictItem> getSchoolSectionList(Navigation param)
    {
        Navigation myParam = param.copyEntity();
        fixNavigationParam(myParam);
        List<DictItem> schoolSectionList = navigationMapper.getSchoolSectionList(myParam);
        return schoolSectionList;
    }

    @SystemServiceLog(description = "SubjectList")
    public List<DictItem> getSubjectList(Navigation param)
    {
        Navigation myParam = param.copyEntity();
        // fixNavigationParam(myParam);
        List<DictItem> subjectList = navigationMapper.getSubjectList(myParam);
        return subjectList;
    }

    @SystemServiceLog(description = "GradeList")
    public List<DictItem> getGradeList(Navigation param)
    {
        Navigation myParam = param.copyEntity();
        fixNavigationParam(myParam);
        List<DictItem> gradeList = navigationMapper.getGradeList(myParam);
        return gradeList;
    }

    @SystemServiceLog(description = "BookModelList")
    public List<DictItem> getBookModelList(Navigation param)
    {
        Navigation myParam = param.copyEntity();
        fixNavigationParam(myParam);
        List<DictItem> bookModelList = navigationMapper.getBookModelList(myParam);
        return bookModelList;
    }

    @SystemServiceLog(description = "EditionTypeList")
    public List<DictItem> getEditionTypeList(Navigation param)
    {
        Navigation myParam = param.copyEntity();
        fixNavigationParam(myParam);
        List<DictItem> editionTypeList = navigationMapper.getEditionTypeList(myParam);
        return editionTypeList;
    }

    public List<DictItem> getAllDicItems(Navigation param) {
        Navigation myParam = param.copyEntity();
        fixNavigationParam(myParam);
        List<DictItem> all_items = navigationMapper.getAllDicItems(myParam);
        return all_items;
    }

    public List<Navigation> getAllSubjectNavi(Navigation param) {
        Navigation myParam = param.copyEntity();
        fixNavigationParam(myParam);
        List<Navigation> all_items = navigationMapper.getAllSubjectNavi(myParam);
        return all_items;
    }


    public List<DictItem> getNaviSchoolSectionList(Navigation param)
    {
        Navigation myParam = param.copyEntity();
        fixNavigationParam(myParam);
        List<DictItem> schoolSectionList = navigationMapper.getNaviSchoolSectionList(myParam);
        return schoolSectionList;
    }

    @SystemServiceLog(description = "NaviSubjectList")
    public List<DictItem> getNaviSubjectList(Navigation param)
    {
        Navigation myParam = param.copyEntity();
        fixNavigationParam(myParam);
        List<DictItem> subjectList = navigationMapper.getNaviSubjectList(myParam);
        return subjectList;
    }

    @SystemServiceLog(description = "NaviGradeList")
    public List<DictItem> getNaviGradeList(Navigation param)
    {
        Navigation myParam = param.copyEntity();
        fixNavigationParam(myParam);
        List<DictItem> gradeList = navigationMapper.getNaviGradeList(myParam);
        return gradeList;
    }

    @SystemServiceLog(description = "NaviBookModelList")
    public List<DictItem> getNaviBookModelList(Navigation param)
    {
        Navigation myParam = param.copyEntity();
        fixNavigationParam(myParam);
        List<DictItem> bookModelList = navigationMapper.getNaviBookModelList(myParam);
        return bookModelList;
    }

    @SystemServiceLog(description = "NaviEditionTypeList")
    public List<DictItem> getNaviEditionTypeList(Navigation param)
    {
        Navigation myParam = param.copyEntity();
        fixNavigationParam(myParam);
        List<DictItem> editionTypeList = navigationMapper.getNaviEditionTypeList(myParam);
        return editionTypeList;
    }

    public List<DictItem> getUniqueList(List<DictItem> orgList, Navigation param)
    {
        Map<String, String> uniqueName = new HashMap<>();
        Map<String, String> uniqueValue = new HashMap<>();
        List<DictItem> uniqueList = new ArrayList<>();

        for(DictItem org : orgList) {
            uniqueName.put(org.getDictid(), org.getDictname());
            uniqueValue.put(org.getDictid(), org.getDictvalue());
        }

        Set<Map.Entry<String, String>> items = uniqueName.entrySet();

        for(Map.Entry<String, String> item : items)
        {
            DictItem subject = new DictItem();
            String key = item.getKey();
            String value = item.getValue();
            subject.setDictid(key);
            subject.setDictname(uniqueName.get(key));
            if (uniqueValue.get(key) != null)
                subject.setDictvalue(uniqueValue.get(key));
            else
                subject.setDictvalue("");
            subject.setLang(param.getLang());
            uniqueList.add(subject);
        }

        orgList.clear();
        orgList.addAll(uniqueList);

        return uniqueList;
    }

    public List<DictItem> getCatalogSchoolSectionList(Navigation param) {
        Navigation myParam = param.copyEntity();
        fixNavigationParam(myParam);
        List<DictItem> orgList = navigationMapper.getCatalogSchoolSectionList(myParam);
         getUniqueList(orgList, param);
        return orgList;
    }

    public List<DictItem> getCatalogSubjectList(Navigation param) {
        Navigation myParam = param.copyEntity();
        fixNavigationParam(myParam);
        List<DictItem> orgList = navigationMapper.getCatalogSubjectList(myParam);
        getUniqueList(orgList, param);
        return orgList;
    }

    public List<DictItem> getCatalogGradeList(Navigation param) {
        Navigation myParam = param.copyEntity();
        fixNavigationParam(myParam);
        List<DictItem> orgList = navigationMapper.getCatalogGradeList(myParam);
        getUniqueList(orgList, param);
        return orgList;
    }

    public List<DictItem> getCatalogBookModelList(Navigation param) {
        Navigation myParam = param.copyEntity();
        fixNavigationParam(myParam);
        List<DictItem> orgList = navigationMapper.getCatalogBookModelList(myParam);
        getUniqueList(orgList, param);
        return orgList;
    }

    public List<DictItem> getCatalogEditionTypeList(Navigation param) {
        Navigation myParam = param.copyEntity();
        fixNavigationParam(myParam);
        List<DictItem> orgList = navigationMapper.getCatalogEditionTypeList(myParam);
        getUniqueList(orgList, param);
        return orgList;
    }

    public Navigation getByExample(Navigation param) {
        if (param == null) return param;
        return navigationMapper.getByExample(param);
    }

    public Navigation getByNaviId(String naviid) {
        return navigationMapper.getByNaviId(naviid);
    }

    public List<Navigation> getCatalogList(Navigation param) {
        Navigation myParam = param.copyEntity();
        fixNavigationParam(myParam);
        if (StringUtils.isEmpty(param.getLang()))
            param.setLang(IBaseMicroConstants.zh_CN);
        return navigationMapper.getCatalogList(myParam);
    }

    public int getCount(Navigation entity) {
        Map<String, Object> data = new HashMap<>();
        data.put("schoolsectionid", entity.getSchoolsectionid());
        data.put("subjectid", entity.getSubjectid());
        data.put("editiontypeid", entity.getEditiontypeid());
        data.put("bookmodelid", entity.getBookmodelid());
        return navigationMapper.getCount(data);
    }

    public void remove(Navigation entity) {
        Navigation myEntity = entity.copyEntity();
        fixNavigationParam(myEntity);
        navigationMapper.remove(myEntity);
    }
}
