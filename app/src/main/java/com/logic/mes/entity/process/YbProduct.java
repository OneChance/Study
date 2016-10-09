package com.logic.mes.entity.process;


import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.enums.AssignType;

@Table("yb")
public class YbProduct {

    @PrimaryKey(AssignType.AUTO_INCREMENT)
    private int id;

    private String jzbh;
    private String yzd;
    private String hbp;
    private String zb;
    private String dp;
    private String kxs;
    private String lds;
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

    public String getJzbh() {
        return jzbh;
    }

    public void setJzbh(String jzbh) {
        this.jzbh = jzbh;
    }
}
