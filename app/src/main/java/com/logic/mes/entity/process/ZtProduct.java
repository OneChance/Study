package com.logic.mes.entity.process;

import com.logic.mes.entity.server.ItemCol;
import com.logic.mes.entity.server.ItemKey;

public class ZtProduct {

    @ItemCol(col = "torrCode")
    private String th;

    @ItemKey()
    private String xh;

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
}
