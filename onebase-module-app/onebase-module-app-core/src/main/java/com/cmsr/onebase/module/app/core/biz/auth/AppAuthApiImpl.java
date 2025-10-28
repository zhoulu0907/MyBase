package com.cmsr.onebase.module.app.core.biz.auth;

import com.cmsr.onebase.module.app.api.auth.AppAuthApi;
import com.cmsr.onebase.module.app.api.auth.dto.*;
import com.cmsr.onebase.module.app.core.dal.cache.auth.CachedAppAuthPermissionProvider;
import com.cmsr.onebase.module.app.core.dal.cache.auth.CachedAppAuthRoleProvider;
import com.cmsr.onebase.module.app.core.dal.cache.menu.CachedAppMenuProvider;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @Author：huangjie
 * @Date：2025/10/27 14:06
 */
@Setter
@Service
public class AppAuthApiImpl implements AppAuthApi {

    @Autowired
    private CachedAppAuthRoleProvider cachedAppAuthRoleProvider;

    @Autowired
    private CachedAppMenuProvider cachedAppMenuProvider;

    @Autowired
    private CachedAppAuthPermissionProvider cachedAppAuthPermissionProvider;

    @Override
    public UserRole findRoles(Long userId, Long applicationId) {
        return cachedAppAuthRoleProvider.findByApplicationIdAndUserId(applicationId, userId);
    }

    @Override
    public MenuDTO findMenuById(Long menuId) {
        return cachedAppMenuProvider.findById(menuId);
    }

    @Override
    public Set<Long> findAccessibleMenuIds(Long applicationId, Set<Long> roleIds) {
        Set<Long> result = new HashSet<>();
        for (Long roleId : roleIds) {
            Set<Long> accessibleMenuIds = cachedAppAuthPermissionProvider.findAccessibleMenuIds(applicationId, roleId);
            result.addAll(accessibleMenuIds);
        }
        return result;
    }

    @Override
    public List<PermissionDTO> findPermissions(Long applicationId, Set<Long> roleIds, Long menuId) {
        return List.of();
    }

    @Override
    public List<DataGroupDTO> findDataGroups(Long applicationId, Set<Long> roleIds, Long menuId) {
        return List.of();
    }

    @Override
    public List<FieldDTO> findFields(Long applicationId, Set<Long> roleIds, Long menuId) {
        return List.of();
    }
}
