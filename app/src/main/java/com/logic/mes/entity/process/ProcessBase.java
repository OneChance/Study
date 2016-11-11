package com.logic.mes.entity.process;


public class ProcessBase {

    //需要保存在数据库里的属性
    public String lrsj;
    public String code;

    public String getLrsj() {
        return lrsj;
    }

    public void setLrsj(String lrsj) {
        this.lrsj = lrsj;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
