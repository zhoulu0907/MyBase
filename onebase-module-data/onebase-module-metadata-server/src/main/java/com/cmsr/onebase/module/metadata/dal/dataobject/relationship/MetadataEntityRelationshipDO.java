package com.cmsr.onebase.module.metadata.dal.dataobject.relationship;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cmsr.onebase.framework.tenant.core.db.TenantBaseDO;
import lombok.*;

/**
 * 实体关系表 DO
 */
@TableName(value = "metadata_entity_relationship")
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MetadataEntityRelationshipDO extends TenantBaseDO {

    public MetadataEntityRelationshipDO setId(Long id) {
        super.setId(id);
        return this;
    }

    /**
     * 关系名称
     */
    private String relationName;

    /**
     * 源实体ID
     */
    private Long sourceEntityId;

    /**
     * 目标实体ID
     */
    private Long targetEntityId;

    /**
     * 关系类型(ONE_TO_ONE,ONE_TO_MANY,MANY_TO_ONE,MANY_TO_MANY)
     */
    private String relationshipType;

    /**
     * 源字段id
     */
    private String sourceFieldId;

    /**
     * 目标字段id
     */
    private String targetFieldId;

    /**
     * 级联操作类型(read,all,delete,none)
     */
    private String cascadeType;

    /**
     * 关系描述
     */
    private String description;

    /**
     * 运行模式：0 编辑态，1 运行态
     */
    private Integer runMode;

    /**
     * 应用ID
     */
    private Long appId;

    /**
     * 版本锁标识
     */
    private Integer lockVersion;

}
