package com.tianwen.springcloud.microservice.base.entity;

import com.tianwen.springcloud.commonapi.base.BaseEntity;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;

@Table(name = "t_e_edition")
public class EditionType extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "editiontypeid")
    @ApiModelProperty("editiontypeid")
    private String editiontypeid;

    @Column(name = "editiontypename")
    @ApiModelProperty(required = true)
    private String editiontypename;

    @Column(name = "delflag")
    @ApiModelProperty(required = true)
    private String delflag;

    public String getEditiontypeid() {
        return editiontypeid;
    }

    public void setEditiontypeid(String editiontypeid) {
        this.editiontypeid = editiontypeid;
    }

    public String getEditiontypename() {
        return editiontypename;
    }

    public void setEditiontypename(String editiontypename) {
        this.editiontypename = editiontypename;
    }

    public String getDelflag() {
        return delflag;
    }

    public void setDelflag(String delflag) {
        this.delflag = delflag;
    }
}
