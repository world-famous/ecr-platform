package com.tianwen.springcloud.ecrapi.controller;

import com.tianwen.springcloud.commonapi.base.response.Response;
import com.tianwen.springcloud.commonapi.query.QueryTree;
import com.tianwen.springcloud.ecrapi.api.PaperParameterRestApi;
import com.tianwen.springcloud.microservice.question.api.PaperParameterMicroApi;
import com.tianwen.springcloud.microservice.question.entity.PaperParam;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/paperparameter")
public class PaperParameterRestController extends BaseRestController implements PaperParameterRestApi {
    @Autowired
    private PaperParameterMicroApi paperParameterMicroApi;

    @Override
    public Response<PaperParam> getPaperParameterList(@RequestBody QueryTree queryTree) {
        return paperParameterMicroApi.getList(queryTree);
    }

    @Override
    public Response addPaperParameter(@RequestBody PaperParam paperParam) {

        List<String> typenamelist = paperParam.getTypenamelist();

        String typename = StringUtils.join(typenamelist, ",");

        Map<String, Object> map = new HashMap<>();
        map.put("type", paperParam.getType());
        map.put("typename", typename);
        paperParam.setTypename(typename);
        return paperParameterMicroApi.add(paperParam);
    }

    @Override
    public Response<PaperParam> deletePaperParameter(@PathVariable(value = "paramid") String paramid) {
        return paperParameterMicroApi.delete(paramid);
    }

    @Override
    public Response<PaperParam> editPaperParameter(@RequestBody PaperParam paperParam) {

        List<String> typenamelist = paperParam.getTypenamelist();

        String typename = StringUtils.join(typenamelist, ",");

        Map<String, Object> map = new HashMap<>();
        map.put("type", paperParam.getType());
        map.put("typename", typename);
        paperParam.setTypename(typename);
        return paperParameterMicroApi.update(paperParam);
    }

    @Override
    public Response<PaperParam> getPaperParameter(@PathVariable(value = "paramid") String paramid) {
        return null;
    }
}
