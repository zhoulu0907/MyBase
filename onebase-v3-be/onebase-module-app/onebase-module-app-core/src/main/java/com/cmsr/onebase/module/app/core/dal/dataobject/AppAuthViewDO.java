package com.cmsr.onebase.module.app.core.dal.dataobject;

import com.cmsr.onebase.framework.orm.entity.BaseBizEntity;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import lombok.Data;

/**
 * @Author：huangjie
 * @Date：2025/8/5 15:12
 */
@Data
@Table(value = "app_auth_view")
public class AppAuthViewDO extends BaseBizEntity {

    @Column(value = "role_uuid", comment = "角色id")
    private String roleUuid;

    @Column(value = "menu_uuid", comment = "菜单id")
    private String menuUuid;

    @Column(value = "view_uuid", comment = "实体id")
    private String viewUuid;

    @Column(value = "is_allowed", comment = "是否可访问")
    private Integer isAllowed;

}
