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

    @Column(name = "application_code", nullable = false, comment = "应用code")
    private String applicationCode;

    @Column(name = "role_code", nullable = false, comment = "角色code")
    private String roleCode;

    @Column(name = "menu_code", nullable = false, comment = "菜单code")
    private String menuCode;

    @Column(name = "is_page_allowed", nullable = false, comment = "页面是否可访问")
    private Boolean pageAllowed;

    @Column(name = "is_all_entities_allowed", nullable = false, comment = "所有实体可访问")
    private Boolean allEntitiesAllowed;

    @Column(name = "is_all_fields_allowed", nullable = false,  comment = "所有字段可操作")
    private Boolean allFieldsAllowed;
}
