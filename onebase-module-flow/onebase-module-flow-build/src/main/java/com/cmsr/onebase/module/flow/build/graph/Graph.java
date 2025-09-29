package com.cmsr.onebase.module.flow.build.graph;

import com.cmsr.onebase.module.flow.context.condition.RuleItem;
import com.cmsr.onebase.module.metadata.api.entity.dto.EntityFieldJdbcTypeRespDTO;
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
            List<GraphNodeDataConditions> filterCondition = node.getData().getFilterCondition();
            if (CollectionUtils.isNotEmpty(filterCondition)) {
                for (GraphNodeDataConditions nodeDataConditions : filterCondition) {
                    List<RuleItem> conditions = nodeDataConditions.getConditions();
                    if (CollectionUtils.isNotEmpty(conditions)) {
                        for (RuleItem condition : conditions) {
                            String fieldId = condition.getFieldId();
                            addToFieldIds(fieldId, fieldIds);
                        }
                    }
                }
            }
            List<RuleItem> fields = node.getData().getFields();
            if (CollectionUtils.isNotEmpty(fields)) {
                for (RuleItem field : fields) {
                    String fieldId = field.getFieldId();
                    addToFieldIds(fieldId, fieldIds);
                }
            }
            recursionFindFieldId(node.getBlocks(), fieldIds);
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
        if (split.length == 1 && NumberUtils.isDigits(split[0])) {
            return NumberUtils.toLong(split[0]);
        } else if (split.length == 2 && NumberUtils.isDigits(split[1])) {
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
            List<GraphNodeDataConditions> filterCondition = node.getData().getFilterCondition();
            if (CollectionUtils.isNotEmpty(filterCondition)) {
                for (GraphNodeDataConditions nodeDataConditions : filterCondition) {
                    List<RuleItem> conditions = nodeDataConditions.getConditions();
                    if (CollectionUtils.isNotEmpty(conditions)) {
                        for (RuleItem condition : conditions) {
                            String fieldId = condition.getFieldId();
                            Long id = parseFieldId(fieldId);
                            if (id != null) {
                                EntityFieldJdbcTypeRespDTO fieldInfo = fieldInfoMap.get(id);
                                if (fieldInfo != null) {
                                    condition.setFieldType(fieldInfo.getFieldType());
                                    condition.setJdbcType(fieldInfo.getJdbcType());
                                }
                            }
                        }
                    }
                }
            }
            List<RuleItem> fields = node.getData().getFields();
            if (CollectionUtils.isNotEmpty(fields)) {
                for (RuleItem field : fields) {
                    String fieldId = field.getFieldId();
                    Long id = parseFieldId(fieldId);
                    if (id != null) {
                        EntityFieldJdbcTypeRespDTO fieldInfo = fieldInfoMap.get(id);
                        if (fieldInfo != null) {
                            field.setFieldType(fieldInfo.getFieldType());
                            field.setJdbcType(fieldInfo.getJdbcType());
                        }
                    }
                }
            }
        }
    }
}
