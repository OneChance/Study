package com.logic.mes.entity.process;


import com.logic.mes.entity.server.ItemCol;

import java.util.List;

public class JbProduct extends ProcessBase {

    private String brickId;
    @ItemCol(col = "yy")
    private String reason;
    @ItemCol(col = "groupId")
    private String groupId;

    private List<JbDetail> jbDetailList;

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

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public List<JbDetail> getJbDetailList() {
        return jbDetailList;
    }

    public void setJbDetailList(List<JbDetail> jbDetailList) {
        this.jbDetailList = jbDetailList;
    }
}
