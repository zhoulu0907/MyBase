package com.cmsr.onebase.module.metadata.core.domain.query;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class MetadataDataMethodSubEntityContext {

    /**
     * 子表的业务实体
     */
    private Long entityId;

    /**
     * 子表的字段列表, key-字段id，value-字段值
     */
    private List<Map<Long, Object>> subData;

}
