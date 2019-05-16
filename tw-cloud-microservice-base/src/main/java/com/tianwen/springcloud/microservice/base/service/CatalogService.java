package com.tianwen.springcloud.microservice.base.service;

import com.github.pagehelper.Page;
import com.tianwen.springcloud.commonapi.base.response.Response;
import com.tianwen.springcloud.commonapi.log.SystemServiceLog;
import com.tianwen.springcloud.commonapi.query.Pagination;
import com.tianwen.springcloud.commonapi.query.QueryTree;
import com.tianwen.springcloud.datasource.base.BaseService;
import com.tianwen.springcloud.datasource.util.QueryUtils;
import com.tianwen.springcloud.microservice.base.dao.CatalogMapper;
import com.tianwen.springcloud.microservice.base.entity.Catalog;
import com.tianwen.springcloud.microservice.base.entity.Navigation;
import com.tianwen.springcloud.microservice.base.entity.UserLoginInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class CatalogService extends BaseService<Catalog>
{
    @Autowired
    private CatalogMapper catalogMapper;

    public String increment(String s)
    {
        if (s == null)
            s = "00000";
        Matcher m = Pattern.compile("\\d+").matcher(s);
        if (!m.find())
            throw new NumberFormatException();
        String num = m.group();
        int inc = Integer.parseInt(num) + 1;
        String incStr = String.format("%0" + num.length() + "d", inc);
        return  m.replaceFirst(incStr);
    }

    @SystemServiceLog(description = "")
    public Response<Catalog> search(QueryTree queryTree)
    {
        Pagination pagination = queryTree.getPagination();
        Map<String, Object> map = QueryUtils.queryTree2Map(queryTree);
        Long count = catalogMapper.getCount(map);
        map.put("start", pagination.getStart());
        map.put("numPerPage", pagination.getNumPerPage());
        List<Catalog> queryList = catalogMapper.getList(map);
        Page<Catalog> result = new Page<Catalog>(pagination.getPageNo(), pagination.getNumPerPage());
        result.addAll(queryList);
        result.setTotal(count);
        return new Response<Catalog>(result);
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

    @SystemServiceLog(description = "")
    public int insertCatalog(Catalog entity) {
        String maxSibling = catalogMapper.getMaxSibling(entity.getParentcatalogid());

        maxSibling = increment(maxSibling);

        entity.setSequence(maxSibling);
        return save(entity);
    }

    public List<Catalog> getSiblings(Catalog src) {
        return catalogMapper.getSiblings(src);
    }

    public String getNaviFromCatalog(String catalogid) {
        return catalogMapper.getNaviFromCatalog(catalogid);
    }

    public void deleteByBook(String bookid) {
        catalogMapper.deleteByBook(bookid);
    }

    public List<Catalog> getCatalogList(Map<String, Object> subKey) {
        return catalogMapper.getList(subKey);
    }

    public void getByBookChapterName(Map<String, String> cataloginfo) {
        catalogMapper.getByBookChapterName(cataloginfo);
    }

    public Response<Catalog> getByExample(Catalog example) {
        return new Response<>(catalogMapper.getByExample(example));
    }

    public List<String> getMyAllCatalogs(String catalogid){
        return catalogMapper.getMyAllCatalogs(catalogid);
    }

    public long getCount(Map<String, Object> param) {
        return catalogMapper.getCount(param);
    }
}
