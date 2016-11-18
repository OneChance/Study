package com.logic.mes.entity.process;


import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.enums.AssignType;
import com.logic.mes.entity.server.ItemCol;
import com.logic.mes.entity.server.ItemKey;

@Table("zt_detail")
public class ZtProduct {

    @PrimaryKey(AssignType.AUTO_INCREMENT)
    private int id;

    @ItemCol(col = "torrCode")
    private String th;

    @ItemKey()
    private String xh;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTh() {
        return th;
    }

    public void setTh(String th) {
        this.th = th;
    }

    public String getXh() {
        return xh;
    }

    public void setXh(String xh) {
        this.xh = xh;
    }
}
