package com.tianwen.springcloud.ecrapi.controller;

import com.tianwen.springcloud.commonapi.base.response.Response;
import com.tianwen.springcloud.commonapi.query.QueryTree;
import com.tianwen.springcloud.ecrapi.api.NoticeRestApi;
import com.tianwen.springcloud.microservice.bussinessassist.api.NoticeMicroApi;
import com.tianwen.springcloud.microservice.bussinessassist.entity.Notice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/notice")
public  class NoticeRestController extends BaseRestController implements NoticeRestApi {

    @Autowired
    private NoticeMicroApi noticeMicroApi;

    @Override
    public Response<Notice> getNoticeList(@RequestBody QueryTree queryTree) {
        return noticeMicroApi.getList(queryTree);
    }
}
