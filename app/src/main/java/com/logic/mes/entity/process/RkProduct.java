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

@Table("rk")
public class RkProduct extends ProcessBase {

    @PrimaryKey(AssignType.AUTO_INCREMENT)
    private int id;

    @ItemCol(col = "jzrq")
    private String jzrq;
    private String hj;

    @Mapping(Relation.OneToMany)
    @MapCollection(ArrayList.class)
    private List<RkDetail> detailList;

    public RkProduct() {
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

    public List<RkDetail> getDetailList() {
        return detailList;
    }

    public void setDetailList(List<RkDetail> detailList) {
        this.detailList = detailList;
    }
}