package com.cmsr.onebase.module.flow.component.data;

import com.cmsr.onebase.module.flow.context.condition.ConditionItem;
import com.cmsr.onebase.module.flow.context.graph.FieldTypeProcessable;
import com.cmsr.onebase.module.flow.context.graph.NodeData;
import com.cmsr.onebase.module.flow.context.graph.NodeType;
import com.cmsr.onebase.module.metadata.core.semantic.dto.SemanticFieldSchemaDTO;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @Author：huangjie
 * @Date：2025/9/29 22:10
 */
@Data
@NodeType("dataAdd")
public class DataAddNodeData extends NodeData implements FieldTypeProcessable, Serializable {

    /**
     * mainTable
     * subTable
     */
    private String addType;

    private String mainTableName;

    private String subTableName;

    private Boolean batchType;

    private String dataNodeId;

    /**
     * 数据透传给API接口，不需要转换类型，因此不需要补充fieldType
     */
    private List<ConditionItem> fields;


    public String resolveTargetTableName() {
        if (StringUtils.equalsIgnoreCase("mainTable", addType)) {
            return mainTableName;
        } else if (StringUtils.equalsIgnoreCase("subTable", addType)) {
            return subTableName;
        } else {
            throw new IllegalArgumentException("数据添加addType类型错误: " + addType);
        }
    }

    @Override
    public Set<String> getTableNames() {
        return Set.of(resolveTargetTableName());
    }

    @Override
    public void processFieldTypes(Map<String, Map<String, SemanticFieldSchemaDTO>> fieldInfoMap) {

    }
}
