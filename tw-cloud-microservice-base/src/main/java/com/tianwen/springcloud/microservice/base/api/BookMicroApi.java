package com.tianwen.springcloud.microservice.base.api;

import com.tianwen.springcloud.commonapi.base.ICRUDMicroApi;
import com.tianwen.springcloud.commonapi.base.response.Response;
import com.tianwen.springcloud.commonapi.query.QueryTree;
import com.tianwen.springcloud.microservice.base.entity.Book;
import com.tianwen.springcloud.microservice.base.entity.Navigation;
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
@FeignClient(value = "base-service", url = "http://localhost:2226/base-service/book")
public interface BookMicroApi extends ICRUDMicroApi<Book>
{
    @RequestMapping(value = "/getList", method = RequestMethod.POST)
    public Response<Book> getList(@RequestBody QueryTree queryTree);

    @RequestMapping(value = "/getBook", method = RequestMethod.POST)
    public Response<Book> getBook(@RequestBody Map<String, Object> param);

    @RequestMapping(value = "/get/{id}", method = RequestMethod.GET)
    public Response<Book> get(@PathVariable(value = "id") String id);

    @RequestMapping(value = "/insert", method = RequestMethod.POST)
    public Response<Book> insert(@RequestBody Book entity);

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public Response<Book> update(@RequestBody Book entity);

    @RequestMapping(value = "/getByNavigation", method = RequestMethod.POST)
    public Response<Book> getByNavigation(@RequestBody Navigation navi);

    //Author : GOD 2019-2-22 Reason : BatchFileUpload
    @RequestMapping(value = "/getBookByNavigation", method = RequestMethod.POST)
    public Response<Book> getBookByNavigation(@RequestBody Navigation navi);
    //Author : GOD 2019-2-22 Reason : BatchFileUpload

    @RequestMapping(value = "/deleteByBookId/{bookid}", method = RequestMethod.POST)
    public Response<Book> deleteByBookId(@PathVariable(value = "bookid") String bookid);

    @RequestMapping(value= "/getBookByBookname", method = RequestMethod.POST)
    public Response<Book> getBookByBookname(@RequestBody Map<String, Object> param);
}
