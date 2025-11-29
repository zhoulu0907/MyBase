package com.cmsr.onebase.module.flow.build.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "自动化工作流 - 脚本连接器动作详情VO")
@Data
public class ConnectorScriptVO {

    @Schema(description = "脚本ID")
    private Long id;

    @Schema(description = "脚本ID")
    private Long scriptId;

    @Schema(description = "连接器UUID")
    private Long connectorUuid;

    @Schema(description = "脚本名称")
    private String scriptName;

    @Schema(description = "脚本类型")
    private String scriptType;

    @Schema(description = "脚本描述")
    private String description;

    @Schema(description = "脚本内容")
    private String rawScript;

    @Schema(description = "输入参数")
    private String inputParameter;

    @Schema(description = "输出参数")
    private String outputParameter;

    @Schema(description = "应用ID")
    private Long applicationId;

    private LocalDateTime createTime;

}
