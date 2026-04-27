package com.cmsr.onebase.module.metadata.api.entity.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;

/**
 * 根据字段ID列表查询JDBC类型 请求DTO
 */
@Data
@Schema(description = "根据字段ID列表查询JDBC类型 请求DTO")
public class EntityFieldJdbcTypeReqDTO {

    @Schema(description = "字段ID列表", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "字段ID列表不能为空")
    private List<Long> fieldIds;
}
