package com.tianwen.springcloud.ecrapi.api;

import com.tianwen.springcloud.commonapi.base.response.Response;
import com.tianwen.springcloud.commonapi.query.QueryTree;
import com.tianwen.springcloud.microservice.base.entity.UserLoginInfo;
import com.tianwen.springcloud.microservice.bussinessassist.entity.Audit;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * ECRApi对外接口
 * 
 * @author wangbin
 * @version [版本号, 2017年5月11日]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */

public interface AuditRestApi
{
    @RequestMapping(value = "/getAuditList", method = RequestMethod.POST)
    public Response<com.tianwen.springcloud.microservice.resource.entity.Audit> getAuditList(@RequestBody QueryTree queryTree, @RequestHeader(value = "token") String token);

    @RequestMapping(value = "/allowResource", method = RequestMethod.POST)
    public Response<Audit> allowResource(@RequestBody Map<String, Object> param, @RequestHeader(value = "token") String token);

    @RequestMapping(value = "/denyResource", method = RequestMethod.POST)
    public Response<Audit> denyResource(@RequestBody Map<String, Object> param, @RequestHeader(value = "token") String token);

    @RequestMapping(value = "/getAuditStatistics", method = RequestMethod.POST)
    Response<Map<String, Object>> getAuditStatistics(@RequestBody QueryTree queryTree, @RequestHeader(value = "token") String token);

    //    Author : GOD 2019-2-18 Bug ID: #781
    @RequestMapping(value = "/getAuditStatisticsDetail", method = RequestMethod.POST)
    Response<Long> getAuditStatisticsDetail(@RequestBody QueryTree queryTree, @RequestHeader(value = "token") String token);
    //    Author : GOD 2019-2-18 Bug ID: #781

    @RequestMapping(value = "/getAuditUserList", method = RequestMethod.GET)
    public Response<UserLoginInfo> getAuditUserList(@RequestHeader(value = "token") String token);

    @RequestMapping(value = "/getByContentid/{contentid}", method = RequestMethod.GET)
    public Response<Audit> getByContentid(@PathVariable(value = "contentid") String contentid);

    @RequestMapping(value = "/exportToExcel", method = RequestMethod.POST)
    public Response<Map<String, Object>> exportToExcel(@RequestBody QueryTree queryTree) throws Exception;
}
