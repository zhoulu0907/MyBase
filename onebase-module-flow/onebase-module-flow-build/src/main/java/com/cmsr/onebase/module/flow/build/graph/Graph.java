package com.cmsr.onebase.module.flow.build.graph;

import com.cmsr.onebase.module.flow.context.condition.ConditionItem;
import com.cmsr.onebase.module.metadata.api.entity.dto.EntityFieldJdbcTypeRespDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.common.collect.Lists;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @Author：huangjie
 * @Date：2025/9/29 20:33
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Graph {

    private List<GraphNode> nodes;


    public List<Long> findAllFieldId() {
        Set<Long> fieldIds = new HashSet<>();
        recursionFindFieldId(nodes, fieldIds);
        return Lists.newArrayList(fieldIds);
    }

    private void recursionFindFieldId(List<GraphNode> nodes, Set<Long> fieldIds) {
        if (CollectionUtils.isEmpty(nodes)) {
            return;
        }

        for (GraphNode node : nodes) {
            // 处理过滤条件中的字段
            processFilterConditionsForFieldId(node.getData().getFilterCondition(), fieldIds);

            // 处理节点数据中的字段
            processFieldsForFieldId(node.getData().getFields(), fieldIds);

            // 递归处理子节点
            recursionFindFieldId(node.getBlocks(), fieldIds);
        }
    }

    private void processFilterConditionsForFieldId(List<GraphNodeDataConditions> filterConditions, Set<Long> fieldIds) {
        if (CollectionUtils.isEmpty(filterConditions)) {
            return;
        }
        for (GraphNodeDataConditions conditionGroup : filterConditions) {
            if (CollectionUtils.isEmpty(conditionGroup.getConditions())) {
                continue;
            }
            for (ConditionItem condition : conditionGroup.getConditions()) {
                if (condition != null) {
                    addToFieldIds(condition.getFieldId(), fieldIds);
                }
            }

        }
    }

    private void processFieldsForFieldId(List<ConditionItem> fields, Set<Long> fieldIds) {
        if (CollectionUtils.isEmpty(fields)) {
            return;
        }
        for (ConditionItem field : fields) {
            addToFieldIds(field.getFieldId(), fieldIds);
        }
    }

    private void addToFieldIds(String fieldId, Set<Long> fieldIds) {
        if (StringUtils.isEmpty(fieldId)) {
            return;
        }
        Long id = parseFieldId(fieldId);
        if (id != null) {
            fieldIds.add(id);
        }
    }

    private Long parseFieldId(String fieldId) {
        if (StringUtils.isEmpty(fieldId)) {
            return null;
        }
        String[] split = StringUtils.split(fieldId, ".");
        if (split.length == 1 && StringUtils.isNumeric(split[0])) {
            return NumberUtils.toLong(split[0]);
        } else if (split.length == 2 && StringUtils.isNumeric(split[1])) {
            return NumberUtils.toLong(split[1]);
        }
        return null;
    }


    public void updateFieldDataType(Map<Long, EntityFieldJdbcTypeRespDTO> fieldInfoMap) {
        recursionUpdateFieldDataType(nodes, fieldInfoMap);
    }

    private void recursionUpdateFieldDataType(List<GraphNode> nodes, Map<Long, EntityFieldJdbcTypeRespDTO> fieldInfoMap) {
        if (CollectionUtils.isEmpty(nodes)) {
            return;
        }

        for (GraphNode node : nodes) {
            // 处理过滤条件中的字段类型
            processFilterConditionsForDataType(node.getData().getFilterCondition(), fieldInfoMap);
            // 处理节点数据中的字段类型
            processFieldsForDataType(node.getData().getFields(), fieldInfoMap);
            // 递归处理子节点
            recursionUpdateFieldDataType(node.getBlocks(), fieldInfoMap);
        }
    }

    private void processFilterConditionsForDataType(List<GraphNodeDataConditions> filterConditions,
                                                    Map<Long, EntityFieldJdbcTypeRespDTO> fieldInfoMap) {
        if (CollectionUtils.isEmpty(filterConditions)) {
            return;
        }
        for (GraphNodeDataConditions conditionGroup : filterConditions) {
            if (CollectionUtils.isEmpty(conditionGroup.getConditions())) {
                continue;
            }
            for (ConditionItem condition : conditionGroup.getConditions()) {
                if (condition != null) {
                    updateRuleItemDataType(condition, fieldInfoMap);
                }
            }
        }
    }

    private void processFieldsForDataType(List<ConditionItem> fields, Map<Long, EntityFieldJdbcTypeRespDTO> fieldInfoMap) {
        if (CollectionUtils.isEmpty(fields)) {
            return;
        }
        for (ConditionItem field : fields) {
            updateRuleItemDataType(field, fieldInfoMap);
        }
    }

    private void updateRuleItemDataType(ConditionItem conditionItem, Map<Long, EntityFieldJdbcTypeRespDTO> fieldInfoMap) {
        if (conditionItem == null || StringUtils.isEmpty(conditionItem.getFieldId())) {
            return;
        }
        String fieldId = conditionItem.getFieldId();
        Long id = parseFieldId(fieldId);
        if (id != null) {
            EntityFieldJdbcTypeRespDTO fieldInfo = fieldInfoMap.get(id);
            if (fieldInfo != null) {
                conditionItem.setFieldType(fieldInfo.getFieldType());
                conditionItem.setJdbcType(fieldInfo.getJdbcType());
            }
        }
    }
}
