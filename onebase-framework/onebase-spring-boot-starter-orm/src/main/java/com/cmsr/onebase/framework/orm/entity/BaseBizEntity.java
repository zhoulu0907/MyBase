package com.cmsr.onebase.framework.orm.entity;


import com.mybatisflex.annotation.Column;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 字段：ID、创建人、创建时间、更新人、更新时间、删除标识、租户ID、应用ID、版本ID、版本状态；
 * <p>
 * 适用场景：需要参与应用发布及版本管理的相关实体；
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BaseBizEntity extends BaseEntity {
    public static final String APPLICATION_ID = "application_id";

    public static final String VERSION_TAG = "version_tag";

    @Column(value = "application_id", comment = "应用ID")
    private Long applicationId;

    @Column(value = "tenant_id", comment = "租户ID", tenantId = true)
    private Long tenantId;

    @Column(value = "version_tag", comment = "版本标签")
    private Long versionTag;

}
