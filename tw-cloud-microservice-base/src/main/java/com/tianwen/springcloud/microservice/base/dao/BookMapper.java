package com.tianwen.springcloud.microservice.base.dao;

import com.tianwen.springcloud.datasource.mapper.MyMapper;
import com.tianwen.springcloud.microservice.base.entity.Book;
import com.tianwen.springcloud.microservice.base.entity.CatalogHeader;
import com.tianwen.springcloud.microservice.base.entity.Navigation;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface BookMapper extends MyMapper<Book>
{
    List<Book> getList(Map<String, Object> param);

    int getCount(Map<String, Object> map);

    Book getByNavigation(Navigation navi);

    List<Book> getBookByNavigation(Navigation navi);

    int deleteByBookId(String bookid);

    Book getBook(Map<String, Object> param);

    List<CatalogHeader> getCatalogHeaderInfo(Navigation param);

    Book getBookByBookname(Map<String, Object> param);
}
