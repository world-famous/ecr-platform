package com.tianwen.springcloud.microservice.base.controller;

import com.alibaba.fastjson.JSONObject;
import com.tianwen.springcloud.commonapi.base.response.Response;
import com.tianwen.springcloud.commonapi.constant.IStateCode;
import com.tianwen.springcloud.commonapi.log.SystemControllerLog;
import com.tianwen.springcloud.commonapi.query.OrderMethod;
import com.tianwen.springcloud.commonapi.query.QueryCondition;
import com.tianwen.springcloud.commonapi.query.QueryTree;
import com.tianwen.springcloud.datasource.base.AbstractCRUDController;
import com.tianwen.springcloud.datasource.util.QueryUtils;
import com.tianwen.springcloud.microservice.base.api.CatalogMicroApi;
import com.tianwen.springcloud.microservice.base.constant.IBaseMicroConstants;
import com.tianwen.springcloud.microservice.base.entity.*;
import com.tianwen.springcloud.microservice.base.service.BookService;
import com.tianwen.springcloud.microservice.base.service.CatalogService;
import com.tianwen.springcloud.microservice.base.service.NavigationService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.utils.DateUtils;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/catalog")
public class CatalogController extends AbstractCRUDController<Catalog> implements CatalogMicroApi
{
    @Autowired
    private CatalogService catalogService;

    @Autowired
    private NavigationService navigationService;

    @Autowired
    private BookService bookService;

    @Value("${ESEARCH_SERVER}")
    private String esearchServer;

    public JSONObject getKeyword()
    {
        JSONObject keyword, fields;

        keyword = new JSONObject();
        keyword.put("type", "keyword");
        keyword.put("ignore_above", 256);
        fields = new JSONObject();
        fields.put("keyword", keyword);

        return fields;
    }

    @Override
    public Response<Catalog> makeESDb() throws Exception{
        String indexUrl = "http://" + esearchServer + "/" + IBaseMicroConstants.INDEX_CATALOG;

        try{
            HttpResponse response;
            HttpClient httpClient = new DefaultHttpClient();
            HttpDelete httpDelete = new HttpDelete(indexUrl);
            response = httpClient.execute(httpDelete);
        }
        catch (Exception e){
            e.printStackTrace();
        }

        String[] keys = {"catalogid", "parentcatalogid", "creator", "catalogname", "description",
                "pagenum", "type", "status", "createtime", "lastmodifytime", "sequence", "bookid"};
        String[] types = {"text", "text", "text", "text", "text", "long", "text", "text", "date", "date", "text", "text"};

        int i, len = keys.length;

        JSONObject fields = new JSONObject();
        JSONObject properties = new JSONObject();
        JSONObject catalogs = new JSONObject();
        JSONObject mapping = new JSONObject();

        for (i = 0; i < len; i++)
        {
            JSONObject item = new JSONObject();

            item.put("type", types[i]);
            if (types[i].equalsIgnoreCase("text")) {
                item.put("fields", getKeyword());
                item.put("search_analyzer", "ik_smart");
                item.put("analyzer", "ik_smart");
            }
            else if (types[i].equalsIgnoreCase("date"))
            {
                item.put("fields", getKeyword());
                item.put("format", "yyyy-MM-dd HH:mm:ss");
            }

            fields.put(keys[i], item);
        }

        properties.put("properties", fields);
        catalogs.put(IBaseMicroConstants.TYPE_CATALOGS, properties);
        mapping.put("mappings", catalogs);

        HttpClient httpClient = new DefaultHttpClient();
        HttpPut httpPut = new HttpPut(indexUrl);

        StringEntity paramEntity = new StringEntity(mapping.toJSONString(), ContentType.APPLICATION_JSON);
        httpPut.setEntity(paramEntity);
        httpPut.setHeader("Content-Type", "application/json");
        httpClient.execute(httpPut);

        return new Response<>();
    }

    @Override
    public Response<Catalog> batchSaveToES(@RequestBody QueryTree queryTree) {
        int start;
        try{
            start = (int)queryTree.getQueryCondition("start").getFieldValues()[0];
        }
        catch (Exception e) { start = 1; }

        int pageSize = 50;
        int pageNo = start / pageSize;

        if (pageNo == 0)
            pageNo = 1;

        QueryTree resQuery = new QueryTree();
        resQuery.getPagination().setNumPerPage(pageSize);
        resQuery.getPagination().setPageNo(pageNo);

        List<Catalog> dataList = search(resQuery).getPageInfo().getList();

        if (!CollectionUtils.isEmpty(dataList)) {
            for (Catalog item : dataList)
                saveToES(item);
        }
        else
            return new Response<>(IStateCode.PARAMETER_IS_INVALID, "");

        return new Response<>();
    }

    @Override
    public Response<Catalog> saveToES(@RequestBody Catalog entity) {
        JSONObject json = new JSONObject();

        json.put("catalogid", entity.getCatalogid());
        json.put("parentcatalogid", entity.getParentcatalogid());
        json.put("creator", entity.getCreator());
        json.put("catalogname", entity.getCatalogname());
        json.put("description", entity.getDescription());
        json.put("pagenum", entity.getPagenum());
        json.put("catalogtype", entity.getCatalogtype());
        json.put("status", entity.getStatus());
        if (entity.getCreatetime() != null)
            json.put("createtime", DateUtils.formatDate(entity.getCreatetime(), "yyyy-MM-dd HH:mm:ss"));
        if (entity.getLastmodifytime() != null)
            json.put("lastmodifytime", DateUtils.formatDate(entity.getLastmodifytime(), "yyyy-MM-dd HH:mm:ss"));

        json.put("sequence", entity.getSequence());
        json.put("bookid", entity.getBookid());

        HttpPut httpPut = new HttpPut("http://" + esearchServer + "/" + IBaseMicroConstants.INDEX_CATALOG + "/" + IBaseMicroConstants.TYPE_CATALOGS + "/" + entity.getCatalogid());
        HttpClient httpClient = new DefaultHttpClient();

        try
        {
            StringEntity paramEntity = new StringEntity(json.toJSONString(), ContentType.APPLICATION_JSON);
            httpPut.setEntity(paramEntity);
            httpPut.setHeader("Content-Type", "application/json");
            httpPut.setHeader("Accept", "application/json");
            httpClient.execute(httpPut);
        }
        catch (Exception e){
            return new Response<>(IStateCode.HTTP_404, IBaseMicroConstants.ES_SERVER_ERROR);
        }

        return new Response<>();
    }

    @Override
    public Response<Catalog> removeFromES(@RequestBody Catalog entity) {
        HttpDelete httpDelete = new HttpDelete("http://" + esearchServer + "/" + IBaseMicroConstants.INDEX_CATALOG + "/" + IBaseMicroConstants.TYPE_CATALOGS + "/" + entity.getCatalogid());
        HttpClient httpClient = new DefaultHttpClient();
        try {
            httpClient.execute(httpDelete);
        }
        catch (Exception e){
            return new Response<>(IStateCode.HTTP_404, IBaseMicroConstants.ES_SERVER_ERROR);
        }

        return new Response<>();
    }

    @Override
    public Response<Catalog> removeFromES(@PathVariable(value = "id") String id) {
        HttpDelete httpDelete = new HttpDelete("http://" + esearchServer + "/" + IBaseMicroConstants.INDEX_CATALOG + "/" + IBaseMicroConstants.TYPE_CATALOGS + "/" + id);
        HttpClient httpClient = new DefaultHttpClient();
        try {
            httpClient.execute(httpDelete);
        }
        catch (Exception e){
            return new Response<>(IStateCode.HTTP_404, IBaseMicroConstants.ES_SERVER_ERROR);
        }

        return new Response<>();
    }

    public void removeCatalog(Catalog parent)
    {
        if (parent != null)
        {
            Catalog search = new Catalog();
            search.setParentcatalogid(parent.getCatalogid());
            List<Catalog> children = catalogService.select(search, 0, 0);

            for(Catalog child : children)
                removeCatalog(child);

            catalogService.delete(parent);
        }
    }

    @Override
    @ApiOperation(value = "根据ID删除用户信息", notes = "根据ID删除用户信息")
    @ApiImplicitParam(name = "id", value = "用户ID", required = true, dataType = "String", paramType = "path")
    @SystemControllerLog(description = "根据用户ID删除用户信息")
    public Response<Catalog> delete(@PathVariable(value = "id") String id)
    {
        Catalog entity = catalogService.selectByKey(id);

        if (entity != null)
            removeCatalog(entity);

        return new Response<>(entity);
    }

    @Override
    @ApiOperation(value = "根据实体对象删除用户信息", notes = "根据实体对象删除用户信息")
    @ApiImplicitParam(name = "entity", value = "用户信息实体", required = true, dataType = "UserLoginInfo", paramType = "body")
    @SystemControllerLog(description = "根据实体对象删除用户信息")
    public Response<Catalog> deleteByEntity(@RequestBody Catalog entity)
    {
        int ret = catalogService.delete(entity);
        Response<Catalog> resp = new Response<>(entity);
        resp.getServerResult().setResultCode(Integer.toString(ret));
        return resp;
    }
    
    @Override
    @ApiOperation(value = "用户搜索", notes = "用户搜索")
    @ApiImplicitParam(name = "queryTree", value = "搜索条件树", required = true, dataType = "QueryTree", paramType = "body")
    @SystemControllerLog(description = "用户搜索")
    public Response<Catalog> search(@RequestBody QueryTree queryTree)
    {
        return catalogService.search(queryTree);
    }
    
    @Override
    @ApiOperation(value = "获取用户列表", notes = "获取用户列表")
    @ApiImplicitParam(name = "queryTree", value = "搜索条件树", required = true, dataType = "QueryTree", paramType = "body")
    @SystemControllerLog(description = "获取用户列表")
    public Response<Catalog> getList(@RequestBody QueryTree queryTree)
    {
        Example example = QueryUtils.queryTree2Example(queryTree, UserLoginInfo.class);
        List<Catalog> catList = catalogService.selectByExample(example);
        
        Response<Catalog> resp = new Response<Catalog>(catList);
        
        return resp;
    }

    @Override
    @ApiOperation(value = "获取用户列表", notes = "获取用户列表")
    @ApiImplicitParam(name = "queryTree", value = "搜索条件树", required = true, dataType = "QueryTree", paramType = "body")
    @SystemControllerLog(description = "获取用户列表")
    public Response<Catalog> getByExample(@RequestBody Catalog example) {
        return catalogService.getByExample(example);
    }

    @Override
    @ApiOperation(value = "", notes = "")
    @ApiImplicitParam(name = "queryTree", value = "搜索条件树", required = true, dataType = "QueryTree", paramType = "body")
    @SystemControllerLog(description = "")
    public Response<Catalog> get(@PathVariable(value = "id") String id)
    {
        Catalog cat = catalogService.selectByKey(id);
        return new Response<>(cat);
    }

    @Override
    @ApiOperation(value = "", notes = "")
    @ApiImplicitParam(name = "queryTree", value = "搜索条件树", required = true, dataType = "QueryTree", paramType = "body")
    @SystemControllerLog(description = "")
    public Response<Catalog> insertCatalog(@RequestBody Catalog entity) {
        if (entity.getParentcatalogid() == null)
            entity.setParentcatalogid("0");
        int ret = catalogService.insertCatalog(entity);
        if (ret == 1) {
            ret = 200;
            saveToES(entity);
        }
        Response<Catalog> resp = new Response<>(entity);
        resp.getServerResult().setResultCode(Integer.toString(ret));
        return resp;
    }

    @Override
    @ApiOperation(value = "", notes = "")
    @ApiImplicitParam(name = "queryTree", value = "搜索条件树", required = true, dataType = "QueryTree", paramType = "body")
    @SystemControllerLog(description = "")
    public Response<Catalog> modifyCatalog(@RequestBody Catalog entity) {
        int ret = catalogService.updateNotNull(entity);
        if (ret == 1) {
            ret = 200;
            entity = catalogService.selectByKey(entity.getCatalogid());
            saveToES(entity);
        }
        Response<Catalog> resp = new Response<>(entity);
        resp.getServerResult().setResultCode(Integer.toString(ret));
        return resp;
    }

    @Override
    @ApiOperation(value = "", notes = "")
    @ApiImplicitParam(name = "queryTree", value = "搜索条件树", required = true, dataType = "QueryTree", paramType = "body")
    @SystemControllerLog(description = "")
    public Response<Catalog> moveCatalog(@RequestBody Map<String, Object> param) {
        Catalog src = catalogService.selectByKey(param.get("fromcatalogid").toString());
        Catalog tar = catalogService.selectByKey(param.get("tocatalogid").toString());
        List<Catalog> siblings = catalogService.getSiblings(src);

        String sequence;
        for(Catalog cat : siblings)
        {
            sequence = cat.getSequence();
            if (Integer.parseInt(sequence) >= Integer.parseInt(tar.getSequence()) && !cat.getCatalogid().equals(src.getCatalogid()))
            {
                sequence = catalogService.increment(sequence);
                cat.setSequence(sequence);
                cat.setLastmodifytime(new Timestamp(System.currentTimeMillis()));
                catalogService.updateNotNull(cat);
                saveToES(cat);
            }
        }

        src.setSequence(tar.getSequence());
        src.setLastmodifytime(new Timestamp(System.currentTimeMillis()));
        catalogService.updateNotNull(src);
        saveToES(src);

        return new Response<>(new Catalog());
    }

    @Override
    @ApiOperation(value = "", notes = "")
    @SystemControllerLog(description = "")
    public Response<Catalog> deleteByBook(@PathVariable(value = "bookid") String bookid) {
        catalogService.deleteByBook(bookid);
        return new Response<>();
    }

    @Override
    public Response<Catalog> getByBookChapterName(@RequestBody Map<String, String> cataloginfo) {
        catalogService.getByBookChapterName(cataloginfo);
        return null;
    }

    @Override
    public Response<String> getMyAllCatalogs(@PathVariable(value = "parent") String parent) {
        List<String> path = catalogService.getMyAllCatalogs(parent);
        path.add(parent);
        return new Response<>(path);
    }

    @Override
    public Response<Object> getSubNaviInfoList(@RequestBody List<String> catalogids) {
        List<Object> result = new ArrayList<>();
        for (String catalogid : catalogids) {
            if (StringUtils.isEmpty(catalogid)) continue;
            Catalog catalog = catalogService.selectByKey(catalogid);
            if (catalog == null) continue;
            QueryTree bookQuery = new QueryTree();
            bookQuery.addCondition(new QueryCondition("bookid", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, catalog.getBookid()));
            Response<Book> resp = bookService.search(bookQuery);
            List<Book> books;
            if (resp.getPageInfo() == null)
                continue;
            books = resp.getPageInfo().getList();
            if (CollectionUtils.isEmpty(books))
                continue;
            Book book = books.get(0);
            if (book == null || StringUtils.isEmpty(book.getNaviid()) || StringUtils.isEmpty(book.getGradeid()))
                continue;
            Navigation navigation = navigationService.selectByKey(book.getNaviid());
            if (navigation != null) {
                String catalogname = catalog.getCatalogname();
                if (catalog.getParentcatalogid().equals("0"))
                    navigation.setChapter(catalogname);
                else {
                    Catalog parentCatalog = get(catalog.getParentcatalogid()).getResponseEntity();
                    if (parentCatalog != null) {
                        if (parentCatalog.getParentcatalogid().equals("0")) {
                            navigation.setChapter(parentCatalog.getCatalogname());
                            navigation.setSection(catalogname);
                        } else {
                            Catalog parentCatalog2 = get(parentCatalog.getParentcatalogid()).getResponseEntity();
                            if (parentCatalog2 != null) {
                                navigation.setChapter(parentCatalog2.getCatalogname());
                                navigation.setSection(parentCatalog.getCatalogname());
                                navigation.setLesson(catalogname);
                            }
                        }
                    }
                }
                navigation.setGradeid(book.getGradeid());
                navigation.setGrade(book.getGrade());
                navigation.setSchoolsection(book.getSchoolsection());
                navigation.setSubject(book.getSubject());
                navigation.setEditiontype(book.getEditiontype());
                navigation.setBookmodel(book.getBookmodel());
                navigation.setBookid(book.getBookid());
                navigation.setBookname(book.getBookname());
                result.add(navigation);
            }
        }
        return new Response<>(result);
    }

    public List<String> getCatalogIds(String section, String subject, String grade, String edition, String bookmodel)
    {
        Map<String, Object> param = new HashMap<>();
        if (section != null)
            section = "," + section + ",";
        if (subject != null)
            subject = "," + subject + ",";
        if (grade != null)
            grade = "," + grade + ",";
        if (edition != null)
            edition = "," + edition + ",";
        if (bookmodel != null)
            bookmodel = "," + bookmodel + ",";
        param.put("schoolsectionid", section);
        param.put("subjectid", subject);
        param.put("gradeid", grade);
        param.put("editiontypeid", edition);
        param.put("bookmodelid", bookmodel);

        List<Catalog> catList = catalogService.getCatalogList(param);
        List<String> catIds = new ArrayList<>();
        if (!CollectionUtils.isEmpty(catList))
        {
            for(Catalog catalog : catList)
                catIds.add(catalog.getCatalogid());
        }

        return catIds;
    }

    boolean isChildHasId(CatalogTree catalog, String id)
    {
        for(CatalogTree child : catalog.getChildren())
            if (child.getCatalogids().indexOf(id) != -1)
                return true;
        return false;
    }

    public CatalogTree getHeaderTree(Navigation key, List<CatalogTree> total) {
        for(CatalogTree section : total)
        {
            if (!section.getLabel().equals(key.getSchoolsection()))
                continue;
            for (CatalogTree subject : section.getChildren())
            {
                if (!subject.getLabel().equals(key.getSubject()))
                    continue;
                if (subject.getChildren().isEmpty())
                    return subject;
                if (StringUtils.isEmpty(key.getGradeid()))
                {
                    if (key.getParentcatalogid().equals("0"))
                        return subject;
                    for (CatalogTree oneCat : subject.getChildren())
                    {
                        if (oneCat.getCatalogids().indexOf(key.getParentcatalogid()) == -1)
                            continue;
                        for (CatalogTree twoCat : oneCat.getChildren())
                            if (twoCat.getCatalogids().indexOf(key.getParentcatalogid()) != -1)
                                return twoCat;
                        return oneCat;
                    }
                }
                for (CatalogTree grade : subject.getChildren())
                {
                    if (!grade.getLabel().equals(key.getGrade()))
                        continue;
                    if (grade.getChildren().isEmpty())
                        return grade;
                    if (StringUtils.isEmpty(key.getEditiontypeid()))
                    {
                        if (key.getParentcatalogid().equals("0"))
                            return grade;
                        for (CatalogTree oneCat : subject.getChildren())
                        {
                            if (oneCat.getCatalogids().indexOf(key.getParentcatalogid()) == -1)
                                continue;
                            for (CatalogTree twoCat : oneCat.getChildren())
                                if (twoCat.getCatalogids().indexOf(key.getParentcatalogid()) != -1)
                                    return twoCat;
                            return oneCat;
                        }
                    }
                    for (CatalogTree editiontype : grade.getChildren())
                    {
                        if (!editiontype.getLabel().equals(key.getEditiontype()))
                            continue;
                        if (editiontype.getChildren().isEmpty())
                            return editiontype;
                        if (StringUtils.isEmpty(key.getBookmodelid())) {
                            if (key.getParentcatalogid().equals("0"))
                                return editiontype;
                            for (CatalogTree oneCat : editiontype.getChildren()) {
                                if (oneCat.getCatalogids().indexOf(key.getParentcatalogid()) == -1)
                                    continue;
                                for (CatalogTree twoCat : oneCat.getChildren())
                                    if (twoCat.getCatalogids().indexOf(key.getParentcatalogid()) != -1)
                                        return twoCat;
                                return oneCat;
                            }
                        }
                        for (CatalogTree bookmodel : editiontype.getChildren()) {
                            if (!bookmodel.getLabel().equals(key.getBookmodel()))
                                continue;
                            if (key.getParentcatalogid().equals("0"))
                                return bookmodel;
                            for (CatalogTree oneCat : bookmodel.getChildren()) {
                                if (oneCat.getCatalogids().indexOf(key.getParentcatalogid()) == -1)
                                    continue;
                                for (CatalogTree twoCat : oneCat.getChildren())
                                    if (twoCat.getCatalogids().indexOf(key.getParentcatalogid()) != -1)
                                        return twoCat;
                                return oneCat;
                            }
                        }
                    }
                }
            }
        }

        return null;
    }



    @Override
    @ApiOperation(value = "", notes = "")
    @SystemControllerLog(description = "")
    public Response<CatalogTree> getCatalogTree(@RequestBody Navigation param) {
        if (param.getLang() == null)
            param.setLang(IBaseMicroConstants.zh_CN);
        List<Navigation> teachCatalogList = navigationService.getCatalogList(param);

        List<CatalogTree> result = new ArrayList<>();

        List<DictItem> sectionList = navigationService.getCatalogSchoolSectionList(param);
        List<DictItem> subjectList = navigationService.getCatalogSubjectList(param);
        List<DictItem> gradeList = navigationService.getCatalogGradeList(param);
        List<DictItem> editionTypeList = navigationService.getCatalogEditionTypeList(param);
        List<DictItem> bookModeList = navigationService.getCatalogBookModelList(param);

        if(sectionList.size() == 1 && subjectList.size() == 1 && gradeList.size() == 1 && editionTypeList.size() == 1 && bookModeList.size() == 1){
            DictItem gbSection = sectionList.get(0);
            DictItem gbSubject = subjectList.get(0);
            DictItem gbGrade = gradeList.get(0);
            DictItem gbEditionType = editionTypeList.get(0);
            DictItem gbBookModel = bookModeList.get(0);

            List<String> gbCatalogIds = getCatalogIds(gbSection.getDictvalue(), gbSubject.getDictvalue(), gbGrade.getDictvalue(), gbEditionType.getDictvalue(), gbBookModel.getDictvalue());
            Navigation gbNavi = new Navigation();
            gbNavi.setSchoolsectionid(gbSection.getDictvalue());
            gbNavi.setSubjectid(gbSubject.getDictvalue());
            gbNavi.setGradeid(gbGrade.getDictvalue());
            gbNavi.setEditiontypeid(gbEditionType.getDictvalue());
            gbNavi.setBookmodelid(gbBookModel.getDictvalue());
            List<Book> gbBooks = bookService.getBookByNavigation(gbNavi);

            CatalogTree gbSectionTree = new CatalogTree(gbSection.getDictname(),gbSection.getDictvalue());
            CatalogTree gbSubjectTree = new CatalogTree(gbSubject.getDictname(),gbSubject.getDictvalue());
            CatalogTree gbGradeTree = new CatalogTree(gbGrade.getDictname(),gbGrade.getDictvalue());
            CatalogTree gbEditionTypeTree = new CatalogTree(gbEditionType.getDictname(),gbEditionType.getDictvalue());
            CatalogTree gbBookModelTree = new CatalogTree(gbBookModel.getDictname(),gbBookModel.getDictvalue());

            gbSectionTree.setCatalogids(gbCatalogIds);
            gbSubjectTree.setCatalogids(gbCatalogIds);
            gbGradeTree.setCatalogids(gbCatalogIds);
            gbEditionTypeTree.setCatalogids(gbCatalogIds);
            gbBookModelTree.setCatalogids(gbCatalogIds);

            for(Book gbBook: gbBooks){
                CatalogTree bookTree = new CatalogTree(gbBook.getBookname(),gbBook.getBookid());
                bookTree.addCatalogid(bookTree.getId());
                List<Navigation> parentChapters = getBookChapter(gbBook,"0",teachCatalogList);
                for(Navigation parent: parentChapters){
                    List<Navigation> childChapters = getBookChapter(gbBook,parent.getCatalogid(),teachCatalogList);
                    CatalogTree chapterTree = new CatalogTree(parent.getCatalogname(),parent.getCatalogid());
                    chapterTree.addCatalogid(chapterTree.getId());
                    bookTree.addCatalogid(chapterTree.getId());
                    for(Navigation child: childChapters){
                        CatalogTree sectionTree = new CatalogTree(child.getCatalogname(),child.getCatalogid());
                        bookTree.addCatalogid(sectionTree.getId());
                        chapterTree.addCatalogid(sectionTree.getId());
                        sectionTree.addCatalogid(sectionTree.getId());
                        chapterTree.addChildren(sectionTree);
                        List<Navigation> lessonChildChapters = getBookChapter(gbBook,child.getCatalogid(),teachCatalogList);
                        for(Navigation lessonChild: lessonChildChapters){
                            CatalogTree lessonTree = new CatalogTree(lessonChild.getCatalogname(),lessonChild.getCatalogid());
                            bookTree.addCatalogid(lessonTree.getId());
                            chapterTree.addCatalogid(lessonTree.getId());
                            sectionTree.addCatalogid(lessonTree.getId());
                            chapterTree.addCatalogid(lessonTree.getId());
                            lessonTree.addCatalogid(lessonTree.getId());
                            sectionTree.addChildren(lessonTree);
                        }

                    }
                    bookTree.addChildren(chapterTree);
                }
                gbBookModelTree.addChildren(bookTree);
            }

            gbEditionTypeTree.addChildren(gbBookModelTree);
            gbGradeTree.addChildren(gbEditionTypeTree);
            gbSubjectTree.addChildren(gbGradeTree);
            gbSectionTree.addChildren(gbSubjectTree);
            if(gbCatalogIds.size() > 0) {
                result.add(gbSectionTree);
            }

            return new Response<>(result);

        }





        for (DictItem section : sectionList)
        {
            CatalogTree sectionTree = new CatalogTree(section.getDictname(), section.getDictvalue());
            for (DictItem subject : subjectList)
            {
                CatalogTree subjectTree = new CatalogTree(subject.getDictname(), subject.getDictvalue());
                for (DictItem grade : gradeList)
                {
                    CatalogTree gradeTree = new CatalogTree(grade.getDictname(), grade.getDictvalue());
                    for (DictItem editionType : editionTypeList) {
                        CatalogTree editionTypeTree = new CatalogTree(editionType.getDictname(), editionType.getDictvalue());
                        for (DictItem bookModel : bookModeList) {
                            Navigation subNavi = new Navigation();

                            subNavi.setSchoolsectionid(section.getDictvalue());
                            subNavi.setSubjectid(subject.getDictvalue());
                            subNavi.setGradeid(grade.getDictvalue());
                            subNavi.setEditiontypeid(editionType.getDictvalue());
                            subNavi.setBookmodelid(bookModel.getDictvalue());

                            Book book = bookService.getByNavigation(subNavi);

                            if (book != null && book.getBookid() != null) {
                                CatalogTree bookModelTree = new CatalogTree(bookModel.getDictname(), bookModel.getDictvalue());
                                bookModelTree.setCatalogids(getCatalogIds(section.getDictvalue(), subject.getDictvalue(), grade.getDictvalue(), editionType.getDictvalue(), bookModel.getDictvalue()));
                                if (bookModelTree.getCatalogids().size() != 0)
                                    editionTypeTree.addChildren(bookModelTree);
                            }
                        }

                        editionTypeTree.setId(editionType.getDictvalue());
                        editionTypeTree.setCatalogids(getCatalogIds(section.getDictvalue(), subject.getDictvalue(), grade.getDictvalue(), editionType.getDictvalue(), null));
                        if (editionTypeTree.getCatalogids().size() != 0)
                            gradeTree.addChildren(editionTypeTree);
                    }
                    gradeTree.setCatalogids(getCatalogIds(section.getDictvalue(), subject.getDictvalue(), grade.getDictvalue(), null, null));
                    if (gradeTree.getCatalogids().size() != 0)
                        subjectTree.addChildren(gradeTree);
                }
                subjectTree.setCatalogids(getCatalogIds(section.getDictvalue(), subject.getDictvalue(), null, null, null));
                if (subjectTree.getCatalogids().size() != 0)
                    sectionTree.addChildren(subjectTree);
            }
            sectionTree.setCatalogids(getCatalogIds(section.getDictvalue(), null, null, null, null));
            if (sectionTree.getCatalogids().size() != 0)
                result.add(sectionTree);
        }

        for(Navigation teachCatalog : teachCatalogList)
        {
            if (teachCatalog.getCatalogid() == null)
                continue;

            CatalogTree parent = getHeaderTree(teachCatalog, result);
            if (parent == null) continue;
            CatalogTree data = new CatalogTree(teachCatalog.getCatalogname(), teachCatalog.getCatalogid());
            data.addCatalogid(teachCatalog.getCatalogid());

            parent.addCatalogid(teachCatalog.getCatalogid());
            parent.getChildren().add(data);
        }

        return new Response<>(result);
    }

    public List<Navigation> getBookChapter(Book book, String parentId, List<Navigation> lists){
        List<Navigation> returnList = new ArrayList<Navigation>();
        for(Navigation list: lists){
            if(list.getParentcatalogid().equals(parentId) && list.getBookid().equals(book.getBookid())){
                returnList.add(list);
            }
        }
        return returnList;
    }
    public Response<CatalogTree> getCatalogChapterTree(@RequestBody Navigation param) {

        List<CatalogTree> result = new ArrayList<>();
        return new Response<>(result);
    }


    @Override
    public Response<Catalog> getCatalogIds(@RequestBody Map<String, Object> param) {
        long count = catalogService.getCount(param);
        List<Catalog> result = catalogService.getCatalogList(param);
        Response<Catalog> resp = new Response<>(result);
        resp.getPageInfo().setTotal(count);

        return resp;
    }

    @Override
    @ApiOperation(value = "", notes = "")
    @SystemControllerLog(description = "")
    public Response<CatalogTree> getCatalogHeaderByLevel(@RequestBody Navigation param) {
        if (param.getLang() == null)
            param.setLang(IBaseMicroConstants.zh_CN);

        List<CatalogTree> result = new ArrayList<>();
        List<DictItem> items = new ArrayList<>();
        Integer level = param.getLevel();
        if (level == null)
            level = 0;

        if (level < 5) {
            switch (level) {
                case 0:
                    items = navigationService.getCatalogSchoolSectionList(param);
                    break;
                case 1:
                    items = navigationService.getCatalogSubjectList(param);
                    break;
                case 2:
                    items = navigationService.getCatalogGradeList(param);
                    break;
                case 3:
                    items = navigationService.getCatalogEditionTypeList(param);
                    break;
                case 4:
                    items = navigationService.getCatalogBookModelList(param);
                    break;
                default:
                    break;
            }

            for (DictItem item : items)
            {
                CatalogTree catalogTree = new CatalogTree();
                catalogTree.setLabel(item.getDictname());
                catalogTree.setId(item.getDictvalue());
                result.add(catalogTree);
            }
        }
        else
        {
            Map<String, Object> item = new HashMap<>();

            item.put("schoolsectionid", param.getSchoolsectionid());
            item.put("subjectid", param.getSubjectid());
            item.put("gradeid", param.getGradeid());
            item.put("editiontypeid", param.getEditiontypeid());
            item.put("bookmodelid", param.getBookmodelid());
            item.put("parentcatalogid", "0");

            List<Catalog> cats = catalogService.getCatalogList(item);
            for(Catalog cat : cats)
            {
                CatalogTree catalog = new CatalogTree();
                List<String> ids = new ArrayList<>();
                ids.add(cat.getCatalogid());
                catalog.setId(cat.getCatalogid());
                catalog.setLabel(cat.getCatalogname());
                catalog.setCatalogids(ids);
                catalog.setParentcatalogid("0");
                catalog.setChildcount(cat.getChildcount());

                result.add(catalog);
            }
        }

        return new Response<>(result);
    }

    @Override
    @ApiOperation(value = "", notes = "")
    @SystemControllerLog(description = "")
    public Response<CatalogTree> getCatalogHeader(@RequestBody Navigation param) {
        if (param.getLang() == null)
            param.setLang(IBaseMicroConstants.zh_CN);
        List<CatalogTree> result = new ArrayList<>();

        List<CatalogHeader> headers = bookService.getCatalogHeaderInfo(param);

        String section = "", subject = "", grade = "", edition = "", model = "";
        List<String> subjects = new ArrayList<>();
        List<String> grades = new ArrayList<>();
        List<String> editions = new ArrayList<>();
        List<String> models = new ArrayList<>();

        for(CatalogHeader header : headers)
        {
            CatalogTree sectionTree;
            if (!section.equalsIgnoreCase(header.getSchoolsectionid())) {
                sectionTree = new CatalogTree();
                sectionTree.setId(header.getSchoolsectionid());
                sectionTree.setLabel(header.getSchoolsection());
                JSONObject extInfo = new JSONObject();
                extInfo.put("dictid", header.getDictsection());
                sectionTree.setExtInfo(extInfo);

                result.add(sectionTree);
                section = header.getSchoolsectionid();
                subjects.clear();
                subject = "";
            }
            else
                sectionTree = result.get(result.size() - 1);

            CatalogTree subjectTree;
            if (!subject.equalsIgnoreCase(header.getSubjectid())) {
                if (subjects.indexOf(header.getSubjectid()) != -1)
                    continue;

                subjectTree = new CatalogTree();
                subjectTree.setId(header.getSubjectid());
                subjectTree.setLabel(header.getSubject());
                JSONObject extInfo = new JSONObject();
                extInfo.put("dictid", header.getDictsubject());
                subjectTree.setExtInfo(extInfo);

                sectionTree.addChildren(subjectTree);
                subject = header.getSubjectid();
                grades.clear();
                grade = "";
                subjects.add(header.getSubjectid());
            }
            else
                subjectTree = sectionTree.getChildren().get(sectionTree.getChildren().size() - 1);

            CatalogTree gradeTree;
            if (!grade.equalsIgnoreCase(header.getGradeid())) {
                if (grades.indexOf(header.getGradeid()) != -1)
                    continue;

                gradeTree = new CatalogTree();
                gradeTree.setId(header.getGradeid());
                gradeTree.setLabel(header.getGrade());
                JSONObject extInfo = new JSONObject();
                extInfo.put("dictid", header.getDictgrade());
                gradeTree.setExtInfo(extInfo);

                subjectTree.addChildren(gradeTree);
                grade = header.getGradeid();
                editions.clear();
                edition = "";
                grades.add(header.getGradeid());
            }
            else
                gradeTree = subjectTree.getChildren().get(subjectTree.getChildren().size() - 1);

            CatalogTree editionTree;
            if (!edition.equalsIgnoreCase(header.getEditiontypeid())) {
                if (editions.indexOf(header.getEditiontypeid()) != -1)
                    continue;

                editionTree = new CatalogTree();
                editionTree.setId(header.getEditiontypeid());
                editionTree.setLabel(header.getEditiontype());
                JSONObject extInfo = new JSONObject();
                extInfo.put("dictid", header.getDictedition());
                editionTree.setExtInfo(extInfo);

                gradeTree.addChildren(editionTree);
                edition = header.getEditiontypeid();
                models.clear();
                model = "";
                editions.add(header.getEditiontypeid());
            }
            else
                editionTree = gradeTree.getChildren().get(gradeTree.getChildren().size() - 1);

            CatalogTree modelTree;
            if (!model.equalsIgnoreCase(header.getBookmodelid())) {
                if (models.indexOf(header.getBookmodelid()) != -1)
                    continue;

                modelTree = new CatalogTree();
                modelTree.setId(header.getBookmodelid());
                modelTree.setLabel(header.getBookmodel());
                JSONObject extInfo = new JSONObject();
                extInfo.put("dictid", header.getDictmodel());
                extInfo.put("bookid", header.getBookid());
                modelTree.setExtInfo(extInfo);

                editionTree.addChildren(modelTree);
                model = header.getBookmodelid();
                models.add(header.getBookmodelid());

                QueryTree catalogQuery = new QueryTree();
                catalogQuery.addCondition(new QueryCondition("bookid", QueryCondition.Prepender.AND, QueryCondition.Operator.IN, header.getBookid()));
                catalogQuery.addCondition(new QueryCondition("parentcatalogid", QueryCondition.Prepender.AND, QueryCondition.Operator.IN, "0"));
                catalogQuery.addCondition(new QueryCondition("getalldata", QueryCondition.Prepender.AND, QueryCondition.Operator.IN, "1"));
                catalogQuery.orderBy("sequence", OrderMethod.Method.ASC);
                List<Catalog> catalogs = search(catalogQuery).getPageInfo().getList();

                if (catalogs != null){
                    for(Catalog cat : catalogs){
                        CatalogTree catalog = new CatalogTree();
                        List<String> ids = new ArrayList<>();
                        ids.add(cat.getCatalogid());
                        catalog.setId(cat.getCatalogid());
                        catalog.setLabel(cat.getCatalogname());
                        catalog.setCatalogids(ids);
                        catalog.setParentcatalogid("0");
                        catalog.setChildcount(cat.getChildcount());

                        JSONObject pathInfo = new JSONObject();
                        List<String> path = new ArrayList<>();

                        path.add(sectionTree.getLabel());
                        path.add(subjectTree.getLabel());
                        path.add(gradeTree.getLabel());
                        path.add(editionTree.getLabel());
                        path.add(modelTree.getLabel());
                        path.add(catalog.getLabel());
                        pathInfo.put("path", path);
                        catalog.setExtInfo(pathInfo);

                        modelTree.addChildren(catalog);
                    }
                }
            }
        }

/*        for(CatalogTree sectionTree : result){
            List<CatalogTree> subjectTrees = sectionTree.getChildren();

            for(CatalogTree subjectTree : subjectTrees){
                List<CatalogTree> gradeTrees = subjectTree.getChildren();

                for(CatalogTree gradeTree : gradeTrees) {
                    List<CatalogTree> editionTrees = gradeTree.getChildren();

                    for(CatalogTree editionTree : editionTrees){
                        List<CatalogTree> modelTrees = editionTree.getChildren();

                        for(CatalogTree modelTree : modelTrees) {
                            Map<String, Object> search = new HashMap<>();
                            search.put("bookid", modelTree.getExtInfo().getString("bookid"));
                            search.put("parentcatalogid", "0");

                            List<Catalog> catalogs = catalogService.getCatalogList(search);
                            if (catalogs == null)
                                catalogs = new ArrayList<>();

                            for(Catalog cat : catalogs){
                                CatalogTree catalog = new CatalogTree();
                                List<String> ids = new ArrayList<>();
                                ids.add(cat.getCatalogid());
                                catalog.setId(cat.getCatalogid());
                                catalog.setLabel(cat.getCatalogname());
                                catalog.setCatalogids(ids);
                                catalog.setParentcatalogid("0");
                                catalog.setChildcount(cat.getChildcount());

                                JSONObject extInfo = new JSONObject();
                                List<String> path = new ArrayList<>();

                                path.add(sectionTree.getLabel());
                                path.add(subjectTree.getLabel());
                                path.add(gradeTree.getLabel());
                                path.add(editionTree.getLabel());
                                path.add(modelTree.getLabel());
                                path.add(catalog.getLabel());
                                extInfo.put("path", path);
                                catalog.setExtInfo(extInfo);

                                modelTree.addChildren(catalog);
                            }
                        }
                    }
                }
            }
        }*/


        return new Response<>(result);
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
