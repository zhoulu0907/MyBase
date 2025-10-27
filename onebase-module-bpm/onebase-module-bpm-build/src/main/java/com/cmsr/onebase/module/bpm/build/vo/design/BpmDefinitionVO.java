package com.cmsr.onebase.module.bpm.build.vo.design;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 流程定义视图
 *
 * @author liyang
 * @date 2025/10/19
 */

@Data
@Schema(description = "流程定义VO")
public class BpmDefinitionVO {
    @Schema(description = "主键id")
    private Long id;

    @Schema(description = "流程编码")
    private String flowCode;

    @Schema(description = "流程名称")
    private String flowName;

    @Schema(description = "流程版本")
    private String version;

    @Schema(description = "流程版本备注")
    private String versionAlias;

    @Schema(description = "版本状态")
    private String versionStatus;

    /**
     * 业务ID，用于关联业务系统的业务数据
     *
     * 通常为表单ID
     */
    @NotNull(message = "业务ID不能为空")
    @Schema(description = "业务ID")
    private Long businessId;
}