package com.logic.mes.entity.process;

import com.logic.mes.entity.server.ItemCol;
import com.logic.mes.entity.server.ItemKey;

public class ZxProduct {

    @ItemCol(col = "caseCode")
    private String xh;

    @ItemKey()
    private String hh;

    private String sl;

    private String db;

    private String level;

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getXh() {
        return xh;
    }

    public void setXh(String xh) {
        this.xh = xh;
    }

    public String getHh() {
        return hh;
    }

    public void setHh(String hh) {
        this.hh = hh;
    }

    public String getDb() {
        return db;
    }

    public void setDb(String db) {
        this.db = db;
    }

    public String getSl() {
        return sl;
    }

    public void setSl(String sl) {
        this.sl = sl;
    }
}
