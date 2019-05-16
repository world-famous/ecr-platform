package com.tianwen.springcloud.microservice.base.controller;

import com.tianwen.springcloud.commonapi.base.response.Response;
import com.tianwen.springcloud.commonapi.log.SystemControllerLog;
import com.tianwen.springcloud.commonapi.query.QueryTree;
import com.tianwen.springcloud.datasource.base.AbstractCRUDController;
import com.tianwen.springcloud.microservice.base.api.BookMicroApi;
import com.tianwen.springcloud.microservice.base.entity.Book;
import com.tianwen.springcloud.microservice.base.entity.Navigation;
import com.tianwen.springcloud.microservice.base.service.BookService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/book")
public class  BookController extends AbstractCRUDController<Book> implements BookMicroApi
{
    @Autowired
    private BookService bookService;

    @Override
    @ApiOperation(value = "根据ID删除用户信息", notes = "根据ID删除用户信息")
    @ApiImplicitParam(name = "id", value = "用户ID", required = true, dataType = "String", paramType = "path")
    @SystemControllerLog(description = "根据用户ID删除用户信息")
    public Response<Book> delete(@PathVariable(value = "id") String id)
    {
        Book entity = bookService.selectByKey(id);
        int ret = bookService.deleteByBookId(id);
        Response<Book> resp = new Response<>(entity);
        resp.getServerResult().setResultCode(Integer.toString(ret));
        return resp;
    }

    @Override
    @ApiOperation(value = "根据实体对象删除用户信息", notes = "根据实体对象删除用户信息")
    @ApiImplicitParam(name = "entity", value = "用户信息实体", required = true, dataType = "UserLoginInfo", paramType = "body")
    @SystemControllerLog(description = "根据实体对象删除用户信息")
    public Response<Book> deleteByEntity(@RequestBody Book entity)
    {
        int ret = bookService.deleteByBookId(entity.getBookid());
        Response<Book> resp = new Response<>(entity);
        resp.getServerResult().setResultCode(Integer.toString(ret));
        return resp;
    }
    
    @Override
    @ApiOperation(value = "用户搜索", notes = "用户搜索")
    @ApiImplicitParam(name = "queryTree", value = "搜索条件树", required = true, dataType = "QueryTree", paramType = "body")
    @SystemControllerLog(description = "用户搜索")
    public Response<Book> search(@RequestBody QueryTree queryTree)
    {
        return bookService.search(queryTree);
    }
    
    @Override
    @ApiOperation(value = "获取用户列表", notes = "获取用户列表")
    @ApiImplicitParam(name = "queryTree", value = "搜索条件树", required = true, dataType = "QueryTree", paramType = "body")
    @SystemControllerLog(description = "获取用户列表")
    public Response<Book> getList(@RequestBody QueryTree queryTree)
    {
        return bookService.search(queryTree);
    }

    @Override
    public Response<Book> getBook(@RequestBody Map<String, Object> param) {
        Book book = bookService.getBook(param);
        return new Response<>(book);
    }

    @Override
    public Response<Book> getBookByBookname(@RequestBody Map<String, Object> param) {
        Book book = bookService.getBookByBookname(param);
        return new Response<>(book);
    }

    @Override
    @ApiOperation(value = "", notes = "")
    @ApiImplicitParam(name = "queryTree", value = "搜索条件树", required = true, dataType = "QueryTree", paramType = "body")
    @SystemControllerLog(description = "")
    public Response<Book> get(@PathVariable(value = "id") String id)
    {
        Book bookInfo = bookService.selectByKey(id);

        Response<Book> resp = new Response<>(bookInfo);

        return resp;
    }

    @Override
    @ApiOperation(value = "", notes = "")
    @ApiImplicitParam(name = "queryTree", value = "搜索条件树", required = true, dataType = "QueryTree", paramType = "body")
    @SystemControllerLog(description = "")
    public Response<Book> insert(@RequestBody Book entity)
    {
        int ret = bookService.save(entity);

        Response<Book> resp = new Response<>(entity);
        resp.getServerResult().setResultCode(Integer.toString(ret));

        return resp;
    }

    @Override
    @ApiOperation(value = "", notes = "")
    @ApiImplicitParam(name = "queryTree", value = "搜索条件树", required = true, dataType = "QueryTree", paramType = "body")
    @SystemControllerLog(description = "")
    public Response<Book> update(@RequestBody Book entity)
    {
        int ret;

        if (entity.getBookid() == null)
            ret = bookService.save(entity);
        else
            ret = bookService.updateNotNull(entity);

        Response<Book> resp = new Response<>(entity);
        resp.getServerResult().setResultCode(Integer.toString(ret));

        return resp;
    }

    @Override
    public Response<Book> getByNavigation(@RequestBody Navigation navi) {
        Book entity = bookService.getByNavigation(navi);

        return new Response<>(entity);
    }
//Author : GOD 2019-2-22 Reason : BatchFileUpload
    @Override
    public Response<Book> getBookByNavigation(@RequestBody Navigation navi) {
        List<Book> entity = bookService.getBookByNavigation(navi);

        return new Response<>(entity);
    }
    //Author : GOD 2019-2-22 Reason : BatchFileUpload
    @Override
    @ApiOperation(value = "", notes = "")
    @SystemControllerLog(description = "")
    public Response<Book> deleteByBookId(@PathVariable(value = "bookid") String bookid) {
        bookService.deleteByBookId(bookid);
        return new Response<>();
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
