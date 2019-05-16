package com.tianwen.springcloud.ecrapi.controller;

import com.tianwen.springcloud.commonapi.base.response.Response;
import com.tianwen.springcloud.commonapi.query.QueryTree;
import com.tianwen.springcloud.ecrapi.api.SynonymRestApi;
import com.tianwen.springcloud.ecrapi.constant.IErrorMessageConstants;
import com.tianwen.springcloud.microservice.base.entity.Synonym;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/synonym")
public class SynonymResController extends BaseRestController implements SynonymRestApi {
    @Override
    public Response<Synonym> getList(@RequestBody QueryTree queryTree) {
        return synonymMicroApi.getList(queryTree);
    }

    @Override
    public Response<Synonym> insert(@RequestBody Synonym entity) {
        if (entity.getSearchkey() == null)
            entity.setSearchkey("");
        return synonymMicroApi.insert(entity);
    }

    @Override
    public Response<Synonym> modify(@RequestBody Synonym entity) {
        if (entity.getSearchkey() == null)
            entity.setSearchkey("");
        return synonymMicroApi.update(entity);
    }

    @Override
    public Response<Synonym> remove(@RequestBody Map<String, List<String>> param) {
        List<String> synIdList = param.get("synids");
        for (String id : synIdList) {
            Response<Synonym> resp = synonymMicroApi.remove(id);
            if (!resp.getServerResult().getResultCode().equalsIgnoreCase(IErrorMessageConstants.OPERATION_SUCCESS))
                return resp;
        }
        return new Response<>();
    }


    @Override
    public Response<Synonym> getByOriginal(@PathVariable(value = "original") String original) {
        return synonymMicroApi.getByOriginal(original);
    }

    @Override
    public Response<Synonym> getBySynonym(@PathVariable(value = "synonym") String synonym) {
        return synonymMicroApi.getBySynonym(synonym);
    }

    @Override
    public Response<Synonym> getBySearchkey(@PathVariable(value = "searchkey") String searchkey) {
        return synonymMicroApi.getBySearchkey(searchkey);
    }
}
