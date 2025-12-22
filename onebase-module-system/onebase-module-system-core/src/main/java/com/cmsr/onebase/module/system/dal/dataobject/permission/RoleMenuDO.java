package com.cmsr.onebase.module.system.dal.dataobject.permission;

import com.cmsr.onebase.framework.tenant.core.db.TenantBaseDO;
import com.mybatisflex.annotation.Table;
import com.mybatisflex.annotation.Column;
import lombok.Data;

/**
 * 角色和菜单关联
 *
 * @author ma
 */
@Table(value = "system_role_menu")
@Data
public class RoleMenuDO extends TenantBaseDO {

    // 字段常量
    public static final String ROLE_ID = "role_id";
    public static final String MENU_ID = "menu_id";

    /**
     * 角色ID
     */
    @Column(value = ROLE_ID)
    private Long roleId;
    /**
     * 菜单ID
     */
    @Column(value = MENU_ID)
    private Long menuId;

}
