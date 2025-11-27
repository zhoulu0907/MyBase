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
@Table(value = "app_auth_permission")
public class AppAuthPermissionDO extends BaseAppEntity {

    @Column(value = "role_id", comment = "角色id")
    private Long roleId;

    @Column(value = "menu_id", comment = "菜单id")
    private Long menuId;

    @Column(value = "is_page_allowed", comment = "页面是否可访问")
    private Integer isPageAllowed;

    @Column(value = "is_all_views_allowed", comment = "所有视图可访问")
    private Integer isAllViewsAllowed;

    @Column(value = "is_all_fields_allowed", comment = "所有字段可操作")
    private Integer isAllFieldsAllowed;

    @Column(value = "operation_tags", comment = "操作权限标签")
    private String operationTags;

}
