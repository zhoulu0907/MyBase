package com.cmsr.onebase.module.app.core.dal.dataobject;

import com.cmsr.onebase.framework.orm.entity.BaseAppEntity;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import lombok.Data;

/**
 * @Author：huangjie
 * @Date：2025/8/5 15:12
 */
@Data
@Table(value = "app_auth_view")
public class AuthViewDO extends BaseAppEntity {

    @Column(value = "role_id", comment = "角色id")
    private Long roleId;

    @Column(value = "menu_id", comment = "菜单id")
    private Long menuId;

    @Column(value = "view_id", comment = "实体id")
    private Long viewId;

    @Column(value = "is_allowed", comment = "是否可访问")
    private Integer isAllowed;

}
