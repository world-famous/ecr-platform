package com.tianwen.springcloud.microservice.base.controller;

import com.tianwen.springcloud.commonapi.base.response.Response;
import com.tianwen.springcloud.commonapi.query.QueryCondition;
import com.tianwen.springcloud.commonapi.query.QueryTree;
import com.tianwen.springcloud.datasource.base.AbstractCRUDController;
import com.tianwen.springcloud.microservice.base.api.AreaMicroApi;
import com.tianwen.springcloud.microservice.base.api.SynonymMicroApi;
import com.tianwen.springcloud.microservice.base.entity.*;
import com.tianwen.springcloud.microservice.base.service.AreaService;
import com.tianwen.springcloud.microservice.base.service.OrganizationService;
import com.tianwen.springcloud.microservice.base.service.SynonymService;
import com.tianwen.springcloud.microservice.base.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/synonym")
public class SynonymController extends AbstractCRUDController<Synonym> implements SynonymMicroApi
{
    @Autowired
    private SynonymService synonymService;

    @Override
    public void validate(MethodType methodType, Object p) {

    }

    @Override
    public Response<Synonym> getList(@RequestBody QueryTree queryTree) {
        Response<Synonym> result = synonymService.search(queryTree);
        return result;
    }

    @Override
    public Response<Synonym> insert(@RequestBody Synonym entity) {
        synonymService.save(entity);
        return new Response<>(entity);
    }

    @Override
    public Response<Synonym> remove(@PathVariable(value = "synid") String synid) {
        Synonym entity = synonymService.selectByKey(synid);
        synonymService.delete(entity);
        return new Response<>(entity);
    }

    @Override
    public Response<String> getByHint(@PathVariable(value = "hint") String hint) {
        List<String> matchResult = synonymService.getSynonymByHint(hint);
        return new Response<>(matchResult);
    }

    @Override
    public Response<Synonym> getByOriginal(@PathVariable(value = "original") String original) {
        List<Synonym> result = synonymService.getSynonymByOrginal(original);
        return new Response<>(result);
    }

    @Override
    public Response<Synonym> getBySynonym(@PathVariable(value = "synonym") String synonym) {
        List<Synonym> result = synonymService.getSynonymBySynonym(synonym);
        return new Response<>(result);
    }

    @Override
    public Response<Synonym> getBySearchkey(@PathVariable(value = "searchkey") String searchkey) {
        List<Synonym> result = synonymService.getSynonymBySearchkey(searchkey);
        return new Response<>(result);
    }
}
