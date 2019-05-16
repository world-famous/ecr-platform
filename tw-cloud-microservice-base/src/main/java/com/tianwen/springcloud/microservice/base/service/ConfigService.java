package com.tianwen.springcloud.microservice.base.service;

import com.tianwen.springcloud.datasource.base.BaseService;
import com.tianwen.springcloud.microservice.base.dao.ConfigMapper;
import com.tianwen.springcloud.microservice.base.entity.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ConfigService extends BaseService<Config> {
    @Autowired
    private ConfigMapper configMapper;
}
