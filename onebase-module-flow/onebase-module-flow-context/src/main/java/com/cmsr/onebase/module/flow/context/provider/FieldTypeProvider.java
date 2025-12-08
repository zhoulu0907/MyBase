package com.cmsr.onebase.module.flow.context.provider;

import com.cmsr.onebase.module.flow.context.graph.JsonGraph;

/**
 * @Author：huangjie
 * @Date：2025/10/14 18:17
 */
public interface FieldTypeProvider {

    void completeFieldType(Long applicationId, JsonGraph jsonGraph);

}
