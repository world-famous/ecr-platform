package com.tianwen.springcloud.ecrapi.api;

import com.tianwen.springcloud.commonapi.base.response.Response;
import com.tianwen.springcloud.commonapi.query.QueryTree;
import com.tianwen.springcloud.microservice.base.entity.DictType;
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

public interface DictTypeRestApi
{
    @RequestMapping(value = "/getTypeList", method = RequestMethod.POST)
    public Response<DictType> getTypeList(@RequestBody QueryTree queryTree);

    @RequestMapping(value = "/insertType", method = RequestMethod.POST)
    public Response<DictType> insertType(@RequestBody DictType entity);

    @RequestMapping(value = "/modifyType", method = RequestMethod.POST)
    public Response<DictType> modifyType(@RequestBody DictType entity);

    @RequestMapping(value = "/removeType/{dicttypeid}", method = RequestMethod.GET)
    public Response<DictType> removeType(@PathVariable(value = "dicttypeid") String dicttypeid);
}
