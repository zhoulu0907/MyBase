package com.cmsr.onebase.module.flow.component.utils;

import com.cmsr.onebase.framework.tenant.core.util.TenantUtils;
import com.cmsr.onebase.module.flow.context.FieldTypeProvider;
import com.cmsr.onebase.module.flow.context.condition.ConditionItem;
import com.cmsr.onebase.module.flow.context.condition.Conditions;
import com.cmsr.onebase.module.flow.context.graph.JsonGraph;
import com.cmsr.onebase.module.flow.context.graph.JsonGraphNode;
import com.cmsr.onebase.module.flow.context.graph.NodeData;
import com.cmsr.onebase.module.flow.context.graph.nodes.*;
import com.cmsr.onebase.module.metadata.api.entity.MetadataEntityFieldApi;
import com.cmsr.onebase.module.metadata.api.entity.dto.EntityFieldJdbcTypeReqDTO;
import com.cmsr.onebase.module.metadata.api.entity.dto.EntityFieldJdbcTypeRespDTO;
import com.google.common.collect.Lists;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @Author：huangjie
 * @Date：2025/10/14 17:53
 */
@Setter
@Component
public class FieldTypeProviderImpl implements FieldTypeProvider {

    @Autowired
    private MetadataEntityFieldApi metadataEntityFieldApi;

    @Override
    public void completeFieldType(JsonGraph jsonGraph) {
        List<Long> allFieldId = findAllFieldId(jsonGraph);
        Map<Long, EntityFieldJdbcTypeRespDTO> fieldInfoMap = selectFieldInfoMap(allFieldId);
        updateFieldDataType(jsonGraph, fieldInfoMap);
    }

    private List<Long> findAllFieldId(JsonGraph jsonGraph) {
        Set<Long> fieldIds = new HashSet<>();
        recursionFindFieldId(jsonGraph.getNodes(), fieldIds);
        return Lists.newArrayList(fieldIds);
    }

    private void recursionFindFieldId(List<JsonGraphNode> nodes, Set<Long> fieldIds) {
        if (CollectionUtils.isEmpty(nodes)) {
            return;
        }
        for (JsonGraphNode node : nodes) {
            NodeData nodeData = node.getData();
            if (nodeData instanceof DataAddNodeData n) {
                processForFieldIdSub(n.getFields(), fieldIds);
            } else if (nodeData instanceof DataDeleteeNodeData n) {
                processForFieldIdTop(n.getFilterCondition(), fieldIds);
            } else if (nodeData instanceof DataQueryMultipleNodeData n) {
                processForFieldIdTop(n.getFilterCondition(), fieldIds);
            } else if (nodeData instanceof DataQueryNodeData n) {
                processForFieldIdTop(n.getFilterCondition(), fieldIds);
            } else if (nodeData instanceof DataUpdateNodeData n) {
                processForFieldIdTop(n.getFilterCondition(), fieldIds);
                processForFieldIdSub(n.getFields(), fieldIds);
            } else if (nodeData instanceof IfCaseNodeData n) {
                processForFieldIdTop(n.getFilterCondition(), fieldIds);
            } else if (nodeData instanceof StartDateFieldNodeData n) {
                processForFieldIdTop(n.getFilterCondition(), fieldIds);
            } else if (nodeData instanceof StartEntityNodeData n) {
                processForFieldIdTop(n.getFilterCondition(), fieldIds);
            } else if (nodeData instanceof StartFormNodeData n) {
                processForFieldIdTop(n.getFilterCondition(), fieldIds);
            } else if (nodeData instanceof SwitchCaseNodeData n) {
                processForFieldIdTop(n.getFilterCondition(), fieldIds);
            }
            // 递归处理子节点
            recursionFindFieldId(node.getBlocks(), fieldIds);
        }
    }

    private void processForFieldIdTop(List<Conditions> filterCondition, Set<Long> fieldIds) {
        if (CollectionUtils.isEmpty(filterCondition)) {
            return;
        }
        for (Conditions conditions : filterCondition) {
            processForFieldIdSub(conditions.getConditions(), fieldIds);
        }
    }

    private void processForFieldIdSub(List<ConditionItem> filterConditions, Set<Long> fieldIds) {
        if (CollectionUtils.isEmpty(filterConditions)) {
            return;
        }
        for (ConditionItem conditionItem : filterConditions) {
            if (StringUtils.isNumeric(conditionItem.getFieldId())) {
                fieldIds.add(NumberUtils.toLong(conditionItem.getFieldId()));
            }
        }
    }

    private Map<Long, EntityFieldJdbcTypeRespDTO> selectFieldInfoMap(List<Long> fieldIds) {
        EntityFieldJdbcTypeReqDTO reqDTO = new EntityFieldJdbcTypeReqDTO();
        reqDTO.setFieldIds(fieldIds);
        List<EntityFieldJdbcTypeRespDTO> fieldJdbcTypes = TenantUtils.executeIgnore(() -> metadataEntityFieldApi.getFieldJdbcTypes(reqDTO));
        return fieldJdbcTypes.stream()
                .collect(Collectors.toMap(EntityFieldJdbcTypeRespDTO::getFieldId, info -> info));
    }

    public void updateFieldDataType(JsonGraph jsonGraph, Map<Long, EntityFieldJdbcTypeRespDTO> fieldInfoMap) {
        recursionUpdateFieldDataType(jsonGraph.getNodes(), fieldInfoMap);
    }

    private void recursionUpdateFieldDataType(List<JsonGraphNode> nodes, Map<Long, EntityFieldJdbcTypeRespDTO> fieldInfoMap) {
        if (CollectionUtils.isEmpty(nodes)) {
            return;
        }
        for (JsonGraphNode node : nodes) {
            NodeData nodeData = node.getData();
            if (nodeData instanceof DataAddNodeData n) {
                processForFieldTypeSub(n.getFields(), fieldInfoMap);
            } else if (nodeData instanceof DataDeleteeNodeData n) {
                processForFieldIdTypeTop(n.getFilterCondition(), fieldInfoMap);
            } else if (nodeData instanceof DataQueryMultipleNodeData n) {
                processForFieldIdTypeTop(n.getFilterCondition(), fieldInfoMap);
            } else if (nodeData instanceof DataQueryNodeData n) {
                processForFieldIdTypeTop(n.getFilterCondition(), fieldInfoMap);
            } else if (nodeData instanceof DataUpdateNodeData n) {
                processForFieldIdTypeTop(n.getFilterCondition(), fieldInfoMap);
                processForFieldTypeSub(n.getFields(), fieldInfoMap);
            } else if (nodeData instanceof IfCaseNodeData n) {
                processForFieldIdTypeTop(n.getFilterCondition(), fieldInfoMap);
            } else if (nodeData instanceof StartDateFieldNodeData n) {
                processForFieldIdTypeTop(n.getFilterCondition(), fieldInfoMap);
            } else if (nodeData instanceof StartEntityNodeData n) {
                processForFieldIdTypeTop(n.getFilterCondition(), fieldInfoMap);
            } else if (nodeData instanceof StartFormNodeData n) {
                processForFieldIdTypeTop(n.getFilterCondition(), fieldInfoMap);
            } else if (nodeData instanceof SwitchCaseNodeData n) {
                processForFieldIdTypeTop(n.getFilterCondition(), fieldInfoMap);
            }
            // 递归处理子节点
            recursionUpdateFieldDataType(node.getBlocks(), fieldInfoMap);
        }


    }

    private void processForFieldIdTypeTop(List<Conditions> filterCondition, Map<Long, EntityFieldJdbcTypeRespDTO> fieldInfoMap) {
        if (CollectionUtils.isEmpty(filterCondition)) {
            return;
        }
        for (Conditions conditions : filterCondition) {
            processForFieldTypeSub(conditions.getConditions(), fieldInfoMap);
        }
    }

    private void processForFieldTypeSub(List<ConditionItem> fields, Map<Long, EntityFieldJdbcTypeRespDTO> fieldInfoMap) {
        if (CollectionUtils.isEmpty(fields)) {
            return;
        }
        for (ConditionItem conditionItem : fields) {
            if (!StringUtils.isNumeric(conditionItem.getFieldId())) {
                continue;
            }
            EntityFieldJdbcTypeRespDTO fieldInfo = fieldInfoMap.get(NumberUtils.toLong(conditionItem.getFieldId()));
            if (fieldInfo == null) {
                continue;
            }
            conditionItem.setJdbcType(fieldInfo.getJdbcType());
            conditionItem.setFieldType(fieldInfo.getFieldType());
        }
    }
}
