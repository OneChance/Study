package com.logic.mes.entity.process;



import com.logic.mes.entity.server.ItemCol;
import com.logic.mes.entity.server.ItemKey;


public class PbDetail {

    @ItemKey()
    private String brickId;
    private String length;
    private String level;
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

    public String getStation() {
        return station;
    }

    public void setStation(String station) {
        this.station = station;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }
}
