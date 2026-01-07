package com.cmsr.onebase.module.system.dal.dataobject.permission;

import com.cmsr.onebase.framework.orm.entity.BaseTenantEntity;

import com.mybatisflex.annotation.Table;
import com.mybatisflex.annotation.Column;
import lombok.Data;

/**
 * 用户和角色关联
 *
 * @author ma
 */
@Data
@Table(value = "system_user_role")
public class UserRoleDO extends BaseTenantEntity {

    public static final String USER_ID = "user_id";
    public static final String ROLE_ID = "role_id";

    /**
     * 用户 ID
     */
    @Column(value = USER_ID)
    private Long userId;
    /**
     * 角色 ID
     */
    @Column(value = ROLE_ID)
    private Long roleId;

}
