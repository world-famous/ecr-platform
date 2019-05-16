package com.tianwen.springcloud.ecrapi.controller;

import com.tianwen.springcloud.commonapi.base.response.Response;
import com.tianwen.springcloud.commonapi.query.QueryCondition;
import com.tianwen.springcloud.commonapi.query.QueryTree;
import com.tianwen.springcloud.ecrapi.api.FileRestApi;
import com.tianwen.springcloud.microservice.resource.entity.FileInfo;
import com.tianwen.springcloud.ecrapi.constant.ICommonConstants;
import com.tianwen.springcloud.ecrapi.constant.IErrorMessageConstants;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping (value = "/file")
public class FileRestController extends BaseRestController implements FileRestApi{
    public Response getListByFields( @RequestBody Map<String,String> fields, @RequestHeader( value = "token") String token){
        Response<FileInfo> response = fileMicroApi.getListByFields(fields);
        return response;
    }
}
