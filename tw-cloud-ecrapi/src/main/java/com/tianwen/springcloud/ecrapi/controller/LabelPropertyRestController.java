package com.tianwen.springcloud.ecrapi.controller;

import com.tianwen.springcloud.commonapi.base.response.Response;
import com.tianwen.springcloud.commonapi.query.QueryTree;
import com.tianwen.springcloud.ecrapi.api.LabelPropertyRestApi;
import com.tianwen.springcloud.microservice.question.api.LabelPropertyMicroApi;
import com.tianwen.springcloud.microservice.question.entity.LabelProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/labelproperty")
public class LabelPropertyRestController extends BaseRestController implements LabelPropertyRestApi {

    @Autowired
    private LabelPropertyMicroApi labelPropertyMicroApi;

    @Override
    public Response<LabelProperty> getLabelPropertyList(@RequestBody QueryTree queryTree) {
        return labelPropertyMicroApi.getList(queryTree);
    }

    @Override
    public Response addLabelProperty(@RequestBody LabelProperty labelProperty) {
        return labelPropertyMicroApi.add(labelProperty);
    }

    @Override
    public Response<LabelProperty> deleteLabelProperty(@PathVariable(value = "labelid") String labelid) {
        return labelPropertyMicroApi.delete(labelid);
    }

    @Override
    public Response<LabelProperty> editLabelProperty(@RequestBody LabelProperty labelProperty) {
        return labelPropertyMicroApi.update(labelProperty);
    }
}
