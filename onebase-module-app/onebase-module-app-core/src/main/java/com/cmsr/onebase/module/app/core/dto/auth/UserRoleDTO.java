package com.cmsr.onebase.module.app.core.dto.auth;

import lombok.Data;

import java.util.Set;

/**
 * @Author：huangjie
 * @Date：2025/10/27 10:02
 */
@Data
public class UserRoleDTO {

    private boolean adminRole;

    private Set<Long> roleIds;

    private Set<String> roleUuids;

}
