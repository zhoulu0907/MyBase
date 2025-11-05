package com.cmsr.onebase.module.app.core.dal.dataobject.auth;

import com.cmsr.onebase.framework.tenant.core.db.TenantBaseDO;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * @Author：huangjie
 * @Date：2025/8/5 15:11
 */
@Data
@Table(name = "app_auth_role_dept")
public class AuthRoleDeptDO extends TenantBaseDO {
    
    @Column(name = "role_id", nullable = false, comment = "角色id")
    private Long roleId;

    @Column(name = "dept_id", nullable = false, comment = "用户Id")
    private Long deptId;

    @Column(name = "is_include_child", nullable = false, comment = "是否包含子部门")
    private Integer isIncludeChild;

}
