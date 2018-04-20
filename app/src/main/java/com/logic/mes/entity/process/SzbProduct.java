package com.logic.mes.entity.process;


import com.logic.mes.entity.server.ItemCol;
import com.logic.mes.entity.server.ItemKey;

public class SzbProduct extends ProcessBase {

    @ItemKey()
    private String brickId;
    @ItemCol(col = "cszbcd")
    private String cszbcd;
    @ItemCol(col = "dywzbb")
    private String dywzbb;
    @ItemCol(col = "ccwz")
    private String ccwz;

    public String getBrickId() {
        return brickId;
    }

    public void setBrickId(String brickId) {
        this.brickId = brickId;
    }

    public String getCszbcd() {
        return cszbcd;
    }

    public void setCszbcd(String cszbcd) {
        this.cszbcd = cszbcd;
    }

    public String getDywzbb() {
        return dywzbb;
    }

    public void setDywzbb(String dywzbb) {
        this.dywzbb = dywzbb;
    }

    public String getCcwz() {
        return ccwz;
    }

    public void setCcwz(String ccwz) {
        this.ccwz = ccwz;
    }
}
