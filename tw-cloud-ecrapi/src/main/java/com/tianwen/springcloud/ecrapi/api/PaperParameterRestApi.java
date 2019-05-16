package com.tianwen.springcloud.ecrapi.api;

import com.tianwen.springcloud.commonapi.base.response.Response;
import com.tianwen.springcloud.commonapi.query.QueryTree;
import com.tianwen.springcloud.microservice.question.entity.PaperParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

public interface PaperParameterRestApi {

    @RequestMapping(value = "/getPaperParameterList", method = RequestMethod.POST)
    public Response<PaperParam> getPaperParameterList(@RequestBody QueryTree queryTree);

    @RequestMapping(value = "/addPaperParameter", method = RequestMethod.POST)
    public Response addPaperParameter(@RequestBody PaperParam paperParam);

    @RequestMapping(value = "/deletePaperParameter/{paramid}", method = RequestMethod.GET)
    public Response<PaperParam> deletePaperParameter(@PathVariable(value = "paramid")String paramid);

    @RequestMapping(value = "/editPaperParameter", method = RequestMethod.POST)
    public Response<PaperParam> editPaperParameter(@RequestBody PaperParam paperParam);

    @RequestMapping(value = "/getPaperParameter/{paramid}", method = RequestMethod.GET)
    public Response<PaperParam> getPaperParameter(@PathVariable(value = "paramid")String paramid);
}
