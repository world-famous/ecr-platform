package com.tianwen.springcloud.ecrapi.api;

import com.tianwen.springcloud.commonapi.base.response.Response;
import com.tianwen.springcloud.commonapi.query.QueryTree;
import com.tianwen.springcloud.microservice.bussinessassist.entity.FeedBack;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;
import java.util.Map;

public interface FeedbackRestApi {

    @RequestMapping(value = "/upLoadFeedback", method = RequestMethod.POST)
    public Response<FeedBack> upLoadFeedback(@RequestBody FeedBack feedBack, @RequestHeader(value = "token")String token);

    @RequestMapping(value = "/getFeedbackList", method = RequestMethod.POST)
    public Response<FeedBack> getFeedbackList(@RequestBody QueryTree queryTree);

    @RequestMapping(value = "/batchDelete", method = RequestMethod.POST)
    public Response<FeedBack> batchDelete(@RequestBody List<String>feedbackids);

    @RequestMapping(value = "/auditFeedback", method = RequestMethod.POST)
    public Response<String> auditFeedback(@RequestBody String feedbackid, @RequestHeader(value = "token")String token);

    @RequestMapping(value = "/exportToExcel", method = RequestMethod.POST)
    public Response<Map<String, Object>> exportToExcel(@RequestBody QueryTree queryTree) throws Exception;

    @RequestMapping(value = "/setScore2Feedback", method = RequestMethod.POST)
    public Response setScore2Feedback(@RequestBody FeedBack feedBack, @RequestHeader(value = "token")String token);
}