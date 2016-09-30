package com.logic.mes.entity.base;

import com.litesuits.orm.db.annotation.Table;

import java.io.Serializable;

@Table("broken_type")
public class BrokenType implements Serializable {
    private long id;
    private String produceCode;
    private String dataName;
    private String dataCode;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getProduceCode() {
        return produceCode;
    }

    public void setProduceCode(String produceCode) {
        this.produceCode = produceCode;
    }

    public String getDataName() {
        return dataName;
    }

    public void setDataName(String dataName) {
        this.dataName = dataName;
    }

    public String getDataCode() {
        return dataCode;
    }

    public void setDataCode(String dataCode) {
        this.dataCode = dataCode;
    }
}
