package com.tianwen.springcloud.ecrapi.api;

import com.tianwen.springcloud.commonapi.base.response.Response;

import com.tianwen.springcloud.commonapi.query.QueryTree;
import com.tianwen.springcloud.microservice.base.entity.Theme;
import org.springframework.web.bind.annotation.*;

/**
 * Created by gems on 2018.12.19.
 */

public interface ThemeRestApi {
    @RequestMapping(value = "/insertTheme", method = RequestMethod.POST)
    public Response insertTheme(@RequestBody Theme entity, @RequestHeader(value = "token") String token);

    @RequestMapping(value = "/modifyTheme", method = RequestMethod.POST)
    public Response modifyTheme(@RequestBody Theme entity, @RequestHeader(value = "token") String token);

    @RequestMapping(value = "/removeTheme/{themeid}", method = RequestMethod.GET)
    public Response removeTheme(@PathVariable(value = "themeid") String themeid, @RequestHeader(value = "token") String token);

    @RequestMapping(value = "/getTheme/{themeid}", method = RequestMethod.GET)
    public Response getTheme(@PathVariable(value = "themeid") String themeid, @RequestHeader(value = "token") String token);

    @RequestMapping(value = "/getThemeById/{themeid}", method = RequestMethod.GET)
    public Response getThemeById(@PathVariable(value = "themeid") String themeid, @RequestHeader(value = "token") String token);

    @RequestMapping(value = "/getThemeList", method = RequestMethod.POST)
    public Response getThemeList(@RequestBody QueryTree queryTree, @RequestHeader(value = "token") String token);

    @RequestMapping(value = "/getResourceList", method = RequestMethod.POST)
    public Response getResourceList(@RequestBody QueryTree queryTree, @RequestHeader(value = "token")  String token);

    @RequestMapping(value = "/getHomeList", method = RequestMethod.POST)
    public Response getHomeList(@RequestBody QueryTree queryTree, @RequestHeader(value = "token")  String token);
}
