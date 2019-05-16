package com.tianwen.springcloud.microservice.base.api;

import com.tianwen.springcloud.commonapi.base.ICRUDMicroApi;
import com.tianwen.springcloud.commonapi.base.response.Response;
import com.tianwen.springcloud.commonapi.query.QueryTree;
import com.tianwen.springcloud.microservice.base.entity.Area;
import com.tianwen.springcloud.microservice.base.entity.AreaTree;
import com.tianwen.springcloud.microservice.base.entity.Synonym;
import com.tianwen.springcloud.microservice.base.entity.UserLoginInfo;
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
@FeignClient(value = "base-service", url = "http://localhost:2226/base-service/synonym")
public interface SynonymMicroApi extends ICRUDMicroApi<Synonym>
{
    @RequestMapping(value = "/getList", method = RequestMethod.POST)
    public Response<Synonym> getList(@RequestBody QueryTree queryTree);

    @RequestMapping(value = "/insert", method = RequestMethod.POST)
    public Response<Synonym> insert(@RequestBody Synonym entity);

    @RequestMapping(value = "/remove/{synid}", method = RequestMethod.GET)
    public Response<Synonym> remove(@PathVariable(value = "synid") String synid);

    @RequestMapping(value = "/getByHint/{hint}", method = RequestMethod.GET)
    public Response<String> getByHint(@PathVariable(value = "hint") String hint);

    @RequestMapping(value = "/getByOriginal/{original}", method = RequestMethod.GET)
    public Response<Synonym> getByOriginal(@PathVariable(value = "original") String original);

    @RequestMapping(value = "/getBySynonym/{synonym}", method = RequestMethod.GET)
    public Response<Synonym> getBySynonym(@PathVariable(value = "synonym") String synonym);

    @RequestMapping(value = "/getBySearchkey/{searchkey}", method = RequestMethod.GET)
    public Response<Synonym> getBySearchkey(@PathVariable(value = "searchkey") String searchkey);
}
