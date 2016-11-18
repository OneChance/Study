package com.logic.mes.entity.process;


import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.enums.AssignType;
import com.logic.mes.entity.server.ItemCol;
import com.logic.mes.entity.server.ItemKey;

@Table("pb_detail")
public class PbDetail {
    @PrimaryKey(AssignType.AUTO_INCREMENT)
    private int id;

    @ItemKey()
    private String brickId;
    private String length;
    @ItemCol(col="gw")
    private String station;

    public String getBrickId() {
        return brickId;
    }

    public void setBrickId(String brickId) {
        this.brickId = brickId;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStation() {
        return station;
    }

    public void setStation(String station) {
        this.station = station;
    }
}
