package com.logic.mes.entity.process;


import com.logic.mes.entity.server.ItemCol;

import java.util.List;

public class PbProduct extends ProcessBase {

    @ItemCol(col = "jx")
    private String jx;
    @ItemCol(col = "xwcd")
    private String xwcd;
    @ItemCol(col = "gdh")
    private String gdh;

    public String getGdh() {
        return gdh;
    }

    public void setGdh(String gdh) {
        this.gdh = gdh;
    }

    private List<PbDetail> detailList;

    public String getJx() {
        return jx;
    }

    public void setJx(String jx) {
        this.jx = jx;
    }

    public List<PbDetail> getDetailList() {
        return detailList;
    }

    public String getXwcd() {
        return xwcd;
    }

    public void setXwcd(String xwcd) {
        this.xwcd = xwcd;
    }

    public void setDetailList(List<PbDetail> detailList) {
        this.detailList = detailList;
    }
}
