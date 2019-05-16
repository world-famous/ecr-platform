package com.tianwen.springcloud.microservice.base.service;

import java.lang.String;

import com.tianwen.springcloud.commonapi.base.response.Response;
import com.tianwen.springcloud.commonapi.query.Pagination;
import com.tianwen.springcloud.commonapi.query.QueryTree;
import com.tianwen.springcloud.datasource.base.BaseService;
import com.tianwen.springcloud.datasource.util.QueryUtils;
import com.tianwen.springcloud.microservice.base.dao.SynonymMapper;
import com.tianwen.springcloud.microservice.base.entity.Synonym;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SynonymService extends BaseService<Synonym> {

    @Autowired
    private SynonymMapper synonymMapper;

    public Response<Synonym> search(QueryTree queryTree)
    {
        Map<String, Object> map = QueryUtils.queryTree2Map(queryTree);

        Pagination pagination = queryTree.getPagination();

        Long count = synonymMapper.getCount(map);

        map.put("start", pagination.getStart());
        map.put("numPerPage", pagination.getNumPerPage());

        List<Synonym> synonyms = synonymMapper.querySynonymForList(map);
        Response<Synonym> response = new Response<>(synonyms);
        response.getPageInfo().setTotal(count);
        return response;
    }

    public List<Synonym> getSynonymByOrginal(String original)
    {
        Map<String, Object> param = new HashMap<>();
        param.put("original", original);
        return synonymMapper.getSynonymByOrginal(param);
    }

    public List<Synonym> getSynonymBySynonym(String synonym)
    {
        Map<String, Object> param = new HashMap<>();
        param.put("synonym", synonym);
        return synonymMapper.getSynonymBySynonym(param);
    }

    public List<Synonym> getSynonymBySearchkey(String searchkey)
    {
        Map<String, Object> param = new HashMap<>();
        param.put("searchkey", searchkey);
        return synonymMapper.getSynonymBySearchkey(param);
    }

    public List<String> getSynonymByHint(String hint) {
        List<String> result = new ArrayList<>();
        result.add(hint);

        Map<String, Object> orgParam = new HashMap<>();
        orgParam.put("synonym", hint);
        List<String> originals = synonymMapper.getOriginals(orgParam);
        result.addAll(originals);

        Map<String, Object> synParam = new HashMap<>();
        synParam.put("original", hint);
        List<String> synonyms = synonymMapper.getSynonyms(synParam);
        result.addAll(synonyms);

        return result;
    }
}
