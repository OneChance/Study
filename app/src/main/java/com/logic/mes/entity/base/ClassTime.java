package com.logic.mes.entity.base;

import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.enums.AssignType;

import java.io.Serializable;

@Table("class_time")
public class ClassTime implements Serializable {
    @PrimaryKey(AssignType.AUTO_INCREMENT)
    private Long id;
    private Long orgid =0l;
    private Integer class1 =0;
    private String start1 ="";
    private String end1 ="";
    private Integer class2 =0;
    private String start2="";
    private String end2 ="";
    private Integer class3 =0;
    private String start3 ="";
    private String end3="";

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOrgid() {
        return orgid;
    }

    public void setOrgid(Long orgid) {
        this.orgid = orgid;
    }

    public Integer getClass1() {
        return class1;
    }

    public void setClass1(Integer class1) {
        this.class1 = class1;
    }

    public String getStart1() {
        return start1;
    }

    public void setStart1(String start1) {
        this.start1 = start1;
    }

    public String getEnd1() {
        return end1;
    }

    public void setEnd1(String end1) {
        this.end1 = end1;
    }

    public Integer getClass2() {
        return class2;
    }

    public void setClass2(Integer class2) {
        this.class2 = class2;
    }

    public String getStart2() {
        return start2;
    }

    public void setStart2(String start2) {
        this.start2 = start2;
    }

    public String getEnd2() {
        return end2;
    }

    public void setEnd2(String end2) {
        this.end2 = end2;
    }

    public Integer getClass3() {
        return class3;
    }

    public void setClass3(Integer class3) {
        this.class3 = class3;
    }

    public String getStart3() {
        return start3;
    }

    public void setStart3(String start3) {
        this.start3 = start3;
    }

    public String getEnd3() {
        return end3;
    }

    public void setEnd3(String end3) {
        this.end3 = end3;
    }
}
