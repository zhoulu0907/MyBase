package com.cmsr.onebase.module.system.dal.dataobject.permission;

import com.cmsr.onebase.framework.tenant.core.db.TenantBaseDO;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * 角色和菜单关联
 *
 * @author ma
 */
@Table(name = "system_role_menu")
@Data
public class RoleMenuDO extends TenantBaseDO {

    // 字段常量
    public static final String ROLE_ID = "role_id";
    public static final String MENU_ID = "menu_id";

    /**
     * 角色ID
     */
    @Column(name = ROLE_ID)
    private Long roleId;
    /**
     * 菜单ID
     */
    @Column(name = MENU_ID)
    private Long menuId;

}
