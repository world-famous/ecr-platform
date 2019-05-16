package com.tianwen.springcloud.ecrapi.api;

import com.tianwen.springcloud.commonapi.base.response.Response;
import com.tianwen.springcloud.commonapi.query.QueryTree;
import com.tianwen.springcloud.microservice.bussinessassist.entity.Good;
import com.tianwen.springcloud.microservice.resource.entity.Resource;
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

public interface GoodRestApi
{
    @RequestMapping(value = "/setGoodThumbnail", method = RequestMethod.POST)
    public Response setGoodThumbnail(@RequestBody Resource entity, @RequestHeader(value = "token") String token);

    @RequestMapping(value = "/getGoodsList", method = RequestMethod.POST)
    public Response getGoodsList(@RequestBody QueryTree queryTree, @RequestHeader(value = "token") String token);

    @RequestMapping(value = "/getGoodsListByStatus", method = RequestMethod.POST)
    public Response getGoodsListByStatus(@RequestBody QueryTree queryTree, @RequestHeader(value = "token") String token);

    @RequestMapping(value = "/getGoodsStatus", method = RequestMethod.GET)
    Response<Map<String, Object>> getGoodsStatus(@RequestHeader(value = "token") String token);

    @RequestMapping(value = "/getGoodsInfo/{goodid}", method = RequestMethod.GET)
    public Response<Good> getGoodsInfo(@PathVariable(value = "goodid") String goodid, @RequestHeader(value = "token") String token);

    @RequestMapping(value = "/getGoodsInfoByProductId/{productid}", method = RequestMethod.GET)
    Response<Good> getGoodsInfoByProductId(@PathVariable(value = "productid") String productid, @RequestHeader(value = "token") String token);

    @RequestMapping(value = "/modifyScoreToGoodsList", method = RequestMethod.POST)
    public Response<Good> modifyScoreToGoodsList(@RequestBody Map<String, Object> info, @RequestHeader(value = "token") String token);

    @RequestMapping(value = "/modifySellStatusToGoodsList", method = RequestMethod.POST)
    public Response<Good> modifySellStatusToGoodsList(@RequestBody Map<String, Object> info, @RequestHeader(value = "token") String token);
}
