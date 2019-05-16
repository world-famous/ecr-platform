package com.tianwen.springcloud.microservice.base.entity;

import com.alibaba.fastjson.JSONObject;
import com.tianwen.springcloud.commonapi.base.BaseEntity;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;
import java.sql.Time;
import java.sql.Timestamp;

@Table(name = "t_con_teaching")
public class Teaching extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "SELECT pg_nextval('seq_teachid_t_con_teaching')")
    @Column(name = "teachid")
    @ApiModelProperty("")
    private String teachid;

    @Column(name = "classid")
    @ApiModelProperty(value = "", required = true)
    private String classid;

    @Column(name = "teacher")
    @ApiModelProperty(value = "", required = true)
    private String teacher;

    @Column(name = "subjectid")
    @ApiModelProperty(value = "", required = true)
    private String subjectid;

    @Transient
    private String subject;

    @Column(name = "createtime")
    @ApiModelProperty(value = "", required = true)
    private Timestamp createtime;

    public Teaching(){}

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getTeachid() {
        return teachid;
    }

    public void setTeachid(String teachid) {
        this.teachid = teachid;
    }

    public String getClassid() {
        return classid;
    }

    public void setClassid(String classid) {
        this.classid = classid;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public String getSubjectid() {
        return subjectid;
    }

    public void setSubjectid(String subjectid) {
        this.subjectid = subjectid;
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
}
