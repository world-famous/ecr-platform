package com.tianwen.springcloud.microservice.base.dao;

import com.tianwen.springcloud.datasource.mapper.MyMapper;
import com.tianwen.springcloud.microservice.base.entity.Catalog;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface CatalogMapper extends MyMapper<Catalog>
{
    List<String> getMyAllCatalogs(String parent);

    List<Catalog> getList(Map<String, Object> param);

    Long getCount(Map<String, Object> map);

    String getMaxSibling(String parentcatalogid);

    List<Catalog> getSiblings(Catalog src);

    String getNaviFromCatalog(String catalogid);

    void deleteByBook(String bookid);

    void getByBookChapterName(Map<String, String> cataloginfo);

    Catalog getByExample(Catalog example);
}
