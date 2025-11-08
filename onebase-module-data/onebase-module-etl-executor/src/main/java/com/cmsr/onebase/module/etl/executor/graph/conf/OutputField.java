package com.cmsr.onebase.module.etl.executor.graph.conf;

import com.cmsr.onebase.module.etl.executor.graph.Field;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class OutputField extends Field {

    @SerializedName("targetFieldId")
    private String fieldId;

    private String sourceFieldId;

}
