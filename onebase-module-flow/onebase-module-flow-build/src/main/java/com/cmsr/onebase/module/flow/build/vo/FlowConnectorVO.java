package com.cmsr.onebase.module.flow.build.vo;

import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;

@Data
public class FlowConnectorVO {

    private Long applicationId;

    private Long connectorId;

    private String typeCode;

    private String connectorName;

    private String description;

    private String config;

    private LocalDateTime createTime;

    public JsonNode getConfig() {
        if (StringUtils.isBlank(this.config)) {
            return null;
        }

        return JsonUtils.parseTree(this.config);
    }

    public void setConfig(JsonNode config) {
        if (config == null) {
            return;
        }

        this.config = JsonUtils.toJsonString(config);
    }

}
