package com.cmsr.onebase.module.metadata.api.backupmanage.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 元数据恢复请求 DTO
 *
 * @author matianyu
 * @date 2025-08-12
 */
@Schema(description = "RPC 服务 - 元数据恢复请求")
@Data
public class MetadataRestoreReqDTO {

    @Schema(description = "目标应用ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long targetApplicationId;

    @Schema(description = "数据源列表", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<MetadataDatasourceDTO> datasourceList;

    @Schema(description = "业务实体列表", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<MetadataBusinessEntityDTO> businessEntityList;

    @Schema(description = "实体字段列表", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<MetadataEntityFieldDTO> entityFieldList;

    @Schema(description = "实体关系列表", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<MetadataEntityRelationshipDTO> entityRelationshipList;

    @Schema(description = "校验规则列表", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<MetadataValidationRuleDTO> validationRuleList;

}
