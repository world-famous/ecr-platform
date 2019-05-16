package com.tianwen.springcloud.ecrapi.api;

import com.tianwen.springcloud.commonapi.base.response.Response;
import com.tianwen.springcloud.microservice.resource.entity.Resource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * ECRApi对外接口
 * 
 * @author wangbin
 * @version [版本号, 2017年5月11日]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */

public interface FileServiceRestApi
{
    @RequestMapping(value = "/uploadFile", method = RequestMethod.POST)
    public Response<Resource> uploadFile(@RequestParam("type") int sort, @RequestPart("file") MultipartFile file, @RequestHeader(value = "token") String token) throws IOException;

    @RequestMapping(value = "/download/{contentid}", method = RequestMethod.GET)
    public void downloadResource(HttpServletResponse httpServletResponse, @PathVariable(value = "contentid") String contentid
            , @RequestParam(value = "token") String token) throws Exception;
}
