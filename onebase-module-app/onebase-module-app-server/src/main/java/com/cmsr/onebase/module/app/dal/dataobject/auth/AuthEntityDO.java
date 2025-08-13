package com.cmsr.onebase.module.app.dal.dataobject.auth;

import com.cmsr.onebase.framework.tenant.core.db.TenantBaseDO;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * @Author：huangjie
 * @Date：2025/8/5 15:12
 */
@Data
@Table(name = "app_auth_entity")
public class AuthEntityDO extends TenantBaseDO {

    @Column(name = "application_code", nullable = false, comment = "应用code")
    private String applicationCode;

    @Column(name = "role_code", nullable = false, comment = "角色code")
    private String roleCode;

    @Column(name = "menu_code", nullable = false, comment = "菜单code")
    private String menuCode;

    @Column(name = "entity_code", nullable = false, comment = "实体code")
    private String entityCode;

    @Column(name = "is_allowed", nullable = false, comment = "是否可访问")
    private Boolean allowed;

}
