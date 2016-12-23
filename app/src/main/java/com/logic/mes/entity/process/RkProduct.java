package com.logic.mes.entity.process;


import com.logic.mes.entity.server.ItemCol;
import java.util.ArrayList;
import java.util.List;

public class RkProduct extends ProcessBase {

    @ItemCol(col = "jzrq")
    private String jzrq;
    private String hj;

    private List<RkDetail> detailList;

    public RkProduct() {
        detailList = new ArrayList<>();
    }

    public String getJzrq() {
        return jzrq;
    }

    public void setJzrq(String jzrq) {
        this.jzrq = jzrq;
    }

    public String getHj() {
        return hj;
    }

    public void setHj(String hj) {
        this.hj = hj;
    }

    public List<RkDetail> getDetailList() {
        return detailList;
    }

    public void setDetailList(List<RkDetail> detailList) {
        this.detailList = detailList;
    }
}
