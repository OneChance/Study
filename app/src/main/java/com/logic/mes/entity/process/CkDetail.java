package com.logic.mes.entity.process;


import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.enums.AssignType;
import com.logic.mes.entity.server.ItemCol;

@Table("ck_detail")
public class CkDetail {

    @PrimaryKey(AssignType.AUTO_INCREMENT)
    private int id;

    @ItemCol(col = "lb")
    private String lb;
    @ItemCol(col = "tm")
    private String tm;
    @ItemCol(col = "sl")
    private String sl;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLb() {
        return lb;
    }

    public void setLb(String lb) {
        this.lb = lb;
    }

    public String getTm() {
        return tm;
    }

    public void setTm(String tm) {
        this.tm = tm;
    }

    public String getSl() {
        return sl;
    }

    public void setSl(String sl) {
        this.sl = sl;
    }
}
