package com.cmsr.onebase.module.etl.build.vo.mgt;

import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

@Schema(description = "数据工厂 - ETL - 创建ETL VO")
@Data
public class WorkflowCreateVO {

    @Schema(description = "应用ID")
    @NotNull
    private Long applicationId;

    @Schema(description = "ETL名称")
    @NotBlank(message = "ETL名称不能为空")
    private String flowName;

    @Schema(description = "ETL描述")
    private String declaration;

    @Schema(description = "ETL配置")
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
