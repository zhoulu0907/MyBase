package com.cmsr.onebase.module.etl.executor.graph;

import com.google.gson.JsonObject;
import lombok.Data;

@Data
public class Field {

    private String fieldId;

    private String fieldName;

    private String fieldType;

    private Integer length;

    private Integer precision;

    private Integer scale;

    public void applyJson(JsonObject columnJson) {
        this.setFieldName(columnJson.get("name").getAsString());
        this.setFieldType(columnJson.get("flinkType").getAsString());
        int ignoreLength = columnJson.get("ignoreLength").getAsInt();
        if (ignoreLength == 0) {
            this.setLength(columnJson.get("length").getAsInt());
        }
        int ignorePrecision = columnJson.get("ignorePrecision").getAsInt();
        if (ignorePrecision == 0) {
            this.setPrecision(columnJson.get("precision").getAsInt());
        }
        int ignoreScale = columnJson.get("ignoreScale").getAsInt();
        if (ignoreScale == 0) {
            this.setScale(columnJson.get("scale").getAsInt());
        }
    }
}
