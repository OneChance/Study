package com.logic.mes.entity.process;

import com.logic.mes.entity.DataProcessor;
import com.logic.mes.entity.server.ItemCol;
import com.logic.mes.entity.server.ItemKey;

public class FgqxProduct extends ProcessBase {

    @ItemKey()
    private String ph;
    @ItemCol(col = "cpzps")
    private String cpzps;
    @ItemCol(col = "qxzps")
    private String qxzps;
    @ItemCol(col = "qxs")
    private String qxs;
    @ItemCol(col = "qxsbs")
    private String qxsbs;
    @ItemCol(col = "qxzsp")
    private String qxzsp;


    public String getPh() {
        return ph;
    }

    public void setPh(String ph) {
        this.ph = ph;
    }

    public String getCpzps() {
        return cpzps;
    }

    public void setCpzps(String cpzps) {
        this.cpzps = DataProcessor.NumberNotNull(cpzps);
    }

    public String getQxzps() {
        return qxzps;
    }

    public void setQxzps(String qxzps) {
        this.qxzps = DataProcessor.NumberNotNull(qxzps);
    }

    public String getQxs() {
        return qxs;
    }

    public void setQxs(String qxs) {
        this.qxs = DataProcessor.NumberNotNull(qxs);
    }

    public String getQxsbs() {
        return qxsbs;
    }

    public void setQxsbs(String qxsbs) {
        this.qxsbs = DataProcessor.NumberNotNull(qxsbs);
    }

    public String getQxzsp() {
        return qxzsp;
    }

    public void setQxzsp(String qxzsp) {
        this.qxzsp = DataProcessor.NumberNotNull(qxzsp);
    }
}
