package com.cmsr.onebase.framework.security.runtime.service;

import com.cmsr.onebase.framework.security.runtime.OperationEnum;
import com.cmsr.onebase.module.app.api.permission.AppPermissionApi;
import com.cmsr.onebase.module.app.api.permission.dto.PermissionDTO;
import com.cmsr.onebase.module.app.api.permission.dto.RoleDTO;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 没考虑性能优化，简单实现，需要在底层做权限缓存
 *
 * @Author：huangjie
 * @Date：2025/10/24 18:21
 */
@Setter
@Service
public class RuntimeSecurityPermissionService {

    @Autowired
    private AppPermissionApi appPermissionApi;

    /**
     * 获取用户在应用下面的角色，如果角色不存在，则返回空，代表这个用户没有权限访问这个应用
     *
     * @param applicationId
     * @param userId
     * @return
     */
    public Set<Long> getRoleIds(Long applicationId, Long userId) {
        return appPermissionApi.findRoles(applicationId, userId).stream().map(RoleDTO::getId).collect(Collectors.toSet());
    }

    public Set<Long> getAccessibleMenus(Long applicationId, Long userId) {
        List<Long> roleIds = appPermissionApi.findRoles(applicationId, userId).stream().map(RoleDTO::getId).toList();
        Set<Long> result = new HashSet<>();
        for (Long roleId : roleIds) {
            List<PermissionDTO> permissions = appPermissionApi.findPermissions(applicationId, roleId);
            for (PermissionDTO permissionDTO : permissions) {
                if (permissionDTO.getIsPageAllowed() == 1) {
                    result.add(permissionDTO.getMenuId());
                }
            }
        }
        return result;
    }

    public Set<Long> getAccessibleEntities(Long applicationId, Long userId) {
        Set<Long> menuIds = getAccessibleMenus(applicationId, userId);
        return menuIds.stream().map(menuId -> appPermissionApi.findEntityByMenuId(menuId)).collect(Collectors.toSet());
    }

    public Set<OperationEnum> getPagePermissions(Long applicationId, Long pageId) {
        return null;
    }
}
