package com.cmsr.onebase.module.app.core.biz.auth;

import com.cmsr.onebase.module.app.api.auth.AppAuthApi;
import com.cmsr.onebase.module.app.api.auth.dto.*;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * @Author：huangjie
 * @Date：2025/10/27 14:06
 */
@Setter
@Service
public class AppAuthApiImpl implements AppAuthApi {

    @Override
    public UserRole findRoles(Long userId, Long applicationId) {
        return null;
    }

    @Override
    public MenuDTO findMenuById(Long menuId) {
        return null;
    }

    @Override
    public Set<Long> findAccessibleMenuIds(Long applicationId, Set<Long> roleIds) {
        return Set.of();
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
