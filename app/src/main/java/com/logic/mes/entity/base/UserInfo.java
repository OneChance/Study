package com.logic.mes.entity.base;


import com.litesuits.orm.db.annotation.MapCollection;
import com.litesuits.orm.db.annotation.Mapping;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.enums.Relation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Table("user_data")
public class UserInfo implements Serializable{

    private int id;

    @Mapping(Relation.OneToOne)
    User user;

    @Mapping(Relation.OneToOne)
    ClassTime classTimes;

    @Mapping(Relation.OneToMany)
    @MapCollection(ArrayList.class)
    List<ProduceDef> produceDef;

    @Mapping(Relation.OneToMany)
    @MapCollection(ArrayList.class)
    List<BrokenType> brokenType;

    @Mapping(Relation.OneToMany)
    @MapCollection(ArrayList.class)
    List<TableSet> tableSet;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public ClassTime getClassTimes() {
        return classTimes;
    }

    public void setClassTimes(ClassTime classTimes) {
        this.classTimes = classTimes;
    }

    public List<ProduceDef> getProduceDef() {
        return produceDef;
    }

    public void setProduceDef(List<ProduceDef> produceDef) {
        this.produceDef = produceDef;
    }

    public List<BrokenType> getBrokenType() {
        return brokenType;
    }

    public void setBrokenType(List<BrokenType> brokenType) {
        this.brokenType = brokenType;
    }

    public List<TableSet> getTableSet() {
        return tableSet;
    }

    public void setTableSet(List<TableSet> tableSet) {
        this.tableSet = tableSet;
    }
}
