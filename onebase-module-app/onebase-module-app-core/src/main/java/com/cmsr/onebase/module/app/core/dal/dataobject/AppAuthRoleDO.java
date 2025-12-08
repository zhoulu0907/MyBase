package com.cmsr.onebase.module.app.core.dal.dataobject;

import com.cmsr.onebase.framework.orm.entity.BaseBizEntity;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import lombok.Data;

/**
 * @Author：huangjie
 * @Date：2025/8/5 15:10
 */
@Data
@Table(value = "app_auth_role")
public class AppAuthRoleDO extends BaseBizEntity {

    @Column(value = "role_uuid", comment = "角色id")
    private String roleUuid;

    @Column(value = "role_code", comment = "角色编码")
    private String roleCode;

    @Column(value = "role_name", comment = "角色名称")
    private String roleName;

    @Column(value = "role_type", comment = "角色类型，1系统管理员2系统默认用户3用户定义")
    private Integer roleType;

    @Column(value = "description", comment = "描述")
    private String description;
}
