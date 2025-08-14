package com.cmsr.onebase.module.app.dal.dataobject.auth;

import com.cmsr.onebase.framework.tenant.core.db.TenantBaseDO;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * @Author：huangjie
 * @Date：2025/8/5 15:07
 */
@Data
@Table(name = "app_auth_data_group")
public class AuthDataGroupDO extends TenantBaseDO {

    @Column(name = "application_code", nullable = false, comment = "应用code")
    private String applicationCode;

    @Column(name = "role_code", nullable = false, comment = "角色code")
    private String roleCode;

    @Column(name = "menu_id", nullable = false, comment = "菜单code")
    private String menuCode;

    @Column(name = "group_code", nullable = false, comment = "数据权限组code")
    private String groupCode;

    @Column(name = "group_name", nullable = false, length = 100, comment = "组名称")
    private String groupName;

    @Column(name = "group_order", nullable = false, comment = "组排序")
    private Integer groupOrder;

    @Column(name = "description", length = 256, comment = "描述")
    private String description;

    @Column(name = "scope_field_name", nullable = false, comment = "关联业务实体字段名称")
    private String scopeFieldName;

    @Column(name = "scope_level", nullable = false, length = 32, comment = "关联业务实体字段对应的权限范围")
    private String scopeLevel;

    @Column(name = "is_operable", nullable = false, comment = "是否可以操作")
    private Boolean operable;

}
