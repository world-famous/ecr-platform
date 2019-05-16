package com.tianwen.springcloud.microservice.base.entity;

import com.tianwen.springcloud.commonapi.base.BaseEntity;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;

@Table(name = "t_e_bookmodel")
public class BookModel  extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bookmodelid")
    @ApiModelProperty("bookmodelid")
    private String bookmodelid;

    @Column(name = "bookmodelname")
    @ApiModelProperty(required = true)
    private String bookmodelname;

    @Column(name = "delflag")
    @ApiModelProperty(required = true)
    private String delflag;

    public String getBookmodelid() {
        return bookmodelid;
    }

    public void setBookmodelid(String bookmodelid) {
        this.bookmodelid = bookmodelid;
    }

    public String getBookmodelname() {
        return bookmodelname;
    }

    public void setBookmodelname(String bookmodelname) {
        this.bookmodelname = bookmodelname;
    }

    public String getDelflag() {
        return delflag;
    }

    public void setDelflag(String delflag) {
        this.delflag = delflag;
    }
}
