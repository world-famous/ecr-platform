package com.tianwen.springcloud.ecrapi.api;

import com.tianwen.springcloud.commonapi.base.response.Response;
import com.tianwen.springcloud.commonapi.query.QueryTree;
import com.tianwen.springcloud.microservice.bussinessassist.entity.Action;
import org.springframework.web.bind.annotation.*;

import java.util.List;

public interface ActionRestApi {
    @RequestMapping(value = "/getActionList", method = RequestMethod.POST)
    public Response<Action> getActionList(@RequestBody QueryTree queryTree);

    @RequestMapping(value = "/batchActionDelete", method = RequestMethod.POST)
    public Response<Action> batchActionDelete(@RequestBody List<String> actionids);

    @RequestMapping(value = "/inputUserAction", method = RequestMethod.POST)
    public Response inputUserAction(@RequestBody Action action, @RequestHeader(value = "token")String token);
}