package com.tianwen.springcloud.ecrapi.api;

import com.tianwen.springcloud.commonapi.base.response.Response;
import com.tianwen.springcloud.commonapi.query.QueryTree;
import com.tianwen.springcloud.microservice.question.entity.Paper;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

public interface PaperRestApi {

    @RequestMapping(value = "/getPaperList", method = RequestMethod.POST)
    public Response<Paper>getPaperList(@RequestBody QueryTree queryTree);

    @RequestMapping(value = "/getPaperStatistics", method = RequestMethod.GET)
    public Response<Map<String,Object>>getPaperStatistics();

    @RequestMapping(value = "/batchDeletePaper", method = RequestMethod.POST)
    public Response<Paper> batchDeletePaper(@RequestBody List<String> paperids);

    @RequestMapping(value = "/modifyPaper", method = RequestMethod.POST)
    public Response<Paper> modifyPaper(@RequestBody Paper paper, @RequestHeader(value = "token") String token);

    @RequestMapping(value = "/insertPaper", method = RequestMethod.POST)
    public Response<Paper> insertPaper(@RequestBody Paper paper);

    @RequestMapping(value = "/deletePaper/{paperid}", method = RequestMethod.GET)
    public Response<Paper> deletePaper(@PathVariable(value = "paperid")String paperid);
}
