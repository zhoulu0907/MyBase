package com.cmsr.onebase.module.flow.build.vo;

import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

@Data
public class UpdateFlowConnectorReqVO {

    @NotNull
    private Long connectorId;

    @NotBlank
    private String connecotrName;

    private String description;

    private String config;

    public JsonNode getConfig() {
        if (StringUtils.isBlank(this.config)) {
            return null;
        }

        return JsonUtils.parseTree(this.config);
    }

    public String getConfigAsStr() {
        return this.config;
    }

    public void setConfig(JsonNode config) {
        if (config == null) {
            return;
        }

        this.config = JsonUtils.toJsonString(config);
    }

}
