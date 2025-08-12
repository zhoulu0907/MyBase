package com.cmsr.onebase.module.metadata.controller.admin.backupmanage.vo;

import com.cmsr.onebase.module.metadata.dal.dataobject.datasource.MetadataDatasourceDO;
import com.cmsr.onebase.module.metadata.dal.dataobject.entity.MetadataBusinessEntityDO;
import com.cmsr.onebase.module.metadata.dal.dataobject.entity.MetadataEntityFieldDO;
import com.cmsr.onebase.module.metadata.dal.dataobject.relationship.MetadataEntityRelationshipDO;
import com.cmsr.onebase.module.metadata.dal.dataobject.validation.MetadataValidationRuleDO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 元数据备份响应 VO
 *
 * @author matianyu
 * @date 2025-08-12
 */
@Schema(description = "管理后台 - 元数据备份响应")
@Data
public class MetadataBackupRespVO {

    @Schema(description = "应用ID", example = "1")
    private Long appId;

    @Schema(description = "备份时间")
    private LocalDateTime backupTime;

    @Schema(description = "数据源列表")
    private List<MetadataDatasourceDO> datasourceList;

    @Schema(description = "业务实体列表")
    private List<MetadataBusinessEntityDO> businessEntityList;

    @Schema(description = "实体字段列表")
    private List<MetadataEntityFieldDO> entityFieldList;

    @Schema(description = "实体关系列表")
    private List<MetadataEntityRelationshipDO> entityRelationshipList;

    @Schema(description = "校验规则列表")
    private List<MetadataValidationRuleDO> validationRuleList;

}
