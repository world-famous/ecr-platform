package com.tianwen.springcloud.ecrapi.api;

import com.tianwen.springcloud.commonapi.base.response.Response;
import com.tianwen.springcloud.commonapi.query.QueryTree;
import com.tianwen.springcloud.microservice.bussinessassist.entity.Advert;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

public interface AdvertRestApi {
    @RequestMapping(value = "/getAdvretList", method = RequestMethod.POST)
    public Response getAdvretList(@RequestBody QueryTree queryTree, @RequestHeader(value = "token") String token);

    @RequestMapping(value = "/getAdvretCount", method = RequestMethod.POST)
    public Response getAdvretCount(@RequestBody QueryTree queryTree, @RequestHeader(value = "token") String token);

    @RequestMapping(value = "/insertAdvret", method = RequestMethod.POST)
    public Response insertAdvret(@RequestBody Advert entity, @RequestHeader(value = "token") String token);

    @RequestMapping(value = "/modifyAdvret", method = RequestMethod.POST)
    public Response modifyAdvret(@RequestBody Advert entity, @RequestHeader(value = "token") String token);

    @RequestMapping(value = "/setAdvretStatus", method = RequestMethod.POST)
    public Response setAdvretStatus(@RequestBody Map<String, Object> param, @RequestHeader(value = "token") String token);

    @RequestMapping(value = "/removeAdvret/{advertid}", method = RequestMethod.GET)
    public Response removeAdvret(@PathVariable(value = "advertid") String advertid, @RequestHeader(value = "token") String token);
}