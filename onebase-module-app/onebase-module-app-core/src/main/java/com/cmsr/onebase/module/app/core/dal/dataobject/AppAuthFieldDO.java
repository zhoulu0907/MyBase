package com.cmsr.onebase.module.app.core.dal.dataobject;

import com.cmsr.onebase.framework.orm.entity.BaseAppEntity;
import com.cmsr.onebase.framework.orm.entity.BaseBizEntity;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import lombok.Data;

/**
 * @Author：huangjie
 * @Date：2025/8/5 15:08
 */
@Data
@Table(value = "app_auth_field")
public class AppAuthFieldDO extends BaseBizEntity {

    @Column(value = "role_uuid", comment = "角色id")
    private String roleUuid;

    @Column(value = "menu_uuid", comment = "菜单id")
    private String menuUuid;

    @Column(value = "field_uuid", comment = "字段id")
    private String fieldUuid;

    @Column(value = "is_can_read", comment = "是否可阅读")
    private Integer isCanRead;

    @Column(value = "is_can_edit", comment = "是否可编辑")
    private Integer isCanEdit;

    @Column(value = "is_can_download", comment = "是否可下载")
    private Integer isCanDownload;
}
