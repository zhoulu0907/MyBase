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
@Table(name = "app_auth_feature")
public class AuthFeatureDO extends TenantBaseDO {

    @Column(name = "application_id", nullable = false, comment = "应用Id")
    private Long applicationId;

    @Column(name = "menu_id", nullable = false, comment = "菜单Id")
    private Long menuId;

    @Column(name = "is_page_allowed", nullable = false, comment = "页面是否可访问")
    private Boolean isPageAllowed;

    @Column(name = "is_all_entities_allowed", nullable = false, comment = "关联的所有视图是否可访问")
    private Boolean isAllEntitiesAllowed;

}
