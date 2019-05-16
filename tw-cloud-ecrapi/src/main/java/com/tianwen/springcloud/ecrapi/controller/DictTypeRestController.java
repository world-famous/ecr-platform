package com.tianwen.springcloud.ecrapi.controller;

import com.tianwen.springcloud.commonapi.base.response.Response;
import com.tianwen.springcloud.commonapi.query.QueryTree;
import com.tianwen.springcloud.ecrapi.api.DictTypeRestApi;
import com.tianwen.springcloud.microservice.base.entity.DictType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = "/dicttype")
public class DictTypeRestController extends BaseRestController implements DictTypeRestApi
{
    @Override
    public Response<DictType> getTypeList(@RequestBody QueryTree queryTree) {
        return dictTypeMicroApi.getList(queryTree);
    }

    @Override
    public Response<DictType> insertType(@RequestBody DictType entity) {
        if (entity.getIseditable() == null)
            entity.setIseditable("1");
        return dictTypeMicroApi.insert(entity);
    }

    @Override
    public Response<DictType> modifyType(@RequestBody DictType entity) {
        return dictTypeMicroApi.modify(entity);
    }

    @Override
    public Response<DictType> removeType(@PathVariable(value = "dicttypeid") String dicttypeid) {
        return dictTypeMicroApi.remove(dicttypeid);
    }
}
