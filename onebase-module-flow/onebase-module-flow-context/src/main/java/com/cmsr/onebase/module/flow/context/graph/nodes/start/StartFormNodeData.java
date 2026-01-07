package com.cmsr.onebase.module.flow.context.graph.nodes.start;

import com.cmsr.onebase.module.flow.context.condition.Conditions;
import com.cmsr.onebase.module.flow.context.graph.FieldTypeHelper;
import com.cmsr.onebase.module.flow.context.graph.FieldTypeProcessable;
import com.cmsr.onebase.module.flow.context.graph.NodeData;
import com.cmsr.onebase.module.flow.context.graph.NodeType;
import com.cmsr.onebase.module.flow.context.table.TableFieldSchemas;
import com.cmsr.onebase.module.metadata.core.semantic.dto.SemanticFieldSchemaDTO;
import lombok.Data;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @Author：huangjie
 * @Date：2025/9/9 11:05
 */
@Data
@NodeType("startForm")
public class StartFormNodeData extends NodeData implements FieldTypeProcessable, Serializable {

    /**
     * 应用ID，!!!后补充!!!
     */
    private Long applicationId;
    /**
     * 流程ID，!!!后补充!!!
     */
    private Long processId;

    /**
     * 页面ID，!!!后补充!!!
     */
    private Long pageId;

    /**
     * 表名，!!!后补充!!!
     */
    private String tableName;

    /**
     * 字段信息,!!!后补充!!!
     */
    private TableFieldSchemas tableFieldSchemas = new TableFieldSchemas();


    private String triggerRange;

    private List<String> recordTriggerEvents;

    private String fieldTriggerEvents;

    private String pageUuid;

    private Boolean isChildTriggerAllowed;

    private List<Conditions> filterCondition;

    @Override
    public Set<String> getTableNames() {
        Set<String> tableNames = new HashSet<>();
        tableNames.add(tableName);
        Set<String> ss = FieldTypeHelper.extractTableNames(filterCondition);
        tableNames.addAll(ss);
        return tableNames;
    }

    @Override
    public void processFieldTypes(Map<String, Map<String, SemanticFieldSchemaDTO>> fieldInfoMap) {
        tableFieldSchemas.setTableFieldSchemas(fieldInfoMap);
        FieldTypeHelper.processConditionList(getFilterCondition(), fieldInfoMap, 2);
    }
}
