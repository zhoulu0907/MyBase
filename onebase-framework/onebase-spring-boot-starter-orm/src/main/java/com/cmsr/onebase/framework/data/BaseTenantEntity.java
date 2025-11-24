package com.cmsr.onebase.framework.data;

import com.mybatisflex.annotation.Column;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class BaseTenantEntity extends BaseEntity {

    @Column(value = "application_id", comment = "应用ID")
    private Long applicationId;

    @Column(value = "tenant_id", comment = "租户ID", tenantId = true)
    private Long tenantId;

}
