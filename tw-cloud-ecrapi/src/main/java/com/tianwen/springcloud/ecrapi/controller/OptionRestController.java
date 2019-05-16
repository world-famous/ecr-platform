package com.tianwen.springcloud.ecrapi.controller;

import com.tianwen.springcloud.commonapi.base.response.Response;
import com.tianwen.springcloud.commonapi.log.SystemControllerLog;
import com.tianwen.springcloud.commonapi.query.QueryTree;
import com.tianwen.springcloud.ecrapi.api.OptionRestApi;
import com.tianwen.springcloud.microservice.bussinessassist.entity.Option;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/option")
public  class OptionRestController extends BaseRestController implements OptionRestApi {
    @Override
    @ApiOperation(value = "获取操作日志列表", notes = "获取操作日志列表")
    @ApiImplicitParam(name = "querytree", value = "搜索条件树", required = true, dataType = "Querytree", paramType = "body")
    @SystemControllerLog(description = "获取操作日志列表")
    public Response<Option> getOptionList(@RequestBody QueryTree queryTree) {
//        int optionNum = optionMicroApi.getOptionCount().getResponseEntity();
//        if (optionNum > ICommonConstants.OPTION_LIMIT)
//            optionMicroApi.deleteSomeOption();
        return optionMicroApi.getList(queryTree);
    }

    @Override
    @ApiOperation(value = "根据id列表删除操作日志列表", notes = "根据id列表删除操作日志列表")
    @ApiImplicitParam(name = "id列表", value = "操作日志id列表", required = true, dataType = "List<String>", paramType = "body")
    @SystemControllerLog(description = "根据id列表删除操作日志列表")
    public Response<Option> batchOptionDelete(@RequestBody List<String> optionids) {
        for (String optionid: optionids){
            optionMicroApi.delete(optionid);
        }
        return new Response<>();
    }
}
