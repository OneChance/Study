package com.logic.mes.entity.server;


import java.util.Map;

public class ServerResult {

    public String code;
    public String info;
    public BagData datas;

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

    public BagData getDatas() {
        return datas;
    }

    public void setDatas(BagData datas) {
        this.datas = datas;
    }

    public String getRelVal(String preStep, String afterStep, String value) {
        if (datas.getBagDatas() != null && datas.getBagDatas().size() > 0) {
            Map<String, String> dataMap = datas.getBagDatas().get(0);
            String afterValue = dataMap.get(afterStep + "_" + value);
            if (afterValue != null && !afterValue.equals("")) {
                return afterValue;
            } else {
                return dataMap.get(preStep + "_" + value) == null ? "" : dataMap.get(preStep + "_" + value);
            }
        }
        return "";
    }

    public String getVal(String value) {
        if (datas.getBagDatas() != null && datas.getBagDatas().size() > 0) {
            Map<String, String> dataMap = datas.getBagDatas().get(0);
            return dataMap.get(value) == null ? "" : dataMap.get(value);
        }
        return "";
    }
}
