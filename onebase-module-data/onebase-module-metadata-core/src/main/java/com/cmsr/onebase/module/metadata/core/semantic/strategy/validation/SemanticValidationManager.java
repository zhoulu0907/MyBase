package com.cmsr.onebase.module.metadata.core.semantic.strategy.validation;

import com.cmsr.onebase.module.metadata.core.enums.MetadataDataMethodOpEnum;
import com.cmsr.onebase.module.metadata.core.service.entity.MetadataEntityFieldCoreService;
import com.cmsr.onebase.module.metadata.core.service.number.AutoNumberService;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.validation.MetadataValidationFormatDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.validation.MetadataValidationLengthDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.validation.MetadataValidationRangeDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.validation.MetadataValidationRequiredDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.validation.MetadataValidationRuleDefinitionDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.validation.MetadataValidationRuleGroupDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.validation.MetadataValidationUniqueDO;
import com.cmsr.onebase.module.metadata.core.dal.database.MetadataValidationFormatRepository;
import com.cmsr.onebase.module.metadata.core.dal.database.MetadataValidationLengthRepository;
import com.cmsr.onebase.module.metadata.core.dal.database.MetadataValidationRangeRepository;
import com.cmsr.onebase.module.metadata.core.dal.database.MetadataValidationRequiredRepository;
import com.cmsr.onebase.module.metadata.core.dal.database.MetadataValidationRuleDefinitionRepository;
import com.cmsr.onebase.module.metadata.core.dal.database.MetadataValidationRuleGroupRepository;
import com.cmsr.onebase.module.metadata.core.dal.database.MetadataValidationUniqueRepository;
import com.cmsr.onebase.module.metadata.core.service.datasource.MetadataDatasourceCoreService;
import com.cmsr.onebase.module.metadata.core.service.entity.MetadataBusinessEntityCoreService;
import com.cmsr.onebase.module.metadata.core.semantic.dto.SemanticRecordDTO;
import com.cmsr.onebase.module.metadata.core.semantic.dto.SemanticEntitySchemaDTO;
import com.cmsr.onebase.module.metadata.core.semantic.dto.SemanticFieldSchemaDTO;
import com.cmsr.onebase.module.metadata.core.semantic.dto.SemanticEntityValueDTO;
import com.cmsr.onebase.module.metadata.core.semantic.dto.SemanticRelationSchemaDTO;
import com.cmsr.onebase.module.metadata.core.semantic.dto.enums.SemanticConnectorCardinalityEnum;
import com.cmsr.onebase.module.metadata.core.semantic.strategy.validation.impl.SemanticUniqueValidationService;

import jakarta.annotation.Resource;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 语义校验管理器
 *
 * <p>
 * 职责：
 * - 聚合并驱动各类字段级校验服务（长度、格式、范围、必填、唯一、规则组等）
 * - 支持主实体字段与关联对象（子表/关联表）的字段校验
 * - 统一将规则查询与上下文构建收敛在 {@link #buildContext(SemanticEntitySchemaDTO, java.util.List, java.util.Map)} 中
 *
 * 设计要点：
 * - 主实体字段与连接器“关系属性字段”共同参与规则装载；唯一性批量查询仅针对主实体表执行
 * - 连接器数据按基数（ONE/MANY）分别取单行或多行原始值逐条校验
 * - 通过 {@link SemanticValidationService} 接口可扩展更多校验能力
 * </p>
 */
@Component
public class SemanticValidationManager {

    private final List<SemanticValidationService> validationServices;

    @Resource
    private AutoNumberService autoNumberService;

    @Resource
    private MetadataEntityFieldCoreService metadataEntityFieldCoreService;

    @Resource
    private MetadataValidationLengthRepository lengthRepository;
    @Resource
    private MetadataValidationFormatRepository formatRepository;
    @Resource
    private MetadataValidationRangeRepository rangeRepository;
    @Resource
    private MetadataValidationRequiredRepository requiredRepository;
    @Resource
    private MetadataValidationUniqueRepository uniqueRepository;
    @Resource
    private MetadataValidationRuleGroupRepository ruleGroupRepository;
    @Resource
    private MetadataValidationRuleDefinitionRepository ruleDefinitionRepository;
    @Resource
    private MetadataBusinessEntityCoreService metadataBusinessEntityCoreService;
    @Resource
    private MetadataDatasourceCoreService metadataDatasourceCoreService;

    /**
     * 构造函数
     *
     * @param validationServices 已注册的字段级校验服务集合
     */
    public SemanticValidationManager(List<SemanticValidationService> validationServices) { this.validationServices = validationServices; }


    /**
     * 校验入口：对主实体与关联对象字段执行规则校验
     *
     * - 合并主实体字段与连接器“关系属性字段”以构建统一的规则上下文
     * - 主实体字段先行校验，随后根据连接器基数（ONE/MANY）逐行校验关联对象
     *
     * @param recordDTO 统一语义载体（包含实体模型与值模型）
     */
    public void validate(SemanticRecordDTO recordDTO) {
        List<SemanticFieldSchemaDTO> mainFields = recordDTO.getEntitySchema().getFields();
        List<SemanticFieldSchemaDTO> connectorFields = collectConnectorAttributeFields(recordDTO.getEntitySchema());
        List<SemanticFieldSchemaDTO> allFields = new ArrayList<>();
        if (mainFields != null) { allFields.addAll(mainFields); }
        // 合并连接器“关系属性字段”，用于统一装载校验规则
        if (connectorFields != null && !connectorFields.isEmpty()) { allFields.addAll(connectorFields); }

        Map<String, Object> mainData = recordDTO.getEntityValue().getCurrentEntityRawMap();
        MetadataDataMethodOpEnum operationType = recordDTO.getRecordContext().getOperationType();
        // 统一在 buildContext 中完成规则查询与上下文构建
        SemanticValidationContext context = buildContext(recordDTO.getEntitySchema(), allFields, mainData);
        validateEntity(mainFields, mainData, operationType, context);

        Map<String, Object> row;
        SemanticEntityValueDTO value = recordDTO.getEntityValue();
        if (value != null) {
            if (recordDTO.getEntitySchema().getConnectors() != null) {
                for (SemanticRelationSchemaDTO c : recordDTO.getEntitySchema().getConnectors()) {
                    if (c.getCardinality() == SemanticConnectorCardinalityEnum.ONE) {
                        row = value.getConnectorRawObject(c.getTargetEntityTableName());
                        if (row != null && connectorFieldsFor(c) != null) {
                            validateEntity(connectorFieldsFor(c), row, operationType, context.copyWithTableName(c.getTargetEntityTableName()));
                        }
                    } else if (c.getCardinality() == SemanticConnectorCardinalityEnum.MANY) {
                        List<Map<String, Object>> list = value.getConnectorRawList(c.getTargetEntityTableName());
                        if (list != null && !list.isEmpty() && connectorFieldsFor(c) != null) {
                            for (Map<String, Object> r : list) { validateEntity(connectorFieldsFor(c), r, operationType, context.copyWithTableName(c.getTargetEntityTableName())); }
                        }
                    }
                }
            }
        }
    }

    /**
     * 执行字段级校验
     *
     * - 将字段集合与数据、操作类型传递给已注册的所有 {@link SemanticValidationService}
     * - 每个校验服务自行决定支持的字段类型与规则
     */
    public void validateEntity(List<SemanticFieldSchemaDTO> fields, Map<String, Object> data, MetadataDataMethodOpEnum operationType, SemanticValidationContext context) {
        for (SemanticValidationService service : validationServices) {
            service.validateEntity(fields, data, operationType, context);
        }
    }

    /**
     * 构建语义校验上下文
     *
     * - 统一查询并缓存各类规则（长度、格式、范围、必填、唯一、规则组/定义）
     * - 唯一性批量存在查询仅对主实体字段执行：避免对连接器字段错误地使用主表的唯一规则
     * - 构建字段ID到字段名的映射，供规则执行快速定位字段
     */
    private SemanticValidationContext buildContext(SemanticEntitySchemaDTO entity, List<SemanticFieldSchemaDTO> fields, Map<String, Object> data) {
        Map<String, List<MetadataValidationLengthDO>> lengthRules = new HashMap<>();
        Map<String, List<MetadataValidationFormatDO>> formatRules = new HashMap<>();
        Map<String, List<MetadataValidationRangeDO>> rangeRules = new HashMap<>();
        Map<String, List<MetadataValidationRequiredDO>> requiredRules = new HashMap<>();
        Map<String, List<MetadataValidationUniqueDO>> uniqueRules = new HashMap<>();
        Map<String, Boolean> uniqueExists = new HashMap<>();

        List<String> fieldUuids = fields.stream().map(SemanticFieldSchemaDTO::getFieldUuid).collect(Collectors.toList());

        for (var item : lengthRepository.findByFieldUuids(fieldUuids)) {
            lengthRules.computeIfAbsent(item.getFieldUuid(), k -> new ArrayList<>()).add(item);
        }
        for (var item : formatRepository.findByFieldUuids(fieldUuids)) {
            formatRules.computeIfAbsent(item.getFieldUuid(), k -> new ArrayList<>()).add(item);
        }
        for (var item : rangeRepository.findByFieldUuids(fieldUuids)) {
            rangeRules.computeIfAbsent(item.getFieldUuid(), k -> new ArrayList<>()).add(item);
        }
        for (var item : requiredRepository.findByFieldUuids(fieldUuids)) {
            requiredRules.computeIfAbsent(item.getFieldUuid(), k -> new ArrayList<>()).add(item);
        }
        for (var item : uniqueRepository.findByFieldUuids(fieldUuids)) {
            uniqueRules.computeIfAbsent(item.getFieldUuid(), k -> new ArrayList<>()).add(item);
        }

        // 唯一性批量存在校验：限定主实体字段，避免误用连接器表
        Map<String, Boolean> batchExists = getUniqueValidationService().checkUniqueExistsBatch(entity, entity.getFields(), data, uniqueRules);
        uniqueExists.putAll(batchExists);

        List<MetadataValidationRuleGroupDO> ruleGroups = findActiveRuleGroups(entity.getEntityUuid());
        Map<String, List<MetadataValidationRuleDefinitionDO>> ruleDefsByGroup = new HashMap<>();
        List<String> groupUuids = ruleGroups.stream().map(MetadataValidationRuleGroupDO::getGroupUuid).collect(Collectors.toList());
        for (var def : ruleDefinitionRepository.selectByGroupUuids(groupUuids)) {
            ruleDefsByGroup.computeIfAbsent(def.getGroupUuid(), k -> new ArrayList<>()).add(def);
        }

        Map<String, String> fieldUuidToNameMap = fields.stream().collect(Collectors.toMap(SemanticFieldSchemaDTO::getFieldUuid, SemanticFieldSchemaDTO::getFieldName));

        return new SemanticValidationContext(lengthRules, formatRules, rangeRules, requiredRules, uniqueRules, uniqueExists, ruleGroups, ruleDefsByGroup, fieldUuidToNameMap, entity.getTableName());   
    }

    /**
     * 查询启用的自定义规则组
     *
     * @param entityUuid 实体UUID
     * @return 启用的规则组列表
     */
    private List<MetadataValidationRuleGroupDO> findActiveRuleGroups(String entityUuid) {
        QueryWrapper qw = ruleGroupRepository.query()
                .eq(MetadataValidationRuleGroupDO::getEntityUuid, entityUuid)
                .eq(MetadataValidationRuleGroupDO::getValidationType, "SELF_DEFINED")
                .eq(MetadataValidationRuleGroupDO::getRgStatus, 1);
        return ruleGroupRepository.list(qw);
    }


    /**
     * 获取唯一性校验服务实例
     *
     * - 优先返回上下文中已注册的服务
     * - 如未注册，则返回一个默认实例（保证流程可执行）
     */
    private SemanticUniqueValidationService getUniqueValidationService() {
        for (SemanticValidationService s : validationServices) {
            if (s instanceof SemanticUniqueValidationService u) {
                return u;
            }
        }
        return new SemanticUniqueValidationService();
    }

    /**
     * 收集连接器的“关系属性字段”集合
     *
     * @param entity 实体模型
     * @return 连接器的关系属性字段列表
     */
    private List<SemanticFieldSchemaDTO> collectConnectorAttributeFields(SemanticEntitySchemaDTO entity) {
        if (entity == null || entity.getConnectors() == null) { return java.util.Collections.emptyList(); }
        List<SemanticFieldSchemaDTO> result = new ArrayList<>();
        for (SemanticRelationSchemaDTO c : entity.getConnectors()) {
            if (c.getRelationAttributes() != null) { result.addAll(c.getRelationAttributes()); }
        }
        return result;
    }

    /**
     * 获取指定连接器的字段集合（关系属性字段）
     *
     * @param connector 连接器模型
     * @return 字段列表；为空时返回空集合
     */
    private List<SemanticFieldSchemaDTO> connectorFieldsFor(SemanticRelationSchemaDTO connector) {
        if (connector == null || connector.getRelationAttributes() == null) { return java.util.Collections.emptyList(); }
        return connector.getRelationAttributes();
    }
}
