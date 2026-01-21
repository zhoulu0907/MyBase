package com.cmsr.onebase.module.metadata.api.datasource.dto.export;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 长度验证规则导出DTO
 * 对应表: metadata_validation_length
 *
 * @author bty418
 * @date 2026-01-21
 */
@Schema(description = "长度验证规则导出DTO")
@Data
public class MetadataValidationLengthExportDTO {

    @Schema(description = "长度校验UUID")
    private String lengthUuid;

    @Schema(description = "规则组UUID")
    private String groupUuid;

    @Schema(description = "实体UUID")
    private String entityUuid;

    @Schema(description = "字段UUID")
    private String fieldUuid;

    @Schema(description = "是否启用：1-启用，0-禁用")
    private Integer isEnabled;

    @Schema(description = "最小长度")
    private Integer minLength;

    @Schema(description = "最大长度")
    private Integer maxLength;

    @Schema(description = "校验前是否去空格")
    private Integer trimBefore;

    @Schema(description = "提示信息")
    private String promptMessage;
}
