package com.logic.mes.entity.server;

import java.util.List;
import java.util.Map;

public class BagData {

    private List<Map<String, String>> bagDatas;

    public List<Map<String, String>> getBagDatas() {
        return bagDatas;
    }

    public void setBagDatas(List<Map<String, String>> bagDatas) {
        this.bagDatas = bagDatas;
    }
}
