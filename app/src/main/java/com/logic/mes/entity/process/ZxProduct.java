package com.logic.mes.entity.process;


import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.enums.AssignType;
import com.logic.mes.entity.server.ItemCol;
import com.logic.mes.entity.server.ItemKey;

@Table("zx_detail")
public class ZxProduct {

    @PrimaryKey(AssignType.AUTO_INCREMENT)
    private int id;

    @ItemCol(col = "caseCode")
    private String xh;

    @ItemKey()
    private String hh;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getXh() {
        return xh;
    }

    public void setXh(String xh) {
        this.xh = xh;
    }

    public String getHh() {
        return hh;
    }

    public void setHh(String hh) {
        this.hh = hh;
    }
}
