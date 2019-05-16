package com.tianwen.springcloud.ecrapi.controller;

import com.tianwen.springcloud.commonapi.base.response.Response;
import com.tianwen.springcloud.commonapi.query.QueryTree;
import com.tianwen.springcloud.ecrapi.api.PaperRestApi;
import com.tianwen.springcloud.ecrapi.constant.IErrorMessageConstants;
import com.tianwen.springcloud.microservice.base.entity.UserLoginInfo;
import com.tianwen.springcloud.microservice.question.api.PaperMicroApi;
import com.tianwen.springcloud.microservice.question.entity.Paper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/paper")
public class PaperRestController extends BaseRestController implements PaperRestApi {
    @Autowired
    private PaperMicroApi paperMicroApi;

    @Override
    public Response<Paper> getPaperList(@RequestBody QueryTree queryTree) {
        return paperMicroApi.search(queryTree);
    }

    @Override
    public Response<Map<String, Object>> getPaperStatistics() {
        return paperMicroApi.getPaperStatistics();
    }

    @Override
    public Response<Paper> batchDeletePaper(@RequestBody List<String> paperids) {
        Response<Paper> response = new Response<>();
        List<Paper> paperList = new ArrayList<>();
        for (String paperid : paperids){
            Paper paper = paperMicroApi.delete(paperid).getResponseEntity();
            if (paper != null)
                paperList.add(paper);
        }
        response.getPageInfo().setList(paperList);
        return response;
    }

    @Override
    public Response<Paper> modifyPaper(@RequestBody Paper paper, @RequestHeader(value = "token") String token) {
        UserLoginInfo userLoginInfo = getUserByToken(token).getResponseEntity();
        if (userLoginInfo != null)
            paper.setLastmodifier(userLoginInfo.getUserId());
        else
            return new Response(IErrorMessageConstants.ERR_MSG_TOKEN_NOT_CORRECT);
        return paperMicroApi.update(paper);
    }

    @Override
    public Response<Paper> insertPaper(@RequestBody Paper paper) {
        return paperMicroApi.add(paper);
    }

    @Override
    public Response<Paper> deletePaper(@PathVariable(value = "paperid") String paperid) {
        return paperMicroApi.delete(paperid);
    }
}
