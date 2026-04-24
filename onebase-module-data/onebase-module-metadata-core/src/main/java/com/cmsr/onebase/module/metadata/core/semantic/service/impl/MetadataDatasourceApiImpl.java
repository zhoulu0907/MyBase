package com.cmsr.onebase.module.metadata.core.semantic.service.impl;

import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import com.cmsr.onebase.module.metadata.api.datasource.MetadataDatasourceApi;
import com.cmsr.onebase.module.metadata.api.datasource.dto.DatasourceCreateDefaultReqDTO;
import com.cmsr.onebase.module.metadata.api.datasource.dto.DatasourceImportReqDTO;
import com.cmsr.onebase.module.metadata.api.datasource.dto.DatasourceSaveReqDTO;
import com.cmsr.onebase.module.metadata.api.datasource.dto.export.*;
import com.cmsr.onebase.module.metadata.core.config.MetadataConfig;
import com.cmsr.onebase.module.metadata.core.dal.database.*;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.datasource.MetadataAppAndDatasourceDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.datasource.MetadataDatasourceDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.FieldTypeMappingDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataBusinessEntityDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataEntityFieldDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.field.MetadataEntityFieldOptionDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.number.MetadataAutoNumberConfigDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.number.MetadataAutoNumberRuleItemDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.relationship.MetadataEntityRelationshipDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.validation.*;
import com.cmsr.onebase.module.metadata.core.enums.MetadataBooleanLiteralEnum;
import com.cmsr.onebase.module.metadata.core.enums.MetadataDataTypeCodeEnum;
import com.cmsr.onebase.module.metadata.core.enums.MetadataDefaultValueKeywordEnum;
import com.cmsr.onebase.module.metadata.core.enums.MetadataDatasourceTypeEnum;
import com.cmsr.onebase.module.metadata.core.service.datasource.MetadataDatasourceCoreService;
import com.mybatisflex.core.query.QueryWrapper;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 数据源管理 API 默认实现
 *
 * @author matianyu
 * @date 2025-09-12
 */
@Service
@Slf4j
public class MetadataDatasourceApiImpl implements MetadataDatasourceApi {

    @Resource
    private MetadataDatasourceCoreService metadataDatasourceCoreService;

    @Resource
    private MetadataConfig metadataConfig;

    // 16张表对应的Repository
    @Resource
    private MetadataDatasourceRepository metadataDatasourceRepository;
    @Resource
    private MetadataAppAndDatasourceRepository metadataAppAndDatasourceRepository;
    @Resource
    private MetadataBusinessEntityRepository metadataBusinessEntityRepository;
    @Resource
    private MetadataEntityFieldRepository metadataEntityFieldRepository;
    @Resource
    private MetadataEntityFieldOptionRepository metadataEntityFieldOptionRepository;
    @Resource
    private MetadataEntityRelationshipRepository metadataEntityRelationshipRepository;
    @Resource
    private MetadataAutoNumberConfigRepository metadataAutoNumberConfigRepository;
    @Resource
    private MetadataAutoNumberRuleItemRepository metadataAutoNumberRuleItemRepository;
    @Resource
    private MetadataValidationRuleGroupRepository metadataValidationRuleGroupRepository;
    @Resource
    private MetadataValidationRuleDefinitionRepository metadataValidationRuleDefinitionRepository;
    @Resource
    private MetadataValidationRequiredRepository metadataValidationRequiredRepository;
    @Resource
    private MetadataValidationUniqueRepository metadataValidationUniqueRepository;
    @Resource
    private MetadataValidationLengthRepository metadataValidationLengthRepository;
    @Resource
    private MetadataValidationRangeRepository metadataValidationRangeRepository;
    @Resource
    private MetadataValidationFormatRepository metadataValidationFormatRepository;
    @Resource
    private MetadataValidationChildNotEmptyRepository metadataValidationChildNotEmptyRepository;
    @Resource
    private FieldTypeMappingRepository fieldTypeMappingRepository;

    @Resource
    private TemporaryDatasourceService temporaryDatasourceService;

    @Override
    @Operation(summary = "创建默认数据源")
    public Long createDefaultDatasource(@Valid @RequestBody DatasourceCreateDefaultReqDTO reqDTO) {
        try {
            // 从配置类中读取默认数据源配置
            Map<String, Object> config = new HashMap<>();
            config.put("host", metadataConfig.getDefaultDatasourceHost());
            config.put("port", metadataConfig.getDefaultDatasourcePort());
            config.put("database", metadataConfig.getDefaultDatasourceDatabase());
            config.put("username", metadataConfig.getDefaultDatasourceUsername());
            config.put("password", metadataConfig.getDefaultDatasourcePassword());

            // 将配置转换为JSON字符串
            String configJson = String.format(
                    "{\"host\":\"%s\",\"port\":%d,\"database\":\"%s\",\"username\":\"%s\",\"password\":\"%s\"}",
                    config.get("host"), config.get("port"), config.get("database"),
                    config.get("username"), config.get("password")
            );

            // 调用 core 模块的基础服务，使用MetadataConfig中配置的数据源类型
            Long datasourceId = metadataDatasourceCoreService.createDefaultDatasource(
                    reqDTO.getApplicationId(),
                    reqDTO.getAppUid(),
                    metadataConfig.getDefaultDatasourceType(),  // 使用配置的数据源类型
                    configJson
            );

            log.info("创建默认数据源成功，数据源ID: {}，应用ID: {}，类型: {}",
                    datasourceId, reqDTO.getApplicationId(), metadataConfig.getDefaultDatasourceType());
            return datasourceId;
        } catch (Exception e) {
            log.error("创建默认数据源失败", e);
            throw new RuntimeException("创建默认数据源失败: " + e.getMessage());
        }
    }

    @Override
    @Operation(summary = "创建数据源")
    public Long createDatasource(@Valid @RequestBody DatasourceSaveReqDTO reqDTO) {
        try {
            // 将config转换为JSON字符串
            String configJson = reqDTO.getConfig() != null ? reqDTO.getConfig().toString() : "{}";

            // 创建数据源DO
            MetadataDatasourceDO datasource = MetadataDatasourceDO.builder()
                    .datasourceName(reqDTO.getName())
                    .code(reqDTO.getCode())
                    .datasourceType(reqDTO.getDatasourceType())
                    .config(configJson)
                    .description(reqDTO.getRemark())
                    .versionTag(1L)
                    .datasourceOrigin(1)
                    .build();

            // 调用 core 模块的基础服务创建数据源
            Long datasourceId = metadataDatasourceCoreService.createDatasource(datasource);

            // 创建关联关系（使用数据源UUID）
            metadataDatasourceCoreService.createAppDatasourceRelation(
                    reqDTO.getApplicationId(),
                    datasource.getDatasourceUuid(),
                    reqDTO.getDatasourceType(),
                    reqDTO.getAppUid()
            );

            log.info("创建数据源成功，数据源ID: {}，应用ID: {}", datasourceId, reqDTO.getApplicationId());
            return datasourceId;
        } catch (Exception e) {
            log.error("创建数据源失败", e);
            throw new RuntimeException("创建数据源失败: " + e.getMessage());
        }
    }

    @Override
    @Operation(summary = "获取数据源信息")
    public Object getDatasource(@PathVariable("id") Long id) {
        try {
            // 调用 core 模块的基础服务
            MetadataDatasourceDO datasource = metadataDatasourceCoreService.getDatasource(id);
            log.debug("获取数据源信息成功，ID: {}", id);
            return datasource;
        } catch (Exception e) {
            log.error("获取数据源信息失败，ID: {}", id, e);
            throw new RuntimeException("获取数据源信息失败: " + e.getMessage());
        }
    }

    @Override
    public Object exportDatasource(Long applicationId, Long versionTag) {
        log.info("开始导出数据源，applicationId: {}, versionTag: {}", applicationId, versionTag);
        MetadataExportDataDTO exportData = new MetadataExportDataDTO();

        // 1. 查询数据源
        QueryWrapper datasourceQuery = new QueryWrapper();
        datasourceQuery.eq(MetadataDatasourceDO::getApplicationId, applicationId);
        datasourceQuery.eq(MetadataDatasourceDO::getVersionTag, versionTag);
        MetadataDatasourceDO datasourceDO = metadataDatasourceRepository.getOne(datasourceQuery);
        if (datasourceDO == null) {
            log.warn("未找到数据源，applicationId: {}, versionTag: {}", applicationId, versionTag);
            return exportData;
        }
        String datasourceUuid = datasourceDO.getDatasourceUuid();

        // 转换为ExportDTO - datasourceName/code/datasourceType/config/description设为null
        MetadataDatasourceExportDTO datasourceExportDTO = new MetadataDatasourceExportDTO();
        datasourceExportDTO.setDatasourceUuid(datasourceUuid);
        datasourceExportDTO.setDatasourceOrigin(datasourceDO.getDatasourceOrigin());
        exportData.setDatasource(datasourceExportDTO);

        // 2. 查询应用与数据源关联
        QueryWrapper appDatasourceQuery = new QueryWrapper();
        appDatasourceQuery.eq(MetadataAppAndDatasourceDO::getApplicationId, applicationId);
        appDatasourceQuery.eq(MetadataAppAndDatasourceDO::getVersionTag, versionTag);
        MetadataAppAndDatasourceDO appDatasourceDO = metadataAppAndDatasourceRepository.getOne(appDatasourceQuery);
        if (appDatasourceDO != null) {
            MetadataAppDatasourceExportDTO appDatasourceExportDTO = new MetadataAppDatasourceExportDTO();
            appDatasourceExportDTO.setDatasourceUuid(appDatasourceDO.getDatasourceUuid());
            appDatasourceExportDTO.setDatasourceType(appDatasourceDO.getDatasourceType());
            exportData.setAppDatasource(appDatasourceExportDTO);
        }

        // 3. 查询业务实体
        QueryWrapper entityQuery = new QueryWrapper();
        entityQuery.eq(MetadataBusinessEntityDO::getApplicationId, applicationId);
        entityQuery.eq(MetadataBusinessEntityDO::getVersionTag, versionTag);
        java.util.List<MetadataBusinessEntityDO> entities = metadataBusinessEntityRepository.list(entityQuery);
        java.util.List<String> entityUuids = new java.util.ArrayList<>();
        for (MetadataBusinessEntityDO entity : entities) {
            entityUuids.add(entity.getEntityUuid());
            MetadataBusinessEntityExportDTO entityExportDTO = new MetadataBusinessEntityExportDTO();
            entityExportDTO.setEntityUuid(entity.getEntityUuid());
            entityExportDTO.setDisplayName(entity.getDisplayName());
            entityExportDTO.setCode(entity.getCode());
            entityExportDTO.setEntityType(entity.getEntityType());
            entityExportDTO.setDescription(entity.getDescription());
            entityExportDTO.setDatasourceUuid(entity.getDatasourceUuid());
            // tableName去掉前5位（app_uid_前缀）
            String tableName = entity.getTableName();
            if (tableName != null && tableName.length() > 5) {
                entityExportDTO.setTableNameSuffix(tableName.substring(5));
            } else {
                entityExportDTO.setTableNameSuffix(tableName);
            }
            entityExportDTO.setDisplayConfig(entity.getDisplayConfig());
            entityExportDTO.setStatus(entity.getStatus());
            exportData.getBusinessEntities().add(entityExportDTO);
        }

        if (CollectionUtils.isEmpty(entityUuids)) {
            log.info("未找到业务实体，导出完成");
            return exportData;
        }

        // 4. 查询实体字段
        QueryWrapper fieldQuery = new QueryWrapper();
        fieldQuery.eq(MetadataEntityFieldDO::getApplicationId, applicationId);
        fieldQuery.eq(MetadataEntityFieldDO::getVersionTag, versionTag);
        java.util.List<MetadataEntityFieldDO> fields = metadataEntityFieldRepository.list(fieldQuery);
        java.util.List<String> fieldUuids = new java.util.ArrayList<>();
        for (MetadataEntityFieldDO field : fields) {
            fieldUuids.add(field.getFieldUuid());
            MetadataEntityFieldExportDTO fieldExportDTO = convertFieldToExportDTO(field);
            exportData.getEntityFields().add(fieldExportDTO);
        }

        // 5. 查询字段选项
        if (CollectionUtils.isNotEmpty(fieldUuids)) {
            QueryWrapper optionQuery = new QueryWrapper();
            optionQuery.eq(MetadataEntityFieldOptionDO::getApplicationId, applicationId);
            optionQuery.eq(MetadataEntityFieldOptionDO::getVersionTag, versionTag);
            java.util.List<MetadataEntityFieldOptionDO> options = metadataEntityFieldOptionRepository.list(optionQuery);
            for (MetadataEntityFieldOptionDO option : options) {
                MetadataEntityFieldOptionExportDTO optionExportDTO = new MetadataEntityFieldOptionExportDTO();
                optionExportDTO.setOptionUuid(option.getOptionUuid());
                optionExportDTO.setFieldUuid(option.getFieldUuid());
                optionExportDTO.setOptionValue(option.getOptionValue());
                optionExportDTO.setOptionLabel(option.getOptionLabel());
                optionExportDTO.setOptionOrder(option.getOptionOrder());
                optionExportDTO.setIsEnabled(option.getIsEnabled());
                optionExportDTO.setDescription(option.getDescription());
                exportData.getFieldOptions().add(optionExportDTO);
            }
        }

        // 6. 查询实体关系
        QueryWrapper relationshipQuery = new QueryWrapper();
        relationshipQuery.eq(MetadataEntityRelationshipDO::getApplicationId, applicationId);
        relationshipQuery.eq(MetadataEntityRelationshipDO::getVersionTag, versionTag);
        java.util.List<MetadataEntityRelationshipDO> relationships = metadataEntityRelationshipRepository.list(relationshipQuery);
        for (MetadataEntityRelationshipDO relationship : relationships) {
            MetadataEntityRelationshipExportDTO relExportDTO = new MetadataEntityRelationshipExportDTO();
            relExportDTO.setRelationshipUuid(relationship.getRelationshipUuid());
            relExportDTO.setRelationName(relationship.getRelationName());
            relExportDTO.setSourceEntityUuid(relationship.getSourceEntityUuid());
            relExportDTO.setSourceFieldUuid(relationship.getSourceFieldUuid());
            relExportDTO.setTargetEntityUuid(relationship.getTargetEntityUuid());
            relExportDTO.setTargetFieldUuid(relationship.getTargetFieldUuid());
            relExportDTO.setSelectFieldUuid(relationship.getSelectFieldUuid());
            relExportDTO.setRelationshipType(relationship.getRelationshipType());
            relExportDTO.setCascadeType(relationship.getCascadeType());
            relExportDTO.setDescription(relationship.getDescription());
            exportData.getEntityRelationships().add(relExportDTO);
        }

        // 7. 查询自动编号配置
        QueryWrapper autoNumberQuery = new QueryWrapper();
        autoNumberQuery.eq(MetadataAutoNumberConfigDO::getApplicationId, applicationId);
        autoNumberQuery.eq(MetadataAutoNumberConfigDO::getVersionTag, versionTag);
        java.util.List<MetadataAutoNumberConfigDO> autoNumbers = metadataAutoNumberConfigRepository.list(autoNumberQuery);
        java.util.List<String> configUuids = new java.util.ArrayList<>();
        for (MetadataAutoNumberConfigDO autoNumber : autoNumbers) {
            configUuids.add(autoNumber.getConfigUuid());
            MetadataAutoNumberConfigExportDTO autoExportDTO = new MetadataAutoNumberConfigExportDTO();
            autoExportDTO.setConfigUuid(autoNumber.getConfigUuid());
            autoExportDTO.setFieldUuid(autoNumber.getFieldUuid());
            autoExportDTO.setNumberMode(autoNumber.getNumberMode());
            autoExportDTO.setDigitWidth(autoNumber.getDigitWidth());
            autoExportDTO.setOverflowContinue(autoNumber.getOverflowContinue());
            autoExportDTO.setInitialValue(autoNumber.getInitialValue());
            autoExportDTO.setResetCycle(autoNumber.getResetCycle());
            autoExportDTO.setResetOnInitialChange(autoNumber.getResetOnInitialChange());
            autoExportDTO.setIsEnabled(autoNumber.getIsEnabled());
            autoExportDTO.setSequenceOrder(autoNumber.getSequenceOrder());
            exportData.getAutoNumberConfigs().add(autoExportDTO);
        }

        // 8. 查询自动编号规则项
        if (CollectionUtils.isNotEmpty(configUuids)) {
            QueryWrapper ruleItemQuery = new QueryWrapper();
            ruleItemQuery.in(MetadataAutoNumberRuleItemDO::getConfigUuid, configUuids);
            java.util.List<MetadataAutoNumberRuleItemDO> ruleItems = metadataAutoNumberRuleItemRepository.list(ruleItemQuery);
            for (MetadataAutoNumberRuleItemDO item : ruleItems) {
                MetadataAutoNumberRuleItemExportDTO itemExportDTO = new MetadataAutoNumberRuleItemExportDTO();
                itemExportDTO.setRuleItemUuid(item.getRuleItemUuid());
                itemExportDTO.setConfigUuid(item.getConfigUuid());
                itemExportDTO.setItemType(item.getItemType());
                itemExportDTO.setItemOrder(item.getItemOrder());
                itemExportDTO.setFormat(item.getFormat());
                itemExportDTO.setTextValue(item.getTextValue());
                itemExportDTO.setRefFieldUuid(item.getRefFieldUuid());
                itemExportDTO.setIsEnabled(item.getIsEnabled());
                exportData.getAutoNumberRuleItems().add(itemExportDTO);
            }
        }

        // 9-16. 查询验证规则
        exportValidationRules(exportData, applicationId, versionTag, fieldUuids);

        log.info("导出数据源完成，共导出: {} 个实体, {} 个字段", entities.size(), fields.size());
        return exportData;
    }

    /**
     * 导出验证规则相关数据
     */
    private void exportValidationRules(MetadataExportDataDTO exportData, Long applicationId, Long versionTag, java.util.List<String> fieldUuids) {
        if (CollectionUtils.isEmpty(fieldUuids)) {
            return;
        }

        // 验证规则组
        QueryWrapper ruleGroupQuery = new QueryWrapper();
        ruleGroupQuery.eq(MetadataValidationRuleGroupDO::getApplicationId, applicationId);
        ruleGroupQuery.eq(MetadataValidationRuleGroupDO::getVersionTag, versionTag);
        java.util.List<MetadataValidationRuleGroupDO> ruleGroups = metadataValidationRuleGroupRepository.list(ruleGroupQuery);
        for (MetadataValidationRuleGroupDO rg : ruleGroups) {
            MetadataValidationRuleGroupExportDTO rgDTO = new MetadataValidationRuleGroupExportDTO();
            rgDTO.setGroupUuid(rg.getGroupUuid());
            rgDTO.setRgName(rg.getRgName());
            rgDTO.setRgDesc(rg.getRgDesc());
            rgDTO.setRgStatus(rg.getRgStatus());
            rgDTO.setValMethod(rg.getValMethod());
            rgDTO.setPopPrompt(rg.getPopPrompt());
            rgDTO.setPopType(rg.getPopType());
            rgDTO.setValidationType(rg.getValidationType());
            rgDTO.setEntityUuid(rg.getEntityUuid());
            exportData.getValidationRuleGroups().add(rgDTO);
        }

        // 验证规则定义
        QueryWrapper ruleDefQuery = new QueryWrapper();
        ruleDefQuery.eq(MetadataValidationRuleDefinitionDO::getApplicationId, applicationId);
        ruleDefQuery.eq(MetadataValidationRuleDefinitionDO::getVersionTag, versionTag);
        java.util.List<MetadataValidationRuleDefinitionDO> ruleDefs = metadataValidationRuleDefinitionRepository.list(ruleDefQuery);
        for (MetadataValidationRuleDefinitionDO rd : ruleDefs) {
            MetadataValidationRuleDefinitionExportDTO rdDTO = new MetadataValidationRuleDefinitionExportDTO();
            rdDTO.setDefinitionUuid(rd.getDefinitionUuid());
            rdDTO.setGroupUuid(rd.getGroupUuid());
            rdDTO.setParentRuleUuid(rd.getParentRuleUuid());
            rdDTO.setEntityUuid(rd.getEntityUuid());
            rdDTO.setFieldUuid(rd.getFieldUuid());
            rdDTO.setLogicType(rd.getLogicType());
            rdDTO.setOperator(rd.getOperator());
            rdDTO.setLogicOperator(rd.getLogicOperator());
            rdDTO.setFieldCode(rd.getFieldCode());
            rdDTO.setValueType(rd.getValueType());
            rdDTO.setFieldValue(rd.getFieldValue());
            rdDTO.setFieldValue2(rd.getFieldValue2());
            rdDTO.setStatus(rd.getStatus());
            exportData.getValidationRuleDefinitions().add(rdDTO);
        }

        // 必填验证
        QueryWrapper requiredQuery = new QueryWrapper();
        requiredQuery.in(MetadataValidationRequiredDO::getFieldUuid, fieldUuids);
        java.util.List<MetadataValidationRequiredDO> requireds = metadataValidationRequiredRepository.list(requiredQuery);
        for (MetadataValidationRequiredDO r : requireds) {
            MetadataValidationRequiredExportDTO rDTO = new MetadataValidationRequiredExportDTO();
            rDTO.setRequiredUuid(r.getRequiredUuid());
            rDTO.setGroupUuid(r.getGroupUuid());
            rDTO.setEntityUuid(r.getEntityUuid());
            rDTO.setFieldUuid(r.getFieldUuid());
            rDTO.setIsEnabled(r.getIsEnabled());
            rDTO.setPromptMessage(r.getPromptMessage());
            exportData.getValidationRequireds().add(rDTO);
        }

        // 唯一验证
        QueryWrapper uniqueQuery = new QueryWrapper();
        uniqueQuery.in(MetadataValidationUniqueDO::getFieldUuid, fieldUuids);
        java.util.List<MetadataValidationUniqueDO> uniques = metadataValidationUniqueRepository.list(uniqueQuery);
        for (MetadataValidationUniqueDO u : uniques) {
            MetadataValidationUniqueExportDTO uDTO = new MetadataValidationUniqueExportDTO();
            uDTO.setUniqueUuid(u.getUniqueUuid());
            uDTO.setGroupUuid(u.getGroupUuid());
            uDTO.setEntityUuid(u.getEntityUuid());
            uDTO.setFieldUuid(u.getFieldUuid());
            uDTO.setIsEnabled(u.getIsEnabled());
            uDTO.setPromptMessage(u.getPromptMessage());
            exportData.getValidationUniques().add(uDTO);
        }

        // 长度验证
        QueryWrapper lengthQuery = new QueryWrapper();
        lengthQuery.in(MetadataValidationLengthDO::getFieldUuid, fieldUuids);
        java.util.List<MetadataValidationLengthDO> lengths = metadataValidationLengthRepository.list(lengthQuery);
        for (MetadataValidationLengthDO l : lengths) {
            MetadataValidationLengthExportDTO lDTO = new MetadataValidationLengthExportDTO();
            lDTO.setLengthUuid(l.getLengthUuid());
            lDTO.setGroupUuid(l.getGroupUuid());
            lDTO.setEntityUuid(l.getEntityUuid());
            lDTO.setFieldUuid(l.getFieldUuid());
            lDTO.setIsEnabled(l.getIsEnabled());
            lDTO.setMinLength(l.getMinLength());
            lDTO.setMaxLength(l.getMaxLength());
            lDTO.setTrimBefore(l.getTrimBefore());
            lDTO.setPromptMessage(l.getPromptMessage());
            exportData.getValidationLengths().add(lDTO);
        }

        // 范围验证
        QueryWrapper rangeQuery = new QueryWrapper();
        rangeQuery.in(MetadataValidationRangeDO::getFieldUuid, fieldUuids);
        java.util.List<MetadataValidationRangeDO> ranges = metadataValidationRangeRepository.list(rangeQuery);
        for (MetadataValidationRangeDO r : ranges) {
            MetadataValidationRangeExportDTO rDTO = new MetadataValidationRangeExportDTO();
            rDTO.setRangeUuid(r.getRangeUuid());
            rDTO.setGroupUuid(r.getGroupUuid());
            rDTO.setEntityUuid(r.getEntityUuid());
            rDTO.setFieldUuid(r.getFieldUuid());
            rDTO.setIsEnabled(r.getIsEnabled());
            rDTO.setRangeType(r.getRangeType());
            rDTO.setMinValue(r.getMinValue());
            rDTO.setMaxValue(r.getMaxValue());
            rDTO.setMinDate(r.getMinDate());
            rDTO.setMaxDate(r.getMaxDate());
            rDTO.setIncludeMin(r.getIncludeMin());
            rDTO.setIncludeMax(r.getIncludeMax());
            rDTO.setPromptMessage(r.getPromptMessage());
            exportData.getValidationRanges().add(rDTO);
        }

        // 格式验证
        QueryWrapper formatQuery = new QueryWrapper();
        formatQuery.in(MetadataValidationFormatDO::getFieldUuid, fieldUuids);
        java.util.List<MetadataValidationFormatDO> formats = metadataValidationFormatRepository.list(formatQuery);
        for (MetadataValidationFormatDO f : formats) {
            MetadataValidationFormatExportDTO fDTO = new MetadataValidationFormatExportDTO();
            fDTO.setFormatUuid(f.getFormatUuid());
            fDTO.setGroupUuid(f.getGroupUuid());
            fDTO.setEntityUuid(f.getEntityUuid());
            fDTO.setFieldUuid(f.getFieldUuid());
            fDTO.setIsEnabled(f.getIsEnabled());
            fDTO.setFormatCode(f.getFormatCode());
            fDTO.setRegexPattern(f.getRegexPattern());
            fDTO.setFlags(f.getFlags());
            fDTO.setPromptMessage(f.getPromptMessage());
            exportData.getValidationFormats().add(fDTO);
        }

        // 子表非空验证
        QueryWrapper childNotEmptyQuery = new QueryWrapper();
        childNotEmptyQuery.in(MetadataValidationChildNotEmptyDO::getFieldUuid, fieldUuids);
        java.util.List<MetadataValidationChildNotEmptyDO> childNotEmptys = metadataValidationChildNotEmptyRepository.list(childNotEmptyQuery);
        for (MetadataValidationChildNotEmptyDO c : childNotEmptys) {
            MetadataValidationChildNotEmptyExportDTO cDTO = new MetadataValidationChildNotEmptyExportDTO();
            cDTO.setChildNotEmptyUuid(c.getChildNotEmptyUuid());
            cDTO.setGroupUuid(c.getGroupUuid());
            cDTO.setEntityUuid(c.getEntityUuid());
            cDTO.setFieldUuid(c.getFieldUuid());
            cDTO.setChildEntityUuid(c.getChildEntityUuid());
            cDTO.setIsEnabled(c.getIsEnabled());
            cDTO.setMinRows(c.getMinRows());
            cDTO.setPromptMessage(c.getPromptMessage());
            exportData.getValidationChildNotEmptys().add(cDTO);
        }
    }

    /**
     * 转换字段DO为ExportDTO
     */
    private MetadataEntityFieldExportDTO convertFieldToExportDTO(MetadataEntityFieldDO field) {
        MetadataEntityFieldExportDTO dto = new MetadataEntityFieldExportDTO();
        dto.setFieldUuid(field.getFieldUuid());
        dto.setEntityUuid(field.getEntityUuid());
        dto.setFieldName(field.getFieldName());
        dto.setDisplayName(field.getDisplayName());
        dto.setFieldType(field.getFieldType());
        dto.setDataLength(field.getDataLength());
        dto.setDecimalPlaces(field.getDecimalPlaces());
        dto.setDefaultValue(field.getDefaultValue());
        dto.setDescription(field.getDescription());
        dto.setIsSystemField(field.getIsSystemField());
        dto.setIsPrimaryKey(field.getIsPrimaryKey());
        dto.setIsRequired(field.getIsRequired());
        dto.setIsUnique(field.getIsUnique());
        dto.setSortOrder(field.getSortOrder());
        dto.setValidationRules(field.getValidationRules());
        dto.setStatus(field.getStatus());
        dto.setFieldCode(field.getFieldCode());
        dto.setDictTypeId(field.getDictTypeId());
        return dto;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void importDatasource(Long newApplicationId, String appUid, Long tenantId, Long versionTag, Object importData, DatasourceImportReqDTO reqDTO) {
        // 将Object转换为MetadataExportDataDTO
        MetadataExportDataDTO exportData;
        if (importData == null) {
            log.warn("导入数据为空，跳过导入");
            return;
        } else if (importData instanceof MetadataExportDataDTO) {
            exportData = (MetadataExportDataDTO) importData;
        } else {
            String json = JsonUtils.toJsonString(importData);
            exportData = JsonUtils.parseObject(json, MetadataExportDataDTO.class);
        }
        if (exportData == null) {
            log.warn("解析导入数据失败，跳过导入");
            return;
        }

        log.info("开始导入数据源，newApplicationId: {}, appUid: {}, tenantId: {}, versionTag: {}",
                newApplicationId, appUid, tenantId, versionTag);

        if (exportData.getDatasource() == null) {
            log.warn("数据源信息为空，跳过导入");
            return;
        }

        // 先删除已有的编辑态数据，避免唯一约束冲突
        deleteApplicationVersionData(newApplicationId, versionTag);

        java.time.LocalDateTime now = java.time.LocalDateTime.now();

        // 1. 导入数据源 - 使用默认配置创建
        MetadataDatasourceExportDTO dsExport = exportData.getDatasource();
        String configJsonDefault = buildDefaultDatasourceConfig();

        MetadataDatasourceDO datasourceDO = MetadataDatasourceDO.builder()
                .datasourceUuid(dsExport.getDatasourceUuid())
                .datasourceName("默认数据源")
                .code(generateDatasourceCode())
                .datasourceType(metadataConfig.getDefaultDatasourceType())
                .config(configJsonDefault)
                .description("导入创建的默认数据源")
                .versionTag(versionTag)
                .applicationId(newApplicationId)
                .datasourceOrigin(dsExport.getDatasourceOrigin())
                .build();
        datasourceDO.setTenantId(tenantId);
        metadataDatasourceRepository.save(datasourceDO);

        // 2. 导入应用与数据源关联
        if (exportData.getAppDatasource() != null) {
            MetadataAppDatasourceExportDTO appDsExport = exportData.getAppDatasource();
            MetadataAppAndDatasourceDO appDatasourceDO = new MetadataAppAndDatasourceDO();
            appDatasourceDO.setApplicationId(newApplicationId);
            appDatasourceDO.setDatasourceUuid(appDsExport.getDatasourceUuid());
            appDatasourceDO.setDatasourceType(appDsExport.getDatasourceType());
            appDatasourceDO.setVersionTag(versionTag);
            appDatasourceDO.setAppUid(appUid);
            appDatasourceDO.setTenantId(tenantId);
            metadataAppAndDatasourceRepository.save(appDatasourceDO);
        }

        // 3. 导入业务实体
        java.util.List<MetadataBusinessEntityDO> entityDOs = new java.util.ArrayList<>();
        for (MetadataBusinessEntityExportDTO entityExport : exportData.getBusinessEntities()) {
            MetadataBusinessEntityDO entityDO = new MetadataBusinessEntityDO();
            entityDO.setEntityUuid(entityExport.getEntityUuid());
            entityDO.setDisplayName(entityExport.getDisplayName());
            entityDO.setCode(entityExport.getCode());
            entityDO.setEntityType(entityExport.getEntityType());
            entityDO.setDescription(entityExport.getDescription());
            entityDO.setDatasourceUuid(entityExport.getDatasourceUuid());
            // tableName = appUid + "_" + tableNameSuffix
            String tableName = appUid + "_" + entityExport.getTableNameSuffix();
            entityDO.setTableName(tableName);
            entityDO.setDisplayConfig(entityExport.getDisplayConfig());
            entityDO.setStatus(entityExport.getStatus());
            entityDO.setVersionTag(versionTag);
            entityDO.setApplicationId(newApplicationId);
            entityDO.setTenantId(tenantId);
            entityDOs.add(entityDO);
        }
        if (CollectionUtils.isNotEmpty(entityDOs)) {
            metadataBusinessEntityRepository.saveBatch(entityDOs);
        }

        // 4. 导入实体字段
        java.util.List<MetadataEntityFieldDO> fieldDOs = new java.util.ArrayList<>();
        for (MetadataEntityFieldExportDTO fieldExport : exportData.getEntityFields()) {
            MetadataEntityFieldDO fieldDO = convertExportDTOToFieldDO(fieldExport, newApplicationId, tenantId, versionTag);
            fieldDOs.add(fieldDO);
        }
        if (CollectionUtils.isNotEmpty(fieldDOs)) {
            metadataEntityFieldRepository.saveBatch(fieldDOs);
        }

        // 5. 导入字段选项
        java.util.List<MetadataEntityFieldOptionDO> optionDOs = new java.util.ArrayList<>();
        for (MetadataEntityFieldOptionExportDTO optionExport : exportData.getFieldOptions()) {
            MetadataEntityFieldOptionDO optionDO = new MetadataEntityFieldOptionDO();
            optionDO.setOptionUuid(optionExport.getOptionUuid());
            optionDO.setFieldUuid(optionExport.getFieldUuid());
            optionDO.setOptionValue(optionExport.getOptionValue());
            optionDO.setOptionLabel(optionExport.getOptionLabel());
            optionDO.setOptionOrder(optionExport.getOptionOrder());
            optionDO.setIsEnabled(optionExport.getIsEnabled());
            optionDO.setDescription(optionExport.getDescription());
            optionDO.setVersionTag(versionTag);
            optionDO.setApplicationId(newApplicationId);
            optionDO.setTenantId(tenantId);
            optionDOs.add(optionDO);
        }
        if (CollectionUtils.isNotEmpty(optionDOs)) {
            metadataEntityFieldOptionRepository.saveBatch(optionDOs);
        }

        // 6. 导入实体关系
        java.util.List<MetadataEntityRelationshipDO> relationshipDOs = new java.util.ArrayList<>();
        for (MetadataEntityRelationshipExportDTO relExport : exportData.getEntityRelationships()) {
            MetadataEntityRelationshipDO relDO = new MetadataEntityRelationshipDO();
            relDO.setRelationshipUuid(relExport.getRelationshipUuid());
            relDO.setRelationName(relExport.getRelationName());
            relDO.setSourceEntityUuid(relExport.getSourceEntityUuid());
            relDO.setSourceFieldUuid(relExport.getSourceFieldUuid());
            relDO.setTargetEntityUuid(relExport.getTargetEntityUuid());
            relDO.setTargetFieldUuid(relExport.getTargetFieldUuid());
            relDO.setSelectFieldUuid(relExport.getSelectFieldUuid());
            relDO.setRelationshipType(relExport.getRelationshipType());
            relDO.setCascadeType(relExport.getCascadeType());
            relDO.setDescription(relExport.getDescription());
            relDO.setVersionTag(versionTag);
            relDO.setApplicationId(newApplicationId);
            relDO.setTenantId(tenantId);
            relationshipDOs.add(relDO);
        }
        if (CollectionUtils.isNotEmpty(relationshipDOs)) {
            metadataEntityRelationshipRepository.saveBatch(relationshipDOs);
        }

        // 7. 导入自动编号配置
        java.util.List<MetadataAutoNumberConfigDO> autoNumberDOs = new java.util.ArrayList<>();
        for (MetadataAutoNumberConfigExportDTO autoExport : exportData.getAutoNumberConfigs()) {
            MetadataAutoNumberConfigDO autoDO = new MetadataAutoNumberConfigDO();
            autoDO.setConfigUuid(autoExport.getConfigUuid());
            autoDO.setFieldUuid(autoExport.getFieldUuid());
            autoDO.setNumberMode(autoExport.getNumberMode());
            autoDO.setDigitWidth(autoExport.getDigitWidth());
            autoDO.setOverflowContinue(autoExport.getOverflowContinue());
            autoDO.setInitialValue(autoExport.getInitialValue());
            autoDO.setResetCycle(autoExport.getResetCycle());
            autoDO.setResetOnInitialChange(autoExport.getResetOnInitialChange());
            autoDO.setIsEnabled(autoExport.getIsEnabled());
            autoDO.setSequenceOrder(autoExport.getSequenceOrder());
            autoDO.setVersionTag(versionTag);
            autoDO.setApplicationId(newApplicationId);
            autoDO.setTenantId(tenantId);
            autoNumberDOs.add(autoDO);
        }
        if (CollectionUtils.isNotEmpty(autoNumberDOs)) {
            metadataAutoNumberConfigRepository.saveBatch(autoNumberDOs);
        }

        // 8. 导入自动编号规则项
        java.util.List<MetadataAutoNumberRuleItemDO> ruleItemDOs = new java.util.ArrayList<>();
        for (MetadataAutoNumberRuleItemExportDTO itemExport : exportData.getAutoNumberRuleItems()) {
            MetadataAutoNumberRuleItemDO itemDO = new MetadataAutoNumberRuleItemDO();
            itemDO.setRuleItemUuid(itemExport.getRuleItemUuid());
            itemDO.setConfigUuid(itemExport.getConfigUuid());
            itemDO.setItemType(itemExport.getItemType());
            itemDO.setItemOrder(itemExport.getItemOrder());
            itemDO.setFormat(itemExport.getFormat());
            itemDO.setTextValue(itemExport.getTextValue());
            itemDO.setRefFieldUuid(itemExport.getRefFieldUuid());
            itemDO.setIsEnabled(itemExport.getIsEnabled());
            itemDO.setVersionTag(versionTag);
            itemDO.setApplicationId(newApplicationId);
            ruleItemDOs.add(itemDO);
        }
        if (CollectionUtils.isNotEmpty(ruleItemDOs)) {
            metadataAutoNumberRuleItemRepository.saveBatch(ruleItemDOs);
        }

        // 9-16. 导入验证规则
        importValidationRules(exportData, newApplicationId, tenantId, versionTag);

        // 17. 在业务库创建物理表和字段
        createPhysicalTablesAndColumns(entityDOs, fieldDOs, datasourceDO);

        log.info("导入数据源完成，共导入: {} 个实体, {} 个字段", entityDOs.size(), fieldDOs.size());
    }

    private void importValidationRules(MetadataExportDataDTO exportData, Long applicationId, Long tenantId, Long versionTag) {
        // 验证规则组
        java.util.List<MetadataValidationRuleGroupDO> ruleGroupDOs = new java.util.ArrayList<>();
        for (MetadataValidationRuleGroupExportDTO rgExport : exportData.getValidationRuleGroups()) {
            MetadataValidationRuleGroupDO rgDO = new MetadataValidationRuleGroupDO();
            rgDO.setGroupUuid(rgExport.getGroupUuid());
            rgDO.setRgName(rgExport.getRgName());
            rgDO.setRgDesc(rgExport.getRgDesc());
            rgDO.setRgStatus(rgExport.getRgStatus());
            rgDO.setValMethod(rgExport.getValMethod());
            rgDO.setPopPrompt(rgExport.getPopPrompt());
            rgDO.setPopType(rgExport.getPopType());
            rgDO.setValidationType(rgExport.getValidationType());
            rgDO.setEntityUuid(rgExport.getEntityUuid());
            rgDO.setVersionTag(versionTag);
            rgDO.setApplicationId(applicationId);
            rgDO.setTenantId(tenantId);
            ruleGroupDOs.add(rgDO);
        }
        if (CollectionUtils.isNotEmpty(ruleGroupDOs)) {
            metadataValidationRuleGroupRepository.saveBatch(ruleGroupDOs);
        }

        // 验证规则定义
        java.util.List<MetadataValidationRuleDefinitionDO> ruleDefDOs = new java.util.ArrayList<>();
        for (MetadataValidationRuleDefinitionExportDTO rdExport : exportData.getValidationRuleDefinitions()) {
            MetadataValidationRuleDefinitionDO rdDO = new MetadataValidationRuleDefinitionDO();
            rdDO.setDefinitionUuid(rdExport.getDefinitionUuid());
            rdDO.setGroupUuid(rdExport.getGroupUuid());
            rdDO.setParentRuleUuid(rdExport.getParentRuleUuid());
            rdDO.setEntityUuid(rdExport.getEntityUuid());
            rdDO.setFieldUuid(rdExport.getFieldUuid());
            rdDO.setLogicType(rdExport.getLogicType());
            rdDO.setOperator(rdExport.getOperator());
            rdDO.setLogicOperator(rdExport.getLogicOperator());
            rdDO.setFieldCode(rdExport.getFieldCode());
            rdDO.setValueType(rdExport.getValueType());
            rdDO.setFieldValue(rdExport.getFieldValue());
            rdDO.setFieldValue2(rdExport.getFieldValue2());
            rdDO.setStatus(rdExport.getStatus());
            rdDO.setVersionTag(versionTag);
            rdDO.setApplicationId(applicationId);
            rdDO.setTenantId(tenantId);
            ruleDefDOs.add(rdDO);
        }
        if (CollectionUtils.isNotEmpty(ruleDefDOs)) {
            metadataValidationRuleDefinitionRepository.saveBatch(ruleDefDOs);
        }

        // 必填验证
        java.util.List<MetadataValidationRequiredDO> requiredDOs = new java.util.ArrayList<>();
        for (MetadataValidationRequiredExportDTO rExport : exportData.getValidationRequireds()) {
            MetadataValidationRequiredDO rDO = new MetadataValidationRequiredDO();
            rDO.setRequiredUuid(rExport.getRequiredUuid());
            rDO.setGroupUuid(rExport.getGroupUuid());
            rDO.setEntityUuid(rExport.getEntityUuid());
            rDO.setFieldUuid(rExport.getFieldUuid());
            rDO.setIsEnabled(rExport.getIsEnabled());
            rDO.setPromptMessage(rExport.getPromptMessage());
            rDO.setVersionTag(versionTag);
            rDO.setApplicationId(applicationId);
            rDO.setTenantId(tenantId);
            requiredDOs.add(rDO);
        }
        if (CollectionUtils.isNotEmpty(requiredDOs)) {
            metadataValidationRequiredRepository.saveBatch(requiredDOs);
        }

        // 唯一验证
        java.util.List<MetadataValidationUniqueDO> uniqueDOs = new java.util.ArrayList<>();
        for (MetadataValidationUniqueExportDTO uExport : exportData.getValidationUniques()) {
            MetadataValidationUniqueDO uDO = new MetadataValidationUniqueDO();
            uDO.setUniqueUuid(uExport.getUniqueUuid());
            uDO.setGroupUuid(uExport.getGroupUuid());
            uDO.setEntityUuid(uExport.getEntityUuid());
            uDO.setFieldUuid(uExport.getFieldUuid());
            uDO.setIsEnabled(uExport.getIsEnabled());
            uDO.setPromptMessage(uExport.getPromptMessage());
            uDO.setVersionTag(versionTag);
            uDO.setApplicationId(applicationId);
            uDO.setTenantId(tenantId);
            uniqueDOs.add(uDO);
        }
        if (CollectionUtils.isNotEmpty(uniqueDOs)) {
            metadataValidationUniqueRepository.saveBatch(uniqueDOs);
        }

        // 长度验证
        java.util.List<MetadataValidationLengthDO> lengthDOs = new java.util.ArrayList<>();
        for (MetadataValidationLengthExportDTO lExport : exportData.getValidationLengths()) {
            MetadataValidationLengthDO lDO = new MetadataValidationLengthDO();
            lDO.setLengthUuid(lExport.getLengthUuid());
            lDO.setGroupUuid(lExport.getGroupUuid());
            lDO.setEntityUuid(lExport.getEntityUuid());
            lDO.setFieldUuid(lExport.getFieldUuid());
            lDO.setIsEnabled(lExport.getIsEnabled());
            lDO.setMinLength(lExport.getMinLength());
            lDO.setMaxLength(lExport.getMaxLength());
            lDO.setTrimBefore(lExport.getTrimBefore());
            lDO.setPromptMessage(lExport.getPromptMessage());
            lDO.setVersionTag(versionTag);
            lDO.setApplicationId(applicationId);
            lDO.setTenantId(tenantId);
            lengthDOs.add(lDO);
        }
        if (CollectionUtils.isNotEmpty(lengthDOs)) {
            metadataValidationLengthRepository.saveBatch(lengthDOs);
        }

        // 范围验证
        java.util.List<MetadataValidationRangeDO> rangeDOs = new java.util.ArrayList<>();
        for (MetadataValidationRangeExportDTO rExport : exportData.getValidationRanges()) {
            MetadataValidationRangeDO rDO = new MetadataValidationRangeDO();
            rDO.setRangeUuid(rExport.getRangeUuid());
            rDO.setGroupUuid(rExport.getGroupUuid());
            rDO.setEntityUuid(rExport.getEntityUuid());
            rDO.setFieldUuid(rExport.getFieldUuid());
            rDO.setIsEnabled(rExport.getIsEnabled());
            rDO.setRangeType(rExport.getRangeType());
            rDO.setMinValue(rExport.getMinValue());
            rDO.setMaxValue(rExport.getMaxValue());
            rDO.setMinDate(rExport.getMinDate());
            rDO.setMaxDate(rExport.getMaxDate());
            rDO.setIncludeMin(rExport.getIncludeMin());
            rDO.setIncludeMax(rExport.getIncludeMax());
            rDO.setPromptMessage(rExport.getPromptMessage());
            rDO.setVersionTag(versionTag);
            rDO.setApplicationId(applicationId);
            rDO.setTenantId(tenantId);
            rangeDOs.add(rDO);
        }
        if (CollectionUtils.isNotEmpty(rangeDOs)) {
            metadataValidationRangeRepository.saveBatch(rangeDOs);
        }

        // 格式验证
        java.util.List<MetadataValidationFormatDO> formatDOs = new java.util.ArrayList<>();
        for (MetadataValidationFormatExportDTO fExport : exportData.getValidationFormats()) {
            MetadataValidationFormatDO fDO = new MetadataValidationFormatDO();
            fDO.setFormatUuid(fExport.getFormatUuid());
            fDO.setGroupUuid(fExport.getGroupUuid());
            fDO.setEntityUuid(fExport.getEntityUuid());
            fDO.setFieldUuid(fExport.getFieldUuid());
            fDO.setIsEnabled(fExport.getIsEnabled());
            fDO.setFormatCode(fExport.getFormatCode());
            fDO.setRegexPattern(fExport.getRegexPattern());
            fDO.setFlags(fExport.getFlags());
            fDO.setPromptMessage(fExport.getPromptMessage());
            fDO.setVersionTag(versionTag);
            fDO.setApplicationId(applicationId);
            fDO.setTenantId(tenantId);
            formatDOs.add(fDO);
        }
        if (CollectionUtils.isNotEmpty(formatDOs)) {
            metadataValidationFormatRepository.saveBatch(formatDOs);
        }

        // 子表非空验证
        java.util.List<MetadataValidationChildNotEmptyDO> childNotEmptyDOs = new java.util.ArrayList<>();
        for (MetadataValidationChildNotEmptyExportDTO cExport : exportData.getValidationChildNotEmptys()) {
            MetadataValidationChildNotEmptyDO cDO = new MetadataValidationChildNotEmptyDO();
            cDO.setChildNotEmptyUuid(cExport.getChildNotEmptyUuid());
            cDO.setGroupUuid(cExport.getGroupUuid());
            cDO.setEntityUuid(cExport.getEntityUuid());
            cDO.setFieldUuid(cExport.getFieldUuid());
            cDO.setChildEntityUuid(cExport.getChildEntityUuid());
            cDO.setIsEnabled(cExport.getIsEnabled());
            cDO.setMinRows(cExport.getMinRows());
            cDO.setPromptMessage(cExport.getPromptMessage());
            cDO.setVersionTag(versionTag);
            cDO.setApplicationId(applicationId);
            cDO.setTenantId(tenantId);
            childNotEmptyDOs.add(cDO);
        }
        if (CollectionUtils.isNotEmpty(childNotEmptyDOs)) {
            metadataValidationChildNotEmptyRepository.saveBatch(childNotEmptyDOs);
        }
    }

    private MetadataEntityFieldDO convertExportDTOToFieldDO(MetadataEntityFieldExportDTO fieldExport, Long applicationId, Long tenantId, Long versionTag) {
        MetadataEntityFieldDO fieldDO = new MetadataEntityFieldDO();
        fieldDO.setFieldUuid(fieldExport.getFieldUuid());
        fieldDO.setEntityUuid(fieldExport.getEntityUuid());
        fieldDO.setFieldName(fieldExport.getFieldName());
        fieldDO.setDisplayName(fieldExport.getDisplayName());
        fieldDO.setFieldType(fieldExport.getFieldType());
        fieldDO.setDataLength(fieldExport.getDataLength());
        fieldDO.setDecimalPlaces(fieldExport.getDecimalPlaces());
        fieldDO.setDefaultValue(fieldExport.getDefaultValue());
        fieldDO.setDescription(fieldExport.getDescription());
        fieldDO.setIsSystemField(fieldExport.getIsSystemField());
        fieldDO.setIsPrimaryKey(fieldExport.getIsPrimaryKey());
        fieldDO.setIsRequired(fieldExport.getIsRequired());
        fieldDO.setIsUnique(fieldExport.getIsUnique());
        fieldDO.setSortOrder(fieldExport.getSortOrder());
        fieldDO.setValidationRules(fieldExport.getValidationRules());
        fieldDO.setStatus(fieldExport.getStatus());
        fieldDO.setFieldCode(fieldExport.getFieldCode());
        fieldDO.setDictTypeId(fieldExport.getDictTypeId());
        fieldDO.setVersionTag(versionTag);
        fieldDO.setApplicationId(applicationId);
        fieldDO.setTenantId(tenantId);
        return fieldDO;
    }

    private String buildDefaultDatasourceConfig() {
        return String.format(
                "{\"host\":\"%s\",\"port\":%d,\"database\":\"%s\",\"username\":\"%s\",\"password\":\"%s\"}",
                metadataConfig.getDefaultDatasourceHost(),
                metadataConfig.getDefaultDatasourcePort(),
                metadataConfig.getDefaultDatasourceDatabase(),
                metadataConfig.getDefaultDatasourceUsername(),
                metadataConfig.getDefaultDatasourcePassword()
        );
    }

    private String generateDatasourceCode() {
        return "ds_" + System.currentTimeMillis();
    }

    private void createPhysicalTablesAndColumns(java.util.List<MetadataBusinessEntityDO> entities,
                                                java.util.List<MetadataEntityFieldDO> fields,
                                                MetadataDatasourceDO datasource) {
        if (CollectionUtils.isEmpty(entities)) {
            return;
        }

        log.info("开始在业务库创建物理表，共 {} 个实体", entities.size());

        // 获取字段类型映射
        java.util.List<FieldTypeMappingDO> typeMappings = fieldTypeMappingRepository.list();

        // 按实体UUID分组字段
        Map<String, java.util.List<MetadataEntityFieldDO>> fieldsByEntity = fields.stream()
                .collect(Collectors.groupingBy(MetadataEntityFieldDO::getEntityUuid));

        // 获取业务数据库连接
        try {
            org.anyline.service.AnylineService<?> anylineService = temporaryDatasourceService.createTemporaryService(datasource);

            for (MetadataBusinessEntityDO entity : entities) {
                String tableName = entity.getTableName();
                java.util.List<MetadataEntityFieldDO> entityFields = fieldsByEntity.get(entity.getEntityUuid());

                if (CollectionUtils.isEmpty(entityFields)) {
                    log.warn("实体 {} 没有字段，跳过创建物理表", entity.getEntityUuid());
                    continue;
                }

                // 构建建表SQL
                String createTableSql = buildCreateTableSql(tableName, entityFields, datasource.getDatasourceType(), typeMappings);
                log.debug("创建表SQL: {}", createTableSql);

                // 执行建表
                anylineService.execute(createTableSql);
                log.info("成功创建物理表: {}", tableName);
            }
        } catch (Exception e) {
            log.error("创建物理表失败", e);
            throw new RuntimeException("创建物理表失败: " + e.getMessage(), e);
        }
    }

    private String buildCreateTableSql(String tableName, java.util.List<MetadataEntityFieldDO> fields, String datasourceType, java.util.List<FieldTypeMappingDO> typeMappings) {
        StringBuilder sql = new StringBuilder();
        sql.append("CREATE TABLE IF NOT EXISTS ").append(tableName).append(" (\n");

        java.util.List<String> columnDefs = new java.util.ArrayList<>();
        String primaryKeyField = null;

        for (MetadataEntityFieldDO field : fields) {
            String columnDef = buildColumnDefinition(field, datasourceType, typeMappings);
            columnDefs.add("  " + columnDef);

            if (field.getIsPrimaryKey() != null && field.getIsPrimaryKey() == 1) {
                primaryKeyField = field.getFieldName();
            }
        }

        sql.append(String.join(",\n", columnDefs));

        if (primaryKeyField != null) {
            sql.append(",\n  PRIMARY KEY (").append(primaryKeyField).append(")");
        }

        sql.append("\n)");
        return sql.toString();
    }

    private String buildColumnDefinition(MetadataEntityFieldDO field, String datasourceType, java.util.List<FieldTypeMappingDO> typeMappings) {
        StringBuilder def = new StringBuilder();
        def.append(field.getFieldName()).append(" ");

        String dbType = mapFieldTypeToDbType(field.getFieldType(), field.getDataLength(), field.getDecimalPlaces(), datasourceType, typeMappings);
        def.append(dbType);

        if (field.getIsRequired() != null && field.getIsRequired() == 1) {
            def.append(" NOT NULL");
        }

        if (field.getDefaultValue() != null && !field.getDefaultValue().isEmpty()) {
            String defaultValue = field.getDefaultValue();
            // 对于特殊的默认值（如时间戳函数、数字等）不加引号
            if (isSpecialDefaultValue(defaultValue)) {
                def.append(" DEFAULT ").append(normalizeDefaultValue(defaultValue));
            } else {
                def.append(" DEFAULT '").append(defaultValue).append("'");
            }
        }

        return def.toString();
    }

    /**
     * 判断是否为特殊的默认值（不需要加引号的值）
     *
     * @param defaultValue 默认值
     * @return 是否为特殊默认值
     */
    private boolean isSpecialDefaultValue(String defaultValue) {
        if (defaultValue == null || defaultValue.isEmpty()) {
            return false;
        }
        String upperValue = defaultValue.toUpperCase().trim();
        // 时间戳相关函数
        if (MetadataDefaultValueKeywordEnum.CURRENT_TIMESTAMP.containsIn(upperValue)
                || MetadataDefaultValueKeywordEnum.NOW_CALL.containsIn(upperValue)
                || MetadataDefaultValueKeywordEnum.CURRENT_DATE.containsIn(upperValue)
                || MetadataDefaultValueKeywordEnum.CURRENT_TIME.containsIn(upperValue)) {
            return true;
        }
        // 纯数字（包括小数和负数）
        if (upperValue.matches("^-?\\d+(\\.\\d+)?$")) {
            return true;
        }
        // NULL 值
        if (MetadataDefaultValueKeywordEnum.NULL.equalsTo(upperValue)) {
            return true;
        }
        // 布尔值
        if (MetadataBooleanLiteralEnum.isSqlBooleanLiteral(upperValue)) {
            return true;
        }
        return false;
    }

    /**
     * 标准化默认值（去除引号等）
     *
     * @param defaultValue 原始默认值
     * @return 标准化后的默认值
     */
    private String normalizeDefaultValue(String defaultValue) {
        if (defaultValue == null) {
            return MetadataDefaultValueKeywordEnum.NULL.getKeyword();
        }
        String value = defaultValue.trim();
        // 移除可能存在的外层引号
        if ((value.startsWith("'") && value.endsWith("'")) || (value.startsWith("\"") && value.endsWith("\""))) {
            value = value.substring(1, value.length() - 1);
        }
        return value;
    }

    //todo metadata_field_type_mapping 表中有存字段类型和数据库类型的映射关系，可以改为从该表中读取映射关系，而不是写死在代码中
    private String mapFieldTypeToDbType(String fieldType, Integer dataLength, Integer decimalPlaces, String datasourceType, java.util.List<FieldTypeMappingDO> typeMappings) {
        if (fieldType == null) {
            return buildVarcharDbType(null);
        }
        MetadataDataTypeCodeEnum fieldTypeCode = MetadataDataTypeCodeEnum.fromCode(fieldType);

        // 1. 优先从映射表中查找
        if (CollectionUtils.isNotEmpty(typeMappings)) {
            String dbType = typeMappings.stream()
                    .filter(m -> m.getBusinessFieldType().equalsIgnoreCase(fieldType) &&
                            m.getDatabaseType().equalsIgnoreCase(datasourceType))
                    .findFirst()
                    .map(FieldTypeMappingDO::getDatabaseField)
                    .orElse(null);

            if (dbType != null) {
                // 特殊处理需要长度和精度的类型
                if (isTextFieldType(fieldTypeCode)
                        && !dbType.contains("(")
                        && (MetadataDataTypeCodeEnum.CHAR.matches(dbType) || MetadataDataTypeCodeEnum.VARCHAR.matches(dbType)
                        || dbType.toUpperCase().contains(MetadataDataTypeCodeEnum.CHAR.getCode())
                        || dbType.toUpperCase().contains(MetadataDataTypeCodeEnum.VARCHAR.getCode()))) {
                    return dbType + "(" + resolveLength(dataLength, 255) + ")";
                }

                if (isDecimalFieldType(fieldTypeCode)
                        && !dbType.contains("(")) {
                    return dbType + "(" + resolveLength(dataLength, 18) + "," + resolveScale(decimalPlaces, 2) + ")";
                }

                return dbType;
            }
        }

        // 2. 默认兜底逻辑
        boolean isPostgres = MetadataDatasourceTypeEnum.isPostgresFamily(datasourceType);

        if (fieldTypeCode == null) {
            return buildVarcharDbType(null);
        }

        return switch (fieldTypeCode) {
            case STRING, TEXT -> buildVarcharDbType(dataLength);
            case INTEGER, INT -> isPostgres ? MetadataDataTypeCodeEnum.INTEGER.getCode() : MetadataDataTypeCodeEnum.INT.getCode();
            case LONG, BIGINT -> MetadataDataTypeCodeEnum.BIGINT.getCode();
            case DECIMAL, NUMBER -> buildDecimalDbType(dataLength, decimalPlaces);
            case BOOLEAN, BOOL -> isPostgres ? MetadataDataTypeCodeEnum.BOOLEAN.getCode() : "TINYINT(1)";
            case DATE -> MetadataDataTypeCodeEnum.DATE.getCode();
            case DATETIME, TIMESTAMP -> isPostgres ? MetadataDataTypeCodeEnum.TIMESTAMP.getCode() : MetadataDataTypeCodeEnum.DATETIME.getCode();
            case JSON -> isPostgres ? MetadataDataTypeCodeEnum.JSONB.getCode() : MetadataDataTypeCodeEnum.JSON.getCode();
            default -> buildVarcharDbType(null);
        };
    }

    private boolean isTextFieldType(MetadataDataTypeCodeEnum fieldTypeCode) {
        return fieldTypeCode == MetadataDataTypeCodeEnum.STRING || fieldTypeCode == MetadataDataTypeCodeEnum.TEXT;
    }

    private boolean isDecimalFieldType(MetadataDataTypeCodeEnum fieldTypeCode) {
        return fieldTypeCode == MetadataDataTypeCodeEnum.DECIMAL || fieldTypeCode == MetadataDataTypeCodeEnum.NUMBER;
    }

    private String buildVarcharDbType(Integer dataLength) {
        return MetadataDataTypeCodeEnum.VARCHAR.getCode() + "(" + resolveLength(dataLength, 255) + ")";
    }

    private String buildDecimalDbType(Integer dataLength, Integer decimalPlaces) {
        return MetadataDataTypeCodeEnum.DECIMAL.getCode() + "(" + resolveLength(dataLength, 18) + "," + resolveScale(decimalPlaces, 2) + ")";
    }

    private int resolveLength(Integer value, int defaultValue) {
        return (value != null && value > 0) ? value : defaultValue;
    }

    private int resolveScale(Integer value, int defaultValue) {
        return (value != null && value >= 0) ? value : defaultValue;
    }

    //@Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteApplicationVersionData(Long applicationId, Long versionTag) {
        log.info("开始删除元数据版本数据，applicationId: {}, versionTag: {}", applicationId, versionTag);

        // 1. 删除子表非空验证
        metadataValidationChildNotEmptyRepository.deleteApplicationVersionData(applicationId, versionTag);
        // 2. 删除格式验证
        metadataValidationFormatRepository.deleteApplicationVersionData(applicationId, versionTag);
        // 3. 删除范围验证
        metadataValidationRangeRepository.deleteApplicationVersionData(applicationId, versionTag);
        // 4. 删除长度验证
        metadataValidationLengthRepository.deleteApplicationVersionData(applicationId, versionTag);
        // 5. 删除唯一验证
        metadataValidationUniqueRepository.deleteApplicationVersionData(applicationId, versionTag);
        // 6. 删除必填验证
        metadataValidationRequiredRepository.deleteApplicationVersionData(applicationId, versionTag);
        // 7. 删除验证规则定义
        metadataValidationRuleDefinitionRepository.deleteApplicationVersionData(applicationId, versionTag);
        // 8. 删除验证规则组
        metadataValidationRuleGroupRepository.deleteApplicationVersionData(applicationId, versionTag);
        // 9. 删除自动编号规则项
        metadataAutoNumberRuleItemRepository.deleteApplicationVersionData(applicationId, versionTag);
        // 10. 删除自动编号配置
        metadataAutoNumberConfigRepository.deleteApplicationVersionData(applicationId, versionTag);
        // 11. 删除实体关系
        metadataEntityRelationshipRepository.deleteApplicationVersionData(applicationId, versionTag);
        // 12. 删除字段选项
        metadataEntityFieldOptionRepository.deleteApplicationVersionData(applicationId, versionTag);
        // 13. 删除实体字段
        metadataEntityFieldRepository.deleteApplicationVersionData(applicationId, versionTag);
        // 14. 删除业务实体
        metadataBusinessEntityRepository.deleteApplicationVersionData(applicationId, versionTag);
        // 15. 删除应用与数据源关联
        metadataAppAndDatasourceRepository.deleteApplicationVersionData(applicationId, versionTag);
        // 16. 删除数据源
        metadataDatasourceRepository.deleteApplicationVersionData(applicationId, versionTag);

        log.info("删除元数据版本数据完成，applicationId: {}, versionTag: {}", applicationId, versionTag);
    }
}
