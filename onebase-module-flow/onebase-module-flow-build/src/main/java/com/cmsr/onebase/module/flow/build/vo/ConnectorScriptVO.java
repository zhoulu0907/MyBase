package com.cmsr.onebase.module.flow.build.vo;

import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "自动化工作流 - 脚本连接器动作详情VO")
@Data
public class ConnectorScriptVO {

    @Schema(description = "脚本ID")
    private Long id;

    @Schema(description = "脚本UUID")
    private String scriptUuid;

    @Schema(description = "连接器UUID")
    private String connectorUuid;

    @Schema(description = "脚本名称")
    private String scriptName;

    @Schema(description = "脚本类型")
    private String scriptType;

    @Schema(description = "脚本描述")
    private String description;

    @Schema(description = "脚本内容")
    private String rawScript;

    @Schema(description = "输入参数")
    private JsonNode inputParameter;

    @Schema(description = "输出参数")
    private JsonNode outputParameter;

    @Schema(description = "输入定义")
    private JsonNode inputSchema;

    @Schema(description = "输出定义")
    private JsonNode outputSchema;

    @Schema(description = "应用ID")
    private Long applicationId;

    private LocalDateTime createTime;

}
