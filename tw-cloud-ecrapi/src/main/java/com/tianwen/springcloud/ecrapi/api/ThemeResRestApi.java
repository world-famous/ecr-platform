package com.tianwen.springcloud.ecrapi.api;

import com.tianwen.springcloud.commonapi.base.response.Response;
import com.tianwen.springcloud.commonapi.query.QueryTree;
import com.tianwen.springcloud.microservice.resource.entity.ThemeRes;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * Created by gems on 2018.12.21.
 */
public interface ThemeResRestApi {
    @RequestMapping(value = "/insertThemeRes", method = RequestMethod.POST)
    public Response insertThemeRes(@RequestBody List<ThemeRes> list, @RequestHeader(value = "token") String token);

    @RequestMapping(value = "/removeThemeRes", method = RequestMethod.POST)
    public Response removeThemeRes(@RequestBody List<ThemeRes> list, @RequestHeader(value = "token") String token);

    @RequestMapping(value = "/exportToExcel", method = RequestMethod.POST)
    public Response exportToExcel(@RequestBody QueryTree queryTree, @RequestHeader(value = "token") String token) throws IOException;

    @RequestMapping(value = "/importFromExcel", method = RequestMethod.POST)
    public  Response importFromExcel(@RequestPart (value = "themeresfile")MultipartFile file, @RequestHeader(value = "token") String token);
}
