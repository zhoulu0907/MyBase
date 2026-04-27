package com.cmsr.onebase.module.flow.context.graph.nodes.logic;

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
 * @Date：2025/9/30 9:14
 */
@Data
@NodeType("ifCase")
public class IfCaseNodeData extends NodeData implements FieldTypeProcessable, Serializable {

    private List<Conditions> filterCondition;

    @Override
    public Set<String> getTableNames() {
        return Set.of();
    }

    @Override
    public void processFieldTypes(Map<String, Map<String, SemanticFieldSchemaDTO>> fieldInfoMap) {
        FieldTypeHelper.processConditionList(filterCondition, fieldInfoMap, 3);
    }
}
