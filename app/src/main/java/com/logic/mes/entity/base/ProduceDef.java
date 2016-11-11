package com.logic.mes.entity.base;
import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.enums.AssignType;

import java.io.Serializable;

@Table("produce_def")
public class ProduceDef implements Serializable{
    @PrimaryKey(AssignType.AUTO_INCREMENT)
    private int id;
    private String producename;
    private String  producecode;
    private Long  idx;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProducename() {
        return producename;
    }

    public void setProducename(String producename) {
        this.producename = producename;
    }

    public String getProducecode() {
        return producecode;
    }

    public void setProducecode(String producecode) {
        this.producecode = producecode;
    }

    public Long getIdx() {
        return idx;
    }

    public void setIdx(Long idx) {
        this.idx = idx;
    }
}
