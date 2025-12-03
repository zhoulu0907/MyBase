package com.cmsr.onebase.module.metadata.core.domain.query;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 子实体上下文
 *
 * @author matianyu
 * @date 2025-09-15
 */
@Data
public class MetadataDataMethodSubEntityContext {

    /**
     * 子表的业务实体ID（兼容字段，建议使用entityUuid）
     */
    private Long entityId;

    /**
     * 子表的业务实体UUID
     */
    private String entityUuid;

    /**
     * 子表的字段列表, key-字段id，value-字段值
     */
    private List<Map<Long, Object>> subData;

}
