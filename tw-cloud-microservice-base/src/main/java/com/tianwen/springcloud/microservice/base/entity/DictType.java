package com.tianwen.springcloud.microservice.base.entity;

import com.tianwen.springcloud.commonapi.base.BaseEntity;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.Column;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.sql.Timestamp;

@Table(name = "t_e_sys_dict_type")
public class DictType extends BaseEntity{
    @Transient
    private String oldid;

    @Column(name = "dicttypeid")
    @ApiModelProperty(value = "字典类型编号", required = true)
    private String dicttypeid;

    @Column(name = "dicttypename")
    @ApiModelProperty(value = "字典类型名称", required = true)
    private String dicttypename;

    @Column(name = "parenttypeid")
    @ApiModelProperty("父类型编号")
    private String parenttypeid;

    @Column(name = "description")
    @ApiModelProperty("类型说明")
    private String description;

    @Column(name = "sortno")
    @ApiModelProperty("排列顺序")
    private Integer sortno;

    @Column(name = "iseditable")
    @ApiModelProperty(value = "是否可以编辑", required = true)
    private String iseditable;

    @Column(name = "lang")
    @ApiModelProperty(value = "语言类型", required = true)
    private String lang;

    public String getOldid() {
        return oldid;
    }

    public void setOldid(String oldid) {
        this.oldid = oldid;
    }

    public String getDicttypeid() {
        return dicttypeid;
    }

    public void setDicttypeid(String dicttypeid) {
        this.dicttypeid = dicttypeid;
    }

    public String getDicttypename() {
        return dicttypename;
    }

    public void setDicttypename(String dicttypename) {
        this.dicttypename = dicttypename;
    }

    public String getParenttypeid() {
        return parenttypeid;
    }

    public void setParenttypeid(String parenttypeid) {
        this.parenttypeid = parenttypeid;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getSortno() {
        return sortno;
    }

    public void setSortno(Integer sortno) {
        this.sortno = sortno;
    }

    public String getIseditable() {
        return iseditable;
    }

    public void setIseditable(String iseditable) {
        this.iseditable = iseditable;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }
}
