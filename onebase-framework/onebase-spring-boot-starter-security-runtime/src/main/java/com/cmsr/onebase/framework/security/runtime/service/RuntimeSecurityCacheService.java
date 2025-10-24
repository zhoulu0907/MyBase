package com.cmsr.onebase.framework.security.runtime.service;

import com.cmsr.onebase.module.app.api.permission.AppPermissionApi;
import com.cmsr.onebase.module.app.api.permission.dto.*;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author：huangjie
 * @Date：2025/10/24 18:21
 */
@Setter
@Service
public class RuntimeSecurityCacheService {

    @Autowired
    private AppPermissionApi appPermissionApi;

    @Cacheable(cacheNames = "app:permission:role", key = "#applicationId + ':' + #userId")
    public List<RoleDTO> findRoles(Long applicationId, Long userId) {
        return appPermissionApi.findRoles(applicationId, userId);
    }

    @Cacheable(cacheNames = "app:permission:permission", key = "#applicationId + ':' + #roleId")
    public List<PermissionDTO> findPermissions(Long applicationId, Long roleId) {
        return appPermissionApi.findPermissions(applicationId, roleId);
    }

    @Cacheable(cacheNames = "app:permission:view", key = "#applicationId + ':' + #roleId")
    public List<ViewDTO> findViews(Long applicationId, Long roleId) {
        return appPermissionApi.findViews(applicationId, roleId);
    }

    @Cacheable(cacheNames = "app:permission:dataGroup", key = "#applicationId + ':' + #roleId")
    public List<DataGroupDTO> findDataGroups(Long applicationId, Long roleId) {
        return appPermissionApi.findDataGroups(applicationId, roleId);
    }

    @Cacheable(cacheNames = "app:permission:field", key = "#applicationId + ':' + #roleId")
    public List<FieldDTO> findFields(Long applicationId, Long roleId) {
        return appPermissionApi.findFields(applicationId, roleId);
    }


}
