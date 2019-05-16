package com.tianwen.springcloud.ecrapi.api;

import com.tianwen.springcloud.commonapi.base.response.Response;
import com.tianwen.springcloud.commonapi.query.QueryTree;
import com.tianwen.springcloud.microservice.base.entity.UserLoginInfo;
import com.tianwen.springcloud.microservice.operation.entity.OrderStatInfo;
import com.tianwen.springcloud.microservice.bussinessassist.entity.Good;
import com.tianwen.springcloud.microservice.operation.entity.Order;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


public interface OrderRestApi {

    @RequestMapping(value = "/addToOrder", method = RequestMethod.POST)
    public Response <Order> addToOrder(@RequestBody List<String> contentids, @RequestHeader(value = "token") String token);

    @RequestMapping(value = "/getMyOrderList", method = RequestMethod.POST)
    public Response <Order> getMyOrderList(@RequestBody QueryTree queryTree,@RequestHeader(value = "token")String token);

    @RequestMapping(value = "/payOrder/{orderid}", method = RequestMethod.GET)
    public Response payOrder(@PathVariable(value = "orderid") String orderid, @RequestHeader(value = "token") String token);

    @RequestMapping(value = "/getGoodsListByOrder", method = RequestMethod.POST)
    public Response<Good> getGoodsListByOrder(@RequestBody QueryTree queryTree);

    @RequestMapping(value = "/getOrderList", method = RequestMethod.POST)
    public Response<Order> getOrderList(@RequestBody QueryTree queryTree );

    @RequestMapping(value = "/getOrderStatistics", method = RequestMethod.GET)
    public Response<Map<String, Object>> getOrderStatistics();

    @RequestMapping(value = "/getOrderByArea", method = RequestMethod.GET)
    public Response<OrderStatInfo> getOrderByArea();

    @RequestMapping(value = "/getTopTenUsers", method = RequestMethod.GET)
    public Response<UserLoginInfo> getTopTenUsers();

    @RequestMapping(value = "/export2Excel", method = RequestMethod.GET)
    public Response<Map<String, Object>> export2Excel()throws Exception;

    @RequestMapping(value = "/getTopThreeAreaByDate", method = RequestMethod.GET)
    public Response<Map<String, Object>>getTopThreeAreaByDate();
}
