package com.logic.mes.entity.process;

import com.logic.mes.entity.server.ItemCol;
import com.logic.mes.entity.server.ItemKey;

public class ZtProduct {

    @ItemCol(col = "torrCode")
    private String th;

    private String sl;
    private String dj;
    private String djCode;

    @ItemKey()
    private String xh;

    private String db;

    public String getTh() {
        return th;
    }

    public void setTh(String th) {
        this.th = th;
    }

    public String getXh() {
        return xh;
    }

    public void setXh(String xh) {
        this.xh = xh;
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

    public String getDj() {
        return dj;
    }

    public void setDj(String dj) {
        this.dj = dj;
    }

    public String getDjCode() {
        return djCode;
    }

    public void setDjCode(String djCode) {
        this.djCode = djCode;
    }
}
