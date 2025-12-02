package com.cmsr.onebase.module.metadata.api.backupmanage.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 校验规则 DTO
 *
 * @author matianyu
 * @date 2025-08-12
 */
@Schema(description = "RPC 服务 - 校验规则信息")
@Data
public class MetadataValidationRuleDTO {

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "规则名称")
    private String validationName;

    @Schema(description = "规则编码")
    private String validationCode;

    @Schema(description = "关联实体ID")
    private Long entityId;

    @Schema(description = "关联字段ID")
    private Long fieldId;

    @Schema(description = "校验条件")
    private String validationCondition;

    @Schema(description = "校验类型")
    private String validationType;

    @Schema(description = "校验比较对象")
    private String validationTargetObject;

    @Schema(description = "校验表达式")
    private String validationExpression;

    @Schema(description = "错误提示信息")
    private String errorMessage;

    @Schema(description = "校验时机")
    private String validationTiming;

    @Schema(description = "执行顺序")
    private Integer sortOrder;

    @Schema(description = "版本标识")
    private Long versionTag;

    @Schema(description = "应用ID")
    private Long applicationId;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "创建者")
    private Long creator;

    @Schema(description = "更新者")
    private Long updater;

    @Schema(description = "是否删除")
    private Long deleted;

    @Schema(description = "租户ID")
    private Long tenantId;

}
