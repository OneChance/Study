package com.logic.mes.entity.process;
import java.io.Serializable;

public class Process implements Serializable{

    private  Integer id;
    private String pName;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getpName() {
        return pName;
    }

    public void setpName(String pName) {
        this.pName = pName;
    }
}
