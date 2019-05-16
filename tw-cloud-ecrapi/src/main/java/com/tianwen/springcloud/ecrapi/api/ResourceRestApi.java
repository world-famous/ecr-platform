package com.tianwen.springcloud.ecrapi.api;

import com.tianwen.springcloud.commonapi.base.response.Response;
import com.tianwen.springcloud.commonapi.query.QueryTree;
import com.tianwen.springcloud.microservice.resource.entity.Resource;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
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

public interface ResourceRestApi
{
    @RequestMapping(value = "/{contentid}", method = RequestMethod.GET)
    public Response getResource(@PathVariable(value = "contentid") String contentid);

    @RequestMapping(value = "/uploadResource", method = RequestMethod.POST)
    public Response uploadResource(@RequestBody Resource resource, @RequestHeader String token);

    @RequestMapping(value = "/uploadCollectionResource", method = RequestMethod.POST)
    public Response uploadCollectionResource(@RequestBody  Resource resource, @RequestHeader  String token);

    @RequestMapping(value = "/uploadRewardResource", method = RequestMethod.POST)
    public Response uploadRewardResource(@RequestBody  Resource resource, @RequestHeader  String token);

    @RequestMapping(value = "/uploadEstimateResource", method = RequestMethod.POST)
    public Response uploadEstimateResource(@RequestBody  Resource resource, @RequestHeader  String token);

    @RequestMapping(value = "/getResourceList", method = RequestMethod.POST)
    public Response getResourceList(@RequestBody QueryTree queryTree, @RequestHeader  String token);

    @RequestMapping(value = "/getUploadResourceList", method = RequestMethod.POST)
    public Response getUploadResourceList(@RequestBody QueryTree queryTree, @RequestHeader  String token);

    @RequestMapping(value = "/getUploadResourceListByUser", method = RequestMethod.POST)
    public Response getUploadResourceListByUser(@RequestBody QueryTree queryTree, @RequestHeader  String token);

    @RequestMapping(value = "/getDownloadResourceListByUser", method = RequestMethod.POST)
    Response getDownloadResourceListByUser(@RequestBody QueryTree queryTree, @RequestHeader String token);

    @RequestMapping(value = "/isAlreadyDownload/{contentid}", method = RequestMethod.GET)
    Response isAlreadyDownload(@PathVariable(value = "contentid") String contentid, @RequestHeader(value = "token") String token);

    @RequestMapping(value = "/getFavoriteResourceListByUser", method = RequestMethod.POST)
    Response getFavoriteResourceListByUser(@RequestBody QueryTree queryTree, @RequestHeader String token);

    @RequestMapping(value = "/getBasketResourceListByUser", method = RequestMethod.POST)
    Response getBasketResourceListByUser(@RequestBody QueryTree queryTree, @RequestHeader String token);

    @RequestMapping(value = "/getBasketResourceCountByUser", method = RequestMethod.GET)
    Response getBasketResourceCountByUser(@RequestHeader String token);

    @RequestMapping(value = "/addToBasket/{contentid}", method = RequestMethod.GET)
    Response addToBasket(@PathVariable(value = "contentid") String contentid, @RequestHeader(value = "token") String token);

    @RequestMapping(value = "/removeFromBasket/{contentid}", method = RequestMethod.GET)
    Response removeFromBasket(@PathVariable(value = "contentid") String contentid, @RequestHeader(value = "token") String token);

    @RequestMapping(value = "/getActivityResourceList", method = RequestMethod.POST)
    Response getActivityResourceList(@RequestBody QueryTree queryTree, @RequestHeader(value = "token") String token);

    @RequestMapping(value = "/getCollectedResourceList", method = RequestMethod.POST)
    public Response getCollectedResourceList(@RequestBody QueryTree queryTree, @RequestHeader(value = "token")  String token);

    @RequestMapping(value = "/getRewardResourceList", method = RequestMethod.POST)
    Response getRewardResourceList(@RequestBody QueryTree queryTree, @RequestHeader String token);

    @RequestMapping(value = "/getEstimateResourceList", method = RequestMethod.POST)
    Response getEstimateResourceList(@RequestBody QueryTree queryTree, @RequestHeader String token);

    @RequestMapping(value = "/getUploadResourceByUser", method = RequestMethod.POST)
    public Response getUploadResourceByUser(@RequestBody QueryTree queryTree, @RequestHeader(value = "token")  String token);

    @RequestMapping(value = "/getImportedList", method = RequestMethod.POST)
    public Response getImportedList(@RequestBody QueryTree queryTree, @RequestHeader(value = "token") String token);

    @RequestMapping(value = "/batchStateUpdate", method = RequestMethod.POST)
    public Response batchStateUpdate(@RequestBody Map<String , Object> statusinfo, @RequestHeader(value = "token") String token);

    @RequestMapping(value = "/detailResource/{contentid}", method = RequestMethod.GET)
    public Response detailResource(@PathVariable(value = "contentid") String contentid, @RequestHeader(value = "token") String token);

    @RequestMapping(value = "/editResource", method = RequestMethod.POST)
    public Response editResource(@RequestBody Resource resource, @RequestHeader(value = "token") String token);

    @RequestMapping(value = "/addBatchGoods", method = RequestMethod.POST)
    public Response addBatchGoods(@RequestBody Map<String, Object> goods, @RequestHeader(value = "token") String token);

    @RequestMapping(value = "/downloadResource/{contentid}", method = RequestMethod.GET)
    public Response downloadResource(@PathVariable(value = "contentid") String contentid, @RequestHeader(value = "token") String token) throws Exception;

    //author:han
    @RequestMapping(value = "/downloadResourceExtra", method = RequestMethod.GET)
    public void downloadResourceExtra(HttpServletResponse httpServletResponse, @RequestParam Map<String, Object> param) throws Exception;
    //end

    @RequestMapping(value = "/rateResource", method = RequestMethod.POST)
    public Response rateResource(@RequestBody Map<String, Object> mapList, @RequestHeader(value = "token") String token);

    @RequestMapping(value = "/addToFavorite/{contentid}", method = RequestMethod.GET)
    public Response addToFavorite(@PathVariable(value = "contentid") String contentid, @RequestHeader(value = "token") String token);

    @RequestMapping(value = "/removeFromFavorite/{contentid}", method = RequestMethod.GET)
    public Response removeFromFavorite(@PathVariable(value = "contentid") String contentid, @RequestHeader(value = "token") String token);

    @RequestMapping(value = "/batchRemoveFromFavorite", method = RequestMethod.POST)
    public Response batchRemoveFromFavorite(@RequestBody List<String> contentids, @RequestHeader(value = "token") String token);

    @RequestMapping(value = "/voteResource/{contentid}", method = RequestMethod.GET)
    Response voteResource(@PathVariable(value = "contentid") String contentid, @RequestHeader(value = "token") String token);

    @RequestMapping(value = "/getResourceStatus/{mode}", method = RequestMethod.GET)
    Response getResourceStatus(@PathVariable(value = "mode") int mode, @PathVariable(value = "token") String token);

    @RequestMapping(value = "/getResourceStatistics", method = RequestMethod.GET)
    Response getResourceStatistics(@RequestHeader(value = "token") String token);

    @RequestMapping(value = "/exportToExcel/{mode}", method = RequestMethod.GET)
    Response exportToExcel(@PathVariable(value = "mode") int mode, @RequestHeader(value = "token") String token) throws Exception;

    @RequestMapping(value = "/getTopTenUsers", method = RequestMethod.GET)
    Response getTopTenUsers(@RequestHeader(value = "token") String token);

    @RequestMapping(value = "/getResourceCountByArea", method = RequestMethod.POST)
    Response getResourceCountByArea(@RequestBody Map<String, Object> param, @RequestHeader(value = "token") String token);

    @RequestMapping(value = "/setResourceLabel", method = RequestMethod.POST)
    Response setResourceLabel(@RequestBody Map<String, String> param, @RequestHeader(value = "token") String token);

    @RequestMapping(value = "/deleteBatchResources", method = RequestMethod.POST)
    Response deleteBatchResources(@RequestBody List<String> contentids);

    @RequestMapping(value = "/uploadFileInfo", method = RequestMethod.POST)
    Response uploadFileInfo(@RequestBody Map<String, Object> fileInfos, @RequestHeader(value = "token") String token);

    @RequestMapping(value = "/getGoodsCount", method = RequestMethod.POST)
    public Response getGoodsCount(@RequestBody QueryTree queryTree, @RequestHeader(value = "token") String token);

    @RequestMapping(value = "/downResourceTemplate", method = RequestMethod.GET)
    public Response downResourceTemplate(@RequestHeader(value = "token") String token);

    @RequestMapping(value = "/getActivityDetailList", method = RequestMethod.POST)
    public Response getActivityDetailList(@RequestBody  QueryTree queryTree);

    @RequestMapping(value = "/exportActivityDetail", method = RequestMethod.POST)
    Response exportActivityDetail(@RequestBody QueryTree queryTree) throws Exception;
}
