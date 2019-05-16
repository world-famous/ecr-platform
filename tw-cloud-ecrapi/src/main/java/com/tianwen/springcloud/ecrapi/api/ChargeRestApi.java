package com.tianwen.springcloud.ecrapi.api;

import com.tianwen.springcloud.commonapi.base.response.Response;
import com.tianwen.springcloud.commonapi.query.QueryTree;
import com.tianwen.springcloud.microservice.base.entity.UserLoginInfo;
import com.tianwen.springcloud.microservice.operation.entity.Charge;
import com.tianwen.springcloud.microservice.operation.entity.ChargeStatInfo;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;
import java.util.Map;

/**
 * ECRApi对外接口
 * 
 * @author wangbin
 * @version [版本号, 2017年5月11日]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */

public interface ChargeRestApi
{
    @RequestMapping(value = "/getExchange", method = RequestMethod.GET)
    public Response<Integer> getExchange(@RequestHeader(value = "token") String token);

    @RequestMapping(value = "/chargeToAccount", method = RequestMethod.POST)
    public Response<Charge> chargeToAccount(@RequestBody Map<String, Object> param, @RequestHeader(value = "token") String token);

    @RequestMapping(value = "/orderCharge", method = RequestMethod.POST)
    public Response<Charge> orderChange(@RequestBody Map<String, Object> param, @RequestHeader(value = "token") String token);

    @RequestMapping(value = "/getChargeItemList", method = RequestMethod.POST)
    public Response<Charge> getChargeItemList(@RequestBody QueryTree queryTree, @RequestHeader(value = "token") String token);

    @RequestMapping(value = "/getChargeStatistics", method = RequestMethod.GET)
    public Response<Map<String, Object>> getChargeStatistics(@RequestHeader(value = "token") String token);

    @RequestMapping(value = "/getChargeByArea", method = RequestMethod.POST)
    public Response<List<ChargeStatInfo>> getChargeByArea(@RequestBody Map<String, Object> param);

    @RequestMapping(value = "/getTopTenUsers", method = RequestMethod.GET)
    Response<List<UserLoginInfo>> getTopTenUsers(@RequestHeader(value = "token") String token);

    @RequestMapping(value = "/exportToExcel", method = RequestMethod.GET)
    Response<Map<String, Object>> exportToExcel() throws Exception;
}
