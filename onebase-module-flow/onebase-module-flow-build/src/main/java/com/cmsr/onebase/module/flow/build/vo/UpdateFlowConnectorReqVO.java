package com.cmsr.onebase.module.flow.build.vo;

import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.NullNode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

@Schema(description = "自动化工作流 - 更新连接器请求VO")
@Data
public class UpdateFlowConnectorReqVO {

    @Schema(description = "连接器ID")
    @NotNull(message = "连接器ID不能为空")
    private Long connectorId;

    @Schema(description = "连接器名称")
    @NotBlank(message = "连接器名称不能为空")
    private String connectorName;

    @Schema(description = "连接器描述")
    private String description;

    @Schema(description = "连接器可选配置")
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
