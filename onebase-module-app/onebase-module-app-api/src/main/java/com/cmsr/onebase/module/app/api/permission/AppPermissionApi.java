package com.cmsr.onebase.module.app.api.permission;

import com.cmsr.onebase.module.app.api.permission.dto.*;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

/**
 * @Author：huangjie
 */
@Tag(name = "RPC 服务 - 应用")
public interface AppPermissionApi {

    Long findEntityByMenuId(Long menuId);

    List<RoleDTO> findRoles(Long applicationId, Long userId);

    List<PermissionDTO> findPermissions(Long applicationId, Long roleId);

    List<ViewDTO> findViews(Long applicationId, Long roleId);

    List<DataGroupDTO> findDataGroups(Long applicationId, Long roleId);

    List<FieldDTO> findFields(Long applicationId, Long roleId);

}
