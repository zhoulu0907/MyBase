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

    @Column(name = "application_id", nullable = false, comment = "应用Id")
    private Long applicationId;

    @Column(name = "menu_id", nullable = false, comment = "菜单Id")
    private Long menuId;

    @Column(name = "field_name", nullable = false, length = 100, comment = "字段名称")
    private String fieldName;

    @Column(name = "is_can_read", comment = "是否可阅读")
    private Boolean canRead;

    @Column(name = "is_can_edit", comment = "是否可编辑")
    private Boolean canEdit;

    @Column(name = "is_can_download", comment = "是否可下载")
    private Boolean canDownload;
}
