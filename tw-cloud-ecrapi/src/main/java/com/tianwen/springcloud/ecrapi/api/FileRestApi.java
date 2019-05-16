package com.tianwen.springcloud.ecrapi.api;
import com.tianwen.springcloud.commonapi.base.response.Response;
import com.tianwen.springcloud.commonapi.query.QueryTree;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * ECRApi对外接口
 *
 * @author kim
 * @version [版本号, 2019年2月11日]
 */

public interface FileRestApi {
    @RequestMapping( value = "/getListByFields",method = RequestMethod.POST)
    public Response getListByFields( @RequestBody Map<String, String> fields, @RequestHeader (value = "token") String token);
}
