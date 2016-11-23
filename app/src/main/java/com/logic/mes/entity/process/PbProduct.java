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

@Table("pb")
public class PbProduct extends ProcessBase {
    @PrimaryKey(AssignType.AUTO_INCREMENT)
    private int id;
    @ItemCol(col = "jx")
    private String jx;
    @ItemCol(col = "xwcd")
    private String xwcd;

    @Mapping(Relation.OneToMany)
    @MapCollection(ArrayList.class)
    private List<PbDetail> detailList;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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
