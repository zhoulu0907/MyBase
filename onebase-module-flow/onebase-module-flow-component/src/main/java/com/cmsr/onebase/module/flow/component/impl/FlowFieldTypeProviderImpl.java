package com.cmsr.onebase.module.flow.component.impl;

import com.cmsr.onebase.framework.common.security.ApplicationManager;
import com.cmsr.onebase.module.flow.context.condition.ConditionItem;
import com.cmsr.onebase.module.flow.context.condition.Conditions;
import com.cmsr.onebase.module.flow.context.graph.JsonGraph;
import com.cmsr.onebase.module.flow.context.graph.JsonGraphNode;
import com.cmsr.onebase.module.flow.context.graph.NodeData;
import com.cmsr.onebase.module.flow.context.graph.nodes.*;
import com.cmsr.onebase.module.flow.context.provider.FieldTypeProvider;
import com.cmsr.onebase.module.flow.core.config.FlowProperties;
import com.cmsr.onebase.module.metadata.api.semantic.SemanticDynamicDataApi;
import com.cmsr.onebase.module.metadata.core.semantic.dto.SemanticEntitySchemaDTO;
import com.cmsr.onebase.module.metadata.core.semantic.dto.SemanticFieldSchemaDTO;
import com.cmsr.onebase.module.metadata.core.semantic.dto.enums.SemanticFieldTypeEnum;
import com.mybatisflex.core.tenant.TenantManager;
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
public class FlowFieldTypeProviderImpl implements FieldTypeProvider {

    @Autowired
    private SemanticDynamicDataApi semanticDynamicDataApi;

    @Autowired
    private FlowProperties flowProperties;

//    @Autowired
//    private AppProvider appProvider;


    @Override
    public void completeFieldType(Long applicationId, JsonGraph jsonGraph) {
        Set<String> tableNames = new HashSet<>();
        recursionUpdateFieldDataType(jsonGraph.getNodes(), tableNames);
        if (tableNames.isEmpty()) {
            return;
        }
        Map<String, List<SemanticFieldSchemaDTO>> fieldInfoMap = findTableFieldSchema(applicationId, tableNames);
        recursionUpdateFieldDataType(jsonGraph.getNodes(), fieldInfoMap);
    }


    private void recursionUpdateFieldDataType(List<JsonGraphNode> nodes, Object arg) {
        if (CollectionUtils.isEmpty(nodes)) {
            return;
        }
        for (JsonGraphNode node : nodes) {
            NodeData nodeData = node.getData();
            if (nodeData instanceof IfCaseNodeData n) {
                processConditionList(n.getFilterCondition(), arg, 3);
            } else if (nodeData instanceof StartDateFieldNodeData n) {
                if (arg instanceof Set fieldNames) {
                    fieldNames.add(n.getTableName());
                }
                if (arg instanceof Map fieldInfoMap) {
                    List<SemanticFieldSchemaDTO> fieldSchemaDTOS = (List<SemanticFieldSchemaDTO>) fieldInfoMap.get(n.getTableName());
                    SemanticFieldTypeEnum fieldTypeEnum = findFieldTypeEnum(n.getOffsetFieldName(), fieldSchemaDTOS);
                    n.setOffsetFiledTypeEnum(fieldTypeEnum);
                }
                processConditionList(n.getFilterCondition(), arg, 2);
            } else if (nodeData instanceof StartEntityNodeData n) {
                processConditionList(n.getFilterCondition(), arg, 2);
            } else if (nodeData instanceof StartFormNodeData n) {
                processConditionList(n.getFilterCondition(), arg, 2);
                if (arg instanceof Map fieldInfoMap) {
                    // TODO
                }
            } else if (nodeData instanceof SwitchCaseNodeData n) {
                processConditionList(n.getFilterCondition(), arg, 3);
            }
            // 递归处理子节点
            recursionUpdateFieldDataType(node.getBlocks(), arg);
        }
    }

    private void processConditionList(List<Conditions> filterCondition, Object arg, int section) {
        if (CollectionUtils.isEmpty(filterCondition)) {
            return;
        }
        for (Conditions conditions : filterCondition) {
            processConditionItem(conditions.getConditions(), arg, section);
        }
    }

    /**
     * section 2 是为：tableName.fieldName
     * section 3 是为：节点id.tableName.fieldName
     *
     * @param fields
     * @param arg
     * @param section
     */
    private void processConditionItem(List<ConditionItem> fields, Object arg, int section) {
        if (CollectionUtils.isEmpty(fields)) {
            return;
        }
        for (ConditionItem conditionItem : fields) {
            String fieldKey = conditionItem.getFieldKey();
            if (StringUtils.isEmpty(fieldKey)) {
                continue;
            }
            String[] split = StringUtils.split(fieldKey, '.');
            if (split.length < section) {
                continue;
            }
            String tableName = split[section - 2];
            String fieldName = split[section - 1];
            if (arg instanceof Set fieldNames) {
                fieldNames.add(tableName);
            }
            if (arg instanceof Map fieldInfoMap) {
                List<SemanticFieldSchemaDTO> fieldSchemaDTOS = (List<SemanticFieldSchemaDTO>) fieldInfoMap.get(tableName);
                SemanticFieldTypeEnum fieldTypeEnum = findFieldTypeEnum(fieldName, fieldSchemaDTOS);
                conditionItem.setFieldTypeEnum(fieldTypeEnum);
            }
        }
    }

    private Map<String, List<SemanticFieldSchemaDTO>> findTableFieldSchema(Long applicationId, Set<String> tableNames) {
        Map<String, List<SemanticFieldSchemaDTO>> fieldInfoMap = new HashMap<>();
        for (String tableName : tableNames) {
            SemanticEntitySchemaDTO semanticEntitySchemaDTO = TenantManager.withoutTenantCondition(() -> ApplicationManager.withApplicationIdAndVersionTag(
                    applicationId,
                    flowProperties.getVersionTag(),
                    () -> semanticDynamicDataApi.buildEntitySchemaByTableName(tableName)
            ));
            List<SemanticFieldSchemaDTO> fields = semanticEntitySchemaDTO.getFields();
            fieldInfoMap.put(tableName, fields);
        }
        return fieldInfoMap;
    }

    private SemanticFieldTypeEnum findFieldTypeEnum(String fieldName, List<SemanticFieldSchemaDTO> fieldSchemaDTOS) {
        for (SemanticFieldSchemaDTO fieldSchemaDTO : fieldSchemaDTOS) {
            if (fieldSchemaDTO.getFieldName().equals(fieldName)) {
                return fieldSchemaDTO.getFieldTypeEnum();
            }
        }
        return SemanticFieldTypeEnum.TEXT;
    }

}
