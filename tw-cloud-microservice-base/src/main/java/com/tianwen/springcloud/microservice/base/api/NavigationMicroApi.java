package com.tianwen.springcloud.microservice.base.api;

import com.tianwen.springcloud.commonapi.base.ICRUDMicroApi;
import com.tianwen.springcloud.commonapi.base.response.Response;
import com.tianwen.springcloud.commonapi.query.QueryTree;
import com.tianwen.springcloud.microservice.base.entity.*;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Map;

/**
 * 用户相关对外接口
 * 
 * @author wangbin
 * @version [版本号, 2017年5月11日]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */
@FeignClient(value = "base-service", url = "http://localhost:2226/base-service/navigation")
public interface NavigationMicroApi extends ICRUDMicroApi<Navigation>
{
    @RequestMapping(value = "/getList", method = RequestMethod.POST)
    public Response<Navigation> getList(@RequestBody QueryTree queryTree);

    @RequestMapping(value = "/getTree", method = RequestMethod.GET)
    public Response<NavigationTree> getTree();

    @RequestMapping(value = "/get/{id}", method = RequestMethod.GET)
    public Response<Navigation> get(@PathVariable(value = "id") String id);

    @RequestMapping(value = "/insert", method = RequestMethod.POST)
    public Response<Navigation> insert(@RequestBody Navigation entity);

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public Response<Navigation> update(@RequestBody Navigation entity);

    @RequestMapping(value = "/remove", method = RequestMethod.POST)
    public Response<Navigation> remove(@RequestBody Navigation entity);

    @RequestMapping(value = "/getFromSubjectNavi", method = RequestMethod.POST)
    public Response<Navigation> getByExample(@RequestBody Navigation param);

    @RequestMapping(value = "/getFromCatalogId/{catalogid}", method = RequestMethod.GET)
    public Response<Navigation> getFromCatalogId(@PathVariable(value = "catalogid") String catalogid);

    @RequestMapping(value = "/getSchoolSectionList", method = RequestMethod.POST)
    public Response<DictItem> getSchoolSectionList(@RequestBody  Navigation param);

    @RequestMapping(value = "/getSubjectList", method = RequestMethod.POST)
    public Response<DictItem> getSubjectList(@RequestBody Navigation param);

    @RequestMapping(value = "/getGradeList", method = RequestMethod.POST)
    public Response<DictItem> getGradeList(@RequestBody Navigation param);

    @RequestMapping(value = "/getBookModelList", method = RequestMethod.POST)
    public Response<DictItem> getBookModelList(@RequestBody Navigation param);

    @RequestMapping(value = "/getEditionTypeList", method = RequestMethod.POST)
    public Response<DictItem> getEditionTypeList(@RequestBody Navigation param);

    @RequestMapping(value = "/getNaviSchoolSectionList", method = RequestMethod.POST)
    public Response<DictItem> getNaviSchoolSectionList(@RequestBody Navigation param);

    @RequestMapping(value = "/getNaviSubjectList", method = RequestMethod.POST)
    public Response<DictItem> getNaviSubjectList(@RequestBody Navigation param);

    @RequestMapping(value = "/getNaviGradeList", method = RequestMethod.POST)
    public Response<DictItem> getNaviGradeList(@RequestBody Navigation param);

    @RequestMapping(value = "/getNaviBookModelList", method = RequestMethod.POST)
    public Response<DictItem> getNaviBookModelList(@RequestBody Navigation param);

    @RequestMapping(value = "/getNaviEditionTypeList", method = RequestMethod.POST)
    public Response<DictItem> getNaviEditionTypeList(@RequestBody Navigation param);

    @RequestMapping(value = "/getCatalogSchoolSectionList", method = RequestMethod.POST)
    public Response<DictItem> getCatalogSchoolSectionList(@RequestBody Navigation param);

    @RequestMapping(value = "/getCatalogSubjectList", method = RequestMethod.POST)
    public Response<DictItem> getCatalogSubjectList(@RequestBody Navigation param);

    @RequestMapping(value = "/getCatalogGradeList", method = RequestMethod.POST)
    public Response<DictItem> getCatalogGradeList(@RequestBody Navigation param);

    @RequestMapping(value = "/getCatalogBookModelList", method = RequestMethod.POST)
    public Response<DictItem> getCatalogBookModelList(@RequestBody Navigation param);

    @RequestMapping(value = "/getCatalogEditionTypeList", method = RequestMethod.POST)
    public Response<DictItem> getCatalogEditionTypeList(@RequestBody Navigation param);
}
