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

    @Column(name = "application_id", nullable = false, comment = "应用Id")
    private Long applicationId;

    @Column(name = "role_id", nullable = false, comment = "角色Id")
    private Long roleId;

    @Column(name = "menu_id", nullable = false, comment = "菜单Id")
    private Long menuId;

    @Column(name = "entity_id", nullable = false, comment = "实体Id")
    private Long entityId;

    @Column(name = "is_allowed", nullable = false, comment = "是否可访问")
    private Boolean allowed;

}
