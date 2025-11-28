package com.cmsr.onebase.module.metadata.core.dal.dataobject.relationship;

import com.cmsr.onebase.framework.orm.entity.BaseTenantEntity;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 实体关系表 DO
 *
 * @author bty418
 * @date 2025-01-27
 */
@Table(value = "metadata_entity_relationship")
@Data
@EqualsAndHashCode(callSuper = true)
public class MetadataEntityRelationshipDO extends BaseTenantEntity {

    /**
     * 关系名称
     */
    @Column(value = "relation_name", comment = "关系名称")
    private String relationName;

    /**
     * 源实体ID
     */
    @Column(value = "source_entity_id", comment = "源实体ID")
    private Long sourceEntityId;

    /**
     * 目标实体ID
     */
    @Column(value = "target_entity_id", comment = "目标实体ID")
    private Long targetEntityId;

    /**
     * 关系类型(ONE_TO_ONE,ONE_TO_MANY,MANY_TO_ONE,MANY_TO_MANY)
     */
    @Column(value = "relationship_type", comment = "关系类型")
    private String relationshipType;

    /**
     * 源字段id
     */
    @Column(value = "source_field_id", comment = "源字段id")
    private String sourceFieldId;

    /**
     * 目标字段id
     */
    @Column(value = "target_field_id", comment = "目标字段id")
    private String targetFieldId;

    /**
     * 级联操作类型(read,all,delete,none)
     */
    @Column(value = "cascade_type", comment = "级联操作类型")
    private String cascadeType;

    /**
     * 关系描述
     */
    @Column(value = "description", comment = "关系描述")
    private String description;

    /**
     * 运行模式：0 编辑态，1 运行态
     */
    @Column(value = "run_mode", comment = "运行模式：0 编辑态，1 运行态")
    private Integer runMode;

    /**
     * 应用ID
     */
    @Column(value = "app_id", comment = "应用ID")
    private Long appId;

}
