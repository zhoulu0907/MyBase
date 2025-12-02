package com.cmsr.onebase.module.metadata.api.backupmanage.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 元数据备份响应 DTO
 *
 * @author matianyu
 * @date 2025-08-12
 */
@Schema(description = "RPC 服务 - 元数据备份响应")
@Data
public class MetadataBackupRespDTO {

    @Schema(description = "应用ID", example = "1")
    private Long applicationId;

    @Schema(description = "备份时间")
    private LocalDateTime backupTime;

    @Schema(description = "数据源列表")
    private List<MetadataDatasourceDTO> datasourceList;

    @Schema(description = "业务实体列表")
    private List<MetadataBusinessEntityDTO> businessEntityList;

    @Schema(description = "实体字段列表")
    private List<MetadataEntityFieldDTO> entityFieldList;

    @Schema(description = "实体关系列表")
    private List<MetadataEntityRelationshipDTO> entityRelationshipList;

    @Schema(description = "校验规则列表")
    private List<MetadataValidationRuleDTO> validationRuleList;

}
