package com.logic.mes.entity.process;


import com.logic.mes.entity.server.ItemCol;
import com.logic.mes.entity.server.ItemKey;


public class PandianDetail {

    @ItemKey()
    private String brickId;
    private int sequenceNum;
    @ItemCol(col = "weight")
    private double weight;

    public String getBrickId() {
        return brickId;
    }

    public void setBrickId(String brickId) {
        this.brickId = brickId;
    }

    public int getSequenceNum() {
        return sequenceNum;
    }

    public void setSequenceNum(int sequenceNum) {
        this.sequenceNum = sequenceNum;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }
}
