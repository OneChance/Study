package com.logic.mes.entity.process;


import java.util.ArrayList;
import java.util.List;

public class ZtHead extends ProcessBase {

    private List<ZtProduct> detailList;

    public ZtHead() {
        this.detailList = new ArrayList<>();
    }

    public List<ZtProduct> getDetailList() {
        return detailList;
    }

    public void setDetailList(List<ZtProduct> detailList) {
        this.detailList = detailList;
    }
}
