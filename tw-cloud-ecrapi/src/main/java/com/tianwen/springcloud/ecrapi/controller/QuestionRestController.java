package com.tianwen.springcloud.ecrapi.controller;

import com.tianwen.springcloud.commonapi.base.response.Response;
import com.tianwen.springcloud.commonapi.query.QueryTree;
import com.tianwen.springcloud.ecrapi.api.QuestionRestApi;
import com.tianwen.springcloud.ecrapi.constant.IErrorMessageConstants;
import com.tianwen.springcloud.microservice.base.api.UserMicroApi;
import com.tianwen.springcloud.microservice.base.entity.UserLoginInfo;
import com.tianwen.springcloud.microservice.question.api.QuestionMicroApi;
import com.tianwen.springcloud.microservice.question.entity.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/question")
public class QuestionRestController extends BaseRestController implements QuestionRestApi {
    @Autowired
       private QuestionMicroApi questionMicroApi;

    @Autowired
        private UserMicroApi userMicroApi;

    @Override
    public Response<Question> getQuestionList(@RequestBody QueryTree queryTree) {
        return questionMicroApi.getList(queryTree);
    }

    @Override
    public Response<Map<String, Object>> getQuestionStatistics() {
        return questionMicroApi.getQuestionStatistics();
    }

    @Override
    public Response<Question> batchDeleteQuestion(@RequestBody List<String> questionids) {
        Response<Question> response = new Response<>();
        List<Question> questionList = new ArrayList<>();
        for(String questionid : questionids) {
            Question question = questionMicroApi.delete(questionid).getResponseEntity();
            if (question != null)
                questionList.add(question);
        }
        response.getPageInfo().setList(questionList);
        return response;
    }

    @Override
    public Response<Question> modifyQuestion(@RequestBody Question question, @RequestHeader(value = "token")String token) {
        UserLoginInfo userLoginInfo = getUserByToken(token).getResponseEntity();
        if (userLoginInfo != null)
            question.setLastmodifier(userLoginInfo.getUserId());
        else
            return new Response(IErrorMessageConstants.ERR_MSG_TOKEN_NOT_CORRECT);
        return questionMicroApi.update(question);
    }

    @Override
    public Response<Question> insertQuestion(@RequestBody Question question) {
        return questionMicroApi.add(question);
    }
}
