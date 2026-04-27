package com.cmsr.onebase.module.flow.context.graph;

import com.cmsr.onebase.module.metadata.core.semantic.dto.SemanticFieldSchemaDTO;

import java.util.Map;
import java.util.Set;

/**
 * @Author：huangjie
 * @Date：2025/12/14 22:53
 */
public interface FieldTypeProcessable {


    /**
     * 获取节点涉及的表名列表
     * 用于第一阶段：收集所有需要查询的表名
     */
    Set<String> getTableNames();

    /**
     * 处理字段类型信息
     * 用于第二阶段：根据字段元数据更新节点信息
     *
     * @param fieldInfoMap 表名 -> 字段名 -> 字段元数据的映射
     */
    void processFieldTypes(Map<String, Map<String, SemanticFieldSchemaDTO>> fieldInfoMap);

}
