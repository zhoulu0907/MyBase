package com.cmsr.onebase.framework.orm.entity;

import com.mybatisflex.annotation.Column;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 *   字段：ID、创建人、创建时间、更新人、更新时间、删除标识、乐观锁、租户ID、应用ID；
 *   <p>
 *   适用场景：不参与应用发布过程，但需要根据租户对其权限进行管控的表单；
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BaseAppEntity extends BaseEntity {

    @Column(value = "application_id", comment = "应用ID")
    private Long applicationId;

    @Column(value = "tenant_id", comment = "租户ID", tenantId = true)
    private Long tenantId;

}
