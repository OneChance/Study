package com.logic.mes.entity.process;


import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.enums.AssignType;

@Table("fx_detail")
public class FxDetail {

    @PrimaryKey(AssignType.AUTO_INCREMENT)
    private int id;
    private String pieceReason;
    private String weightNum;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPieceReason() {
        return pieceReason;
    }

    public void setPieceReason(String pieceReason) {
        this.pieceReason = pieceReason;
    }

    public String getWeightNum() {
        return weightNum;
    }

    public void setWeightNum(String weightNum) {
        this.weightNum = weightNum;
    }
}
