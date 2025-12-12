package com.cmsr.onebase.module.flow.core.external;

import com.cmsr.onebase.module.flow.context.graph.JsonGraph;

/**
 * @Author：huangjie
 * @Date：2025/10/14 18:17
 */
public interface FlowFieldTypeProvider {

    void completeFieldType(Long applicationId, JsonGraph jsonGraph);

}
