package com.tianwen.springcloud.ecrapi.api;

import com.tianwen.springcloud.commonapi.base.response.Response;
import com.tianwen.springcloud.commonapi.query.QueryTree;
import com.tianwen.springcloud.microservice.bussinessassist.entity.Good;
import org.springframework.web.bind.annotation.*;

public interface ESearchRestApi {
    @RequestMapping(value = "/getSearchResult", method = RequestMethod.POST)
    public Response<Good> getSearchResult(@RequestBody QueryTree queryTree, @RequestHeader(value = "token") String token);

    // apis for catalogs

    @RequestMapping(value = "/initCatalogs", method = RequestMethod.GET)
    public Response initCatalogs() throws Exception;

    @RequestMapping(value = "/setCatalogSize/{size}", method = RequestMethod.GET)
    public Response setCatalogSize(@PathVariable(value = "size") Integer size);

    @RequestMapping(value = "/makeCatalogs/{start}", method = RequestMethod.GET)
    public Response makeCatalogs(@PathVariable(value = "start") Integer start);

    // apis for labels

    @RequestMapping(value = "/initLabels", method = RequestMethod.GET)
    public Response initLabels() throws Exception;

    @RequestMapping(value = "/setLabelSize/{size}", method = RequestMethod.GET)
    public Response setLabelSize(@PathVariable(value = "size") Integer size);

    @RequestMapping(value = "/makeLabels/{start}", method = RequestMethod.GET)
    public Response makeLabels(@PathVariable(value = "start") Integer start);

    // apis for users

    @RequestMapping(value = "/initUsers", method = RequestMethod.GET)
    public Response initUsers() throws Exception;

    @RequestMapping(value = "/setUserSize/{size}", method = RequestMethod.GET)
    public Response setUserSize(@PathVariable(value = "size") Integer size);

    @RequestMapping(value = "/makeUsers/{start}", method = RequestMethod.GET)
    public Response makeUsers(@PathVariable(value = "start") Integer start);

    // apis for dictitems

    @RequestMapping(value = "/initDictItems", method = RequestMethod.GET)
    public Response initDictItems() throws Exception;

    @RequestMapping(value = "/setDictSize/{size}", method = RequestMethod.GET)
    public Response setDictSize(@PathVariable(value = "size") Integer size);

    @RequestMapping(value = "/makeDictItems/{start}", method = RequestMethod.GET)
    public Response makeDictItems(@PathVariable(value = "start") Integer start);

    // apis for organizations

    @RequestMapping(value = "/initOrganizations", method = RequestMethod.GET)
    public Response initOrganizations() throws Exception;

    @RequestMapping(value = "/setOrgSize/{size}", method = RequestMethod.GET)
    public Response setOrgSize(@PathVariable(value = "size") Integer size);

    @RequestMapping(value = "/makeOrganizations/{start}", method = RequestMethod.GET)
    public Response makeOrganizations(@PathVariable(value = "start") Integer start);

    // apis for areas

    @RequestMapping(value = "/initAreas", method = RequestMethod.GET)
    public Response initAreas() throws Exception;

    @RequestMapping(value = "/setAreaSize/{size}", method = RequestMethod.GET)
    public Response setAreaSize(@PathVariable(value = "size") Integer size);

    @RequestMapping(value = "/makeAreas/{start}", method = RequestMethod.GET)
    public Response makeAreas(@PathVariable(value = "start") Integer start);


    // apis for contents

    @RequestMapping(value = "/initContents", method = RequestMethod.GET)
    public Response initContents();

    @RequestMapping(value = "/setContentSize/{size}", method = RequestMethod.GET)
    public Response setContentSize(@PathVariable(value = "size") Integer size);

    @RequestMapping(value = "/makeContents/{start}", method = RequestMethod.GET)
    public Response makeContents(@PathVariable(value = "start") Integer start);

    // apis for connection between contents and catalogs

    @RequestMapping(value = "/initResConCat", method = RequestMethod.GET)
    public Response initResConCat();

    @RequestMapping(value = "/setResConSize/{size}", method = RequestMethod.GET)
    public Response setResConSize(@PathVariable(value = "size") Integer size);

    @RequestMapping(value = "/makeResConCat/{start}", method = RequestMethod.GET)
    public Response makeResConCat(@PathVariable(value = "start") Integer start);

    // apis for goods

    @RequestMapping(value = "/initGoods", method = RequestMethod.GET)
    public Response initGoods();

    @RequestMapping(value = "/setGoodSize/{size}", method = RequestMethod.GET)
    public Response setGoodSize(@PathVariable(value = "size") Integer size);

    @RequestMapping(value = "/makeGoods/{start}", method = RequestMethod.GET)
    public Response makeGoods(@PathVariable(value = "start") Integer start);

    // apis for themes

    @RequestMapping(value = "/initThemes", method = RequestMethod.GET)
    public Response initThemes();

    @RequestMapping(value = "/setThemeSize/{size}", method = RequestMethod.GET)
    public Response setThemeSize(@PathVariable(value = "size") Integer size);

    @RequestMapping(value = "/makeThemes/{start}", method = RequestMethod.GET)
    public Response makeThemes(@PathVariable(value = "start") Integer start);

    @RequestMapping(value = "/initResConThemes", method = RequestMethod.GET)
    public Response initResConThemes();

    @RequestMapping(value = "/setResConThemeSize/{size}", method = RequestMethod.GET)
    public Response setResConThemeSize(@PathVariable(value = "size") Integer size);

    @RequestMapping(value = "/makeResConThemes/{start}", method = RequestMethod.GET)
    public Response makeResConThemes(@PathVariable(value = "start") Integer start);
}