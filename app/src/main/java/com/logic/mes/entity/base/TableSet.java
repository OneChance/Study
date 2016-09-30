package com.logic.mes.entity.base;

import com.litesuits.orm.db.annotation.Table;

import java.io.Serializable;

@Table("table_set")
public class TableSet implements Serializable {
    private Long id;
    private String	 typeCode;
    private String	  dataSet;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    public String getDataSet() {
        return dataSet;
    }

    public void setDataSet(String dataSet) {
        this.dataSet = dataSet;
    }
}
