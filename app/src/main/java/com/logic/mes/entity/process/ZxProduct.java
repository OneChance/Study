package com.logic.mes.entity.process;

import com.logic.mes.entity.server.ItemCol;
import com.logic.mes.entity.server.ItemKey;

public class ZxProduct {

    @ItemCol(col = "caseCode")
    private String xh;

    @ItemKey()
    private String hh;

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
}
