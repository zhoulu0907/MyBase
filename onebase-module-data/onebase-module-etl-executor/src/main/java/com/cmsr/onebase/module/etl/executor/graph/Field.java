package com.cmsr.onebase.module.etl.executor.graph;

import com.cmsr.onebase.module.etl.executor.provider.dao.EtlColumn;
import lombok.Data;

@Data
public class Field {

    private String fieldId;

    private String fieldName;

    private String fieldType;

    private Integer length;

    private Integer precision;

    private Integer scale;

    public void complementColumn(EtlColumn columnDef) {
        this.setFieldName(columnDef.getName());
        this.setFieldType(columnDef.getFlinkType());
        int ignoreLength = columnDef.getIgnoreLength();
        if (ignoreLength == 0) {
            this.setLength(columnDef.getLength());
        }
        int ignorePrecision = columnDef.getIgnorePrecision();
        if (ignorePrecision == 0) {
            this.setPrecision(columnDef.getPrecision());
        }
        int ignoreScale = columnDef.getIgnoreScale();
        if (ignoreScale == 0) {
            this.setScale(columnDef.getScale());
        }
    }
}
