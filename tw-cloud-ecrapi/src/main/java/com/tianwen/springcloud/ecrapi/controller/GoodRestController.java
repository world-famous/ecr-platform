package com.tianwen.springcloud.ecrapi.controller;

import com.tianwen.springcloud.commonapi.base.response.Response;
import com.tianwen.springcloud.commonapi.query.QueryCondition;
import com.tianwen.springcloud.commonapi.query.QueryTree;
import com.tianwen.springcloud.ecrapi.api.GoodRestApi;
import com.tianwen.springcloud.ecrapi.constant.ICommonConstants;
import com.tianwen.springcloud.ecrapi.constant.IErrorMessageConstants;
import com.tianwen.springcloud.microservice.base.entity.UserLoginInfo;
import com.tianwen.springcloud.microservice.bussinessassist.entity.Good;
import com.tianwen.springcloud.microservice.resource.entity.Resource;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping(value = "/good")
public class GoodRestController extends BaseRestController implements GoodRestApi
{
    @Override
    public Response setGoodThumbnail(@RequestBody Resource entity, @RequestHeader(value = "token") String token) {
        if (esSyncFlag.equalsIgnoreCase(ICommonConstants.ES_SAVING_FLAG))
            return new Response<>(IErrorMessageConstants.ERR_CODE_ES_SAVING_MODIFY_DISABLE, IErrorMessageConstants.ERR_MSG_ES_SAVING_MODIFY_DISABLE);

        String contentid = entity.getContentid();
        String thumbnail = entity.getThumbnailpath();

        if (StringUtils.isEmpty(contentid) || StringUtils.isEmpty(thumbnail))
            return new Response(IErrorMessageConstants.ERR_PARAMETER_INVALID, IErrorMessageConstants.ERR_MSG_PARAMETER_INVALID);

        Resource resource = resourceMicroApi.get(contentid).getResponseEntity();

        if (resource == null)
            return new Response(IErrorMessageConstants.ERR_PARAMETER_INVALID, IErrorMessageConstants.ERR_MSG_PARAMETER_INVALID);

        return resourceMicroApi.update(entity);
    }

    @Override
    public Response getGoodsList(@RequestBody QueryTree queryTree, @RequestHeader(value = "token") String token) {
        Response<UserLoginInfo> userResponse = getUserByToken(token);
        UserLoginInfo user = userResponse.getResponseEntity();

        fixGoodQueryTree(queryTree);

        if (user != null) {
            if (!matchRoleId2User(user.getUserId(), ecoUserRoleManager)) {
//                queryTree.addCondition(new QueryCondition("ismanager", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, "1"));
                addSharerangeKeyCondition(queryTree, user);
            }
        }

        fixResourceQueryTree(queryTree);

        Response<Good> resp = goodMicroApi.getList(queryTree);

        List<Good> goods = resp.getPageInfo().getList();
        validateGoods(goods);

        return resp;
    }

    /**
     * Returns good list by status 上架、下架
     * @author jong
     * @date 2019-02-14
     * @param queryTree
     * @param token
     * @return
     */
    @Override
    public Response getGoodsListByStatus(@RequestBody QueryTree queryTree, @RequestHeader(value = "token") String token) {
        Response<UserLoginInfo> userResponse = getUserByToken(token);
        UserLoginInfo user = userResponse.getResponseEntity();

        fixGoodQueryTree(queryTree);

        Response<Good> resp = goodMicroApi.getListByStatus(queryTree);

        List<Good> goods = resp.getPageInfo().getList();
        validateGoods(goods);

        return resp;
    }

    @Override
    @RequestMapping(value = "/getGoodsStatus", method = RequestMethod.GET)
    public Response<Map<String, Object>> getGoodsStatus(@RequestHeader(value = "token") String token)
    {
        QueryTree goodQuery = new QueryTree();
        goodQuery.addCondition(new QueryCondition("isgoods", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, "1"));

        return resourceMicroApi.getResourceStatistics(goodQuery);
    }

    @Override
    public Response<Good> getGoodsInfo(@PathVariable(value = "goodid") String goodid, @RequestHeader(value = "token") String token) {
        QueryTree queryTree = new QueryTree();
        queryTree.addCondition(new QueryCondition("goodid", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, goodid));
        List<Good> goodList = getGoodsList(queryTree, token).getPageInfo().getList();
        Good good = null;
        if (!CollectionUtils.isEmpty(goodList))
            good = goodList.get(0);
        return new Response<>(good);
    }

    @Override
    public Response<Good> getGoodsInfoByProductId(@PathVariable(value = "productid") String productid, @RequestHeader(value = "token") String token) {
        Resource resource = resourceMicroApi.get(productid).getResponseEntity();
        Good good = goodMicroApi.getByContentid(productid).getResponseEntity();
        if (resource != null) {
            validateResource(resource);
            good.setResource(resource);
        }
        return new Response<>(good);
    }

    @Override
    public Response<Good> modifyScoreToGoodsList(@RequestBody Map<String, Object> info, @RequestHeader(value = "token") String token) {
        if (esSyncFlag.equalsIgnoreCase(ICommonConstants.ES_SAVING_FLAG))
            return new Response<>(IErrorMessageConstants.ERR_CODE_ES_SAVING_MODIFY_DISABLE, IErrorMessageConstants.ERR_MSG_ES_SAVING_MODIFY_DISABLE);

        List<String> goodids = (List<String>) info.get("goodids");
        List<Good> result = new ArrayList<>();
        if (!CollectionUtils.isEmpty(goodids)) {
            Integer score = (Integer) info.get("goodprice");
            for (String goodid : goodids) {
                Good good = goodMicroApi.get(goodid).getResponseEntity();
                if (good != null) {
                    good.setGoodprice(score);
                    goodMicroApi.update(good);
                    result.add(good);
                    logOptionEntity(good.getProductid(), good.getGoodid(), ICommonConstants.OPTION_OPTIONTYPE_PRICEMODIFY, token);
                }
            }
        }
        return new Response<>(result);
    }

    @Override
    public Response<Good> modifySellStatusToGoodsList(@RequestBody Map<String, Object> info, @RequestHeader(value = "token") String token) {
        if (esSyncFlag.equalsIgnoreCase(ICommonConstants.ES_SAVING_FLAG))
            return new Response<>(IErrorMessageConstants.ERR_CODE_ES_SAVING_MODIFY_DISABLE, IErrorMessageConstants.ERR_MSG_ES_SAVING_MODIFY_DISABLE);

        List<String> goodids = (List<String>) info.get("goodids");
        List<Good> result = new ArrayList<>();
        if (!CollectionUtils.isEmpty(goodids)) {
            String sellstatus = (String) info.get("sellstatus");

            Timestamp startTime = info.get("onshelftime")==null?null:Timestamp.valueOf(info.get("onshelftime").toString());
            Timestamp endTime = info.get("downshelftime")==null?null:Timestamp.valueOf(info.get("downshelftime").toString());
            for (String goodid : goodids) {
                Good good = goodMicroApi.get(goodid).getResponseEntity();
                if (good != null) {
                    good.setStatus(sellstatus);
                    good.setOnshelftime(startTime);
                    good.setDownshelftime(endTime);
                    goodMicroApi.update(good);
                    result.add(good);

                    if (sellstatus.equals("1"))
                        logOptionEntity(good.getProductid(), good.getGoodid(), ICommonConstants.OPTION_OPTIONTYPE_ALLOWSELL, token);
                    else
                        logOptionEntity(good.getProductid(), good.getGoodid(), ICommonConstants.OPTION_OPTIONTYPE_DENYSELL, token);
                }
            }
        }
        return new Response<>(result);
    }
}
