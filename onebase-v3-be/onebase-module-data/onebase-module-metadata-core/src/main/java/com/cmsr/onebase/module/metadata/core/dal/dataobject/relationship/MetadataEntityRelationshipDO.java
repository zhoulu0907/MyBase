package com.cmsr.onebase.module.metadata.core.dal.dataobject.relationship;

import com.cmsr.onebase.framework.orm.entity.BaseBizEntity;
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
public class MetadataEntityRelationshipDO extends BaseBizEntity {

    /**
     * 源实体UUID字段名常量
     */
    public static final String SOURCE_ENTITY_UUID = "source_entity_uuid";

    /**
     * 目标实体UUID字段名常量
     */
    public static final String TARGET_ENTITY_UUID = "target_entity_uuid";

    /**
     * 关系UUID
     * <p>
     * 用于跨应用、跨版本的唯一标识，与 application_id、version_tag 组成联合唯一约束
     */
    @Column(value = "relationship_uuid", comment = "关系UUID")
    private String relationshipUuid;

    /**
     * 关系名称
     */
    @Column(value = "relation_name", comment = "关系名称")
    private String relationName;

    /**
     * 源实体UUID
     * <p>
     * 关联 metadata_business_entity.entity_uuid
     */
    @Column(value = "source_entity_uuid", comment = "源实体UUID")
    private String sourceEntityUuid;

    /**
     * 目标实体UUID
     * <p>
     * 关联 metadata_business_entity.entity_uuid
     */
    @Column(value = "target_entity_uuid", comment = "目标实体UUID")
    private String targetEntityUuid;

    /**
     * 关系类型
     */
    @Column(value = "relationship_type", comment = "关系类型")
    private String relationshipType;

    /**
     * 源字段UUID
     * <p>
     * 关联 metadata_entity_field.field_uuid
     */
    @Column(value = "source_field_uuid", comment = "源字段UUID")
    private String sourceFieldUuid;

    /**
     * 目标字段UUID
     * <p>
     * 关联 metadata_entity_field.field_uuid
     */
    @Column(value = "target_field_uuid", comment = "目标字段UUID")
    private String targetFieldUuid;

    /**
     * 选择字段UUID
     * <p>
     * 该字段主要用于当关系类型relationshipType为数据选择时：
     * - targetFieldUuid存的是关联表的主键字段uuid
     * - selectFieldUuid存的是关联表中被选择的字段uuid（用于展示给用户的字段）
     * <p>
     * 用户动态建的实体中存的是关联表主键uuid，通过uuid查到一条或多条数据后，
     * 需要把selectFieldUuid对应的字段值取出来展示给用户。因此需要存储该字段。
     * <p>
     * 关联 metadata_entity_field.field_uuid
     */
    @Column(value = "select_field_uuid", comment = "选择字段UUID")
    private String selectFieldUuid;

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
