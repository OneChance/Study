package com.logic.mes.entity.process;


import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.enums.AssignType;
import com.logic.mes.entity.server.ItemCol;
import com.logic.mes.entity.server.ItemKey;

@Table("qgsfh")
public class QgsfhProduct extends ProcessBase {

    @PrimaryKey(AssignType.AUTO_INCREMENT)
    private int id;
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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
}
