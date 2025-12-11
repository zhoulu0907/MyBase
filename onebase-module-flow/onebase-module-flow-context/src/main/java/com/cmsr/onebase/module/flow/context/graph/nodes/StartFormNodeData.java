package com.cmsr.onebase.module.flow.context.graph.nodes;

import com.cmsr.onebase.module.flow.context.condition.Conditions;
import com.cmsr.onebase.module.flow.context.graph.NodeData;
import com.cmsr.onebase.module.metadata.core.semantic.dto.SemanticFieldSchemaDTO;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @Author：huangjie
 * @Date：2025/9/9 11:05
 */
@Data
public class StartFormNodeData extends NodeData implements Serializable {

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
    private Map<String, SemanticFieldSchemaDTO> fieldSchemaMap;


    private String triggerRange;

    private List<String> recordTriggerEvents;

    private String fieldTriggerEvents;

    private String pageUuid;

    private Boolean isChildTriggerAllowed;

    private List<Conditions> filterCondition;

}
