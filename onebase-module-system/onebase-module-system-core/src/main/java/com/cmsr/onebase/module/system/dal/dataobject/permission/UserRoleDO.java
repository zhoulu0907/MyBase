package com.cmsr.onebase.module.system.dal.dataobject.permission;

import com.cmsr.onebase.framework.tenant.core.db.TenantBaseDO;

import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * 用户和角色关联
 *
 * @author ma
 */
@Data
@Table(name = "system_user_role")
public class UserRoleDO extends TenantBaseDO {

    public static final String USER_ID = "user_id";
    public static final String ROLE_ID = "role_id";

    /**
     * 用户 ID
     */
    @Column(name = USER_ID)
    private Long userId;
    /**
     * 角色 ID
     */
    @Column(name = ROLE_ID)
    private Long roleId;

}
