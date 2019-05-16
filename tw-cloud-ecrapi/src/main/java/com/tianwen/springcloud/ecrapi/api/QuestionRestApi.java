package com.tianwen.springcloud.ecrapi.api;

import com.tianwen.springcloud.commonapi.base.response.Response;
import com.tianwen.springcloud.commonapi.query.QueryTree;
import com.tianwen.springcloud.microservice.question.entity.Question;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;
import java.util.Map;

public interface QuestionRestApi {

    @RequestMapping(value = "/getPaperList", method = RequestMethod.POST)
    public Response<Question>getQuestionList(@RequestBody QueryTree queryTree);

    @RequestMapping(value = "/getQuestionStatistics", method = RequestMethod.GET)
    public Response<Map<String, Object>> getQuestionStatistics();

    @RequestMapping(value = "/batchDeleteQuestion", method = RequestMethod.POST)
    public Response<Question> batchDeleteQuestion(@RequestBody List<String> questionids);

    @RequestMapping(value = "/modifyQuestion", method = RequestMethod.POST)
    public Response<Question> modifyQuestion(@RequestBody Question question, @RequestHeader(value = "token") String token);

    @RequestMapping(value = "/insertQuestion", method = RequestMethod.POST)
    public Response<Question> insertQuestion(@RequestBody Question question);
}
