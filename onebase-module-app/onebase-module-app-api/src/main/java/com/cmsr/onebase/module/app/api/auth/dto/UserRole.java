package com.cmsr.onebase.module.app.api.auth.dto;

import lombok.Data;

import java.util.List;
import java.util.Set;

/**
 * @Author：huangjie
 * @Date：2025/10/27 10:02
 */
@Data
public class UserRole {

    private boolean adminRole;

    private Set<Long> roleIds;

    private List<RoleDTO>  roles;
}
