package com.cmsr.onebase.module.metadata.api.datasource.dto.export;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 范围验证规则导出DTO
 * 对应表: metadata_validation_range
 *
 * @author bty418
 * @date 2026-01-21
 */
@Schema(description = "范围验证规则导出DTO")
@Data
public class MetadataValidationRangeExportDTO {

    @Schema(description = "范围校验UUID")
    private String rangeUuid;

    @Schema(description = "规则组UUID")
    private String groupUuid;

    @Schema(description = "实体UUID")
    private String entityUuid;

    @Schema(description = "字段UUID")
    private String fieldUuid;

    @Schema(description = "是否启用：1-启用，0-禁用")
    private Integer isEnabled;

    @Schema(description = "范围类型：NUMBER | DATE")
    private String rangeType;

    @Schema(description = "最小值")
    private BigDecimal minValue;

    @Schema(description = "最大值")
    private BigDecimal maxValue;

    @Schema(description = "最小日期")
    private LocalDateTime minDate;

    @Schema(description = "最大日期")
    private LocalDateTime maxDate;

    @Schema(description = "是否包含最小值")
    private Integer includeMin;

    @Schema(description = "是否包含最大值")
    private Integer includeMax;

    @Schema(description = "提示信息")
    private String promptMessage;
}
