package com.tianwen.springcloud.microservice.base.entity;

import com.tianwen.springcloud.commonapi.base.BaseEntity;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;
import java.sql.Timestamp;

@Table(name = "t_e_teachmaterial_catalog")
public class Catalog extends BaseEntity{

    private static final long serialVersionUID = 6806799806995005201L;


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "SELECT pg_nextval('seq_catalogid_t_e_teachmaterial_catalog')")
    @Column(name = "catalogid")
    @ApiModelProperty("目录Id")
    private String catalogid;

    @Column(name = "parentcatalogid")
    @ApiModelProperty(value = "父目录编号", required = true)
    private String parentcatalogid;

    @Column(name = "creator")
    @ApiModelProperty(value = "创建人", required = true)
    private String creator;

    @Column(name = "catalogname")
    @ApiModelProperty(value = "目录名称", required = true)
    private String catalogname;

    @Column(name = "description")
    @ApiModelProperty(value = "目录描述")
    private String description;

    @Column(name = "pagenum")
    @ApiModelProperty(value = "页码", required = true)
    private Integer pagenum;

    @Column(name = "catalogtype")
    @ApiModelProperty(value = "", required = true, allowableValues = "1,2")
    private String catalogtype;//kmh -> bug number 282 -> start date Jan 28 -> end date Jan 28 ->description "Add catalogtype filed to t_e_teachmaterial_catalog"

    @Column(name = "status")
    @ApiModelProperty(value = "目录状态", required = true, allowableValues = "1,2")
    private String status;

    @Column(name = "createtime")
    @ApiModelProperty(value = "创建时间", required = true)
    private Timestamp createtime;

    @Column(name = "lastmodifytime")
    @ApiModelProperty(value = "最后修改时间", required = true)
    private Timestamp lastmodifytime;

    @Column(name = "sequence")
    @ApiModelProperty(value = "", required = true)
    private String sequence;

    @Column(name = "bookid")
    @ApiModelProperty(value = "", required = true)
    private String bookid;

    @Transient
    private Integer childcount;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getCatalogid() {
        return catalogid;
    }

    public void setCatalogid(String catalogid) {
        this.catalogid = catalogid;
    }

    public String getParentcatalogid() {
        return parentcatalogid;
    }

    public void setParentcatalogid(String parentcatalogid) {
        this.parentcatalogid = parentcatalogid;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getCatalogname() {
        return catalogname;
    }

    public void setCatalogname(String catalogname) {
        this.catalogname = catalogname;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getPagenum() {
        return pagenum;
    }

    public void setPagenum(Integer pagenum) {
        this.pagenum = pagenum;
    }

    public String getCatalogtype() {
        return catalogtype;
    }

    public void setCatalogtype(String catalogtype) {
        this.catalogtype = catalogtype;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getCreatetime() {
        if (createtime == null)
            return null;
        return new Timestamp(createtime.getTime());
    }

    public void setCreatetime(Timestamp createtime) {
        if (createtime == null)
            this.createtime = new Timestamp(System.currentTimeMillis());
        else
            this.createtime = new Timestamp(createtime.getTime());
    }

    public Timestamp getLastmodifytime() {
        if (lastmodifytime == null)
            return null;
        return new Timestamp(lastmodifytime.getTime());
    }

    public void setLastmodifytime(Timestamp lastmodifytime) {
        if (lastmodifytime == null)
            this.lastmodifytime = new Timestamp(System.currentTimeMillis());
        else
            this.lastmodifytime = new Timestamp(lastmodifytime.getTime());
    }

    public String getSequence() {
        return sequence;
    }

    public void setSequence(String sequence) {
        this.sequence = sequence;
    }

    public String getBookid() {
        return bookid;
    }

    public void setBookid(String bookid) {
        this.bookid = bookid;
    }

    public Integer getChildcount() {
        return childcount;
    }

    public void setChildcount(Integer childcount) {
        this.childcount = childcount;
    }

    @Override
    public String toString() {
        return "Catalog[" +
                "catalogid=" + catalogid +
                ", parentcatalogid=" + parentcatalogid +
                ", creator=" + creator +
                ", catalogname=" + catalogname +
                ", description=" + description +
                ", pagenum=" + pagenum +
                ", catalogtype=" + catalogtype +
                ", status=" + status +
                ", createtime=" + createtime +
                ", lastmodifytime=" + lastmodifytime +
                ']';
    }
}
