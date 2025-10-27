package com.cmsr.onebase.module.app.api.permission;

import com.cmsr.onebase.module.app.api.permission.dto.*;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;
import java.util.Set;

/**
 * @Author：huangjie
 */
@Tag(name = "RPC 服务 - 应用")
public interface AppPermissionApi {

    List<RoleDTO> findRoles(Long userId, Long applicationId);

    List<ViewDTO> findViews(Long applicationId, Long menuId);

    MenuDTO findMenuById(Long menuId);

    List<PermissionDTO> findPermissions(Long applicationId, Set<Long> roleIds);

    List<PermissionDTO> findPermissions(Long applicationId, Set<Long> roleIds, Long menuId);

    List<DataGroupDTO> findDataGroups(Long applicationId, Set<Long> roleIds, Long menuId);

    List<FieldDTO> findFields(Long applicationId, Set<Long> roleIds, Long menuId);
}
