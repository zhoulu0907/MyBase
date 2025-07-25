package com.cmsr.onebase.module.metadata.dal.dataobject.entity;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableName;
import com.cmsr.onebase.framework.tenant.core.db.TenantBaseDO;
import lombok.*;

/**
 * 业务实体ER图位置信息表 DO
 */
@TableName(value = "metadata_entity_position")
@KeySequence("metadata_entity_position_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MetadataEntityPositionDO extends TenantBaseDO {

    public MetadataEntityPositionDO setId(Long id) {
        super.setId(id);
        return this;
    }

    /**
     * 数据源ID
     */
    private Long datasourceId;

    /**
     * 业务实体ID
     */
    private Long entityId;

    /**
     * 实体编码
     */
    private String entityCode;

    /**
     * X坐标位置
     */
    private Integer positionX;

    /**
     * Y坐标位置
     */
    private Integer positionY;

    /**
     * 应用ID
     */
    private Long appId;

}
