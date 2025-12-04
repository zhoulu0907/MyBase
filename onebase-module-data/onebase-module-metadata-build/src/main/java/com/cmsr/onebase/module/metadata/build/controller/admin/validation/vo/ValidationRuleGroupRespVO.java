package com.cmsr.onebase.module.metadata.build.controller.admin.validation.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 管理后台 - 校验规则分组 Response VO
 *
 * @author bty418
 * @date 2025-01-25
 */
@Schema(description = "管理后台 - 校验规则分组 Response VO")
@Data
public class ValidationRuleGroupRespVO {

    @Schema(description = "规则组编号", example = "1024")
    private Long id;

    @Schema(description = "规则组UUID", example = "01onal1s-0000-0000-0000-000000000006")
    private String groupUuid;

    @Schema(description = "规则组名称", example = "客户信用评级规则")
    private String rgName;

    @Schema(description = "规则组描述", example = "用于识别高价值潜力的客户")
    private String rgDesc;

    @Schema(description = "规则组状态", example = "ACTIVE")
    private String rgStatus;

    @Schema(description = "校验方式", example = "POP")
    private String valMethod;

    @Schema(description = "弹窗提示内容", example = "不满足条件，无法提交")
    private String popPrompt;

    @Schema(description = "弹窗类型", example = "SHORT")
    private String popType;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "规则定义二维数组，外层数组元素间为OR关系，内层数组元素间为AND关系")
    private List<List<ValidationRuleDefinitionVO>> valueRules;

    @Schema(description = "校验类型：REQUIRED / UNIQUE / LENGTH / RANGE / FORMAT / CHILD_NOT_EMPTY / SELF_DEFINED", example = "REQUIRED")
    private String validationType;

}
