package com.cmsr.onebase.module.metadata.api.datasource.dto.export;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 验证规则定义导出DTO
 * 对应表: metadata_validation_rule_definition
 *
 * @author bty418
 * @date 2026-01-21
 */
@Schema(description = "验证规则定义导出DTO")
@Data
public class MetadataValidationRuleDefinitionExportDTO {

    @Schema(description = "规则定义UUID")
    private String definitionUuid;

    @Schema(description = "所属规则组UUID")
    private String groupUuid;

    @Schema(description = "父规则UUID")
    private String parentRuleUuid;

    @Schema(description = "关联的业务实体UUID")
    private String entityUuid;

    @Schema(description = "关联的实体字段UUID")
    private String fieldUuid;

    @Schema(description = "逻辑类型：LOGIC/CONDITION")
    private String logicType;

    @Schema(description = "操作符")
    private String operator;

    @Schema(description = "逻辑操作符：AND/OR")
    private String logicOperator;

    @Schema(description = "条件字段编码")
    private String fieldCode;

    @Schema(description = "值类型：VARIABLE/STATIC/FORMULA")
    private String valueType;

    @Schema(description = "条件值引用")
    private Long fieldValue;

    @Schema(description = "条件值引用2")
    private Long fieldValue2;

    @Schema(description = "状态：1-激活，0-非激活")
    private Integer status;
}
