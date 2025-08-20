package com.cmsr.onebase.module.app.dal.dataobject.auth;

import com.cmsr.onebase.framework.tenant.core.db.TenantBaseDO;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * @Author：huangjie
 * @Date：2025/8/5 15:08
 */
@Data
@Table(name = "app_auth_permission")
public class AuthPermissionDO extends TenantBaseDO {

    @Column(name = "application_id", nullable = false, comment = "应用id")
    private Long applicationId;

    @Column(name = "role_id", nullable = false, comment = "角色id")
    private Long roleId;

    @Column(name = "menu_id", nullable = false, comment = "菜单id")
    private Long menuId;

    @Column(name = "is_page_allowed", nullable = false, comment = "页面是否可访问")
    private Integer isPageAllowed;

    @Column(name = "is_all_views_allowed", nullable = false, comment = "所有视图可访问")
    private Integer isAllViewsAllowed;

    @Column(name = "is_all_fields_allowed", nullable = false, comment = "所有字段可操作")
    private Integer isAllFieldsAllowed;
}
