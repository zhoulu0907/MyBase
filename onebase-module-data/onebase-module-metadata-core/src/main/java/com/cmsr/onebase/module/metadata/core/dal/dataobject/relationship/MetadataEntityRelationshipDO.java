package com.cmsr.onebase.module.metadata.core.dal.dataobject.relationship;

import jakarta.persistence.Table;
import com.cmsr.onebase.framework.tenant.core.db.TenantBaseDO;
import lombok.*;
import lombok.experimental.SuperBuilder;
/**
 * 实体关系表 DO
 *
 * @author bty418
 * @date 2025-01-27
 */
@Table(name = "metadata_entity_relationship")
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class MetadataEntityRelationshipDO extends TenantBaseDO {

    // 列名常量
    public static final String RELATION_NAME     = "relation_name";
    public static final String SOURCE_ENTITY_ID  = "source_entity_id";
    public static final String TARGET_ENTITY_ID  = "target_entity_id";
    public static final String RELATIONSHIP_TYPE = "relationship_type";
    public static final String SOURCE_FIELD_ID   = "source_field_id";
    public static final String TARGET_FIELD_ID   = "target_field_id";
    public static final String CASCADE_TYPE      = "cascade_type";
    public static final String DESCRIPTION       = "description";
    public static final String RUN_MODE          = "run_mode";
    public static final String APP_ID            = "app_id";

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



}
