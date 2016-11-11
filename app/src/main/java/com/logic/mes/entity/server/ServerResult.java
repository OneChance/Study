package com.logic.mes.entity.server;


import java.util.List;

public class ServerResult {

    public String code;
    public String info;
    public List<BrickInfo> datas;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public List<BrickInfo> getDatas() {
        return datas;
    }

    public void setDatas(List<BrickInfo> datas) {
        this.datas = datas;
    }
}
