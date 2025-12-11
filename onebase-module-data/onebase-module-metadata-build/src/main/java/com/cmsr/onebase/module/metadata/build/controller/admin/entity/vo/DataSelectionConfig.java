package com.cmsr.onebase.module.metadata.build.controller.admin.entity.vo;

import lombok.Data;

/**
 * 数据选择配置
 *
 * @author matianyu
 * @date 2025-07-25
 */
@Data
public class DataSelectionConfig {
    /**
     * 关联关系主键id
     */
    private Long relationId;

    /**
     * 关联的目标实体ID（兼容前端传ID）
     */
    private Long targetEntityId;

    /**
     * 关联的目标实体UUID
     */
    private String targetEntityUuid;

    /**
     * 关联的目标字段ID（兼容前端传ID）
     */
    private Long targetFieldId;

    /**
     * 关联的目标字段UUID
     */
    private String targetFieldUuid;

}
