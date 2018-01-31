package com.logic.mes.entity.process;


import com.logic.mes.entity.server.ItemCol;
import com.logic.mes.entity.server.ItemKey;

public class YbProduct extends ProcessBase {

    @ItemKey()
    private String brickId;
    @ItemCol(col = "yzd")
    private String yzd;
    @ItemCol(col = "hbp")
    private String hbp;
    @ItemCol(col = "zb")
    private String zb;
    @ItemCol(col = "dp")
    private String dp;
    @ItemCol(col = "kxs")
    private String kxs;
    @ItemCol(col = "lds")
    private String lds;
    @ItemCol(col = "zqbb")
    private String zqbb;
    @ItemCol(col = "qps")
    private String qps;
    @ItemCol(col = "dxfq")
    private String dxfq;
    @ItemCol(col = "sfbf")
    private String sfbf;

    public String getYzd() {
        return yzd;
    }

    public void setYzd(String yzd) {
        this.yzd = yzd;
    }

    public String getHbp() {
        return hbp;
    }

    public void setHbp(String hbp) {
        this.hbp = hbp;
    }

    public String getZb() {
        return zb;
    }

    public void setZb(String zb) {
        this.zb = zb;
    }

    public String getDp() {
        return dp;
    }

    public void setDp(String dp) {
        this.dp = dp;
    }

    public String getKxs() {
        return kxs;
    }

    public void setKxs(String kxs) {
        this.kxs = kxs;
    }

    public String getLds() {
        return lds;
    }

    public void setLds(String lds) {
        this.lds = lds;
    }

    public String getZqbb() {
        return zqbb;
    }

    public void setZqbb(String zqbb) {
        this.zqbb = zqbb;
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
        this.dxfq = dxfq;
    }

    public String getSfbf() {
        return sfbf;
    }

    public void setSfbf(String sfbf) {
        this.sfbf = sfbf;
    }
}
