package com.logic.mes.entity.process;


import com.logic.mes.entity.server.ItemCol;

import java.util.List;

public class PandianProduct extends ProcessBase {

    @ItemCol(col = "chejian")
    private String workshop;
    private List<PandianDetail> detailList;

    public String getWorkshop() {
        return workshop;
    }

    public void setWorkshop(String workshop) {
        this.workshop = workshop;
    }

    public List<PandianDetail> getDetailList() {
        return detailList;
    }

    public void setDetailList(List<PandianDetail> detailList) {
        this.detailList = detailList;
    }
}
