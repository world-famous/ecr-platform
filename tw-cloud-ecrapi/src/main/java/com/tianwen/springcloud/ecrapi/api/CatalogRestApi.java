package com.tianwen.springcloud.ecrapi.api;

import com.tianwen.springcloud.commonapi.base.response.Response;
import com.tianwen.springcloud.commonapi.query.QueryTree;
import com.tianwen.springcloud.microservice.base.entity.Catalog;
import com.tianwen.springcloud.microservice.base.entity.CatalogTree;
import com.tianwen.springcloud.microservice.base.entity.Navigation;
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

public interface CatalogRestApi
{
    @RequestMapping(value = "/exportToExcel", method = RequestMethod.POST)
    Response exportToExcel(@RequestBody Navigation param, @RequestHeader(value = "token") String token) throws Exception;

    @RequestMapping(value = "/getGoodStatus", method = RequestMethod.POST)
    Response getGoodStatus(@RequestBody Navigation param, @RequestHeader(value = "token") String token);

    @RequestMapping(value = "/getCatalogTree", method = RequestMethod.POST)
    public Response<CatalogTree> getCatalogTree(@RequestBody Navigation param);

    @RequestMapping(value = "/getCatalogList", method = RequestMethod.POST)
    public Response<CatalogTree> getCatalogList(@RequestBody QueryTree queryTree);

    @RequestMapping(value = "/getCatalogHeaderByLevel", method = RequestMethod.POST)
    public Response<CatalogTree> getCatalogHeaderByLevel(@RequestBody Navigation param);

    @RequestMapping(value = "/getCatalogHeader", method = RequestMethod.POST)
    public Response<CatalogTree> getCatalogHeader(@RequestBody Navigation param);

    @RequestMapping(value = "/getList", method = RequestMethod.POST)
    public Response<Catalog> getList(@RequestBody QueryTree queryTree);

    @RequestMapping(value = "/get/{id}", method = RequestMethod.GET)
    public Response<Catalog> get(@PathVariable(value = "id") String id);

    @RequestMapping(value = "/insertCatalog", method = RequestMethod.POST)
    public Response<Catalog> insertCatalog(@RequestBody Catalog entity, @RequestHeader(value = "token")String token);

    @RequestMapping(value = "/modifyCatalog", method = RequestMethod.POST)
    public Response<Catalog> modifyCatalog(@RequestBody Catalog entity, @RequestHeader(value = "token")String token);

    @RequestMapping(value = "/moveCatalog", method = RequestMethod.POST)
    public Response<Catalog> moveCatalog(@RequestBody Map<String, Object> param, @RequestHeader(value = "token")String token);

    @RequestMapping(value = "/deleteCatalog/{id}", method = RequestMethod.GET)
    public Response<Catalog> deleteCatalog(@PathVariable(value = "id") String id, @RequestHeader(value = "token")String token);
}
