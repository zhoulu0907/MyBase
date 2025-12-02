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
     * 源实体ID字段名常量
     */
    public static final String SOURCE_ENTITY_ID = "source_entity_id";

    /**
     * 目标实体ID字段名常量
     */
    public static final String TARGET_ENTITY_ID = "target_entity_id";

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
     * 关系类型
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
     * 选择字段id
     * <p>
     * 该字段主要用于当关系类型relationshipType为数据选择时：
     * - targetFieldId存的是关联表的主键字段id
     * - selectFieldId存的是关联表中被选择的字段id（用于展示给用户的字段）
     * <p>
     * 用户动态建的实体中存的是关联表主键id，通过id查到一条或多条数据后，
     * 需要把selectFieldId对应的字段值取出来展示给用户。因此需要存储该字段。
     */
    @Column(value = "select_field_id", comment = "选择字段id")
    private String selectFieldId;

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
     * 版本标识
     */
    @Column(value = "version_tag", comment = "版本标识")
    private Long versionTag;

    /**
     * 应用ID
     */
    @Column(value = "application_id", comment = "应用ID")
    private Long applicationId;

}
