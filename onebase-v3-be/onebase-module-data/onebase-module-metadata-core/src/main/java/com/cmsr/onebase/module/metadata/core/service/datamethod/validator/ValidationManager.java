package com.cmsr.onebase.module.metadata.core.service.datamethod.validator;

import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataEntityFieldDO;
import com.cmsr.onebase.module.metadata.core.domain.query.MetadataDataMethodSubEntityContext;
import com.cmsr.onebase.module.metadata.core.enums.MetadataDataMethodOpEnum;
import com.cmsr.onebase.module.metadata.core.enums.MetadataValidationRuleTypeEnum;
import com.cmsr.onebase.module.metadata.core.dal.database.MetadataValidationRequiredRepository;
import com.cmsr.onebase.module.metadata.core.dal.database.MetadataValidationUniqueRepository;
import com.cmsr.onebase.module.metadata.core.dal.database.MetadataValidationLengthRepository;
import com.cmsr.onebase.module.metadata.core.dal.database.MetadataValidationFormatRepository;
import com.cmsr.onebase.module.metadata.core.dal.database.MetadataValidationRangeRepository;
import com.cmsr.onebase.module.metadata.core.service.number.AutoNumberService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 校验管理器
 * 
 * 负责协调各种校验服务的执行，统一管理校验流程
 *
 * @author bty418
 * @date 2025-10-17
 */
@Component
public class ValidationManager {

    private static final Logger log = Logger.getLogger(ValidationManager.class.getName());

    private final List<ValidationService> validationServices;
    
    @Resource
    private AutoNumberService autoNumberService;
    @Resource
    private MetadataValidationRequiredRepository requiredRepository;
    @Resource
    private MetadataValidationUniqueRepository uniqueRepository;
    @Resource
    private MetadataValidationLengthRepository lengthRepository;
    @Resource
    private MetadataValidationFormatRepository formatRepository;
    @Resource
    private MetadataValidationRangeRepository rangeRepository;

    public ValidationManager(List<ValidationService> validationServices) {
        this.validationServices = validationServices;
    }

    /**
     * 执行字段校验
     *
     * @param entityUuid 实体UUID
     * @param field 字段信息
     * @param value 字段值
     * @param data 完整数据对象
     * @param subEntities 子实体上下文列表
     */
    public void validateField(String entityUuid, MetadataEntityFieldDO field, Object value, Map<String, Object> data, List<MetadataDataMethodSubEntityContext> subEntities) {
        String fieldUuid = field.getFieldUuid();
        String fieldType = field.getFieldType();
        String fieldName = field.getFieldName();

        log.fine("开始校验字段：entityUuid=" + entityUuid + ", fieldUuid=" + fieldUuid + ", fieldName=" + fieldName + ", fieldType=" + fieldType);

        // 检查是否为自动编号字段，如果是则跳过所有校验
        if (autoNumberService.hasAutoNumber(fieldUuid)) {
            log.fine("字段" + fieldName + "是自动编号字段，跳过所有校验");
            return;
        }

        // 遍历所有校验服务，执行支持的校验
        for (ValidationService service : validationServices) {
            try {
                if (MetadataValidationRuleTypeEnum.CHILD_NOT_EMPTY.getCode().equals(service.getValidationType())) {
                    continue;
                }
                // 检查是否支持该字段类型
                if (service.supports(fieldType)) {
                    log.fine("执行" + service.getValidationType() + "校验：fieldName=" + fieldName);
                    service.validate(entityUuid, fieldUuid, field, value, data, subEntities);
                }
            } catch (Exception e) {
                // 构造详细的错误消息：字段名-校验类型-失败原因
                String validationType = getValidationTypeName(service.getValidationType());
                String errorMessage = String.format("字段[%s]-%s校验失败：%s",
                            field.getDisplayName(), validationType, e.getMessage());
                log.severe(errorMessage);
                throw new RuntimeException(errorMessage, e);
            }
        }

        log.fine("字段" + fieldName + "校验完成");
    }

    /**
     * 获取校验类型的中文名称
     *
     * @param validationType 校验类型代码
     * @return 校验类型中文名称
     */
    private String getValidationTypeName(String validationType) {
        if (validationType == null) {
            return "未知";
        }

        MetadataValidationRuleTypeEnum typeEnum = MetadataValidationRuleTypeEnum.getByCode(validationType);
        if (typeEnum == null) {
            return validationType;
        }

        return switch (typeEnum) {
            case REQUIRED -> "必填";
            case LENGTH, LENGTH_RANGE -> "长度";
            case FORMAT -> "格式";
            case UNIQUE -> "唯一性";
            case RANGE -> "范围";
            case REGEX -> "正则表达式";
            case CUSTOM, SELF_DEFINED -> "自定义";
            default -> validationType;
        };
    }

    /**
     * 执行实体所有字段的校验
     *
     * @param entityUuid    实体UUID
     * @param fields        字段列表
     * @param data          数据对象
     * @param subEntities   子实体上下文列表
     * @param operationType 操作类型
     */
    public void validateEntity(String entityUuid, List<MetadataEntityFieldDO> fields, Map<String, Object> data, List<MetadataDataMethodSubEntityContext> subEntities, MetadataDataMethodOpEnum operationType) {
        log.info("开始校验实体：entityUuid=" + entityUuid + ", 字段数量=" + fields.size());

        Set<String> fieldUuids = fields.stream()
                .filter(f -> f.getIsSystemField() == null || f.getIsSystemField() != 1)
                .filter(f -> f.getIsPrimaryKey() == null || f.getIsPrimaryKey() != 1)
                .filter(f -> operationType != MetadataDataMethodOpEnum.UPDATE || data.get(f.getFieldName()) != null)
                .map(MetadataEntityFieldDO::getFieldUuid)
                .filter(u -> u != null && !u.isBlank())
                .collect(Collectors.toSet());
        Map<String, Map<String, ? extends java.util.List<?>>> rulesByType = new HashMap<>();
        if (!fieldUuids.isEmpty()) {
            try {
                var requiredList = requiredRepository.findByFieldUuids(fieldUuids);
                Map<String, java.util.List<com.cmsr.onebase.module.metadata.core.dal.dataobject.validation.MetadataValidationRequiredDO>> requiredMap = requiredList.stream().filter(r -> r.getFieldUuid() != null)
                        .collect(Collectors.groupingBy(com.cmsr.onebase.module.metadata.core.dal.dataobject.validation.MetadataValidationRequiredDO::getFieldUuid,
                                Collectors.mapping(r -> r, Collectors.toList())));
                rulesByType.put(MetadataValidationRuleTypeEnum.REQUIRED.getCode(), requiredMap);
            } catch (Exception ignore) {}
            try {
                var uniqueList = uniqueRepository.findByFieldUuids(fieldUuids);
                Map<String, java.util.List<com.cmsr.onebase.module.metadata.core.dal.dataobject.validation.MetadataValidationUniqueDO>> uniqueMap = uniqueList.stream().filter(r -> r.getFieldUuid() != null)
                        .collect(Collectors.groupingBy(com.cmsr.onebase.module.metadata.core.dal.dataobject.validation.MetadataValidationUniqueDO::getFieldUuid,
                                Collectors.mapping(r -> r, Collectors.toList())));
                rulesByType.put(MetadataValidationRuleTypeEnum.UNIQUE.getCode(), uniqueMap);
            } catch (Exception ignore) {}
            try {
                var lengthList = lengthRepository.findByFieldUuids(fieldUuids);
                Map<String, java.util.List<com.cmsr.onebase.module.metadata.core.dal.dataobject.validation.MetadataValidationLengthDO>> lengthMap = lengthList.stream().filter(r -> r.getFieldUuid() != null)
                        .collect(Collectors.groupingBy(com.cmsr.onebase.module.metadata.core.dal.dataobject.validation.MetadataValidationLengthDO::getFieldUuid,
                                Collectors.mapping(r -> r, Collectors.toList())));
                rulesByType.put(MetadataValidationRuleTypeEnum.LENGTH.getCode(), lengthMap);
            } catch (Exception ignore) {}
            try {
                var formatList = formatRepository.findByFieldUuids(fieldUuids);
                Map<String, java.util.List<com.cmsr.onebase.module.metadata.core.dal.dataobject.validation.MetadataValidationFormatDO>> formatMap = formatList.stream().filter(r -> r.getFieldUuid() != null)
                        .collect(Collectors.groupingBy(com.cmsr.onebase.module.metadata.core.dal.dataobject.validation.MetadataValidationFormatDO::getFieldUuid,
                                Collectors.mapping(r -> r, Collectors.toList())));
                rulesByType.put(MetadataValidationRuleTypeEnum.FORMAT.getCode(), formatMap);
            } catch (Exception ignore) {}
            try {
                var rangeList = rangeRepository.findByFieldUuids(fieldUuids);
                Map<String, java.util.List<com.cmsr.onebase.module.metadata.core.dal.dataobject.validation.MetadataValidationRangeDO>> rangeMap = rangeList.stream().filter(r -> r.getFieldUuid() != null)
                        .collect(Collectors.groupingBy(com.cmsr.onebase.module.metadata.core.dal.dataobject.validation.MetadataValidationRangeDO::getFieldUuid,
                                Collectors.mapping(r -> r, Collectors.toList())));
                rulesByType.put(MetadataValidationRuleTypeEnum.RANGE.getCode(), rangeMap);
            } catch (Exception ignore) {}
        }
        for (ValidationService service : validationServices) {
            if (service instanceof PrefetchableValidationService pvs) {
                try {
                    pvs.preloadBatchRules(rulesByType);
                } catch (Exception e) {
                    log.fine("预加载规则到服务失败，type=" + service.getValidationType() + ", err=" + e.getMessage());
                }
            }
        }

        for (ValidationService service : validationServices){
            try{
                if (MetadataValidationRuleTypeEnum.CHILD_NOT_EMPTY.getCode().equals(service.getValidationType())) {
                    log.fine("执行" + service.getValidationType() + "校验：entityUuid=" + entityUuid);
                    service.validate(entityUuid, null, null, null, data, subEntities);
                }
            }catch (Exception e){
                String errorMessage = String.format("子表非空%s校验失败：%s", service.getValidationType(), e.getMessage());;
                log.severe(errorMessage);
                throw new RuntimeException(errorMessage, e);
            }
        }

        for (MetadataEntityFieldDO field : fields) {
            String fieldName = field.getFieldName();
            Object value = data.get(fieldName);
            
            // 跳过系统字段和主键字段
            if (field.getIsSystemField() != null && field.getIsSystemField() == 1) {
                log.fine("跳过系统字段：" + fieldName);
                continue;
            }
            
            if (field.getIsPrimaryKey() != null && field.getIsPrimaryKey() == 1) {
                log.fine("跳过主键字段：" + fieldName);
                continue;
            }

            //跳过当前不更新的字段(更新时生效)
            if (operationType == MetadataDataMethodOpEnum.UPDATE) {
                if (value == null) {
                    continue;
                }
            }

            validateField(entityUuid, field, value, data, subEntities);
        }

        for (ValidationService service : validationServices) {
            if (service instanceof PrefetchableValidationService pvs) {
                try {
                    pvs.clearPrefetchedRules();
                } catch (Exception ignore) {}
            }
        }

        log.info("实体" + entityUuid + "校验完成");
    }
}
