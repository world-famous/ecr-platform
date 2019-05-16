package com.tianwen.springcloud.ecrapi.api;

import com.tianwen.springcloud.commonapi.base.response.Response;
import com.tianwen.springcloud.commonapi.query.QueryTree;
import com.tianwen.springcloud.microservice.bussinessassist.entity.Message;
import org.springframework.web.bind.annotation.*;

public interface MessageRestApi {
    @RequestMapping(value = "/getMessageListByUser", method = RequestMethod.POST)
    public Response<Message> getMessageListByUser(@RequestBody QueryTree queryTree, @RequestHeader(value = "token") String token);

    @RequestMapping(value = "/setMessageRead/{newsid}", method = RequestMethod.GET)
    public Response setMessageRead(@PathVariable(value = "newsid")String newsid);

    @RequestMapping(value = "/insertMessage", method = RequestMethod.POST)
    public Response<Message> insertMessage(@RequestBody Message message, @RequestHeader(value = "token")String token);
}