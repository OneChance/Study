package com.logic.mes.entity.process;


import java.util.ArrayList;
import java.util.List;

public class ZxHead extends ProcessBase {

    private List<ZxProduct> detailList;

    public ZxHead() {
        this.detailList = new ArrayList<>();
    }


    public List<ZxProduct> getDetailList() {
        return detailList;
    }

    public void setDetailList(List<ZxProduct> detailList) {
        this.detailList = detailList;
    }
}
