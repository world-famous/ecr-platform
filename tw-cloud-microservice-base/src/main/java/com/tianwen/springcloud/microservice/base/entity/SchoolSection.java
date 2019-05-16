package com.tianwen.springcloud.microservice.base.entity;

import com.tianwen.springcloud.commonapi.base.BaseEntity;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;

@Table(name = "t_e_schoolsection")
public class SchoolSection  extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schoolsectionid")
    @ApiModelProperty("schoolsectionid")
    private String schoolsectionid;

    @Column(name = "schoolsectionname")
    @ApiModelProperty(required = true)
    private String schoolsectionname;

    @Column(name = "delflag")
    @ApiModelProperty(required = true)
    private String delflag;

    public String getSchoolsectionid() {
        return schoolsectionid;
    }

    public void setSchoolsectionid(String schoolsectionid) {
        this.schoolsectionid = schoolsectionid;
    }

    public String getSchoolsectionname() {
        return schoolsectionname;
    }

    public void setSchoolsectionname(String schoolsectionname) {
        this.schoolsectionname = schoolsectionname;
    }

    public String getDelflag() {
        return delflag;
    }

    public void setDelflag(String delflag) {
        this.delflag = delflag;
    }
}
