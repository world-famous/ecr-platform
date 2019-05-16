package com.tianwen.springcloud.microservice.base.entity;

import com.tianwen.springcloud.commonapi.base.BaseEntity;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;
import java.sql.Timestamp;

@Table(name = "t_e_role")
public class Role extends BaseEntity{

    @Id
    @Column(name = "roleid")
    @ApiModelProperty(value = "角色Id", required = true)
    private String roleid;

    @Column(name = "rolename")
    @ApiModelProperty(value = "角色名称", required = true)
    private String rolename;

    @Column(name = "description")
    @ApiModelProperty(value = "描述")
    private String description;

    @Column(name = "description")
    @ApiModelProperty(value = "状态", required = true)
    private String status;

    @Column(name = "linenum")
    @ApiModelProperty(value = "显示时行号", required = true)
    private Integer linenum;

    @Column(name = "initrole")
    @ApiModelProperty(value = "是否为预置角色")
    private String initrole;

    @Column(name = "creatorid")
    @ApiModelProperty(value = "创建者Id")
    private String creatorid;

    @Column(name = "createtime")
    @ApiModelProperty(value = "创建时间")
    private Timestamp createtime;

    @Column(name = "lastmodifierid")
    @ApiModelProperty(value = "最后修改人Id")
    private String lastmodifierid;

    @Column(name = "lastmodifytime")
    @ApiModelProperty(value = "最后修改时间")
    private Timestamp lastmodifytime;

    @Transient
    private String userid;

    public String getRoleid() {
        return roleid;
    }

    public void setRoleid(String roleid) {
        this.roleid = roleid;
    }

    public String getRolename() {
        return rolename;
    }

    public void setRolename(String rolename) {
        this.rolename = rolename;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getLinenum() {
        return linenum;
    }

    public void setLinenum(Integer linenum) {
        this.linenum = linenum;
    }

    public String getInitrole() {
        return initrole;
    }

    public void setInitrole(String initrole) {
        this.initrole = initrole;
    }

    public String getCreatorid() {
        return creatorid;
    }

    public void setCreatorid(String creatorid) {
        this.creatorid = creatorid;
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

    public String getLastmodifierid() {
        return lastmodifierid;
    }

    public void setLastmodifierid(String lastmodifierid) {
        this.lastmodifierid = lastmodifierid;
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

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }
}
