package com.tianwen.springcloud.ecrapi.api;

import com.tianwen.springcloud.commonapi.base.response.Response;
import com.tianwen.springcloud.commonapi.query.QueryTree;
import com.tianwen.springcloud.microservice.base.entity.Label;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * ECRApi对外接口
 * 
 * @author wangbin
 * @version [版本号, 2017年5月11日]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */

public interface LabelRestApi
{
    @RequestMapping(value = "/addLabel", method = RequestMethod.POST)
    public Response addLabel(@RequestBody Label entity, @RequestHeader(value = "token") String token);

    @RequestMapping(value = "/editLabel", method = RequestMethod.POST)
    public Response editLabel(@RequestBody Label entity, @RequestHeader(value = "token") String token);

    @RequestMapping(value = "/moveLabel", method = RequestMethod.POST)
    public Response moveLabel(@RequestBody Map<String, Object> labelData, @RequestHeader(value = "token") String token);

    @RequestMapping(value = "/deleteLabel/{labelid}", method = RequestMethod.GET)
    public Response deleteLabel(@PathVariable(value = "labelid") String labelid, @RequestHeader(value = "token") String token);

    @RequestMapping(value = "/getLabelList", method = RequestMethod.POST)
    public Response getLabelList(@RequestBody QueryTree queryTree, @RequestHeader(value = "token") String token);

    @RequestMapping(value = "/getLabelTree", method = RequestMethod.POST)
    public Response getLabelTree(@RequestBody QueryTree queryTree, @RequestHeader(value = "token") String token);

    @RequestMapping(value = "/getOneLabelList", method = RequestMethod.POST)
    public Response getOneLabelList(@RequestBody Map<String, Object> param, @RequestHeader(value = "token") String token);

    @RequestMapping(value = "/getTwoLabelList", method = RequestMethod.POST)
    public Response getTwoLabelList(@RequestBody Map<String, Object> param, @RequestHeader(value = "token") String token);

    @RequestMapping(value = "/getThreeLabelList", method = RequestMethod.POST)
    public Response getThreeLabelList(@RequestBody Map<String, Object> param, @RequestHeader(value = "token") String token);

    @RequestMapping(value = "/getLabelStructure", method = RequestMethod.POST)
    public Response getLabelStructure(@RequestBody Map<String ,Object> labelData);

    //author:han
    @RequestMapping(value = "/getLabelById/{labelid}", method = RequestMethod.GET)
    public Response getLabelById(@PathVariable(value = "labelid") String labelid, @RequestHeader(value = "token") String token);
    //end:han
}
