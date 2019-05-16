package com.tianwen.springcloud.microservice.base.entity;

import com.tianwen.springcloud.commonapi.base.BaseEntity;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;
import java.util.List;

@Table(name = "t_e_sys_config")
public class Config extends BaseEntity{

    @Id
    @Column(name = "confname")
    @ApiModelProperty("name")
    private String confname;

    @Column(name = "confvalue")
    @ApiModelProperty(value = "value", required = true)
    private String confvalue;

    public String getConfname() {
        return confname;
    }

    public void setConfname(String confname) {
        this.confname = confname;
    }

    public String getConfvalue() {
        return confvalue;
    }

    public void setConfvalue(String confvalue) {
        this.confvalue = confvalue;
    }
}
