package com.tianwen.springcloud.microservice.base.api;

import com.tianwen.springcloud.commonapi.base.ICRUDMicroApi;
import com.tianwen.springcloud.commonapi.base.response.Response;
import com.tianwen.springcloud.commonapi.query.QueryTree;
import com.tianwen.springcloud.microservice.base.entity.Catalog;
import com.tianwen.springcloud.microservice.base.entity.CatalogTree;
import com.tianwen.springcloud.microservice.base.entity.Navigation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;
import java.util.Map;

/**
 * 用户相关对外接口
 * 
 * @author wangbin
 * @version [版本号, 2017年5月11日]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */
@FeignClient(value = "base-service", url = "http://localhost:2226/base-service/catalog")
public interface CatalogMicroApi extends ICRUDMicroApi<Catalog>
{
    @RequestMapping(value = "/makeESDb", method = RequestMethod.GET)
    public Response<Catalog> makeESDb() throws Exception;

    @RequestMapping(value = "/batchSaveToES", method = RequestMethod.POST)
    public Response<Catalog> batchSaveToES(@RequestBody QueryTree queryTree);

    @RequestMapping(value = "/saveToES", method = RequestMethod.POST)
    public Response<Catalog> saveToES(@RequestBody Catalog entity);

    @RequestMapping(value = "/deleteFromES", method = RequestMethod.POST)
    public Response<Catalog> removeFromES(@RequestBody Catalog entity);

    @RequestMapping(value = "/deleteFromES/{id}", method = RequestMethod.GET)
    public Response<Catalog> removeFromES(@PathVariable(value = "id") String id);

    @RequestMapping(value = "/getCatalogHeaderByLevel", method = RequestMethod.POST)
    public Response<CatalogTree> getCatalogHeaderByLevel(@RequestBody Navigation param);

    @RequestMapping(value = "/getCatalogHeader", method = RequestMethod.POST)
    public Response<CatalogTree> getCatalogHeader(@RequestBody Navigation param);

    @RequestMapping(value = "/getCatalogTree", method = RequestMethod.POST)
    public Response<CatalogTree> getCatalogTree(@RequestBody Navigation param);

    @RequestMapping(value = "/getCatalogIds", method = RequestMethod.POST)
    public Response<Catalog> getCatalogIds(@RequestBody Map<String, Object> param);

    @RequestMapping(value = "/getList", method = RequestMethod.POST)
    public Response<Catalog> getList(@RequestBody QueryTree queryTree);

    @RequestMapping(value = "/getByExample", method = RequestMethod.POST)
    Response<Catalog> getByExample(@RequestBody Catalog example);

    @RequestMapping(value = "/get/{id}", method = RequestMethod.GET)
    public Response<Catalog> get(@PathVariable(value = "id") String id);

    @RequestMapping(value = "/delete/{id}", method = RequestMethod.GET)
    public Response<Catalog> delete(@PathVariable(value = "id") String id);

    @RequestMapping(value = "/insertCatalog", method = RequestMethod.POST)
    public Response<Catalog> insertCatalog(@RequestBody Catalog entity);

    @RequestMapping(value = "/modifyCatalog", method = RequestMethod.POST)
    public Response<Catalog> modifyCatalog(@RequestBody Catalog entity);

    @RequestMapping(value = "/moveCatalog", method = RequestMethod.POST)
    public Response<Catalog> moveCatalog(@RequestBody Map<String, Object> param);

    @RequestMapping(value = "/deleteByBook/{bookid}", method = RequestMethod.POST)
    public Response<Catalog> deleteByBook(@PathVariable(value = "bookid") String bookid);

    @RequestMapping(value = "/getByBookChapterName", method = RequestMethod.POST)
    public Response<Catalog> getByBookChapterName(@RequestBody Map<String, String> cataloginfo);

    @RequestMapping(value = "/getMyAllCatalogs/{parent}", method = RequestMethod.GET)
    public Response<String> getMyAllCatalogs(@PathVariable(value = "parent") String parent);

    @RequestMapping(value = "getSubNaviInfoList", method = RequestMethod.POST)
    public Response<Object> getSubNaviInfoList(@RequestBody List<String> catalogids);
}
