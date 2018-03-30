package com.logic.mes.entity.process;

import com.logic.mes.entity.DataProcessor;
import com.logic.mes.entity.server.ItemKey;
import com.logic.mes.entity.server.ItemCol;

public class PbjProduct extends ProcessBase {

    @ItemKey()
    private String brickId;
    @ItemCol(col = "yy")
    private String yy;
    @ItemCol(col = "yxbc")
    private String yxbcValue;
    @ItemCol(col = "cc")
    private String sizeValue;
    @ItemCol(col = "dj")
    private String djValue;
    @ItemCol(col = "sfhg")
    private String sfhg;
    private String kjcd;

    public String getKjcd() {
        return kjcd;
    }

    public void setKjcd(String kjcd) {
        this.kjcd = kjcd;
    }

    public String getSfhg() {
        return sfhg;
    }

    public void setSfhg(String sfhg) {
        this.sfhg = sfhg;
    }

    public String getBrickId() {
        return brickId;
    }

    public void setBrickId(String brickId) {
        this.brickId = brickId;
    }

    public String getYxbcValue() {
        return yxbcValue;
    }

    public void setYxbcValue(String yxbcValue) {
        this.yxbcValue = DataProcessor.NumberNotNull(yxbcValue);
    }

    public String getSizeValue() {
        return sizeValue;
    }

    public void setSizeValue(String sizeValue) {
        this.sizeValue = DataProcessor.NumberNotNull(sizeValue);
    }

    public String getDjValue() {
        return djValue;
    }

    public void setDjValue(String djValue) {
        this.djValue = djValue;
    }

    public String getYy() {
        return yy;
    }

    public void setYy(String yy) {
        this.yy = yy;
    }
}
