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
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

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
    private SemanticDynamicDataApi semanticDynamicDataApi;

    @Value("${lite-flow.version-tag:1L}")
    private Long versionTag;


    @Override
    public void completeFieldType(Long applicationId, JsonGraph jsonGraph) {
        RecursionUpdateFieldType recursionUpdateFieldDataType = new RecursionUpdateFieldType();
        recursionUpdateFieldDataType.applicationId = applicationId;
        recursionUpdateFieldDataType.jsonGraph = jsonGraph;
        recursionUpdateFieldDataType.doUpdate();

    }

    @Setter
    public static class RecursionUpdateFieldType {

        private Long applicationId;

        private JsonGraph jsonGraph;

        private Map<String, String> nodeTableNameMap;

        public void doUpdate() {
            recursionUpdateFieldDataType(jsonGraph.getNodes());
        }


        private void recursionUpdateFieldDataType(List<JsonGraphNode> nodes) {
            if (CollectionUtils.isEmpty(nodes)) {
                return;
            }
            for (JsonGraphNode node : nodes) {
                NodeData nodeData = node.getData();
                if (nodeData instanceof DataAddNodeData n) {
                    String tableName;
                    if (StringUtils.equals(n.getAddType(), "mainEntity")) {
                        tableName = n.getMainEntityName();
                    } else {
                        tableName = n.getSubEntityName();
                    }
                    nodeTableNameMap.put(node.getId(), tableName);
                } else if (nodeData instanceof DataDeleteeNodeData n) {
                    String tableName;
                    if (StringUtils.equals(n.getDataType(), "mainEntity")) {
                        tableName = n.getMainEntityName();
                    } else {
                        tableName = n.getSubEntityName();
                    }
                    nodeTableNameMap.put(node.getId(), tableName);
                } else if (nodeData instanceof DataQueryMultipleNodeData n) {
                    String tableName;
                    if (StringUtils.equals(n.getDataType(), "mainEntity")) {
                        tableName = n.getMainEntityName();
                    } else {
                        tableName = n.getSubEntityName();
                    }
                    nodeTableNameMap.put(node.getId(), tableName);
                } else if (nodeData instanceof DataQueryNodeData n) {
                    String tableName;
                    if (StringUtils.equals(n.getDataType(), "mainEntity")) {
                        tableName = n.getMainEntityName();
                    } else {
                        tableName = n.getSubEntityName();
                    }
                    nodeTableNameMap.put(node.getId(), tableName);
                } else if (nodeData instanceof DataUpdateNodeData n) {
                    String tableName;
                    if (StringUtils.equals(n.getUpdateType(), "mainEntity")) {
                        tableName = n.getMainEntityName();
                    } else {
                        tableName = n.getSubEntityName();
                    }
                    nodeTableNameMap.put(node.getId(), tableName);
                } else if (nodeData instanceof IfCaseNodeData n) {
                    processForFieldIdTypeTop(n.getFilterCondition());
                } else if (nodeData instanceof StartDateFieldNodeData n) {
                    processForFieldIdTypeTop(n.getEntityName(), n.getFilterCondition());
                } else if (nodeData instanceof StartEntityNodeData n) {
                    processForFieldIdTypeTop(n.getEntityName(), n.getFilterCondition());
                } else if (nodeData instanceof StartFormNodeData n) {
                    processForFieldIdTypeTop(n.getFilterCondition());
                } else if (nodeData instanceof SwitchCaseNodeData n) {
                    processForFieldIdTypeTop(n.getFilterCondition());
                }
                // 递归处理子节点
                recursionUpdateFieldDataType(node.getBlocks());
            }
        }

        private void processForFieldIdTypeTop(List<Conditions> filterCondition) {
            if (CollectionUtils.isEmpty(filterCondition)) {
                return;
            }
            filterCondition.stream().flatMap(conditions -> conditions.getConditions().stream())
                    .map(conditionItem -> {
                        String fieldName = conditionItem.getFieldName();
                        if(fieldName.contains("."))

                    }).collect(Collectors.toSet());
            for (Conditions conditions : filterCondition) {
                processForFieldTypeSub(conditions.getConditions());
            }
        }

        private void processForFieldTypeSub(List<ConditionItem> fields) {
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

}
