package com.tianwen.springcloud.microservice.base.entity;

import com.tianwen.springcloud.commonapi.base.BaseEntity;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;
import java.sql.Timestamp;

@Table(name = "t_e_sys_dict_item")
public class DictItem extends BaseEntity{
    @Column(name = "dictid")
    @ApiModelProperty(value = "字典项编号", required = true)
    private String dictid;

    @Column(name = "dictvalue")
    @ApiModelProperty(value = "字典项值", required = true)
    private String dictvalue;

    @Column(name = "dicttypeid")
    @ApiModelProperty(value = "字典类型编号", required = true)
    private String dicttypeid;

    @Column(name = "dictname")
    @ApiModelProperty("字典项名称")
    private String dictname;

    @Column(name = "status")
    @ApiModelProperty("字典的状态")
    private String status;

    @Column(name = "sortno")
    @ApiModelProperty("排序字段")
    private int sortno;

    @Column(name = "parentdictid")
    @ApiModelProperty("父字典项编号")
    private String parentdictid;

    @Column(name = "remark")
    @ApiModelProperty("备忘")
    private String remark;

    @Column(name = "iseditable")
    @ApiModelProperty(value = "是否可以编辑", required = true)
    private String iseditable;

    @Column(name = "lastmodifytime")
    @ApiModelProperty("最后修改时间")
    private Timestamp lastmodifytime;

    @Column(name = "lang")
    @ApiModelProperty(value = "语言类型", required = true)
    private String lang;

    public String getDictid() {
        return dictid;
    }

    public void setDictid(String dictid) {
        this.dictid = dictid;
    }

    public String getDictvalue() {
        return dictvalue;
    }

    public void setDictvalue(String dictvalue) {
        this.dictvalue = dictvalue;
    }

    public String getDicttypeid() {
        return dicttypeid;
    }

    public void setDicttypeid(String dicttypeid) {
        this.dicttypeid = dicttypeid;
    }

    public String getDictname() {
        return dictname;
    }

    public void setDictname(String dictname) {
        this.dictname = dictname;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getSortno() {
        return sortno;
    }

    public void setSortno(int sortno) {
        this.sortno = sortno;
    }

    public String getParentdictid() {
        return parentdictid;
    }

    public void setParentdictid(String parentdictid) {
        this.parentdictid = parentdictid;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getIseditable() {
        return iseditable;
    }

    public void setIseditable(String iseditable) {
        this.iseditable = iseditable;
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

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }
}
