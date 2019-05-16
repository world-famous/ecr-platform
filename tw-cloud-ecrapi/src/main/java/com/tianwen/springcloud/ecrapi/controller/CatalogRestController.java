package com.tianwen.springcloud.ecrapi.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.tianwen.springcloud.commonapi.base.response.Response;
import com.tianwen.springcloud.commonapi.query.QueryCondition;
import com.tianwen.springcloud.commonapi.query.QueryTree;
import com.tianwen.springcloud.ecrapi.api.CatalogRestApi;
import com.tianwen.springcloud.ecrapi.constant.ICommonConstants;
import com.tianwen.springcloud.ecrapi.constant.IErrorMessageConstants;
import com.tianwen.springcloud.ecrapi.util.CommonUtil;
import com.tianwen.springcloud.microservice.base.constant.IBaseMicroConstants;
import com.tianwen.springcloud.microservice.base.entity.*;
import com.tianwen.springcloud.microservice.resource.constant.IResourceMicroConstants;
import com.tianwen.springcloud.microservice.resource.entity.ResConTypeInfo;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.Timestamp;
import java.util.*;


@RestController
@RequestMapping(value = "/catalog")
public class CatalogRestController extends BaseRestController implements CatalogRestApi
{
    public List<Long> getGoodsCount(String section, String subject, String grade, String edition, String volume, String catalogid, List<String> values)
    {
        List<Long> counts = new ArrayList<>();
        long sum = 0;
        for(String value : values)
            counts.add(0L);

        Map<String, Object> param = new HashMap<>();
        param.put("schoolsectionid", section);
        param.put("subjectid", subject);
        param.put("gradeid", grade);
        param.put("editiontypeid", edition);
        param.put("bookmodelid", volume);
        param.put("catalogid", catalogid);

        List<ResConTypeInfo> countInfo = resourceMicroApi.getResourceCountByCatalog(param).getPageInfo().getList();

        if (countInfo == null) countInfo = new ArrayList<>();

        for(ResConTypeInfo item : countInfo)
        {
            int index = values.indexOf(item.getContenttype());

            if (index == -1) continue;

            long count = counts.get(index);

            count += item.getCount();
            counts.set(index, count);
            sum += count;
        }

        counts.add(sum);

        return counts;
    }

    public void calcGoodsCount(CatalogTree parent, List<String> typeNames, List<String> typeValues)
    {
        parent.setTypeNames(new ArrayList<>(typeNames));
        List<Long> typeCnt = new ArrayList<>();
        long total = 0;

        if (parent.getChildren().isEmpty())
        {
            List<String> catIds = parent.getCatalogids();

            long count;

            catIds.add("");

            for(String value : typeValues)
            {
                QueryTree typeTree = new QueryTree();
                typeTree.addCondition(new QueryCondition("contenttype", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, value));
                typeTree.addCondition(new QueryCondition("isgoods", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, "1"));
                typeTree.addCondition(new QueryCondition("catalogids", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, catIds));

                count = resourceMicroApi.getCount(typeTree).getResponseEntity();
                typeCnt.add(count);
                total += count;
            }
        }
        else
        {
            List<CatalogTree> children = parent.getChildren();
            int i, len = typeNames.size();
            long sum;

            for(CatalogTree child : children)
                calcGoodsCount(child, typeNames, typeValues);

            for (i = 0; i < len; i++)
            {
                sum = 0;
                for (CatalogTree child : children)
                    sum += child.getTypeCnts().get(i);
                typeCnt.add(sum);
                total += sum;
            }

            parent.getTypeNames().add("total");
        }

        typeCnt.add(total);
        parent.setTypeCnts(typeCnt);
        parent.getCatalogids().clear();
    }

    public List<CatalogTree> getGoodsData(Navigation param)
    {
        fixNavigationParam(param);
        List<CatalogTree> catalogTrees = catalogMicroApi.getCatalogTree(param).getPageInfo().getList();
        QueryTree queryTree = new QueryTree();
        queryTree.addCondition(new QueryCondition("dicttypeid", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, "CONTENT_TYPE"));
        List<DictItem> conTypeList = dictItemMicroApi.getList(queryTree).getPageInfo().getList();

        List<String> typeNames = new ArrayList<>();
        List<String> typeValues = new ArrayList<>();
        for (DictItem type : conTypeList)
        {
            typeNames.add(type.getDictname());
            typeValues.add(type.getDictvalue());
        }

        for (CatalogTree tree : catalogTrees)
            calcGoodsCount(tree, typeNames, typeValues);

        return catalogTrees;
    }

    public void writeCatalogTreeToExcel(Sheet sheet, int depth, CatalogTree parent)
    {
        int rowNum = sheet.getLastRowNum() + 1;
        Row row = sheet.createRow(rowNum);
        row.createCell(depth);row.getCell(depth).setCellValue(parent.getLabel());
        int i, len = parent.getTypeCnts().size();
        for (i = 0; i < len; i++) {
            row.createCell(i + 8);
            row.getCell(i + 8).setCellValue(parent.getTypeCnts().get(i));
        }
        for (CatalogTree child : parent.getChildren())
            writeCatalogTreeToExcel(sheet, depth + 1, child);
    }

    @Override
    public Response exportToExcel(@RequestBody Navigation param, @RequestHeader(value = "token") String token) throws Exception{
//        List<CatalogTree> catalogTrees = getGoodsData(param);
        param.setLevel(1);

        if (param.getLang() == null)
            param.setLang(IBaseMicroConstants.zh_CN);

        List<CatalogTree> catalogTrees = getCatalogHeader(param).getPageInfo().getList();
        if (catalogTrees == null)
            catalogTrees = new ArrayList<>();

        QueryTree queryTree = new QueryTree();
        queryTree.addCondition(new QueryCondition("dicttypeid", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, "CONTENT_TYPE"));
        queryTree.addCondition(new QueryCondition("lang", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, param.getLang()));
        List<DictItem> conTypeList = dictItemMicroApi.getList(queryTree).getPageInfo().getList();

        String homePath = httpSession.getServletContext().getRealPath("/") + "excelFiles/";
        boolean isDirExist = new File(homePath).isDirectory();
        if (isDirExist == false) {
            boolean res = new File(homePath).mkdir();
            if (!res) {
                return new Response<>("", "");
            }
        }
        String fileName = CommonUtil.convertToMd5(new Timestamp(System.currentTimeMillis()).toString(), true) + ".xls";
        File chartFile = new File(homePath + fileName);
        boolean createResult = chartFile.createNewFile();

        if (createResult == false)
            return new Response(IErrorMessageConstants.ERR_CODE_FILE_NOT_EXIST, IErrorMessageConstants.ERR_MSG_FILE_NOT_EXIST);

        FileOutputStream fileOutputStream = new FileOutputStream(chartFile);
        org.apache.poi.ss.usermodel.Workbook hssfWorkbook = new HSSFWorkbook();
        Sheet sheet = hssfWorkbook.createSheet();

        int i;
        Row row = sheet.createRow(0);
        row.createCell(0);row.getCell(0).setCellValue("学段");
        row.createCell(1);row.getCell(1).setCellValue("学科");
        row.createCell(2);row.getCell(2).setCellValue("学年");
        row.createCell(3);row.getCell(3).setCellValue("教材版本");
        row.createCell(4);row.getCell(4).setCellValue("册别");
        row.createCell(5);row.getCell(5).setCellValue("章");
        row.createCell(6);row.getCell(6).setCellValue("节");
        row.createCell(7);row.getCell(7).setCellValue("课");
        row.createCell(8);row.getCell(8).setCellValue("合计");
        for (i = 0; i < conTypeList.size(); i++) {
            row.createCell(i + 9);
            row.getCell(i + 9).setCellValue(conTypeList.get(i).getDictname());
        }

        for(CatalogTree catalog : catalogTrees)
            writeCatalogTreeToExcel(sheet, 0, catalog);

        hssfWorkbook.write(fileOutputStream);

        Map<String, Object> result = new HashMap<>();
        result.put("downloadUrl", getRootURL() + "/excelFiles/" + fileName);
        return new Response(result);
    }

    @Override
    public Response getGoodStatus(@RequestBody Navigation param, @RequestHeader(value = "token") String token) {
        List<CatalogTree> catalogTrees = getGoodsData(param);

        return new Response<>(catalogTrees);
    }

    @Override
    @RequestMapping(value = "/getCatalogTree", method = RequestMethod.POST)
    public Response<CatalogTree> getCatalogTree(@RequestBody Navigation param)
    {
        Response<CatalogTree> catalogTree = catalogMicroApi.getCatalogTree(param);
        return catalogTree;
    }

    @Override
    public Response<CatalogTree> getCatalogList(@RequestBody QueryTree queryTree) {
        String parentcatalogid = "";
        try{
            parentcatalogid = queryTree.getQueryCondition("parentcatalogid").getFieldValues()[0].toString();
        }
        catch (Exception e) { return new Response<>(IErrorMessageConstants.ERR_PARAMETER_INVALID, IErrorMessageConstants.ERR_MSG_PARAMETER_INVALID); }

        Map<String, Object> param = new HashMap<>();
        param.put("parentcatalogid", parentcatalogid);
        param.put("start", queryTree.getPagination().getStart());
        param.put("numPerPage", queryTree.getPagination().getNumPerPage());

        Response<Catalog> resp = catalogMicroApi.getCatalogIds(param);
        List<Catalog> catalogs = resp.getPageInfo().getList();
        Collections.reverse(catalogs);

        List<CatalogTree> result = new ArrayList<>();

        List<DictItem> typeList;
        List<String> typeNames = new ArrayList<>();
        List<String> typeValues = new ArrayList<>();

        QueryTree dictQuery = new QueryTree();
        dictQuery.addCondition(new QueryCondition("dicttypeid", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, "CONTENT_TYPE"));
        dictQuery.addCondition(new QueryCondition("lang", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, IBaseMicroConstants.zh_CN));

        typeList = dictItemMicroApi.getList(dictQuery).getPageInfo().getList();

        if (typeList != null)
        {
            for(DictItem type : typeList)
            {
                typeNames.add(type.getDictname());
                typeValues.add(type.getDictvalue());
            }
        }

        Book book = new Book();
        if (!CollectionUtils.isEmpty(catalogs)){
            QueryTree bookQuery = new QueryTree();
            bookQuery.addCondition(new QueryCondition("bookid", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, catalogs.get(0).getBookid()));
            List<Book> books = bookMicroApi.getList(bookQuery).getPageInfo().getList();
            book = books.get(0);
        }


        for (Catalog cat : catalogs)
        {
            CatalogTree catalogTree = new CatalogTree();

            catalogTree.setLabel(cat.getCatalogname());
            catalogTree.setId(cat.getCatalogid());
            catalogTree.setParentcatalogid(parentcatalogid);

            catalogTree.setChildcount(cat.getChildcount());

            catalogTree.setTypeNames(new ArrayList<>(typeNames));
            catalogTree.setTypeCnts(getGoodsCount("", "", "", "", "", catalogTree.getId(), typeValues));
            catalogTree.getTypeNames().add("total");

            List<String> path = new ArrayList<>();
            path.add(book.getSchoolsection());
            path.add(book.getSubject());
            path.add(book.getGrade());
            path.add(book.getEditiontype());
            path.add(book.getBookmodel());

            List<String> catPath = new ArrayList<>();
            String catId = cat.getCatalogid();
            while(true){
                Catalog pathItem = catalogMicroApi.get(catId).getResponseEntity();
                if (pathItem == null)
                    break;
                catPath.add(pathItem.getCatalogname());
                catId = pathItem.getParentcatalogid();
            }
            for(String pathItem : catPath){
                path.add(5, pathItem);
            }

            JSONObject extInfo = new JSONObject();
            extInfo.put("path", path);
            catalogTree.setExtInfo(extInfo);

            List<String> ids = new ArrayList<>();
            ids.add(cat.getCatalogid());
            catalogTree.setCatalogids(ids);

            result.add(catalogTree);
        }

        Response<CatalogTree> finalResult = new Response<>(result);
        finalResult.getPageInfo().setTotal(resp.getPageInfo().getTotal());

        return finalResult;
    }

    @Override
    public Response<CatalogTree> getCatalogHeaderByLevel(@RequestBody Navigation param) {
        Response<CatalogTree> resp = catalogMicroApi.getCatalogHeaderByLevel(param);

        return resp;
    }

    public JSONObject makeGroupByQuery(int section, int subject, int grade, int edition, int model, int istotal){
        JSONArray source = new JSONArray();

        int i, len;
        List<String> items = new ArrayList<>();
        if (section != 0) items.add("schoolsectionid");
        if (subject != 0) items.add("subjectid");
        if (grade != 0) items.add("gradeid");
        if (edition != 0) items.add("editiontypeid");
        if (model != 0) items.add("bookmodelid");
        if (istotal == 0) items.add("contenttype");

        len = items.size();
        for(i = 0; i < len; i++){
            JSONObject terms = new JSONObject();
            terms.put("field", items.get(i) + ".keyword");
            terms.put("missing_bucket", false);
//            terms.put("order", "asc");

            JSONObject identifier = new JSONObject();
            identifier.put("terms", terms);

            JSONObject item = new JSONObject();
            item.put(Integer.toString(i), identifier);

            source.add(item);
        }

        JSONObject composite = new JSONObject();
        composite.put("size", 100000);
        composite.put("sources", source);

        JSONObject groupby = new JSONObject();
        groupby.put("composite", composite);

        JSONObject aggregations = new JSONObject();
        aggregations.put("groupby", groupby);

        JSONObject query = new JSONObject();
        query.put("size", 0);
        query.put("_source", false);
        query.put("stored_fields", "_none_");
        query.put("aggregations", aggregations);

        JSONArray conditions = new JSONArray();

/*        if (!StringUtils.isEmpty(sectionid))
            conditions.add(makeMatchConditions("schoolsectionid", sectionid));

        if (!StringUtils.isEmpty(subjectid))
            conditions.add(makeMatchConditions("subjectid", subjectid));

        if (!StringUtils.isEmpty(gradeid))
            conditions.add(makeMatchConditions("gradeid", gradeid));

        if (!StringUtils.isEmpty(editionid))
            conditions.add(makeMatchConditions("editiontypeid", editionid));

        if (!StringUtils.isEmpty(volumeid))
            conditions.add(makeMatchConditions("bookmodelid", volumeid));*/

        JSONObject contentQuery = new JSONObject();

        return query;
    }

    private Map<String, Long> getResourceCountByCatalog(List<String> catalogids) {
        Map<String, Long> result = new HashMap<>();
        JSONObject query = new JSONObject();
        query.put("query", "select chapterid, resourcetype, count(resourceno) as count from res_con_catalog where chapterid like '"
                + StringUtils.join(catalogids, " ") +"' group by chapterid, resourcetype");

        HttpPost httpPost = new HttpPost(getSearchRootUrl() + "/_xpack/sql?format=json");
        HttpClient httpClient = new DefaultHttpClient();
        String retVal = "";
        int i, len;

        try {
            StringEntity entity = new StringEntity(query.toJSONString(), ContentType.APPLICATION_JSON);
            httpPost.setEntity(entity);
            httpPost.setHeader("Content-Type", "application/json");
            httpPost.setHeader("Accept", "application/json");
            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            if (httpEntity != null)
                retVal = EntityUtils.toString(httpEntity, HTTP.UTF_8);

            JSONObject totalObj = JSONObject.parseObject(retVal);
            JSONArray rowData = totalObj.getJSONArray("rows");

            len = rowData.size();

            for (i = 0; i < len; i++)
            {
                JSONArray item = rowData.getJSONArray(i);
                String key = item.getString(0) + "_" + item.getString(1);
                Long value = item.getLong(2);
                result.put(key, value);
            }
        } catch (Exception e) {}

        return result;
    }

    public Map<String, Long> getGoodsByType(int section, int subject, int grade, int edition, int model, int istotal){
        Map<String, Long> result = new HashMap<>();
        JSONObject query = makeGroupByQuery(section, subject, grade, edition, model, istotal);

        int i, len;

        HttpPost httpPost = new HttpPost(getSearchRootUrl() + "/" + IResourceMicroConstants.INDEX_CONTENT + "/" + IResourceMicroConstants.TYPE_CONTENT + "/_search");
        HttpClient httpClient = new DefaultHttpClient();

        String retVal = "";

        try {
            StringEntity entity = new StringEntity(query.toJSONString(), ContentType.APPLICATION_JSON);
            httpPost.setEntity(entity);
            httpPost.setHeader("Content-Type", "application/json");
            httpPost.setHeader("Accept", "application/json");
            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            if (httpEntity != null)
                retVal = EntityUtils.toString(httpEntity, HTTP.UTF_8);

            JSONObject totalObj = JSONObject.parseObject(retVal);
            JSONObject aggregations = totalObj.getJSONObject("aggregations");
            JSONObject groupby = aggregations.getJSONObject("groupby");
            JSONArray items = groupby.getJSONArray("buckets");
            if (items == null)
                items = new JSONArray();
            len = items.size();

            for (i = 0; i < len; i++)
            {
                JSONObject item = items.getJSONObject(i);
                JSONObject keys = item.getJSONObject("key");
                
                Long rescnt = item.getLongValue("doc_count");
                Set<Map.Entry<String, Object>> entrys = keys.entrySet();
                if (CollectionUtils.isNotEmpty(entrys)) {
                    StringBuilder builder = new StringBuilder();
                    for (Map.Entry<String, Object> entry : entrys) {
                        builder.append(entry.getValue()+"_");
                    }

                    result.put(builder.toString(), rescnt);
                }
            }
        } catch (Exception e) {}

        return result;
    }

    @Override
    public Response<CatalogTree> getCatalogHeader(@RequestBody Navigation param) {
        Response<CatalogTree> catalogHeader = catalogMicroApi.getCatalogHeader(param);

        Integer level = param.getLevel();

        if (level != null && level == 1) {
            List<CatalogTree> sections = catalogHeader.getPageInfo().getList();

            List<DictItem> typeList;
            List<String> typeNames = new ArrayList<>();
            List<String> typeValues = new ArrayList<>();

            QueryTree dictQuery = new QueryTree();
            dictQuery.addCondition(new QueryCondition("dicttypeid", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, "CONTENT_TYPE"));
            dictQuery.addCondition(new QueryCondition("lang", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, IBaseMicroConstants.zh_CN));

            typeList = dictItemMicroApi.getList(dictQuery).getPageInfo().getList();

            if (typeList != null) {
                for (DictItem type : typeList) {
                    typeNames.add(type.getDictname());
                    typeValues.add(type.getDictvalue());
                }
            }

            Map<String, Long> secCount = getGoodsByType(1, 0, 0, 0, 0, 0);
            Map<String, Long> subCount = getGoodsByType(1, 1, 0, 0, 0, 0);
            Map<String, Long> graCount = getGoodsByType(1, 1, 1, 0, 0, 0);
            Map<String, Long> ediCount = getGoodsByType(1, 1, 1, 1, 0, 0);
            Map<String, Long> volCount = getGoodsByType(1, 1, 1, 1, 1, 0);

            if (sections != null) {
                for (CatalogTree section : sections) {
                    List<CatalogTree> subjects = section.getChildren();
                    if (subjects == null) subjects = new ArrayList<>();

                    for (CatalogTree subject : subjects) {
                        List<CatalogTree> grades = subject.getChildren();
                        if (grades == null) grades = new ArrayList<>();

                        for (CatalogTree grade : grades) {
                            List<CatalogTree> editions = grade.getChildren();
                            if (editions == null) editions = new ArrayList<>();

                            for (CatalogTree edition : editions) {
                                List<CatalogTree> volumes = edition.getChildren();
                                if (volumes == null) volumes = new ArrayList<>();

                                for (CatalogTree volume : volumes) {
                                    List<CatalogTree> ancestors = volume.getChildren();
                                    if (ancestors == null) ancestors = new ArrayList<>();

                                    List<String> cids = new ArrayList<>();
                                    for(CatalogTree ancestor : ancestors)
                                            cids.add(ancestor.getId());

                                    Map<String, Long> cidCount = getResourceCountByCatalog(cids);

                                    for (CatalogTree ancestor : ancestors) {
                                        List<Long> typeCnts = new ArrayList<>();
                                        String id = ancestor.getId();
                                        long totalsum = 0;
                                        for(String typeValue : typeValues){
                                            Long sum = cidCount.get(id + "_" + typeValue);
                                            if (sum == null)
                                                sum = Long.valueOf(0);
                                            typeCnts.add(sum);
                                            totalsum += sum;
                                        }
                                        typeCnts.add(0, totalsum);

                                        ancestor.setTypeCnts(typeCnts);
                                        ancestor.setTypeNames(new ArrayList<>(typeNames));
                                        ancestor.getTypeNames().add(0, "合计");
                                    }

                                    volume.setTypeNames(new ArrayList<>(typeNames));

                                    List<Long> typeCnts = new ArrayList<>();
                                    long totalsum = 0;
                                    for(String typeValue : typeValues){
                                        String key = section.getId() + "_" + subject.getId() + "_" + grade.getId() + "_" + edition.getId() + "_" + volume.getId() + "_" + typeValue + "_";
                                        Long sum = volCount.get(key);
                                        if (sum == null)
                                            sum = Long.valueOf(0);
                                        typeCnts.add(sum);
                                        totalsum += sum;
                                    }
                                    typeCnts.add(0, totalsum);
                                    volume.setTypeCnts(typeCnts);

//                                    volume.setTypeCnts(mergeGoodsCount(ancestors, typeValues));
                                    volume.getTypeNames().add(0, "合计");
                                }

                                edition.setTypeNames(new ArrayList<>(typeNames));

                                List<Long> typeCnts = new ArrayList<>();
                                long totalsum = 0;
                                for(String typeValue : typeValues){
                                    String key = section.getId() + "_" + subject.getId() + "_" + grade.getId() + "_" + edition.getId() + "_" + typeValue + "_";
                                    Long sum = ediCount.get(key);
                                    if (sum == null)
                                        sum = Long.valueOf(0);
                                    typeCnts.add(sum);
                                    totalsum += sum;
                                }
                                typeCnts.add(0, totalsum);
                                edition.setTypeCnts(typeCnts);

//                                edition.setTypeCnts(mergeGoodsCount(volumes, typeValues));
                                edition.getTypeNames().add(0, "合计");
                            }

                            grade.setTypeNames(new ArrayList<>(typeNames));

                            List<Long> typeCnts = new ArrayList<>();
                            long totalsum = 0;
                            for(String typeValue : typeValues){
                                String key = section.getId() + "_" + subject.getId() + "_" + grade.getId() + "_" + typeValue + "_";
                                Long sum = graCount.get(key);
                                if (sum == null)
                                    sum = Long.valueOf(0);
                                typeCnts.add(sum);
                                totalsum += sum;
                            }
                            typeCnts.add(0, totalsum);
                            grade.setTypeCnts(typeCnts);

//                            grade.setTypeCnts(mergeGoodsCount(editions, typeValues));
                            grade.getTypeNames().add(0, "合计");
                        }

                        subject.setTypeNames(new ArrayList<>(typeNames));
                        List<Long> typeCnts = new ArrayList<>();
                        long totalsum = 0;
                        for(String typeValue : typeValues){
                            String key = section.getId() + "_" + subject.getId() + "_" +typeValue + "_";
                            Long sum = subCount.get(key);
                            if (sum == null)
                                sum = Long.valueOf(0);
                            typeCnts.add(sum);
                            totalsum += sum;
                        }
                        typeCnts.add(0, totalsum);
                        subject.setTypeCnts(typeCnts);

//                        subject.setTypeCnts(mergeGoodsCount(grades, typeValues));
                        subject.getTypeNames().add(0, "合计");
                    }

                    section.setTypeNames(new ArrayList<>(typeNames));

                    section.setTypeNames(new ArrayList<>(typeNames));
                    List<Long> typeCnts = new ArrayList<>();
                    long totalsum = 0;
                    for(String typeValue : typeValues){
                        String key = section.getId() + "_" + typeValue + "_";
                        Long sum = secCount.get(key);
                        if (sum == null)
                            sum = Long.valueOf(0);
                        typeCnts.add(sum);
                        totalsum += sum;
                    }
                    typeCnts.add(0, totalsum);
                    section.setTypeCnts(typeCnts);

//                    section.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               setTypeCnts(mergeGoodsCount(subjects, typeValues));
                    section.getTypeNames().add(0, "合计");
                }
            }
        }
        return catalogHeader;
    }

    @Override
    @RequestMapping(value = "/getList", method = RequestMethod.POST)
    public Response<Catalog> getList(@RequestBody QueryTree queryTree)
    {
        return catalogMicroApi.getList(queryTree);
    }

    @Override
    @RequestMapping(value = "/get/{id}", method = RequestMethod.GET)
    public Response<Catalog> get(@PathVariable(value = "id") String id)
    {
        return catalogMicroApi.get(id);
    }

    @Override
    @RequestMapping(value = "/insertCatalog", method = RequestMethod.POST)
    public Response<Catalog> insertCatalog(@RequestBody Catalog entity, @RequestHeader(value = "token")String token)
    {
        if (esSyncFlag.equalsIgnoreCase(ICommonConstants.ES_SAVING_FLAG))
            return new Response<>(IErrorMessageConstants.ERR_CODE_ES_SAVING_MODIFY_DISABLE, IErrorMessageConstants.ERR_MSG_ES_SAVING_MODIFY_DISABLE);

        entity.setCreatetime(new Timestamp(System.currentTimeMillis()));
        entity.setLastmodifytime(new Timestamp(System.currentTimeMillis()));
        Response<Catalog> resp = catalogMicroApi.insertCatalog(entity);
        entity = resp.getResponseEntity();
        if (resp.getServerResult().getResultCode().equals("1")) {
            resp.getServerResult().setResultCode(IErrorMessageConstants.OPERATION_SUCCESS);
            resp.getServerResult().setResultMessage(IErrorMessageConstants.OPERATION_SUCCESS_MESSAGE);
        }
        logOptionEntity(entity.getCatalogid(), entity.getCatalogid(), ICommonConstants.OPTION_OPTIONTYPE_CATALOG_ADD, token);
        return resp;
    }

    @Override
    @RequestMapping(value = "/modifyCatalog", method = RequestMethod.POST)
    public Response<Catalog> modifyCatalog(@RequestBody Catalog entity, @RequestHeader(value = "token")String token)
    {
        if (esSyncFlag.equalsIgnoreCase(ICommonConstants.ES_SAVING_FLAG))
            return new Response<>(IErrorMessageConstants.ERR_CODE_ES_SAVING_MODIFY_DISABLE, IErrorMessageConstants.ERR_MSG_ES_SAVING_MODIFY_DISABLE);

        logOptionEntity(entity.getCatalogid(), entity.getCatalogid(), ICommonConstants.OPTION_OPTIONTYPE_CATALOG_ADD, token);
        Response<Catalog> resp = catalogMicroApi.modifyCatalog(entity);
        if (resp.getServerResult().getResultCode().equals("1")) {
            resp.getServerResult().setResultCode(IErrorMessageConstants.OPERATION_SUCCESS);
            resp.getServerResult().setResultMessage(IErrorMessageConstants.OPERATION_SUCCESS_MESSAGE);
        }
        return resp;
    }

    @Override
    @RequestMapping(value = "/moveCatalog", method = RequestMethod.POST)
    public Response<Catalog> moveCatalog(@RequestBody Map<String, Object> param, @RequestHeader(value = "token")String token)
    {
        if (esSyncFlag.equalsIgnoreCase(ICommonConstants.ES_SAVING_FLAG))
            return new Response<>(IErrorMessageConstants.ERR_CODE_ES_SAVING_MODIFY_DISABLE, IErrorMessageConstants.ERR_MSG_ES_SAVING_MODIFY_DISABLE);

        String fromcatalogid = (String) param.get("fromcatalogid");
        String tocatalogid = (String) param.get("tocatalogid");
        logOptionEntity(fromcatalogid, tocatalogid, ICommonConstants.OPTION_OPTIONTYPE_CATALOG_ADD, token);
        Response<Catalog> resp = catalogMicroApi.moveCatalog(param);
        if (resp.getServerResult().getResultCode().equals("1")){
            resp.getServerResult().setResultCode(IErrorMessageConstants.OPERATION_SUCCESS);
            resp.getServerResult().setResultMessage(IErrorMessageConstants.OPERATION_SUCCESS_MESSAGE);
        }
        return resp;
    }

    @Override
    public Response<Catalog> deleteCatalog(@PathVariable(value = "id") String id, @RequestHeader(value = "token")String token) {
        if (esSyncFlag.equalsIgnoreCase(ICommonConstants.ES_SAVING_FLAG))
            return new Response<>(IErrorMessageConstants.ERR_CODE_ES_SAVING_MODIFY_DISABLE, IErrorMessageConstants.ERR_MSG_ES_SAVING_MODIFY_DISABLE);
        logOptionEntity(id, id, ICommonConstants.OPTION_OPTIONTYPE_CATALOG_ADD, token);
        Response<Catalog> resp = catalogMicroApi.delete(id);
        if (resp.getServerResult().getResultCode().equals("1")){
            resp.getServerResult().setResultCode(IErrorMessageConstants.OPERATION_SUCCESS);
            resp.getServerResult().setResultMessage(IErrorMessageConstants.OPERATION_SUCCESS_MESSAGE);
        }
        return resp;
    }
}
