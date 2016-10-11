package com.logic.mes.entity.process;


import com.litesuits.orm.db.annotation.MapCollection;
import com.litesuits.orm.db.annotation.Mapping;
import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.enums.AssignType;
import com.litesuits.orm.db.enums.Relation;

import java.util.ArrayList;
import java.util.List;

@Table("ck")
public class CkProduct {

    @PrimaryKey(AssignType.AUTO_INCREMENT)
    private int id;

    private String jzrq;
    private String hj;

    @Mapping(Relation.OneToMany)
    @MapCollection(ArrayList.class)
    private List<RkDetail> dList;

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

    public List<RkDetail> getdList() {
        return dList;
    }

    public void setdList(List<RkDetail> dList) {
        this.dList = dList;
    }
}
