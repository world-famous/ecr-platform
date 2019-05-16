package com.tianwen.springcloud.microservice.base.entity;

import com.tianwen.springcloud.commonapi.base.BaseEntity;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.List;

@Table(name = "t_e_schooltype")
public class SchoolType extends BaseEntity {

    @Id
    @Column(name = "schooltypeid")
    @ApiModelProperty(value = "", required = true)
    private String schooltypeid;

    @Column(name = "schooltypename")
    @ApiModelProperty(value = "", required = true)
    private String schooltypename;

    public String getSchooltypeid() {
        return schooltypeid;
    }

    public void setSchooltypeid(String schooltypeid) {
        this.schooltypeid = schooltypeid;
    }

    public String getSchooltypename() {
        return schooltypename;
    }

    public void setSchooltypename(String schooltypename) {
        this.schooltypename = schooltypename;
    }
}
