package com.logic.mes.entity.base;

import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.enums.AssignType;

import java.io.Serializable;

@Table("table_type")
public class TableType implements Serializable {
    @PrimaryKey(AssignType.AUTO_INCREMENT)
    private Long id;
    private String typeCode;
    private String typeName;
    private String lineLength;
    private String groupLength;
    private String groupMinLength;

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getLineLength() {
        return lineLength;
    }

    public void setLineLength(String lineLength) {
        this.lineLength = lineLength;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGroupLength() {
        return groupLength;
    }

    public void setGroupLength(String groupLength) {
        this.groupLength = groupLength;
    }

    public String getGroupMinLength() {
        return groupMinLength;
    }

    public void setGroupMinLength(String groupMinLength) {
        this.groupMinLength = groupMinLength;
    }
}
