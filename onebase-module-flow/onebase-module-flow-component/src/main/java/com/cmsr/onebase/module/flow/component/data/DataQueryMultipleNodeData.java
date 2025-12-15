package com.cmsr.onebase.module.flow.component.data;

import com.cmsr.onebase.module.flow.context.condition.Conditions;
import com.cmsr.onebase.module.flow.context.condition.SortItem;
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
 * @Date：2025/9/30 9:01
 */
@Data
@NodeType("dataQueryMultiple")
public class DataQueryMultipleNodeData extends NodeData implements FieldTypeProcessable, Serializable {

    /**
     * mainTable
     * subTable
     */
    private String dataType;

    private String mainTableName;

    private String subTableName;

    private Integer maxCount;

    /**
     * all
     * condition
     */
    private String filterType;

    /**
     * 数据透传给API接口，不需要转换类型，因此不需要补充fieldType
     */
    private List<Conditions> filterCondition;

    private List<SortItem> sortBy;

    public Integer getMaxCountWithDefault(int defaultMaxCount) {
        return maxCount == null ? defaultMaxCount : maxCount;
    }

    public String resolveTargetTableName() {
        if (StringUtils.equalsIgnoreCase("mainTable", dataType)) {
            return mainTableName;
        } else if (StringUtils.equalsIgnoreCase("subTable", dataType)) {
            return subTableName;
        } else {
            throw new IllegalArgumentException("数据查询dataType类型错误: " + dataType);
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
