package com.tianwen.springcloud.microservice.base.service;

import com.github.pagehelper.Page;
import com.tianwen.springcloud.commonapi.base.response.Response;
import com.tianwen.springcloud.commonapi.log.SystemServiceLog;
import com.tianwen.springcloud.commonapi.query.Pagination;
import com.tianwen.springcloud.commonapi.query.QueryTree;
import com.tianwen.springcloud.datasource.base.BaseService;
import com.tianwen.springcloud.datasource.util.QueryUtils;
import com.tianwen.springcloud.microservice.base.constant.IBaseMicroConstants;
import com.tianwen.springcloud.microservice.base.dao.BookMapper;
import com.tianwen.springcloud.microservice.base.entity.Book;
import com.tianwen.springcloud.microservice.base.entity.CatalogHeader;
import com.tianwen.springcloud.microservice.base.entity.Navigation;
import com.tianwen.springcloud.microservice.base.entity.UserLoginInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.Map;

@Service
public class BookService extends BaseService<Book>
{
    @Autowired
    private BookMapper bookMapper;

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

    public int deleteByBookId(String bookid) {
        bookMapper.deleteByBookId(bookid);

        return 1;
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
    public Response<Book> search(QueryTree queryTree)
    {
        // PageHelper
        Pagination pagination = queryTree.getPagination();
        Map<String, Object> map = QueryUtils.queryTree2Map(queryTree);
        if (map.get("lang") == null)
            map.put("lang", IBaseMicroConstants.zh_CN);
        fixNavigationParam(map);
        int count = bookMapper.getCount(map);
        map.put("start", pagination.getStart());
        map.put("numPerPage", pagination.getNumPerPage());
        List<Book> queryList = bookMapper.getList(map);
        Page<Book> result = new Page<Book>(pagination.getPageNo(), pagination.getNumPerPage());
        result.addAll(queryList);
        result.setTotal(count);
        return new Response<>(result);
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

    public Book getByNavigation(Navigation navi) {
        fixNavigationParam(navi);
        return bookMapper.getByNavigation(navi);
    }

    public List<Book> getBookByNavigation(Navigation navi){
        fixNavigationParam(navi);
        return bookMapper.getBookByNavigation(navi);
    }


    public Book getBook(Map<String, Object> param) {
        return bookMapper.getBook(param);
    }

    public List<CatalogHeader> getCatalogHeaderInfo(Navigation param) {
        fixNavigationParam(param);
        return bookMapper.getCatalogHeaderInfo(param);
    }

    public Book getBookByBookname(Map<String, Object> param) {
        return bookMapper.getBookByBookname(param);
    }
}