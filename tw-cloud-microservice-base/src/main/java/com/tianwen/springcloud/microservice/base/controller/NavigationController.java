package com.tianwen.springcloud.microservice.base.controller;

import com.tianwen.springcloud.commonapi.base.response.Response;
import com.tianwen.springcloud.commonapi.log.SystemControllerLog;
import com.tianwen.springcloud.commonapi.query.QueryTree;
import com.tianwen.springcloud.datasource.base.AbstractCRUDController;
import com.tianwen.springcloud.microservice.base.api.NavigationMicroApi;
import com.tianwen.springcloud.microservice.base.constant.IBaseMicroConstants;
import com.tianwen.springcloud.microservice.base.entity.Book;
import com.tianwen.springcloud.microservice.base.entity.DictItem;
import com.tianwen.springcloud.microservice.base.entity.Navigation;
import com.tianwen.springcloud.microservice.base.entity.NavigationTree;
import com.tianwen.springcloud.microservice.base.service.BookService;
import com.tianwen.springcloud.microservice.base.service.CatalogService;
import com.tianwen.springcloud.microservice.base.service.NavigationService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static jdk.nashorn.internal.objects.Global.print;

@RestController
@RequestMapping(value = "/navigation")
public class NavigationController extends AbstractCRUDController<Navigation> implements NavigationMicroApi
{
    @Autowired
    private NavigationService navigationService;

    @Autowired
    private CatalogService catalogService;

    @Override
    @ApiOperation(value = "根据ID删除用户信息", notes = "根据ID删除用户信息")
    @ApiImplicitParam(name = "id", value = "用户ID", required = true, dataType = "String", paramType = "path")
    @SystemControllerLog(description = "根据用户ID删除用户信息")
    public Response<Navigation> delete(@PathVariable(value = "id") String id)
    {
        Navigation entity = navigationService.selectByKey(id);
        int ret = navigationService.delete(entity);
        Response<Navigation> resp = new Response<>(entity);
        resp.getServerResult().setResultCode(Integer.toString(ret));
        return resp;
    }

    @Override
    @ApiOperation(value = "根据实体对象删除用户信息", notes = "根据实体对象删除用户信息")
    @ApiImplicitParam(name = "entity", value = "用户信息实体", required = true, dataType = "UserLoginInfo", paramType = "body")
    @SystemControllerLog(description = "根据实体对象删除用户信息")
    public Response<Navigation> deleteByEntity(@RequestBody Navigation entity)
    {
        int ret = navigationService.delete(entity);
        Response<Navigation> resp = new Response<>(entity);
        resp.getServerResult().setResultCode(Integer.toString(ret));
        return resp;
    }
    
    @Override
    @ApiOperation(value = "用户搜索", notes = "用户搜索")
    @ApiImplicitParam(name = "queryTree", value = "搜索条件树", required = true, dataType = "QueryTree", paramType = "body")
    @SystemControllerLog(description = "用户搜索")
    public Response<Navigation> search(@RequestBody QueryTree queryTree)
    {
        return navigationService.search(queryTree);
    }
    
    @Override
    @ApiOperation(value = "获取用户列表", notes = "获取用户列表")
    @ApiImplicitParam(name = "queryTree", value = "搜索条件树", required = true, dataType = "QueryTree", paramType = "body")
    @SystemControllerLog(description = "获取用户列表")
    public Response<Navigation> getList(@RequestBody QueryTree queryTree)
    {
        return navigationService.search(queryTree);
    }

    @Override
    public Response<NavigationTree> getTree() {

        List<NavigationTree> result = new ArrayList<>();

        // TODO: set param in getTree with language with default of zh_CN
        Navigation param = new Navigation();
        param.setLang(IBaseMicroConstants.zh_CN);
        List<DictItem> allItems = navigationService.getAllDicItems(param);
        List<Navigation> allNavigation = navigationService.getAllSubjectNavi(param);

        List<DictItem> sectionList = allItems.stream()
                .filter(Objects::nonNull)
                .filter(o -> Objects.nonNull(o.getDicttypeid()) && o.getDicttypeid().equals("SCHOOL_SECTION"))
                .filter(o -> Objects.nonNull(o.getParentdictid()) && o.getParentdictid().equals("0"))
                .collect(Collectors.toList());

        for (DictItem section : sectionList)
        {
            NavigationTree sectionNaviTree = new NavigationTree();
            sectionNaviTree.setLabel(section.getDictname());
            sectionNaviTree.setDictItem(section);

            List<DictItem> subjectList = allItems.stream()
                    .filter(Objects::nonNull)
                    .filter(o->Objects.nonNull(o.getDicttypeid()) && o.getDicttypeid().equals("SUBJECT"))
                    .filter(o->Objects.nonNull(o.getParentdictid()) && o.getParentdictid().equals(section.getDictid()))
                    .collect(Collectors.toList());

            for (DictItem subject : subjectList)
            {
                NavigationTree subjectNaviTree = new NavigationTree();
                subjectNaviTree.setLabel(subject.getDictname());
                subjectNaviTree.setDictItem(subject);

                List<String> subjectNavigation = allNavigation.stream()
                        .filter(Objects::nonNull)
                        .filter(o->Objects.nonNull(o.getSubjectid()) && o.getSubjectid().equals(subject.getDictvalue()))
                        .map(Navigation::getEditiontypeid)
                        .collect(Collectors.toList());


                List<DictItem> editionTypeList = allItems.stream()
                        .filter(Objects::nonNull)
                        .filter(o->Objects.nonNull(o.getDicttypeid()) && o.getDicttypeid().equals("EDITION"))
                        .filter(o->Objects.nonNull(o.getDictvalue()) && subjectNavigation.contains(o.getDictvalue()))
                        .collect(Collectors.toList());
                for (DictItem editionType : editionTypeList)
                {
                    if (StringUtils.isEmpty(editionType.getDictname())) continue;;
                    NavigationTree editionTypeNaviTree = new NavigationTree();
                    editionTypeNaviTree.setLabel(editionType.getDictname());
                    editionTypeNaviTree.setDictItem(editionType);

                    List<String> subjectNavigationEdition = allNavigation.stream()
                            .filter(Objects::nonNull)
                            .filter(o->Objects.nonNull(o.getSubjectid()) && o.getSubjectid().equals(subject.getDictvalue()))
                            .filter(o->Objects.nonNull(o.getEditiontypeid()) && o.getEditiontypeid().equals(editionType.getDictvalue()))
                            .map(Navigation::getBookmodel)
                            .collect(Collectors.toList());

                    List<DictItem> bookModelList = allItems.stream()
                            .filter(Objects::nonNull)
                            .filter(o->Objects.nonNull(o.getDicttypeid()) && o.getDicttypeid().equals("VOLUME") || o.getDicttypeid().equals("TERM"))
                            .filter(o->Objects.nonNull(o.getDictvalue()) && subjectNavigationEdition.contains(o.getDictvalue()))
                            .collect(Collectors.toList());

                    for (DictItem bookModel : bookModelList)
                    {
                        if (StringUtils.isEmpty(bookModel.getDictname())) continue;;
                        NavigationTree bookModelNaviTree = new NavigationTree();
                        bookModelNaviTree.setLabel(bookModel.getDictname());
                        bookModelNaviTree.setDictItem(bookModel);

                        editionTypeNaviTree.getChildren().add(bookModelNaviTree);
                    }
                    subjectNaviTree.getChildren().add(editionTypeNaviTree);
                }
                sectionNaviTree.getChildren().add(subjectNaviTree);
            }
            result.add(sectionNaviTree);
        }

        return new Response<>(result);
    }

    @Override
    @ApiOperation(value = "", notes = "")
    @ApiImplicitParam(name = "queryTree", value = "搜索条件树", required = true, dataType = "QueryTree", paramType = "body")
    @SystemControllerLog(description = "")
    public Response<Navigation> get(@PathVariable(value = "id") String id)
    {
        Navigation naviInfo = navigationService.selectByKey(id);

        Response<Navigation> resp = new Response<>(naviInfo);

        return resp;
    }

    @Override
    @ApiOperation(value = "", notes = "")
    @ApiImplicitParam(name = "queryTree", value = "搜索条件树", required = true, dataType = "QueryTree", paramType = "body")
    @SystemControllerLog(description = "")
    public Response<Navigation> insert(@RequestBody Navigation entity)
    {
        int count = navigationService.getCount(entity);
        if (count != 0)
            return new Response<>(entity);
        int ret = navigationService.save(entity);
        Response<Navigation> resp = new Response<>(entity);
        resp.getServerResult().setResultCode(Integer.toString(ret));
        return resp;
    }

    @Override
    @ApiOperation(value = "", notes = "")
    @ApiImplicitParam(name = "queryTree", value = "搜索条件树", required = true, dataType = "QueryTree", paramType = "body")
    @SystemControllerLog(description = "")
    public Response<Navigation> update(@RequestBody Navigation entity)
    {
        int count = navigationService.getCount(entity);
        if (count != 0)
            return new Response<>(entity);
        int ret = navigationService.updateNotNull(entity);
        Response<Navigation> resp = new Response<>(entity);
        resp.getServerResult().setResultCode(Integer.toString(ret));
        return resp;
    }

    @Override
    public Response<Navigation> remove(@RequestBody Navigation entity) {
        Response<Navigation> resp = new Response<>(entity);
        navigationService.remove(entity);
        return resp;
    }

    @Override
    @ApiOperation(value = "", notes = "")
    @SystemControllerLog(description = "获取学段列表")
    public Response<Navigation> getByExample(@RequestBody Navigation param)
    {
        if (param.getLang() == null)
            param.setLang(IBaseMicroConstants.zh_CN);
        Navigation navi = navigationService.getByExample(param);
        return new Response<>(navi);
    }

    @Override
    @ApiOperation(value = "", notes = "")
    @SystemControllerLog(description = "获取学段列表")
    public Response<Navigation> getFromCatalogId(@PathVariable(value = "catalogid") String catalogid)
    {
        String naviid = catalogService.getNaviFromCatalog(catalogid);
        Navigation navi = navigationService.getByNaviId(naviid);
        return new Response<>(navi);
    }

    @Override
    @ApiOperation(value = "获取学段列表", notes = "获取学段列表")
    @SystemControllerLog(description = "获取学段列表")
    public Response<DictItem> getSchoolSectionList(@RequestBody Navigation param)
    {
        if (StringUtils.isEmpty(param.getLang()))
            param.setLang(IBaseMicroConstants.zh_CN);
        List<DictItem> result = navigationService.getSchoolSectionList(param);
        return new Response<>(result);
    }

    @Override
    @ApiOperation(value = "获取学科列表", notes = "获取学科列表")
    @ApiImplicitParam(name = "param", value = "课程导航信息", required = false, dataType = "Map<String, String>", paramType = "body")
    @SystemControllerLog(description = "获取学科列表")
    public Response<DictItem> getSubjectList(@RequestBody Navigation param)
    {
        if (StringUtils.isEmpty(param.getLang()))
            param.setLang(IBaseMicroConstants.zh_CN);
        List<DictItem> result = navigationService.getSubjectList(param);
        return new Response<>(result);
    }

    @Override
    @ApiOperation(value = "获取年级列表", notes = "获取年级列表")
    @ApiImplicitParam(name = "param", value = "课程导航信息", required = false, dataType = "Map<String, String>", paramType = "body")
    @SystemControllerLog(description = "获取年级列表")
    public Response<DictItem> getGradeList(@RequestBody Navigation param)
    {
        if (StringUtils.isEmpty(param.getLang()))
            param.setLang(IBaseMicroConstants.zh_CN);
        List<DictItem> result = navigationService.getGradeList(param);
        return new Response<>(result);
    }

    @Override
    @ApiOperation(value = "获取册别列表", notes = "获取册别列表")
    @ApiImplicitParam(name = "param", value = "课程导航信息", required = false, dataType = "Map<String, String>", paramType = "body")
    @SystemControllerLog(description = "获取册别列表")
    public Response<DictItem> getBookModelList(@RequestBody Navigation param)
    {
        if (StringUtils.isEmpty(param.getLang()))
            param.setLang(IBaseMicroConstants.zh_CN);
        List<DictItem> result = navigationService.getBookModelList(param);
        return new Response<>(result);
    }

    @Override
    @ApiOperation(value = "获取教材版本列表", notes = "获取教材版本列表")
    @SystemControllerLog(description = "获取教材版本列表")
    public Response<DictItem> getEditionTypeList(@RequestBody Navigation param)
    {
        if (StringUtils.isEmpty(param.getLang()))
            param.setLang(IBaseMicroConstants.zh_CN);
        List<DictItem> result = navigationService.getEditionTypeList(param);
        return new Response<>(result);
    }

    @Override
    @ApiOperation(value = "获取学段列表", notes = "获取学段列表")
    @SystemControllerLog(description = "获取学段列表")
    public Response<DictItem> getNaviSchoolSectionList(@RequestBody Navigation param)
    {
        if (StringUtils.isEmpty(param.getLang()))
            param.setLang(IBaseMicroConstants.zh_CN);
        List<DictItem> result = navigationService.getNaviSchoolSectionList(param);
        return new Response<>(result);
    }

    @Override
    @ApiOperation(value = "获取学科列表", notes = "获取学科列表")
    @ApiImplicitParam(name = "param", value = "课程导航信息", required = false, dataType = "Map<String, String>", paramType = "body")
    @SystemControllerLog(description = "获取学科列表")
    public Response<DictItem> getNaviSubjectList(@RequestBody Navigation param)
    {
        if (StringUtils.isEmpty(param.getLang()))
            param.setLang(IBaseMicroConstants.zh_CN);
        List<DictItem> subjectList = navigationService.getNaviSubjectList(param);
        return new Response<>(subjectList);
    }

    @Override
    @ApiOperation(value = "获取年级列表", notes = "获取年级列表")
    @ApiImplicitParam(name = "param", value = "课程导航信息", required = false, dataType = "Map<String, String>", paramType = "body")
    @SystemControllerLog(description = "获取年级列表")
    public Response<DictItem> getNaviGradeList(@RequestBody Navigation param)
    {
        if (StringUtils.isEmpty(param.getLang()))
            param.setLang(IBaseMicroConstants.zh_CN);
        List<DictItem> gradeList = navigationService.getNaviGradeList(param);
        return new Response<>(gradeList);
    }

    @Override
    @ApiOperation(value = "获取册别列表", notes = "获取册别列表")
    @ApiImplicitParam(name = "param", value = "课程导航信息", required = false, dataType = "Map<String, String>", paramType = "body")
    @SystemControllerLog(description = "获取册别列表")
    public Response<DictItem> getNaviBookModelList(@RequestBody Navigation param)
    {
        if (StringUtils.isEmpty(param.getLang()))
            param.setLang(IBaseMicroConstants.zh_CN);
        List<DictItem> bookModelList = navigationService.getNaviBookModelList(param);
        return new Response<>(bookModelList);
    }

    @Override
    @ApiOperation(value = "获取教材版本列表", notes = "获取教材版本列表")
    @ApiImplicitParam(name = "param", value = "课程导航信息", required = false, dataType = "Map<String, String>", paramType = "body")
    @SystemControllerLog(description = "获取教材版本列表")
    public Response<DictItem> getNaviEditionTypeList(@RequestBody Navigation param)
    {
        if (StringUtils.isEmpty(param.getLang()))
            param.setLang(IBaseMicroConstants.zh_CN);
        List<DictItem> editionTypeList = navigationService.getNaviEditionTypeList(param);
        return new Response<>(editionTypeList);
    }

    @Override
    @ApiOperation(value = "获取学段列表", notes = "获取学段列表")
    @ApiImplicitParam(name = "param", value = "课程导航信息", required = false, dataType = "Map<String, String>", paramType = "body")
    @SystemControllerLog(description = "获取学段列表")
    public Response<DictItem> getCatalogSchoolSectionList(@RequestBody Navigation param)
    {
        if (StringUtils.isEmpty(param.getLang()))
            param.setLang(IBaseMicroConstants.zh_CN);
        List<DictItem> data = navigationService.getCatalogSchoolSectionList(param);
        return new Response<>(data);
    }

    @Override
    @ApiOperation(value = "获取学科列表", notes = "获取学科列表")
    @ApiImplicitParam(name = "param", value = "课程导航信息", required = false, dataType = "Map<String, String>", paramType = "body")
    @SystemControllerLog(description = "获取学科列表")
    public Response<DictItem> getCatalogSubjectList(@RequestBody Navigation param)
    {
        if (StringUtils.isEmpty(param.getLang()))
            param.setLang(IBaseMicroConstants.zh_CN);
        List<DictItem> data = navigationService.getCatalogSubjectList(param);
        return new Response<>(data);
    }

    @Override
    @ApiOperation(value = "获取年级列表", notes = "获取年级列表")
    @ApiImplicitParam(name = "param", value = "课程导航信息", required = false, dataType = "Map<String, String>", paramType = "body")
    @SystemControllerLog(description = "获取年级列表")
    public Response<DictItem> getCatalogGradeList(@RequestBody Navigation param)
    {
        if (StringUtils.isEmpty(param.getLang()))
            param.setLang(IBaseMicroConstants.zh_CN);
        List<DictItem> data = navigationService.getCatalogGradeList(param);
        return new Response<>(data);
    }

    @Override
    @ApiOperation(value = "获取册别列表", notes = "获取册别列表")
    @ApiImplicitParam(name = "param", value = "课程导航信息", required = false, dataType = "Map<String, String>", paramType = "body")
    @SystemControllerLog(description = "获取册别列表")
    public Response<DictItem> getCatalogBookModelList(@RequestBody Navigation param)
    {
        if (StringUtils.isEmpty(param.getLang()))
            param.setLang(IBaseMicroConstants.zh_CN);
        List<DictItem> data = navigationService.getCatalogBookModelList(param);
        return new Response<>(data);
    }

    @Override
    @ApiOperation(value = "获取教材版本列表", notes = "获取教材版本列表")
    @ApiImplicitParam(name = "param", value = "课程导航信息", required = false, dataType = "Map<String, String>", paramType = "body")
    @SystemControllerLog(description = "获取教材版本列表")
    public Response<DictItem> getCatalogEditionTypeList(@RequestBody Navigation param)
    {
        if (StringUtils.isEmpty(param.getLang()))
            param.setLang(IBaseMicroConstants.zh_CN);
        List<DictItem> data = navigationService.getCatalogEditionTypeList(param);
        return new Response<>(data);
    }

    @Override
    public void validate(MethodType methodType, Object p)
    {
        switch (methodType)
        {
            case ADD:
                break;
            case DELETE:
                break;
            case GET:
                break;
            case SEARCH:
                break;
            case UPDATE:
                break;
            case BATCHADD:
                break;
            case BATCHDELETEBYENTITY:
                break;
            case BATCHUPDATE:
                break;
            case DELETEBYENTITY:
                break;
            case GETBYENTITY:
                break;
            default:
                break;
        }
    }
}
