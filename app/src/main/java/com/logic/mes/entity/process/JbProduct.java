package com.logic.mes.entity.process;


import com.logic.mes.entity.server.ItemCol;
import com.logic.mes.entity.server.ItemKey;

public class JbProduct extends ProcessBase {

    @ItemKey()
    private String brickId;
    @ItemCol(col = "yy")
    private String reason;
    @ItemCol(col = "lx")
    private String lx;

    public JbProduct() {
        this.code = "jb";
    }

    public String getBrickId() {
        return brickId;
    }

    public void setBrickId(String brickId) {
        this.brickId = brickId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getLx() {
        return lx;
    }

    public void setLx(String lx) {
        this.lx = lx;
    }

}
