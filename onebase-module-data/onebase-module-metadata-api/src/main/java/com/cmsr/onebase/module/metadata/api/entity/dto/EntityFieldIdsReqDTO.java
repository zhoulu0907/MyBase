package com.cmsr.onebase.module.metadata.api.entity.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

/**
 * 根据字段ID列表查询字段完整信息 请求DTO
 *
 * @author matianyu
 * @date 2025-09-25
 */
@Data
@Schema(description = "根据字段ID列表查询字段完整信息 请求DTO")
public class EntityFieldIdsReqDTO {

    @Schema(description = "字段ID列表", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "字段ID列表不能为空")
    private List<Long> fieldIds;
}
