package com.tianwen.springcloud.microservice.base.controller;

import com.tianwen.springcloud.datasource.base.AbstractCRUDController;
import com.tianwen.springcloud.microservice.base.api.SchoolTypeMicroApi;
import com.tianwen.springcloud.microservice.base.entity.SchoolType;
import com.tianwen.springcloud.microservice.base.service.SchoolTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping(value = "/schooltype")
public class SchoolTypeController extends AbstractCRUDController<SchoolType> implements SchoolTypeMicroApi
{
        @Autowired
        private SchoolTypeService schoolTypeService;

        @Override
        public void validate(MethodType methodType, Object p) { }

        @Override
        public void connectSchoolType(@RequestBody  Map<String, String> map) {
                schoolTypeService.connectSchoolType( map );
        }
}
