package com.cmsr.onebase.module.app.api.permission;

import com.cmsr.onebase.module.app.api.permission.dto.PermissionDTO;
import com.cmsr.onebase.module.app.api.permission.dto.RoleDTO;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

/**
 * @Author：huangjie
 */
@Tag(name = "RPC 服务 - 应用")
public interface AppPermissionApi {

    List<RoleDTO> findRoles(Long applicationId, Long userId);

    List<PermissionDTO> findPermissions(Long applicationId, Long roleId);

}
