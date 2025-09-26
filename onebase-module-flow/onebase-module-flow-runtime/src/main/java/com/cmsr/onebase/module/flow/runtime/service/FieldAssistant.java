package com.cmsr.onebase.module.flow.runtime.service;

import com.cmsr.onebase.framework.common.express.JdbcTypeConvertor;
import com.cmsr.onebase.module.flow.context.express.ConditionItem;
import com.cmsr.onebase.module.flow.context.express.RuleItem;
import com.cmsr.onebase.module.metadata.api.entity.MetadataEntityFieldApi;
import com.cmsr.onebase.module.metadata.api.entity.dto.EntityFieldJdbcTypeReqDTO;
import com.cmsr.onebase.module.metadata.api.entity.dto.EntityFieldJdbcTypeRespDTO;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author：huangjie
 * @Date：2025/9/17 13:23
 */
@Slf4j
@Setter
@Component
public class FieldAssistant {

    @Autowired
    private MetadataEntityFieldApi metadataEntityFieldApi;

    /**
     * 转换字段数据
     * 使用下面的方法，先查询到具体的数据JDBC类型
     * com.cmsr.onebase.module.metadata.api.entity.MetadataEntityFieldApi#getFieldJdbcTypes(com.cmsr.onebase.module.metadata.api.entity.dto.EntityFieldJdbcTypeReqDTO)
     * 然后根据下面的方法转换成具体的类型
     * com.cmsr.onebase.framework.common.express.JdbcTypeConvertor#convert(String, java.lang.Object)
     *
     * @param inputParams
     * @return
     */
    public Map<String, Object> convertInputParamsData(Map<Long, String> inputParams) {
        if (inputParams == null || inputParams.isEmpty()) {
            return new HashMap<>();
        }

        List<Long> fieldIds = extractFieldIds(inputParams);
        Map<Long, EntityFieldJdbcTypeRespDTO> fieldInfoMap = getFieldInfoMap(fieldIds);

        return convertInputParamsToResult(inputParams, fieldInfoMap);
    }

    /**
     * 1、根据fieldId设置RuleItem对象的 fieldName 和  fieldJdbcType。
     * 2、根据fieldJdbcType，设置 fieldValue 的值。
     * com.cmsr.onebase.module.metadata.api.entity.MetadataEntityFieldApi#getFieldJdbcTypes(com.cmsr.onebase.module.metadata.api.entity.dto.EntityFieldJdbcTypeReqDTO)
     *
     * @param conditions
     */
    public void fillFilterFieldData(List<ConditionItem> conditions) {
        if (conditions == null || conditions.isEmpty()) {
            return;
        }

        List<Long> fieldIds = collectFieldIdsFromConditions(conditions);
        if (fieldIds.isEmpty()) {
            return;
        }

        Map<Long, EntityFieldJdbcTypeRespDTO> fieldInfoMap = getFieldInfoMap(fieldIds);
        fillRuleItemsWithFieldData(conditions, fieldInfoMap);
    }

    /**
     * 提取字段ID列表
     */
    private List<Long> extractFieldIds(Map<Long, String> inputParams) {
        return inputParams.keySet().stream().collect(Collectors.toList());
    }

    /**
     * 从条件列表中收集所有字段ID
     */
    private List<Long> collectFieldIdsFromConditions(List<ConditionItem> conditions) {
        return conditions.stream()
                .flatMap(condition -> condition.getRules().stream())
                .map(RuleItem::getFieldId)
                .filter(fieldId -> fieldId != null)
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * 获取字段信息映射
     */
    private Map<Long, EntityFieldJdbcTypeRespDTO> getFieldInfoMap(List<Long> fieldIds) {
        EntityFieldJdbcTypeReqDTO reqDTO = new EntityFieldJdbcTypeReqDTO();
        reqDTO.setFieldIds(fieldIds);

        List<EntityFieldJdbcTypeRespDTO> fieldJdbcTypes = metadataEntityFieldApi.getFieldJdbcTypes(reqDTO);

        return fieldJdbcTypes.stream()
                .collect(Collectors.toMap(EntityFieldJdbcTypeRespDTO::getFieldId, info -> info));
    }

    /**
     * 转换输入参数为结果映射
     */
    private Map<String, Object> convertInputParamsToResult(Map<Long, String> inputParams,
                                                           Map<Long, EntityFieldJdbcTypeRespDTO> fieldInfoMap) {
        Map<String, Object> result = new HashMap<>();

        for (Map.Entry<Long, String> entry : inputParams.entrySet()) {
            Long fieldId = entry.getKey();
            String inputValue = entry.getValue();

            EntityFieldJdbcTypeRespDTO fieldInfo = fieldInfoMap.get(fieldId);
            if (fieldInfo == null) {
                throw new IllegalArgumentException("找不到字段ID为 " + fieldId + " 的字段信息");
            }

            Object convertedValue = convertFieldValue(fieldId, fieldInfo, inputValue);
            result.put(fieldInfo.getFieldName(), convertedValue);
        }

        return result;
    }

    /**
     * 填充规则项的字段数据
     */
    private void fillRuleItemsWithFieldData(List<ConditionItem> conditions,
                                            Map<Long, EntityFieldJdbcTypeRespDTO> fieldInfoMap) {
        for (ConditionItem condition : conditions) {
            if (condition.getRules() != null) {
                for (RuleItem rule : condition.getRules()) {
                    fillSingleRuleItem(rule, fieldInfoMap);
                }
            }
        }
    }

    /**
     * 填充单个规则项的字段数据
     */
    private void fillSingleRuleItem(RuleItem rule, Map<Long, EntityFieldJdbcTypeRespDTO> fieldInfoMap) {
        Long fieldId = rule.getFieldId();
        if (fieldId == null) {
            return;
        }

        EntityFieldJdbcTypeRespDTO fieldInfo = fieldInfoMap.get(fieldId);
        if (fieldInfo == null) {
            log.warn("找不到字段ID为 {} 的字段信息", fieldId);
            return;
        }

        // 设置字段名称和JDBC类型
        rule.setFieldName(fieldInfo.getFieldName());
        rule.setFieldJdbcType(fieldInfo.getJdbcType());

        // 转换字段值
        if (!hasValidValue(rule) || fieldInfo.getJdbcType() == null) {
            return;
        }
        // TODO 这里只处理了OperatorTypeEnum类型为value，并且没有根据OpEnum去校验值的多少
        if (rule.getValue().size() == 1) {
            String firstValue = rule.getValue().get(0);
            Object convertedValue = JdbcTypeConvertor.convert(fieldInfo.getJdbcType(), firstValue);
            rule.setFieldValue(convertedValue);
        } else {
            Object[] convertedValue = new Object[rule.getValue().size()];
            for (int i = 0; i < rule.getValue().size(); i++) {
                String value = rule.getValue().get(i);
                convertedValue[i] = JdbcTypeConvertor.convert(fieldInfo.getJdbcType(), value);
            }
            rule.setFieldValue(convertedValue);
        }
    }

    /**
     * 检查规则项是否有有效值
     */
    private boolean hasValidValue(RuleItem rule) {
        return rule.getValue() != null && !rule.getValue().isEmpty();
    }

    /**
     * 转换字段值（带错误处理）
     */
    private Object convertFieldValueWithErrorHandling(Long fieldId, EntityFieldJdbcTypeRespDTO fieldInfo, String value) {
        try {
            return JdbcTypeConvertor.convert(fieldInfo.getJdbcType(), value);
        } catch (Exception e) {
            log.warn("字段值转换失败，字段ID: {}, 字段名: {}, JDBC类型: {}, 原始值: {}, 错误: {}",
                    fieldId, fieldInfo.getFieldName(), fieldInfo.getJdbcType(), value, e.getMessage());
            return value; // 转换失败时保留原始字符串值
        }
    }

    /**
     * 转换字段值
     */
    private Object convertFieldValue(Long fieldId, EntityFieldJdbcTypeRespDTO fieldInfo, String inputValue) {
        if (inputValue == null || fieldInfo.getJdbcType() == null) {
            return inputValue;
        }

        try {
            return JdbcTypeConvertor.convert(fieldInfo.getJdbcType(), inputValue);
        } catch (Exception e) {
            log.warn("字段数据转换失败，字段ID: {}, 字段名: {}, JDBC类型: {}, 输入值: {}, 错误: {}",
                    fieldId, fieldInfo.getFieldName(), fieldInfo.getJdbcType(), inputValue, e.getMessage());
            return inputValue; // 转换失败时保留原始字符串值
        }
    }


}
