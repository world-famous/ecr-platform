package com.tianwen.springcloud.ecrapi.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.tianwen.springcloud.commonapi.base.response.Response;
import com.tianwen.springcloud.commonapi.constant.IStateCode;
import com.tianwen.springcloud.commonapi.query.QueryCondition;
import com.tianwen.springcloud.commonapi.query.QueryTree;
import com.tianwen.springcloud.ecrapi.api.BookRestApi;
import com.tianwen.springcloud.ecrapi.constant.IErrorMessageConstants;
import com.tianwen.springcloud.ecrapi.util.CommonUtil;
import com.tianwen.springcloud.ecrapi.util.ESearchUtil;
import com.tianwen.springcloud.ecrapi.util.FileUtil;
import com.tianwen.springcloud.ecrapi.util.ReadExcel;
import com.tianwen.springcloud.microservice.base.api.CatalogMicroApi;
import com.tianwen.springcloud.microservice.base.entity.*;
import com.tianwen.springcloud.microservice.resource.constant.IResourceMicroConstants;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Date;

@RestController
@RequestMapping(value = "/book")
public class BookRestController extends BaseRestController implements BookRestApi
{
    @Override
    @RequestMapping(value = "/setBook", method = RequestMethod.POST)
    public Response<Map<String, Object>> setBook(@RequestParam(value = "info") String param
            ,@RequestPart(value = "catalogfile") MultipartFile file, @RequestHeader(value = "token") String token)
    {
        UserLoginInfo userLoginInfo = userMicroApi.getByToken(token).getResponseEntity();
        if (userLoginInfo == null)
        {
            Response<Map<String, Object>> resp = new Response<>();
            resp.getServerResult().setResultCode(IErrorMessageConstants.ERR_TOKEN_NOT_CORRECT);
            resp.getServerResult().setResultMessage(IErrorMessageConstants.ERR_MSG_TOKEN_NOT_CORRECT);
            return resp;
        }

        String creator = userLoginInfo.getUserId();
        Timestamp currenttime = new Timestamp(System.currentTimeMillis());

        JSONObject json = JSON.parseObject(param);
        JSONObject subjectnaviinfo = json.getJSONObject("subjectnaviinfo");
        String naviid = null;
        String grade = json.getString("gradeid");
        if (subjectnaviinfo == null || StringUtils.isEmpty(grade))
            return new Response<>(IErrorMessageConstants.ERR_PARAMETER_INVALID, IErrorMessageConstants.ERR_MSG_PARAMETER_INVALID);

        String schoolsection = subjectnaviinfo.getString("schoolsectionid");
        String subject = subjectnaviinfo.getString("subjectid");

        String editiontype = subjectnaviinfo.getString("editiontypeid");
        String bookmodel = subjectnaviinfo.getString("bookmodelid");

        if (StringUtils.isEmpty(schoolsection) || StringUtils.isEmpty(subject) || StringUtils.isEmpty(editiontype) || StringUtils.isEmpty(bookmodel))
            return new Response<>(IErrorMessageConstants.ERR_PARAMETER_INVALID, IErrorMessageConstants.ERR_MSG_PARAMETER_INVALID);

        Navigation example = new Navigation();
        example.setSchoolsectionid(schoolsection);
        example.setSubjectid(subject);
        example.setBookmodelid(bookmodel);
        example.setEditiontypeid(editiontype);
        List<Navigation> navi = navigationMicroApi.searchByEntity(example).getPageInfo().getList();

        if (CollectionUtils.isEmpty(navi)) {
            example.setStatus("1");
            example = navigationMicroApi.add(example).getResponseEntity();
            naviid = example.getNaviid();
        }
        else
            naviid = navi.get(0).getNaviid();

        JSONObject object = json.getJSONObject("bookinfo");

        if (object != null && naviid != null) {
            Map<String, Object> bookParam = new HashMap<>();
            bookParam.put("naviid", naviid);
            bookParam.put("gradeid", grade);

            // If bookid is set(which means 'updating'...) delete existing book
            if(object.get("bookid") != null) {
                Book existBook = bookMicroApi.get(object.get("bookid").toString()).getResponseEntity();
                if (existBook != null) {
                    deleteBook(existBook.getBookid(), token);
                }
            }

            Book book = new Book();
            if (object.get("bookid") != null) {
                String bookid = object.get("bookid").toString();
                if (!bookid.isEmpty())
                {
                    Book curBook = bookMicroApi.get(bookid).getResponseEntity();
                    if (curBook != null)
                        deleteBook(curBook.getBookid(), token);
                    book.setBookid(bookid);
                }
            }
            else
                book.setBookid(null);

            book.setNaviid(naviid);
            book.setGradeid(grade);

            if (object.get("bookname") != null) {
                String bookname = object.get("bookname").toString();
                book.setBookname(bookname);
            }

            if (object.get("bookscore") != null) {
                String bookscore = object.get("bookscore").toString();
                book.setBookscore(Integer.parseInt(bookscore));
            }

            if (object.get("thumnailpath") != null) {
                String thumbnail = object.get("thumnailpath").toString();
                book.setThumbnail(thumbnail);
            }

            if (object.get("filepath") != null) {
                String filepath = object.get("filepath").toString();
//                book.setFilepath(filepath);
            }

            if (object.get("bookdesc") != null) {
                String description = object.get("bookdesc").toString();
                book.setDescription(description);
            }

            book.setCreator(creator);
            book.setType("1");
            book.setCreatetime(currenttime);
            book.setLastmodifytime(currenttime);
            book = bookMicroApi.insert(book).getResponseEntity();

            if (book.getBookid() != null && file != null) {
                String orgFileName = file.getOriginalFilename();
                String fileName = FileUtil.getUniqueFileName(location, FileUtil.getFileExtension(orgFileName));
                String fileFullName = location + "/" + fileName;
                File resourceInfoFile = new File(fileFullName);
                try {
                    file.transferTo(resourceInfoFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //save catalogs of book
                Catalog catalog = new Catalog();
                try{
                    ReadExcel excel = new ReadExcel();
                    List<List<String>> data = excel.read(resourceInfoFile.getPath());

                    String sequence, catalogname;
                    ArrayList<String> parentList = new ArrayList<String>(11);
                    int catIdCnt = data.size(), row, i, dep;

                    for (i = 0; i < 10; i++)
                        parentList.add("");

                    parentList.set(1, "0");
                    dep = 1;

                    for (row = 1; row < catIdCnt; row++)
                    {
                        // Check if dep is bigger than actual row limit
                        if (dep > (data.get(row).size() - 1)) {
                            dep = data.get(row).size() - 1;
                        }
                        catalogname = data.get(row).get(dep);
                        if (!catalogname.isEmpty());
                        else
                        {
                            try
                            {
                                catalogname = data.get(row).get(dep + 1);
                                if (!catalogname.isEmpty())
                                {
                                    dep += 1;
                                    parentList.set(dep, catalog.getCatalogid());
                                }
                                else
                                {
                                    catalogname = data.get(row).get(dep - 1);
                                    dep -= 1;
                                }
                            }
                            catch (Exception e)
                            {
                                catalogname = data.get(row).get(dep - 1);
                                dep -= 1;
                            }
                        }

                        Catalog catalogData = new Catalog();
                        catalogData.setParentcatalogid(parentList.get(dep));
                        catalogData.setDescription("");
                        catalogData.setCatalogname(catalogname);
                        catalogData.setCreatetime(new Timestamp(System.currentTimeMillis()));
                        catalogData.setLastmodifytime(new Timestamp(System.currentTimeMillis()));
                        catalogData.setBookid(book.getBookid());
                        catalog = catalogMicroApi.insertCatalog(catalogData).getResponseEntity();
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }

            Map<String, Object> result = new HashMap<>();
            result.put("bookinfo", book);
            return new Response<>(result);
        }

        return new Response<>();
    }

    /**
     * @author: gennie
     * @date: 1/28/2019
     * @param param
     * @param token
     * @return
     */
    @Override
    @RequestMapping(value = "/update_book_without_catalog", method = RequestMethod.POST)
    public Response<Map<String, Object>> updateBookWithoutCatalog(@RequestParam(value = "info") String param
            , @RequestHeader(value = "token") String token)
    {
        UserLoginInfo userLoginInfo = userMicroApi.getByToken(token).getResponseEntity();
        if (userLoginInfo == null)
        {
            Response<Map<String, Object>> resp = new Response<>();
            resp.getServerResult().setResultCode(IErrorMessageConstants.ERR_TOKEN_NOT_CORRECT);
            resp.getServerResult().setResultMessage(IErrorMessageConstants.ERR_MSG_NOT_LOGIN);
            return resp;
        }

        String creator = userLoginInfo.getUserId();
        Timestamp currenttime = new Timestamp(System.currentTimeMillis());

        JSONObject json = JSON.parseObject(param);
        JSONObject subjectnaviinfo = json.getJSONObject("subjectnaviinfo");
        String naviid = null;
        String grade = json.getString("gradeid");
        if (subjectnaviinfo == null || StringUtils.isEmpty(grade))
            return new Response<>(IErrorMessageConstants.ERR_PARAMETER_INVALID, IErrorMessageConstants.ERR_MSG_PARAMETER_INVALID);

        String schoolsection = subjectnaviinfo.getString("schoolsectionid");
        String subject = subjectnaviinfo.getString("subjectid");

        String editiontype = subjectnaviinfo.getString("editiontypeid");
        String bookmodel = subjectnaviinfo.getString("bookmodelid");

        if (StringUtils.isEmpty(schoolsection) || StringUtils.isEmpty(subject) || StringUtils.isEmpty(editiontype) || StringUtils.isEmpty(bookmodel))
            return new Response<>(IErrorMessageConstants.ERR_PARAMETER_INVALID, IErrorMessageConstants.ERR_MSG_PARAMETER_INVALID);

        Navigation example = new Navigation();
        example.setSchoolsectionid(schoolsection);
        example.setSubjectid(subject);
        example.setBookmodelid(bookmodel);
        example.setEditiontypeid(editiontype);
        List<Navigation> navi = navigationMicroApi.searchByEntity(example).getPageInfo().getList();

        if (CollectionUtils.isEmpty(navi)) {
            example.setStatus("1");
            example = navigationMicroApi.add(example).getResponseEntity();
            naviid = example.getNaviid();
        }
        else
            naviid = navi.get(0).getNaviid();

        JSONObject object = json.getJSONObject("bookinfo");

        if (object != null && naviid != null) {
            Map<String, Object> bookParam = new HashMap<>();

            /*bookParam.put("naviid", naviid);
            bookParam.put("gradeid", grade);*/
            bookParam.put("bookid", object.get("bookid"));
            Book existBook = bookMicroApi.getBook(bookParam).getResponseEntity();
            Book book;
            if (existBook != null) {
                book = existBook;
            } else {
                book = new Book();
            }

            /*
            if (object.get("bookid") != null) {
                String bookid = object.get("bookid").toString();
                if (!bookid.isEmpty())
                {
                    Book curBook = bookMicroApi.get(bookid).getResponseEntity();
                    if (curBook != null)
                        deleteBook(curBook.getBookid(), token);
                    book.setBookid(bookid);
                }
            }
            else
                book.setBookid(null);
                */

            book.setNaviid(naviid);
            book.setGradeid(grade);

            if (object.get("bookname") != null) {
                String bookname = object.get("bookname").toString();
                book.setBookname(bookname);
            }

            if (object.get("bookscore") != null) {
                String bookscore = object.get("bookscore").toString();
                book.setBookscore(Integer.parseInt(bookscore));
            }

            if (object.get("thumnailpath") != null) {
                String thumbnail = object.get("thumnailpath").toString();
                book.setThumbnail(thumbnail);
            }

            if (object.get("bookdesc") != null) {
                String description = object.get("bookdesc").toString();
                book.setDescription(description);
            }

            book.setCreator(creator);
            book.setType("1");

            // Remove miliseconds in timestamp because there's error (convertin date to timestamp) in maven repo
            book.setLastmodifytime(
                new Timestamp(1000 * (System.currentTimeMillis() / 1000))
            );

            book = bookMicroApi.update(book).getResponseEntity();

            Map<String, Object> result = new HashMap<>();
            result.put("bookinfo", book);
            return new Response<>(result);
        }
        return new Response<>();
    }

    @Override
    @RequestMapping(value = "/deleteBook/{bookid}", method = RequestMethod.GET)
    public Response<Book> deleteBook(@PathVariable(value = "bookid") String bookid, @RequestHeader(value = "token") String token)
    {
        Book book = bookMicroApi.get(bookid).getResponseEntity();
        Response<Book> resp = bookMicroApi.delete(bookid);
        catalogMicroApi.deleteByBook(bookid);

        if (resp.getServerResult().getResultCode().equals("1"))
            resp.getServerResult().setResultCode(IErrorMessageConstants.OPERATION_SUCCESS);

        return resp;
    }

    @Override
    @RequestMapping(value = "/getBookInfoList", method = RequestMethod.POST)
    public Response<Book> getBookInfoList(@RequestBody QueryTree queryTree, @RequestHeader(value = "token") String token) {
        return bookMicroApi.getList(queryTree);
    }

    @Override
    @RequestMapping(value = "/getBookInfo/{bookid}", method = RequestMethod.GET)
    public Response<Book> getBookInfo(@PathVariable(value = "bookid") String bookid, @RequestHeader(value = "token") String token) {
        QueryTree queryTree = new QueryTree();
        queryTree.addCondition(new QueryCondition("bookid", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, bookid));
        List<Book> books = bookMicroApi.getList(queryTree).getPageInfo().getList();
        if (CollectionUtils.isEmpty(books)) {
            return new Response<>();
        }
        return new Response<>(books.get(0));
    }

    @Override
    public Response getGoodsStatisticsByBookid(@PathVariable(value = "bookid") String bookid, @RequestHeader(value = "token") String token) {
        Map<String, Object> result = new HashMap<>();

        List<Map<String, Object>> countList = new ArrayList<>();

        QueryTree conTypeTree = new QueryTree();
        conTypeTree.addCondition(new QueryCondition("dicttypeid", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, "CONTENT_TYPE"));
        List<DictItem> typeList = dictItemMicroApi.getList(conTypeTree).getPageInfo().getList();
        int totalCount = 0;

        Map<String, Integer> countData = new HashMap<>();
        if (!CollectionUtils.isEmpty(typeList)) {
            JSONObject query = new JSONObject();
            query.put("query", "select resourcetype, count(resourceno) from res_con_catalog where bookid = '" + bookid + "' group by resourcetype");

            HttpPost httpPost = new HttpPost(getSearchRootUrl() + "/_xpack/sql?format=json");
            HttpClient httpClient = new DefaultHttpClient();

            try{
                StringEntity entity = new StringEntity(query.toJSONString(), ContentType.APPLICATION_JSON);
                httpPost.setEntity(entity);
                httpPost.setHeader("Content-Type", "application/json");
                httpPost.setHeader("Accept", "application/json");
                HttpResponse httpResponse = httpClient.execute(httpPost);
                HttpEntity httpEntity = httpResponse.getEntity();

                String retVal = EntityUtils.toString(httpEntity, HTTP.UTF_8);
                JSONObject totalObj = JSONObject.parseObject(retVal);
                JSONArray items = totalObj.getJSONArray("rows");
                if (items == null)
                    items = new JSONArray();
                int i, len = items.size();

                for (i = 0; i < len; i++)
                {
                    JSONArray rowData = items.getJSONArray(i);
                    countData.put(rowData.getString(0), rowData.getInteger(1));
                }
            }
            catch (Exception e){
                return ESearchUtil.notFoundServerError();
            }
        }
        for(DictItem type : typeList){
            String value = type.getDictvalue();
            String name = type.getDictname();
            Integer count = countData.get(value);
            if (count == null)
                count = Integer.valueOf(0);

            Map<String, Object> resCount = new HashMap<>();
            resCount.put("name", name);
            resCount.put("value", value);
            resCount.put("count", count);
            totalCount += count;
            countList.add(resCount);
        }
        result.put("countInfo", countList);
        result.put("totalCount", totalCount);

        return new Response<>(result);
    }

    @Override
    public Response exportToExcel(@PathVariable(value = "bookid") String bookid, @RequestHeader(value = "token") String token) throws Exception {
        Map<String, Object> result = new HashMap<>();

        QueryTree conTypeTree = new QueryTree();
        conTypeTree.addCondition(new QueryCondition("dicttypeid", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, "CONTENT_TYPE"));
        List<DictItem> typeList = dictItemMicroApi.getList(conTypeTree).getPageInfo().getList();
        int totalCount = 0;

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
        if (!chartFile.createNewFile()) return null;

        FileOutputStream fileOutputStream = new FileOutputStream(chartFile);
        HSSFWorkbook hssfWorkbook = new HSSFWorkbook();
        HSSFSheet hssfSheet = hssfWorkbook.createSheet();
        int row = 0;

        HSSFRow titleRow = hssfSheet.createRow(row);
        titleRow.createCell(0); titleRow.getCell(0).setCellValue("资源类型");
        titleRow.createCell(1); titleRow.getCell(1).setCellValue("资源个数");
        row++;

        if (!CollectionUtils.isEmpty(typeList)) {
            for (DictItem type : typeList) {
                QueryTree queryTree = new QueryTree();
                queryTree.addCondition(new QueryCondition("bookid", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, bookid));
                queryTree.addCondition(new QueryCondition("contenttype", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, type.getDictvalue()));
                queryTree.addCondition(new QueryCondition("isgoods", QueryCondition.Prepender.AND, QueryCondition.Operator.EQUAL, "1"));
                int goodsCount = resourceMicroApi.getCount(queryTree).getResponseEntity().intValue();

                HSSFRow itemrow = hssfSheet.createRow(row);
                itemrow.createCell(0); itemrow.getCell(0).setCellValue(type.getDictname());
                itemrow.createCell(1); itemrow.getCell(1).setCellValue(goodsCount);
                totalCount += goodsCount;
                row++;
            }
        }

        HSSFRow totalRow = hssfSheet.createRow(row);
        totalRow.createCell(0); totalRow.getCell(0).setCellValue("共计");
        totalRow.createCell(1); totalRow.getCell(1).setCellValue(totalCount);

        hssfWorkbook.write(fileOutputStream);

        result.put("downloadUrl", getRootURL() + "/excelFiles/" + fileName);
        return new Response<>(result);
    }
}
