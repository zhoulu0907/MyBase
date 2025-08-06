package com.cmsr.onebase.module.app.dal.dataobject.app;

import com.cmsr.onebase.framework.tenant.core.db.TenantBaseDO;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * @Author：huangjie
 * @Date：2025/8/5 15:11
 */
@Data
@Table(name = "app_auth_user_role")
public class AuthUserRoleDO extends TenantBaseDO {

    @Column(name = "application_id", nullable = false, comment = "应用Id")
    private Long applicationId;

    @Column(name = "user_id", nullable = false, comment = "用户Id")
    private Long userId;

    @Column(name = "role_id", nullable = false, comment = "角色Id")
    private Long roleId;
}
