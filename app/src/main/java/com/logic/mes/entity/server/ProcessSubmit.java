package com.logic.mes.entity.server;


import com.litesuits.orm.db.annotation.MapCollection;
import com.litesuits.orm.db.annotation.Mapping;
import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.enums.AssignType;
import com.litesuits.orm.db.enums.Relation;

import java.util.ArrayList;
import java.util.List;

@Table("submit_data")
public class ProcessSubmit {

    @PrimaryKey(AssignType.AUTO_INCREMENT)
    private int id;

    private String userCode;
    private String userOrg;
    private String userName;
    private String produceCode;
    private Boolean isGroup;

    @Mapping(Relation.OneToMany)
    @MapCollection(ArrayList.class)
    private List<ProcessItem> items;

    private String operationTime;

    public ProcessSubmit() {
        this.isGroup = false;
    }

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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Boolean getGroup() {
        return isGroup;
    }

    public void setGroup(Boolean group) {
        isGroup = group;
    }
}
