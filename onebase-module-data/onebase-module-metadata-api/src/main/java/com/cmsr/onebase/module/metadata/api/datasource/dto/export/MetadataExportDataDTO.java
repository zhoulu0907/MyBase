package com.cmsr.onebase.module.metadata.api.datasource.dto.export;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 元数据导出数据DTO - 平铺式结构
 * 包含16张元数据表的数据，便于导入导出操作
 *
 * @author bty418
 * @date 2026-01-21
 */
@Schema(description = "元数据导出数据DTO")
@Data
public class MetadataExportDataDTO {

    @Schema(description = "数据源 (metadata_datasource)")
    private MetadataDatasourceExportDTO datasource;

    @Schema(description = "应用与数据源关联 (metadata_app_and_datasource)")
    private MetadataAppDatasourceExportDTO appDatasource;

    @Schema(description = "业务实体列表 (metadata_business_entity)")
    private List<MetadataBusinessEntityExportDTO> businessEntities = new ArrayList<>();

    @Schema(description = "实体字段列表 (metadata_entity_field)")
    private List<MetadataEntityFieldExportDTO> entityFields = new ArrayList<>();

    @Schema(description = "字段选项列表 (metadata_entity_field_option)")
    private List<MetadataEntityFieldOptionExportDTO> fieldOptions = new ArrayList<>();

    @Schema(description = "实体关系列表 (metadata_entity_relationship)")
    private List<MetadataEntityRelationshipExportDTO> entityRelationships = new ArrayList<>();

    @Schema(description = "自动编号配置列表 (metadata_auto_number_config)")
    private List<MetadataAutoNumberConfigExportDTO> autoNumberConfigs = new ArrayList<>();

    @Schema(description = "自动编号规则项列表 (metadata_auto_number_rule_item)")
    private List<MetadataAutoNumberRuleItemExportDTO> autoNumberRuleItems = new ArrayList<>();

    @Schema(description = "验证规则组列表 (metadata_validation_rule_group)")
    private List<MetadataValidationRuleGroupExportDTO> validationRuleGroups = new ArrayList<>();

    @Schema(description = "验证规则定义列表 (metadata_validation_rule_definition)")
    private List<MetadataValidationRuleDefinitionExportDTO> validationRuleDefinitions = new ArrayList<>();

    @Schema(description = "必填验证规则列表 (metadata_validation_required)")
    private List<MetadataValidationRequiredExportDTO> validationRequireds = new ArrayList<>();

    @Schema(description = "唯一验证规则列表 (metadata_validation_unique)")
    private List<MetadataValidationUniqueExportDTO> validationUniques = new ArrayList<>();

    @Schema(description = "长度验证规则列表 (metadata_validation_length)")
    private List<MetadataValidationLengthExportDTO> validationLengths = new ArrayList<>();

    @Schema(description = "范围验证规则列表 (metadata_validation_range)")
    private List<MetadataValidationRangeExportDTO> validationRanges = new ArrayList<>();

    @Schema(description = "格式验证规则列表 (metadata_validation_format)")
    private List<MetadataValidationFormatExportDTO> validationFormats = new ArrayList<>();

    @Schema(description = "子表非空验证规则列表 (metadata_validation_child_not_empty)")
    private List<MetadataValidationChildNotEmptyExportDTO> validationChildNotEmptys = new ArrayList<>();
}
