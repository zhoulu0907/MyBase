package com.cmsr.onebase.module.app.core.dal.dataobject.auth;

import com.cmsr.onebase.framework.tenant.core.db.TenantBaseDO;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * @Author：huangjie
 * @Date：2025/8/5 15:11
 */
@Data
@Table(name = "app_auth_role_user")
public class AuthRoleUserDO extends TenantBaseDO {

    public static final String USER_ID = "user_id";
    public static final String ROLE_ID = "role_id";
    
    @Column(name = "role_id", nullable = false, comment = "角色id")
    private Long roleId;

    @Column(name = "user_id", nullable = false, comment = "用户Id")
    private Long userId;

}
