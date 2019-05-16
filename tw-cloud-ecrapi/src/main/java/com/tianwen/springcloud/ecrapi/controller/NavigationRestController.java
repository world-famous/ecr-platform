package com.tianwen.springcloud.ecrapi.controller;

import com.tianwen.springcloud.commonapi.base.response.Response;
import com.tianwen.springcloud.commonapi.query.QueryTree;
import com.tianwen.springcloud.ecrapi.api.NavigationRestApi;
import com.tianwen.springcloud.ecrapi.constant.ICommonConstants;
import com.tianwen.springcloud.microservice.base.entity.DictItem;
import com.tianwen.springcloud.microservice.base.entity.Navigation;
import com.tianwen.springcloud.microservice.base.entity.NavigationTree;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequestMapping(value = "/navigation")
public class NavigationRestController extends BaseRestController implements NavigationRestApi
{
    @Override
    public Response<NavigationTree> getNavigationTree() {
        return navigationMicroApi.getTree();
    }

    @Override
    @RequestMapping(value = "/getNavigationList", method = RequestMethod.POST)
    public Response<Navigation> getNavigationList(@RequestBody QueryTree queryTree)
    {
        return navigationMicroApi.getList(queryTree);
    }

    @Override
    @RequestMapping(value = "/getNavigationInfo/{naviid}", method = RequestMethod.POST)
    public Response<Navigation> getNavigationInfo(@PathVariable(value = "naviid") String naviid)
    {
        return navigationMicroApi.get(naviid);
    }

    @Override
    @RequestMapping(value = "/addNavigation", method = RequestMethod.POST)
    public Response<Navigation> addNavigation(@RequestBody Navigation navigation, @RequestHeader(value = "token") String token)
    {
        navigation.setStatus("1");
        Response<Navigation> response = navigationMicroApi.insert(navigation);
        Navigation navi = response.getResponseEntity();
        if (navi != null && navi.getNaviid() != null)
            logOptionEntity(navi.getNaviid(), navi.getNaviid(), ICommonConstants.OPTION_OPTIONTYPE_SUBNAVI_ADD, token);
        return response;
    }

    @Override
    @RequestMapping(value = "/removeNavigation", method = RequestMethod.POST)
    public Response<Navigation> removeNavigation(@RequestBody Navigation navigation, @RequestHeader(value = "token") String token) {
        Response<Navigation> response = navigationMicroApi.remove(navigation);
        Navigation navi = response.getResponseEntity();
        if (navi != null && navi.getNaviid() != null)
            logOptionEntity(navi.getNaviid(), navi.getNaviid(), ICommonConstants.OPTION_OPTIONTYPE_SUBNAVI_DELETE, token);
        return response;
    }

    @Override
    @RequestMapping(value = "/modifyNavigation", method = RequestMethod.POST)
    public Response<Navigation> modifyNavigation(@RequestBody Navigation navigation, @RequestHeader(value = "token") String token)
    {
        Response<Navigation> response = navigationMicroApi.update(navigation);
        Navigation navi = response.getResponseEntity();
        if (navi != null && navi.getNaviid() != null)
            logOptionEntity(navi.getNaviid(), navi.getNaviid(), ICommonConstants.OPTION_OPTIONTYPE_SUBNAVI_DELETE, token);
        return response;
    }

    @Override
    public Response<Navigation> removeNavigation(@PathVariable(value = "naviid") String naviid, @RequestHeader(value = "token") String token) {
        Response<Navigation> response = navigationMicroApi.delete(naviid);
        Navigation navi = response.getResponseEntity();
        if (navi != null && navi.getNaviid() != null)
            logOptionEntity(navi.getNaviid(), navi.getNaviid(), ICommonConstants.OPTION_OPTIONTYPE_SUBNAVI_DELETE, token);
        return response;
    }

    @Override
    @RequestMapping(value = "/changeNaviStatus", method = RequestMethod.POST)
    public Response<Navigation> changeNaviStatus(@RequestBody Map<String, Object> param, @RequestHeader(value = "token") String token)
    {
        Navigation navi = navigationMicroApi.get(param.get("naviid").toString()).getResponseEntity();
        navi.setStatus(param.get("status").toString());
        logOptionEntity(param.get("naviid").toString(), param.get("naviid").toString(), ICommonConstants.OPTION_OPTIONTYPE_CATALOG_ADD, token);
        return navigationMicroApi.update(navi);
    }

    @Override
    public Response<DictItem> getSchoolSectionList(@RequestBody Navigation param)
    {

        return navigationMicroApi.getSchoolSectionList(param);
    }

    @Override
    public Response<DictItem> getSubjectList(@RequestBody Navigation param)
    {

        return navigationMicroApi.getSubjectList(param);
    }

    @Override
    public Response<DictItem> getGradeList(@RequestBody Navigation param)
    {

        return navigationMicroApi.getGradeList(param);
    }

    @Override
    public Response<DictItem> getBookModelList(@RequestBody Navigation param)
    {

        return navigationMicroApi.getBookModelList(param);
    }

    @Override
    public Response<DictItem> getEditionTypeList(@RequestBody Navigation param)
    {

        return navigationMicroApi.getEditionTypeList(param);
    }

    @Override
    public Response<DictItem> getNaviSchoolSectionList(@RequestBody Navigation param)
    {

        return navigationMicroApi.getNaviSchoolSectionList(param);
    }

    @Override
    public Response<DictItem> getNaviSubjectList(@RequestBody Navigation param)
    {

        return navigationMicroApi.getNaviSubjectList(param);
    }

    @Override
    public Response<DictItem> getNaviGradeList(@RequestBody Navigation param)
    {

        return navigationMicroApi.getNaviGradeList(param);
    }

    @Override
    public Response<DictItem> getNaviBookModelList(@RequestBody Navigation param)
    {

        return navigationMicroApi.getNaviBookModelList(param);
    }

    @Override
    public Response<DictItem> getNaviEditionTypeList(@RequestBody Navigation param)
    {

        return navigationMicroApi.getNaviEditionTypeList(param);
    }

    @Override
    public Response<DictItem> getCatalogSchoolSectionList(@RequestBody Navigation param)
    {

        return navigationMicroApi.getCatalogSchoolSectionList(param);
    }

    @Override
    public Response<DictItem> getCatalogSubjectList(@RequestBody Navigation param)
    {

        return navigationMicroApi.getCatalogSubjectList(param);
    }

    @Override
    public Response<DictItem> getCatalogGradeList(@RequestBody Navigation param)
    {

        return navigationMicroApi.getCatalogGradeList(param);
    }

    @Override
    public Response<DictItem> getCatalogBookModelList(@RequestBody Navigation param)
    {

        return navigationMicroApi.getCatalogBookModelList(param);
    }

    @Override
    public Response<DictItem> getCatalogEditionTypeList(@RequestBody Navigation param)
    {

        return navigationMicroApi.getCatalogEditionTypeList(param);
    }
}
