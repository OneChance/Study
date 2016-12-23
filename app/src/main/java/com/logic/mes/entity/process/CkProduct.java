package com.logic.mes.entity.process;


import com.logic.mes.entity.server.ItemCol;
import java.util.ArrayList;
import java.util.List;

public class CkProduct extends ProcessBase {

    private int id;

    @ItemCol(col = "jzrq")
    private String jzrq;
    private String hj;
    @ItemCol(col = "dh")
    private String bill;

    private List<CkDetail> detailList;

    public CkProduct() {
        detailList = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public List<CkDetail> getDetailList() {
        return detailList;
    }

    public void setDetailList(List<CkDetail> detailList) {
        this.detailList = detailList;
    }

    public String getBill() {
        return bill;
    }

    public void setBill(String bill) {
        this.bill = bill;
    }
}
