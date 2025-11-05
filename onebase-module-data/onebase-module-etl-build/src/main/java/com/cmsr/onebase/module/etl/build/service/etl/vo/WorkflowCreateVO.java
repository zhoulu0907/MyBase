package com.cmsr.onebase.module.etl.build.service.etl.vo;

import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

@Data
public class WorkflowCreateVO {

    @NotNull
    private Long applicationId;

    @NotBlank(message = "ETL名称不能为空")
    private String flowName;

    private String declaration;

    @NotBlank(message = "ETL配置不能为空")
    private String config;

    public JsonNode getConfig() {
        if (StringUtils.isBlank(this.config)) {
            return JsonUtils.createObjectNode();
        }
        return JsonUtils.parseTree(this.config);
    }

    public void setConfig(JsonNode config) {
        this.config = JsonUtils.toJsonString(config);
    }
}
