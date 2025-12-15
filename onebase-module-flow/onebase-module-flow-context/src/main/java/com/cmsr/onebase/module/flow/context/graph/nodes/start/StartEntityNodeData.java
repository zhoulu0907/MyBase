package com.cmsr.onebase.module.flow.context.graph.nodes.start;

import com.cmsr.onebase.module.flow.context.condition.Conditions;
import com.cmsr.onebase.module.flow.context.graph.FieldTypeProcessable;
import com.cmsr.onebase.module.flow.context.graph.FieldTypeHelper;
import com.cmsr.onebase.module.flow.context.graph.NodeData;
import com.cmsr.onebase.module.flow.context.graph.NodeType;
import com.cmsr.onebase.module.metadata.core.semantic.dto.SemanticFieldSchemaDTO;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @Author：huangjie
 * @Date：2025/9/9 13:46
 */
@Data
@NodeType("startEntity")
public class StartEntityNodeData extends NodeData implements FieldTypeProcessable, Serializable {

    /**
     * 应用ID，后补充
     */
    private Long applicationId;
    /**
     * 流程ID，后补充
     */
    private Long processId;

    private String tableName;

    private List<String> triggerEvents;

    /**
     * 过滤条件
     */
    private List<Conditions> filterCondition;

    @Override
    public Set<String> getTableNames() {
        return Set.of(tableName);
    }

    @Override
    public void processFieldTypes(Map<String, Map<String, SemanticFieldSchemaDTO>> fieldInfoMap) {
        FieldTypeHelper.processConditionList(getFilterCondition(), fieldInfoMap, 2);
    }


}
