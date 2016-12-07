package com.logic.mes.entity.process;


import com.litesuits.orm.db.annotation.MapCollection;
import com.litesuits.orm.db.annotation.Mapping;
import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.enums.AssignType;
import com.litesuits.orm.db.enums.Relation;
import com.logic.mes.entity.server.ItemCol;

import java.util.ArrayList;
import java.util.List;

public class CkProduct extends ProcessBase {

    private int id;

    @ItemCol(col = "jzrq")
    private String jzrq;
    private String hj;

    @Mapping(Relation.OneToMany)
    @MapCollection(ArrayList.class)
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
}
