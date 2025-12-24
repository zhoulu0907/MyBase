package com.cmsr.onebase.module.flow.core.external;

import com.cmsr.onebase.framework.common.security.ApplicationManager;
import com.cmsr.onebase.module.flow.context.graph.FieldTypeProcessable;
import com.cmsr.onebase.module.flow.context.graph.JsonGraph;
import com.cmsr.onebase.module.flow.context.graph.JsonGraphNode;
import com.cmsr.onebase.module.flow.context.graph.NodeData;
import com.cmsr.onebase.module.flow.context.graph.nodes.start.StartFormNodeData;
import com.cmsr.onebase.module.flow.core.config.FlowProperties;
import com.cmsr.onebase.module.metadata.api.semantic.SemanticDynamicDataApi;
import com.cmsr.onebase.module.metadata.core.semantic.dto.SemanticEntitySchemaDTO;
import com.cmsr.onebase.module.metadata.core.semantic.dto.SemanticFieldSchemaDTO;
import com.mybatisflex.core.tenant.TenantManager;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @Author：huangjie
 * @Date：2025/10/14 17:53
 */
@Setter
@Component
public class FlowFlowFieldTypeProviderImpl implements FlowFieldTypeProvider {

    @Autowired
    private SemanticDynamicDataApi semanticDynamicDataApi;

    @Autowired
    private FlowProperties flowProperties;

    @Autowired
    private FlowAppProvider flowAppProvider;


    @Override
    public void completeFieldType(Long applicationId, JsonGraph jsonGraph) {
        Set<String> tableNames = new HashSet<>();
        recursionUpdateFieldDataType(applicationId, jsonGraph.getNodes(), tableNames);
        if (tableNames.isEmpty()) {
            return;
        }
        Map<String, Map<String, SemanticFieldSchemaDTO>> fieldInfoMap = findTableFieldSchema(applicationId, tableNames);
        recursionUpdateFieldDataType(applicationId, jsonGraph.getNodes(), fieldInfoMap);
    }


    private void recursionUpdateFieldDataType(Long applicationId, List<JsonGraphNode> nodes, Object arg) {
        if (CollectionUtils.isEmpty(nodes)) {
            return;
        }
        for (JsonGraphNode node : nodes) {
            NodeData nodeData = node.getData();
            if (nodeData instanceof StartFormNodeData n) {
                if (n.getPageId() == null) {
                    String pageUuid = n.getPageUuid();
                    Long pageId = TenantManager.withoutTenantCondition(() -> ApplicationManager.withApplicationIdAndVersionTag(applicationId, flowProperties.getVersionTag(),
                            () -> flowAppProvider.findPageIdByAppIdAndPageUuid(applicationId, pageUuid)
                    ));
                    n.setPageId(pageId);
                }
                if (n.getTableName() == null) {
                    String pageUuid = n.getPageUuid();
                    String tableUuid = TenantManager.withoutTenantCondition(() -> ApplicationManager.withApplicationIdAndVersionTag(applicationId, flowProperties.getVersionTag(),
                            () -> flowAppProvider.findTableUuidByAppIdAndPageUuid(applicationId, pageUuid)
                    ));
                    String tableName = findTableNameByUuid(applicationId, tableUuid);
                    n.setTableName(tableName);
                }
            }
            if (nodeData instanceof FieldTypeProcessable processable) {
                if (arg instanceof Set tableNames) {
                    Set<String> names = processable.getTableNames();
                    tableNames.addAll(names);
                } else if (arg instanceof Map fieldInfoMap) {
                    processable.processFieldTypes(fieldInfoMap);
                }
            }
            // 递归处理子节点
            recursionUpdateFieldDataType(applicationId, node.getBlocks(), arg);
        }
    }


    private Map<String, Map<String, SemanticFieldSchemaDTO>> findTableFieldSchema(Long applicationId, Set<String> tableNames) {
        Map<String, Map<String, SemanticFieldSchemaDTO>> fieldInfoMap = new HashMap<>();
        for (String tableName : tableNames) {
            SemanticEntitySchemaDTO semanticEntitySchemaDTO = TenantManager.withoutTenantCondition(() -> ApplicationManager.withApplicationIdAndVersionTag(
                    applicationId,
                    flowProperties.getVersionTag(),
                    () -> semanticDynamicDataApi.buildEntitySchemaByTableName(tableName)
            ));
            List<SemanticFieldSchemaDTO> fields = semanticEntitySchemaDTO.getFields();
            Map<String, SemanticFieldSchemaDTO> fieldMap = new HashMap<>();
            for (SemanticFieldSchemaDTO field : fields) {
                fieldMap.put(field.getFieldName(), field);
            }
            fieldInfoMap.put(tableName, fieldMap);
        }
        return fieldInfoMap;
    }


    private String findTableNameByUuid(Long applicationId, String entityUuid) {
        SemanticEntitySchemaDTO semanticEntitySchemaDTO = TenantManager.withoutTenantCondition(() -> ApplicationManager.withApplicationIdAndVersionTag(
                applicationId,
                flowProperties.getVersionTag(),
                () -> semanticDynamicDataApi.buildEntitySchemaByUuid(entityUuid)
        ));
        return semanticEntitySchemaDTO.getTableName();
    }

}
