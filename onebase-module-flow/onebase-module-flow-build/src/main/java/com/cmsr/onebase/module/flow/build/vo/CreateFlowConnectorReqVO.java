package com.cmsr.onebase.module.flow.build.vo;

import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "自动化工作流 - 连接器创建请求VO")
@Data
public class CreateFlowConnectorReqVO {

    @Schema(description = "应用ID")
    @NotNull(message = "应用ID不能为空")
    private Long applicationId;

    @Schema(description = "连接器名称")
    @NotBlank(message = "连接器名称不能为空")
    private String connectorName;

    @Schema(description = "连接器描述")
    private String description;

    @Schema(description = "连接器类型")
    @NotBlank(message = "连接器类型不能为空")
    private String typeCode;

    @Schema(description = "连接器可选配置")
    private JsonNode config;

//    public JsonNode getConfig() {
//        if (StringUtils.isBlank(this.config)) {
//            return null;
//        }
//
//        return JsonUtils.parseTree(this.config);
//    }
//
//    public String getConfigAsStr() {
//        return this.config;
//    }
//
//    public void setConfig(JsonNode config) {
//        if (config == null || config instanceof NullNode) {
//            return;
//        }
//
//        this.config = JsonUtils.toJsonString(config);
//    }
}
