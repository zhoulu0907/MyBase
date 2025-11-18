package com.cmsr.onebase.module.flow.build.vo;

import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateFlowConnectorScriptReqVO {

    @NotNull
    private Long connectorId;

    @NotBlank
    private String scriptName;

    private String scriptType;

    private String description;

    @NotBlank
    private String rawScript;

    private JsonNode inputParameter;

    private JsonNode outputParameter;

    public String getInputParameter() {
        if (this.inputParameter == null) {
            return null;
        }
        return JsonUtils.toJsonString(this.inputParameter);
    }

    public String getOutputParameter() {
        if (this.outputParameter == null) {
            return null;
        }
        return JsonUtils.toJsonString(this.outputParameter);
    }
}
