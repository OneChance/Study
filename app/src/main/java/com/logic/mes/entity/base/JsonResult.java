package com.logic.mes.entity.base;


import java.io.Serializable;

public class JsonResult implements Serializable{
    private Long code;
    private String info;

    public Long getCode() {
        return code;
    }

    public void setCode(Long code) {
        this.code = code;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }
}
