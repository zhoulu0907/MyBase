package com.cmsr.onebase.module.flow.build.vo;

import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.NullNode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

@Data
public class CreateFlowConnectorReqVO {

    @NotNull(message = "应用ID不能为空")
    private Long applicationId;

    @NotBlank(message = "连接器名称不能为空")
    private String connectorName;

    private String description;

    @NotBlank(message = "连接器类型不能为空")
    private String typeCode;

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
        if (config == null || config instanceof NullNode) {
            return;
        }

        this.config = JsonUtils.toJsonString(config);
    }
}
