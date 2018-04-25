package com.logic.mes.entity.process;

import com.logic.mes.entity.server.ItemCol;

public class RkDetail {

    @ItemCol(col = "lb")
    private String lb;
    @ItemCol(col = "tm")
    private String tm;
    @ItemCol(col = "sl")
    private String sl;
    @ItemCol(col = "jf")
    private String jf;

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

    public String getJf() {
        return jf;
    }

    public void setJf(String jf) {
        this.jf = jf;
    }
}
