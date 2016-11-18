package com.logic.mes.entity.process;

import com.litesuits.orm.db.annotation.MapCollection;
import com.litesuits.orm.db.annotation.Mapping;
import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.enums.AssignType;
import com.litesuits.orm.db.enums.Relation;

import java.util.ArrayList;
import java.util.List;

@Table("zt")
public class ZtHead extends ProcessBase {

    @PrimaryKey(AssignType.AUTO_INCREMENT)
    private int id;

    @Mapping(Relation.OneToMany)
    @MapCollection(ArrayList.class)
    private List<ZtProduct> detailList;

    public ZtHead() {
        this.detailList = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<ZtProduct> getDetailList() {
        return detailList;
    }

    public void setDetailList(List<ZtProduct> detailList) {
        this.detailList = detailList;
    }
}
