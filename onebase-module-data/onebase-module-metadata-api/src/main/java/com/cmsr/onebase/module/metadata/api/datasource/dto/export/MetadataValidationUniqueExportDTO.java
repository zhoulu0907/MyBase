package com.cmsr.onebase.module.metadata.api.datasource.dto.export;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 唯一验证规则导出DTO
 * 对应表: metadata_validation_unique
 *
 * @author bty418
 * @date 2026-01-21
 */
@Schema(description = "唯一验证规则导出DTO")
@Data
public class MetadataValidationUniqueExportDTO {

    @Schema(description = "唯一校验UUID")
    private String uniqueUuid;

    @Schema(description = "规则组UUID")
    private String groupUuid;

    @Schema(description = "实体UUID")
    private String entityUuid;

    @Schema(description = "字段UUID")
    private String fieldUuid;

    @Schema(description = "是否启用：1-启用，0-禁用")
    private Integer isEnabled;

    @Schema(description = "提示信息")
    private String promptMessage;
}
