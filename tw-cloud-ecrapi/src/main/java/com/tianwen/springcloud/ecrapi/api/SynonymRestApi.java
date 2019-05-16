package com.tianwen.springcloud.ecrapi.api;

import com.tianwen.springcloud.commonapi.base.response.Response;
import com.tianwen.springcloud.commonapi.query.QueryTree;
import com.tianwen.springcloud.microservice.base.entity.Synonym;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;
import java.util.Map;

public interface SynonymRestApi {
    @RequestMapping(value = "/getList", method = RequestMethod.POST)
    public Response<Synonym> getList(@RequestBody QueryTree queryTree);

    @RequestMapping(value = "/insert", method = RequestMethod.POST)
    public Response<Synonym> insert(@RequestBody Synonym entity);

    @RequestMapping(value = "/modify", method = RequestMethod.POST)
    public Response<Synonym> modify(@RequestBody Synonym entity);

    @RequestMapping(value = "/remove", method = RequestMethod.POST)
    public Response<Synonym> remove(@RequestBody Map<String, List<String>> param);

    @RequestMapping(value = "/getByOriginal/{original}", method = RequestMethod.GET)
    public Response<Synonym> getByOriginal(@PathVariable(value = "original") String original);

    @RequestMapping(value = "/getBySynonym/{synonym}", method = RequestMethod.GET)
    public Response<Synonym> getBySynonym(@PathVariable(value = "synonym") String synonym);

    @RequestMapping(value = "/getBySearchkey/{searchkey}", method = RequestMethod.GET)
    public Response<Synonym> getBySearchkey(@PathVariable(value = "searchkey") String searchkey);
}