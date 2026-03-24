package com.cmsr.onebase.module.system.dal.dataobject.external;

import com.cmsr.onebase.framework.orm.entity.BaseEntity;
import com.cmsr.onebase.framework.tenant.core.aop.TenantIgnore;
import com.mybatisflex.annotation.Table;
import com.mybatisflex.annotation.Column;
import lombok.Data;

/**
 * 外部系统用户关联 DO
 *
 */
@Data
@TenantIgnore
@Table(value = "system_external_users")
public class SystemExternalUserDO extends BaseEntity {

    // 字段常量
    public static final String OB_TENANT_ID = "ob_tenant_id";
    public static final String OB_USER_ID = "ob_user_id";
    public static final String PLATFORM_TYPE = "platform_type";
    public static final String EXTERNAL_TENANT_ID = "external_tenant_id";
    public static final String EXTERNAL_USER_ID = "external_user_id";

    /**
     * onebase 租户 id
     */
    @Column(value = OB_TENANT_ID)
    private Long obTenantId;

    /**
     * onebase 用户 id
     */
    @Column(value = OB_USER_ID)
    private Long obUserId;

    /**
     * 平台类型
     */
    @Column(value = PLATFORM_TYPE)
    private String platformType;

    /**
     * 外部系统的租户 id
     */
    @Column(value = EXTERNAL_TENANT_ID)
    private String externalTenantId;

    /**
     * 外部系统的用户 id
     */
    @Column(value = EXTERNAL_USER_ID)
    private String externalUserId;

}

