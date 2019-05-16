package com.tianwen.springcloud.ecrapi.api;

import com.tianwen.springcloud.commonapi.base.response.Response;
import com.tianwen.springcloud.commonapi.query.QueryTree;
import com.tianwen.springcloud.microservice.base.entity.DictItem;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * ECRApi对外接口
 * 
 * @author wangbin
 * @version [版本号, 2017年5月11日]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */

public interface DictItemRestApi
{
    @RequestMapping(value = "/getItemList", method = RequestMethod.POST)
    public Response<DictItem> getItemList(@RequestBody QueryTree queryTree);

    @RequestMapping(value = "/insertItem", method = RequestMethod.POST)
    public Response<DictItem> insertItem(@RequestBody DictItem entity);

    @RequestMapping(value = "/modifyItem", method = RequestMethod.POST)
    public Response<DictItem> modifyItem(@RequestBody DictItem entity);

    @RequestMapping(value = "/removeItem/{dictid}", method = RequestMethod.GET)
    public Response<DictItem> removeItem(@PathVariable(value = "dictid") String dictid);
}
