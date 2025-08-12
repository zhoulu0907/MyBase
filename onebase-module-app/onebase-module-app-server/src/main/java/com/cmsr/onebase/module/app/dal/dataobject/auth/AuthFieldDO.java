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
@Table(name = "app_auth_field")
public class AuthFieldDO extends TenantBaseDO {

    @Column(name = "application_code", nullable = false, comment = "应用Code")
    private String applicationCode;

    @Column(name = "role_code", nullable = false, comment = "角色code")
    private String roleCode;

    @Column(name = "menu_code", nullable = false, comment = "菜单code")
    private String menuCode;

    @Column(name = "field_code", nullable = false, comment = "字段code")
    private String fieldCode;

    @Column(name = "is_can_read", comment = "是否可阅读")
    private Boolean canRead;

    @Column(name = "is_can_edit", comment = "是否可编辑")
    private Boolean canEdit;

    @Column(name = "is_can_download", comment = "是否可下载")
    private Boolean canDownload;
}
