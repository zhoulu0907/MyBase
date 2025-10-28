package com.cmsr.onebase.module.app.api.auth;

import com.cmsr.onebase.module.app.api.auth.dto.*;

import java.util.List;
import java.util.Set;

/**
 * @Author：huangjie
 */
public interface AppAuthApi {

    UserRole findRoles(Long applicationId, Long userId);

    MenuDTO findMenuById(Long menuId);

    Set<Long> findAccessibleMenuIds(Long applicationId, Set<Long> roleIds);

    List<PermissionDTO> findPermissions(Long applicationId, Set<Long> roleIds, Long menuId);

    List<DataGroupDTO> findDataGroups(Long applicationId, Set<Long> roleIds, Long menuId);

    List<FieldDTO> findFields(Long applicationId, Set<Long> roleIds, Long menuId);

}
