package com.cmsr.onebase.module.metadata.api.datasource.dto.export;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 格式验证规则导出DTO
 * 对应表: metadata_validation_format
 *
 * @author bty418
 * @date 2026-01-21
 */
@Schema(description = "格式验证规则导出DTO")
@Data
public class MetadataValidationFormatExportDTO {

    @Schema(description = "格式校验UUID")
    private String formatUuid;

    @Schema(description = "规则组UUID")
    private String groupUuid;

    @Schema(description = "实体UUID")
    private String entityUuid;

    @Schema(description = "字段UUID")
    private String fieldUuid;

    @Schema(description = "是否启用：1-启用，0-禁用")
    private Integer isEnabled;

    @Schema(description = "格式编码：REGEX/EMAIL/MOBILE/...")
    private String formatCode;

    @Schema(description = "正则表达式")
    private String regexPattern;

    @Schema(description = "正则标志：i/m/s")
    private String flags;

    @Schema(description = "提示信息")
    private String promptMessage;
}
