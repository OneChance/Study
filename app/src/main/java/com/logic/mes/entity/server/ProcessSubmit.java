package com.logic.mes.entity.server;


import java.util.List;

public class ProcessSubmit {
    public String userCode;
    public String userOrg;
    public String userName;
    public String produceCode;
    public List<ProcessItem> items;
    public String operationTime;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public String getUserOrg() {
        return userOrg;
    }

    public void setUserOrg(String userOrg) {
        this.userOrg = userOrg;
    }

    public String getProduceCode() {
        return produceCode;
    }

    public void setProduceCode(String produceCode) {
        this.produceCode = produceCode;
    }

    public List<ProcessItem> getItems() {
        return items;
    }

    public void setItems(List<ProcessItem> items) {
        this.items = items;
    }

    public String getOperationTime() {
        return operationTime;
    }

    public void setOperationTime(String operationTime) {
        this.operationTime = operationTime;
    }
}
