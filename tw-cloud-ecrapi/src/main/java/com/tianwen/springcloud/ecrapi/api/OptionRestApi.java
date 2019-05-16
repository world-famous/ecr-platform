package com.tianwen.springcloud.ecrapi.api;

import com.tianwen.springcloud.commonapi.base.response.Response;
import com.tianwen.springcloud.commonapi.query.QueryTree;
import com.tianwen.springcloud.microservice.bussinessassist.entity.Option;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

public interface OptionRestApi {
    @RequestMapping(value = "/getOptionList", method = RequestMethod.POST)
    public Response<Option> getOptionList(@RequestBody QueryTree queryTree);

    @RequestMapping(value = "/batchOptionDelete", method = RequestMethod.POST)
    public Response<Option> batchOptionDelete(@RequestBody List<String>optionids);
}