package com.cmsr.onebase.module.app.dal.dataobject.auth;

import com.cmsr.onebase.framework.tenant.core.db.TenantBaseDO;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * @Author：huangjie
 * @Date：2025/8/5 15:10
 */
@Data
@Table(name = "app_auth_role")
public class AuthRoleDO extends TenantBaseDO {

    @Column(name = "application_id", nullable = false, comment = "应用id")
    private Long applicationId;

    @Column(name = "role_code", nullable = false, length = 64, comment = "角色编码")
    private String roleCode;

    @Column(name = "role_name", nullable = false, length = 64, comment = "角色名称")
    private String roleName;

    @Column(name = "role_type", nullable = false, comment = "角色类型，1系统管理员2系统默认用户3用户定义")
    private Integer roleType;

    @Column(name = "description", length = 256, comment = "描述")
    private String description;
}
