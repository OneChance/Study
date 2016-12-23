package com.logic.mes.entity.process;


import com.logic.mes.entity.server.ItemCol;
import com.logic.mes.entity.server.ItemKey;

public class JbDetail {

    @ItemKey()
    private String brickId;
    private String length;
    private String station;
    @ItemCol(col = "sfbf")
    private String sfbf;

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

    public String getSfbf() {
        return sfbf;
    }

    public void setSfbf(String sfbf) {
        this.sfbf = sfbf;
    }
}
