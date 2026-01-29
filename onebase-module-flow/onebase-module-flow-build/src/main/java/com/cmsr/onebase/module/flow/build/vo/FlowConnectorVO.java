package com.cmsr.onebase.module.flow.build.vo;

import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "自动化工作流 - 连接器VO")
@Data
public class FlowConnectorVO {

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "应用ID")
    private Long applicationId;

    @Schema(description = "连接器UUID")
    private String connectorUuid;

    @Schema(description = "连接器类型编号")
    private String typeCode;

    @Schema(description = "连接器名称")
    private String connectorName;

    @Schema(description = "连接器描述")
    private String description;

    @Schema(description = "连接器可选配置")
    private JsonNode config;

    @Schema(description = "连接器版本")
    private String connectorVersion;

    @Schema(description = "连接器创建时间")
    private LocalDateTime createTime;

    @Schema(description = "环境UUID")
    private String envUuid;

    @Schema(description = "环境名称")
    private String envName;

    @Schema(description = "环境编码（DEV/TEST/PROD）")
    private String envCode;

    @Schema(description = "启用状态（0-禁用，1-启用）")
    private Integer activeStatus;

    @Schema(description = "配置状态（configured-已配置, unconfigured-未配置）")
    private String configStatus;

}
