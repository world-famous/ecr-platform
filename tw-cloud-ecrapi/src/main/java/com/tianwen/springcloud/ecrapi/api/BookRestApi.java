package com.tianwen.springcloud.ecrapi.api;

import com.tianwen.springcloud.commonapi.base.response.Response;
import com.tianwen.springcloud.commonapi.query.QueryTree;
import com.tianwen.springcloud.microservice.base.entity.Book;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * ECRApi对外接口
 * 
 * @author wangbin
 * @version [版本号, 2017年5月11日]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */

public interface BookRestApi
{
    @RequestMapping(value = "/setBook", method = RequestMethod.POST)
    public Response<Map<String, Object>> setBook(@RequestParam(value = "info") String param
            , @RequestPart(value = "catalogfile") MultipartFile file, @RequestHeader(value = "token") String token);

    /**
     * @author gennie
     * @date: 1/28/2019
     */
    @RequestMapping(value = "/update_book_without_catalog", method = RequestMethod.POST)
    public Response<Map<String, Object>> updateBookWithoutCatalog(@RequestParam(value = "info") String param
            ,@RequestHeader(value = "token") String token);

    @RequestMapping(value = "/deleteBook/{bookid}", method = RequestMethod.GET)
    public Response<Book> deleteBook(@PathVariable(value = "bookid") String bookid, @RequestHeader(value = "token") String token);

    @RequestMapping(value = "/getBookInfoList", method = RequestMethod.POST)
    public Response<Book> getBookInfoList(@RequestBody QueryTree queryTree, @RequestHeader(value = "token") String token);

    @RequestMapping(value = "/getBookInfo/{bookid}", method = RequestMethod.GET)
    public Response<Book> getBookInfo(@PathVariable(value = "bookid") String bookid, @RequestHeader(value = "token") String token);

    @RequestMapping(value = "/getGoodsStatisticsByBookid/{bookid}", method = RequestMethod.GET)
    public Response getGoodsStatisticsByBookid(@PathVariable(value = "bookid") String bookid, @RequestHeader(value = "token") String token);

    @RequestMapping(value = "/exportToExcel/{bookid}", method = RequestMethod.GET)
    public Response exportToExcel(@PathVariable(value = "bookid") String bookid, @RequestHeader(value = "token") String token) throws Exception;
}
