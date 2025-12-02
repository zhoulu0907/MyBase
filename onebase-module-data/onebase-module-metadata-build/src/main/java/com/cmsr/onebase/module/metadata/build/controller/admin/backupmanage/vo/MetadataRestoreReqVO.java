package com.cmsr.onebase.module.metadata.build.controller.admin.backupmanage.vo;

import com.cmsr.onebase.module.metadata.core.dal.dataobject.datasource.MetadataDatasourceDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataBusinessEntityDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataEntityFieldDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.relationship.MetadataEntityRelationshipDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.validation.MetadataValidationRuleDefinitionDO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 元数据恢复请求 VO
 *
 * @author matianyu
 * @date 2025-08-12
 */
@Schema(description = "管理后台 - 元数据恢复请求")
@Data
public class MetadataRestoreReqVO {

    @Schema(description = "目标应用ID", example = "1")
    private Long targetApplicationId;

    @Schema(description = "数据源列表", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "数据源列表不能为空")
    private List<MetadataDatasourceDO> datasourceList;

    @Schema(description = "业务实体列表", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "业务实体列表不能为空")
    private List<MetadataBusinessEntityDO> businessEntityList;

    @Schema(description = "实体字段列表", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "实体字段列表不能为空")
    private List<MetadataEntityFieldDO> entityFieldList;

    @Schema(description = "实体关系列表", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "实体关系列表不能为空")
    private List<MetadataEntityRelationshipDO> entityRelationshipList;

    @Schema(description = "校验规则列表", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "校验规则列表不能为空")
    private List<MetadataValidationRuleDefinitionDO> validationRuleList;

}
