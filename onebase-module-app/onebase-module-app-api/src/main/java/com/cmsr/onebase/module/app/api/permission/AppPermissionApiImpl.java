package com.cmsr.onebase.module.app.api.permission;

import com.cmsr.onebase.module.app.api.permission.dto.PermissionDTO;
import com.cmsr.onebase.module.app.api.permission.dto.RoleDTO;
import com.cmsr.onebase.module.app.core.dal.database.auth.AppAuthPermissionRepository;
import com.cmsr.onebase.module.app.core.dal.database.auth.AppAuthRoleRepository;
import jakarta.annotation.Resource;
import lombok.Setter;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

/**
 * @Author：matianyu
 */
@Setter
@Validated
@Service
public class AppPermissionApiImpl implements AppPermissionApi {

    @Resource
    private AppAuthRoleRepository appAuthRoleRepository;

    @Resource
    private AppAuthPermissionRepository appAuthPermissionRepository;

    @Override
    public List<RoleDTO> findRoles(Long applicationId, Long userId) {
        return appAuthRoleRepository.findByApplicationIdAndUserId(applicationId, userId)
                .stream()
                .map(role -> {
                    RoleDTO roleDTO = new RoleDTO();
                    roleDTO.setId(role.getId());
                    roleDTO.setRoleCode(role.getRoleCode());
                    roleDTO.setRoleName(role.getRoleName());
                    return roleDTO;
                }).toList();
    }

    @Override
    public List<PermissionDTO> findPermissions(Long applicationId, Long roleId) {
        return appAuthPermissionRepository.findByApplicationIdAndRoleId(applicationId, roleId)
                .stream()
                .map(permission -> {
                    PermissionDTO permissionDTO = new PermissionDTO();
                    permissionDTO.setApplicationId(permission.getApplicationId());
                    permissionDTO.setRoleId(permission.getRoleId());
                    permissionDTO.setMenuId(permission.getMenuId());
                    permissionDTO.setIsPageAllowed(permission.getIsPageAllowed());
                    permissionDTO.setIsAllViewsAllowed(permission.getIsAllViewsAllowed());
                    permissionDTO.setIsAllFieldsAllowed(permission.getIsAllFieldsAllowed());
                    permissionDTO.setOperationTags(permission.getOperationTags());
                    return permissionDTO;
                }).toList();
    }

}
