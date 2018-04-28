package com.logic.mes.entity.process;


import com.logic.mes.entity.DataProcessor;
import com.logic.mes.entity.server.ItemCol;
import com.logic.mes.entity.server.ItemKey;

public class YbProduct extends ProcessBase {

    @ItemKey()
    private String brickId;
    @ItemCol(col = "zb")
    private String zb;
    @ItemCol(col = "dp")
    private String dp;
    @ItemCol(col = "kxs")
    private String kxs;
    @ItemCol(col = "zqbb")
    private String zqbb;
    @ItemCol(col = "qps")
    private String qps;
    @ItemCol(col = "dxfq")
    private String dxfq;
    @ItemCol(col = "sfbf")
    private String sfbf;
    @ItemCol(col = "lds")
    private String qt;

    public String getZb() {
        return zb;
    }

    public void setZb(String zb) {
        this.zb = DataProcessor.NumberNotNull(zb);
    }

    public String getDp() {
        return dp;
    }

    public void setDp(String dp) {
        this.dp = DataProcessor.NumberNotNull(dp);
    }

    public String getKxs() {
        return kxs;
    }

    public void setKxs(String kxs) {
        this.kxs = DataProcessor.NumberNotNull(kxs);
    }

    public String getZqbb() {
        return zqbb;
    }

    public void setZqbb(String zqbb) {
        this.zqbb = DataProcessor.NumberNotNull(zqbb);
    }

    public String getBrickId() {
        return brickId;
    }

    public void setBrickId(String brickId) {
        this.brickId = brickId;
    }

    public String getQps() {
        return qps;
    }

    public void setQps(String qps) {
        this.qps = qps;
    }

    public String getDxfq() {
        return dxfq;
    }

    public void setDxfq(String dxfq) {
        this.dxfq = DataProcessor.NumberNotNull(dxfq);
    }

    public String getSfbf() {
        return sfbf;
    }

    public void setSfbf(String sfbf) {
        this.sfbf = sfbf;
    }

    public String getQt() {
        return qt;
    }

    public void setQt(String qt) {
        this.qt = qt;
    }
}
