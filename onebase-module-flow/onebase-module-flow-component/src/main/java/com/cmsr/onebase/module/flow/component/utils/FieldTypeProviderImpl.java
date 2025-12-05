package com.cmsr.onebase.module.flow.component.utils;

import com.cmsr.onebase.module.flow.context.FieldTypeProvider;
import com.cmsr.onebase.module.flow.context.condition.ConditionItem;
import com.cmsr.onebase.module.flow.context.condition.Conditions;
import com.cmsr.onebase.module.flow.context.graph.JsonGraph;
import com.cmsr.onebase.module.flow.context.graph.JsonGraphNode;
import com.cmsr.onebase.module.flow.context.graph.NodeData;
import com.cmsr.onebase.module.flow.context.graph.nodes.*;
import com.cmsr.onebase.module.metadata.api.semantic.SemanticDynamicDataApi;
import com.cmsr.onebase.module.metadata.core.semantic.dto.SemanticFieldSchemaDTO;
import com.cmsr.onebase.module.metadata.core.semantic.dto.enums.SemanticFieldTypeEnum;
import com.google.common.collect.Lists;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @Author：huangjie
 * @Date：2025/10/14 17:53
 */
@Setter
@Component
public class FieldTypeProviderImpl implements FieldTypeProvider {

    @Autowired
    private SemanticDynamicDataApi semanticDynamicDataApi;

    @Override
    public void completeFieldType(JsonGraph jsonGraph) {
        Set<String> fieldUuids = new HashSet<>();
        recursionUpdateFieldDataType(jsonGraph.getNodes(), fieldUuids);
        List<SemanticFieldSchemaDTO> semanticFieldSchemaDTOS = semanticDynamicDataApi.buildEntityFieldsSchemaByTableName(Lists.newArrayList(fieldUuids));
        HashMap<String, SemanticFieldSchemaDTO> fieldUuidMap = new HashMap<>();
        for (SemanticFieldSchemaDTO semanticFieldSchemaDTO : semanticFieldSchemaDTOS) {
            fieldUuidMap.put(semanticFieldSchemaDTO.getFieldUuid(), semanticFieldSchemaDTO);
        }
        recursionUpdateFieldDataType(jsonGraph.getNodes(), fieldUuidMap);
    }


    private void recursionUpdateFieldDataType(List<JsonGraphNode> nodes, Object arg) {
        if (CollectionUtils.isEmpty(nodes)) {
            return;
        }
        for (JsonGraphNode node : nodes) {
            NodeData nodeData = node.getData();
            if (nodeData instanceof DataAddNodeData n) {
                processForFieldTypeSub(n.getFields(), arg);
            } else if (nodeData instanceof DataDeleteeNodeData n) {
                processForFieldIdTypeTop(n.getFilterCondition(), arg);
            } else if (nodeData instanceof DataQueryMultipleNodeData n) {
                processForFieldIdTypeTop(n.getFilterCondition(), arg);
            } else if (nodeData instanceof DataQueryNodeData n) {
                processForFieldIdTypeTop(n.getFilterCondition(), arg);
            } else if (nodeData instanceof DataUpdateNodeData n) {
                processForFieldIdTypeTop(n.getFilterCondition(), arg);
                processForFieldTypeSub(n.getFields(), arg);
            } else if (nodeData instanceof IfCaseNodeData n) {
                processForFieldIdTypeTop(n.getFilterCondition(), arg);
            } else if (nodeData instanceof StartDateFieldNodeData n) {
                processForFieldIdTypeTop(n.getFilterCondition(), arg);
            } else if (nodeData instanceof StartEntityNodeData n) {
                processForFieldIdTypeTop(n.getFilterCondition(), arg);
            } else if (nodeData instanceof StartFormNodeData n) {
                processForFieldIdTypeTop(n.getFilterCondition(), arg);
            } else if (nodeData instanceof SwitchCaseNodeData n) {
                processForFieldIdTypeTop(n.getFilterCondition(), arg);
            }
            // 递归处理子节点
            recursionUpdateFieldDataType(node.getBlocks(), arg);
        }
    }

    private void processForFieldIdTypeTop(List<Conditions> filterCondition, Object arg) {
        if (CollectionUtils.isEmpty(filterCondition)) {
            return;
        }
        for (Conditions conditions : filterCondition) {
            processForFieldTypeSub(conditions.getConditions(), arg);
        }
    }

    private void processForFieldTypeSub(List<ConditionItem> fields, Object arg) {
        if (CollectionUtils.isEmpty(fields)) {
            return;
        }
        for (ConditionItem conditionItem : fields) {
            String fieldUuid = conditionItem.getFieldUuid();
            if (conditionItem.getFieldUuid().contains(".")) {
                fieldUuid = StringUtils.substringAfter(fieldUuid, ".");
            }
            if (StringUtils.isEmpty(fieldUuid)) {
                continue;
            }
            if (arg instanceof Set fieldUuids) {
                fieldUuids.add(fieldUuid);
            } else if (arg instanceof Map fieldUuidMap) {
                SemanticFieldSchemaDTO fieldInfo = (SemanticFieldSchemaDTO) fieldUuidMap.get(fieldUuid);
                conditionItem.setFieldType(fieldInfo.getFieldType());
                SemanticFieldTypeEnum fieldTypeEnum = fieldInfo.getFieldTypeEnum();
                //TODO SemanticFieldTypeEnum 要写到 conditionItem里面
            }
        }
    }


}
