package com.cmsr.onebase.module.flow.build.vo;

import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "自动化工作流 - 更新连接器请求VO")
@Data
public class UpdateFlowConnectorReqVO {

    @Schema(description = "连接器ID")
    @NotNull(message = "连接器ID不能为空")
    private Long id;

    @Schema(description = "连接器名称")
    @NotBlank(message = "连接器名称不能为空")
    private String connectorName;

    @Schema(description = "连接器描述")
    private String description;

    @Schema(description = "连接器可选配置")
    private JsonNode config;

}
