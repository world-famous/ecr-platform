package com.tianwen.springcloud.ecrapi.controller;

import com.tianwen.springcloud.commonapi.base.response.Response;
import com.tianwen.springcloud.commonapi.log.SystemControllerLog;
import com.tianwen.springcloud.commonapi.query.QueryTree;
import com.tianwen.springcloud.ecrapi.api.AreaRestApi;
import com.tianwen.springcloud.ecrapi.api.ConfigRestApi;
import com.tianwen.springcloud.ecrapi.constant.ICommonConstants;
import com.tianwen.springcloud.ecrapi.util.SensitiveWordFilter;
import com.tianwen.springcloud.microservice.base.entity.*;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(value = "/config")
public  class ConfigRestController extends BaseRestController implements ConfigRestApi {


    @Override
    public Response<Config> addConfig(@RequestBody Config entity) {
        if (!StringUtils.isEmpty(entity.getConfname()) && configMicroApi.get(entity.getConfname()).getResponseEntity() != null)
            return configMicroApi.update(entity);
        return configMicroApi.add(entity);
    }

    @Override
    public Response<Config> updateConfig(@RequestBody Config entity) {
        if (configMicroApi.get(entity.getConfname()).getResponseEntity() == null)
            return configMicroApi.add(entity);
        return configMicroApi.update(entity);
    }

    @Override
    public Response<Config> getConfigList(@RequestBody QueryTree queryTree) {
        return configMicroApi.search(queryTree);
    }

    @Override
    public Response<Config> removeConfig(@PathVariable(value = "id") String id) {
        return configMicroApi.delete(id);
    }

    @Override
    public Response<Config> getBadWord() {
        return configMicroApi.get(BADWORD_NAME);
    }

    @Override
    public Response<Config> addBadWord(@RequestBody Config entity) {
        entity.setConfname(BADWORD_NAME);
        /**
         * -------------------------------------------------------------------------------------------------
         * @author: jong
         * @date: 2019-02-14
         * initialize Sensitive World Filter when add bad word file
         */
        String wordListFile = ecoServerIp+ ICommonConstants.ECO_FILE_DOWNLOAD_URL + entity.getConfvalue();
        SensitiveWordFilter.initialize(wordListFile);
        /*
         * -------------------------------------------------------------------------------------------------
         */
        return addConfig(entity);
    }
}
