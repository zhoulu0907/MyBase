package com.cmsr.onebase.module.app.core.dal.dataobject;

import com.cmsr.onebase.framework.orm.entity.BaseTenantEntity;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import lombok.Data;

/**
 * @Author：huangjie
 * @Date：2025/8/5 15:11
 */
@Data
@Table(value = "app_auth_role_dept")
public class AppAuthRoleDeptDO extends BaseTenantEntity {

    @Column(value = "role_id", comment = "角色id")
    private Long roleId;

    @Column(value = "dept_id", comment = "用户Id")
    private Long deptId;

    @Column(value = "is_include_child", comment = "是否包含子部门")
    private Integer isIncludeChild;

}
