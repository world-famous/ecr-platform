package com.tianwen.springcloud.ecrapi.api;

import com.tianwen.springcloud.commonapi.base.response.Response;
import com.tianwen.springcloud.commonapi.query.QueryTree;
import com.tianwen.springcloud.microservice.base.entity.DictItem;
import com.tianwen.springcloud.microservice.base.entity.Navigation;
import com.tianwen.springcloud.microservice.base.entity.NavigationTree;
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

public interface NavigationRestApi
{
    @RequestMapping(value = "/getNavigationTree", method = RequestMethod.GET)
    public Response<NavigationTree> getNavigationTree();

    @RequestMapping(value = "/getNavigationList", method = RequestMethod.POST)
    public Response<Navigation> getNavigationList(@RequestBody QueryTree queryTree);

    @RequestMapping(value = "/getNavigationInfo/{naviid}", method = RequestMethod.POST)
    public Response<Navigation> getNavigationInfo(@PathVariable(value = "naviid") String naviid);

    @RequestMapping(value = "/addNavigation", method = RequestMethod.POST)
    public Response<Navigation> addNavigation(@RequestBody Navigation navigation, @RequestHeader(value = "token") String token);

    @RequestMapping(value = "/removeNavigation", method = RequestMethod.POST)
    public Response<Navigation> removeNavigation(@RequestBody Navigation navigation, @RequestHeader(value = "token") String token);

    @RequestMapping(value = "/modifyNavigation", method = RequestMethod.POST)
    public Response<Navigation> modifyNavigation(@RequestBody Navigation navigation, @RequestHeader(value = "token") String token);

    @RequestMapping(value = "/removeNavigation/{naviid}", method = RequestMethod.GET)
    public Response<Navigation> removeNavigation(@PathVariable(value = "naviid") String naviid, @RequestHeader(value = "token") String token);

    @RequestMapping(value = "/changeNaviStatus", method = RequestMethod.POST)
    public Response<Navigation> changeNaviStatus(@RequestBody Map<String, Object> param, @RequestHeader(value = "token") String token);

    @RequestMapping(value = "/getSchoolSectionList", method = RequestMethod.POST)
    public Response<DictItem> getSchoolSectionList(@RequestBody Navigation param);

    @RequestMapping(value = "/getSubjectList", method = RequestMethod.POST)
    public Response<DictItem> getSubjectList(@RequestBody Navigation param);

    @RequestMapping(value = "/getGradeList", method = RequestMethod.POST)
    public Response<DictItem> getGradeList(@RequestBody Navigation param);

    @RequestMapping(value = "/getEditionTypeList", method = RequestMethod.POST)
    public Response<DictItem> getEditionTypeList(@RequestBody Navigation param);

    @RequestMapping(value = "/getBookModelList", method = RequestMethod.POST)
    public Response<DictItem> getBookModelList(@RequestBody Navigation param);

    @RequestMapping(value = "/getNaviSchoolSectionList", method = RequestMethod.POST)
    public Response<DictItem> getNaviSchoolSectionList(@RequestBody Navigation param);

    @RequestMapping(value = "/getNaviSubjectList", method = RequestMethod.POST)
    public Response<DictItem> getNaviSubjectList(@RequestBody Navigation param);

    @RequestMapping(value = "/getNaviGradeList", method = RequestMethod.POST)
    public Response<DictItem> getNaviGradeList(@RequestBody Navigation param);

    @RequestMapping(value = "/getNaviEditionTypeList", method = RequestMethod.POST)
    public Response<DictItem> getNaviEditionTypeList(@RequestBody Navigation param);

    @RequestMapping(value = "/getNaviBookModelList", method = RequestMethod.POST)
public Response<DictItem> getNaviBookModelList(@RequestBody Navigation param);

    @RequestMapping(value = "/getCatalogSchoolSectionList", method = RequestMethod.POST)
    public Response<DictItem> getCatalogSchoolSectionList(@RequestBody Navigation param);

    @RequestMapping(value = "/getCatalogSubjectList", method = RequestMethod.POST)
    public Response<DictItem> getCatalogSubjectList(@RequestBody Navigation param);

    @RequestMapping(value = "/getCatalogGradeList", method = RequestMethod.POST)
    public Response<DictItem> getCatalogGradeList(@RequestBody Navigation param);

    @RequestMapping(value = "/getCatalogEditionTypeList", method = RequestMethod.POST)
    public Response<DictItem> getCatalogEditionTypeList(@RequestBody Navigation param);

    @RequestMapping(value = "/getCatalogBookModelList", method = RequestMethod.POST)
    public Response<DictItem> getCatalogBookModelList(@RequestBody Navigation param);
}
