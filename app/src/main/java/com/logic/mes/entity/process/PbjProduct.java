package com.logic.mes.entity.process;

import com.logic.mes.entity.DataProcessor;
import com.logic.mes.entity.server.ItemKey;
import com.logic.mes.entity.server.ItemCol;

public class PbjProduct extends ProcessBase {

    @ItemKey()
    private String brickId;
    @ItemCol(col = "bb")
    private String bbValue;
    @ItemCol(col = "zcbc")
    private String zcbcValue;
    @ItemCol(col = "zdbc")
    private String zdbcValue;
    @ItemCol(col = "yxbc")
    private String yxbcValue;
    @ItemCol(col = "cc")
    private String sizeValue;
    @ItemCol(col = "dj")
    private String djValue;
    @ItemCol(col = "sfhg")
    private String sfhg;

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

    public String getBbValue() {
        return bbValue;
    }

    public void setBbValue(String bbValue) {
        this.bbValue = DataProcessor.NumberNotNull(bbValue);
    }

    public String getZcbcValue() {
        return zcbcValue;
    }

    public void setZcbcValue(String zcbcValue) {
        this.zcbcValue = DataProcessor.NumberNotNull(zcbcValue);
    }

    public String getZdbcValue() {
        return zdbcValue;
    }

    public void setZdbcValue(String zdbcValue) {
        this.zdbcValue = DataProcessor.NumberNotNull(zdbcValue);
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
}
