package com.cmsr.onebase.module.flow.core.graph.data;

import lombok.Data;

/**
 * @Author：huangjie
 * @Date：2025/9/9 13:46
 */
@Data
public class StartEntityNodeData {

    private Long entityId;

    private String[] triggerEvents;

    private Long[] triggerFieldIds;

    private String[][] filterCondition;

}
