package com.cmsr.onebase.module.metadata.core.domain.query;

import com.cmsr.onebase.module.metadata.core.enums.MetadataDataMethodOpEnum;
import lombok.Data;

import java.util.Map;

@Data
public class MetadataDataMethodCoreContext {

    /**
     * 操作追踪ID
     */
    private String businessTraceId;

    /**
     * 操作类型
     */
    private MetadataDataMethodOpEnum metadataDataMethodOpEnum;
    /**
     * 实体ID
     */
    private Long entityId;
    /**
     * 数据ID
     */
    private Object id;
    /**
     * 数据内容：字段列表，key-字段id，value-字段值
     */
    private Map<String, Object> data;
    /**
     * 方法编码（可选）
     */
    private String methodCode;



}
