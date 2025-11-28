package com.cmsr.onebase.module.app.core.dal.dataobject;

import com.cmsr.onebase.framework.orm.entity.BaseAppEntity;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import lombok.Data;

/**
 * @Author：huangjie
 * @Date：2025/8/5 15:08
 */
@Data
@Table(value = "app_auth_field")
public class AppAuthFieldDO extends BaseAppEntity {

    @Column(value = "role_id", comment = "角色id")
    private Long roleId;

    @Column(value = "menu_id", comment = "菜单id")
    private Long menuId;

    @Column(value = "field_id", comment = "字段id")
    private Long fieldId;

    @Column(value = "is_can_read", comment = "是否可阅读")
    private Integer isCanRead;

    @Column(value = "is_can_edit", comment = "是否可编辑")
    private Integer isCanEdit;

    @Column(value = "is_can_download", comment = "是否可下载")
    private Integer isCanDownload;
}
