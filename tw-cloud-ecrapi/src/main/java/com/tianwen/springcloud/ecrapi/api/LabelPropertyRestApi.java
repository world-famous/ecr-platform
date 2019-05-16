package com.tianwen.springcloud.ecrapi.api;

import com.tianwen.springcloud.commonapi.base.response.Response;
import com.tianwen.springcloud.commonapi.query.QueryTree;
import com.tianwen.springcloud.microservice.question.entity.LabelProperty;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

public interface LabelPropertyRestApi {

    @RequestMapping(value = "/getLabelPropertyList", method = RequestMethod.POST)
    public Response<LabelProperty> getLabelPropertyList(@RequestBody QueryTree queryTree);

    @RequestMapping(value = "/addLabelProperty", method = RequestMethod.POST)
    public Response addLabelProperty(@RequestBody LabelProperty labelProperty);

    @RequestMapping(value = "/deleteLabelProperty/{labelid}", method = RequestMethod.GET)
    public Response<LabelProperty> deleteLabelProperty(@PathVariable(value = "labelid") String labelid);

    @RequestMapping(value = "/editLabelProperty", method = RequestMethod.POST)
    public Response<LabelProperty> editLabelProperty(@RequestBody LabelProperty labelProperty);
}
