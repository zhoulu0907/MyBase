package com.cmsr.onebase.framework.security.runtime.service;

import com.cmsr.onebase.framework.security.runtime.dto.DataPermission;
import com.cmsr.onebase.framework.security.runtime.dto.DataPermissionItem;
import com.cmsr.onebase.framework.security.runtime.dto.FieldPermission;
import com.cmsr.onebase.framework.security.runtime.dto.MenuOperation;
import com.cmsr.onebase.module.app.api.permission.AppPermissionApi;
import com.cmsr.onebase.module.app.api.permission.dto.*;
import lombok.Setter;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
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
public class RTPermissionService {

    public static RTPermissionService instance;

    @Autowired
    private AppPermissionApi appPermissionApi;

    /**
     * 获取用户在应用下面的角色，如果角色不存在，则返回空，代表这个用户没有权限访问这个应用
     *
     * @param applicationId
     * @param userId
     * @return
     */
    private Set<Long> getRoleIds(Long userId, Long applicationId) {
        return appPermissionApi.findRoles(userId, applicationId).stream().map(RoleDTO::getId).collect(Collectors.toSet());
    }

    public Set<Long> getAccessibleMenus(Long userId, Long applicationId) {
        Set<Long> roleIds = getRoleIds(userId, applicationId);
        Set<Long> result = new HashSet<>();
        List<PermissionDTO> permissions = appPermissionApi.findPermissions(applicationId, roleIds);
        for (PermissionDTO permissionDTO : permissions) {
            if (Objects.equals(permissionDTO.getIsPageAllowed(), NumberUtils.INTEGER_ONE)) {
                result.add(permissionDTO.getMenuId());
            }
        }
        return result;
    }

    public Set<Long> getAccessibleViews(Long userId, Long applicationId, Long menuId) {
        Set<Long> roleIds = getRoleIds(userId, applicationId);
        Set<Long> result = new HashSet<>();
        boolean isAllViewsAllowed = false;
        List<PermissionDTO> permissions = appPermissionApi.findPermissions(applicationId, roleIds, menuId);
        for (PermissionDTO permission : permissions) {
            if (Objects.equals(permission.getIsAllViewsAllowed(), NumberUtils.INTEGER_ONE)) {
                isAllViewsAllowed = true;
            } else {
                result.addAll(permission.getViewIds());
            }
        }
        if (isAllViewsAllowed) {
            List<ViewDTO> views = appPermissionApi.findViews(applicationId, menuId);
            for (ViewDTO view : views) {
                result.add(view.getId());
            }
        }
        return result;
    }

    public boolean checkMenuEntity(Long applicationId, Long menuId, Long entityId) {
        MenuDTO menuDTO = appPermissionApi.findMenuById(menuId);
        if (menuDTO.getApplicationId().equals(applicationId) && menuDTO.getEntityId().equals(entityId)) {
            return true;
        } else {
            return false;
        }
    }


    public MenuOperation getMenuOperation(Long userId, Long applicationId, Long menuId) {
        MenuOperation menuOperation = new MenuOperation();
        Set<Long> roleIds = getRoleIds(userId, applicationId);
        List<PermissionDTO> permissionDTOs = appPermissionApi.findPermissions(applicationId, roleIds, menuId);
        for (PermissionDTO permissionDTO : permissionDTOs) {
            if (Objects.equals(permissionDTO.getIsPageAllowed(), NumberUtils.INTEGER_ONE)) {
                menuOperation.setAccessible(true);
            }
            for (String operationTag : permissionDTO.getOperationTags()) {
                switch (operationTag) {
                    case "create":
                        menuOperation.setCanCreate(true);
                        break;
                    case "edit":
                        menuOperation.setCanEdit(true);
                        break;
                    case "delete":
                        menuOperation.setCanDelete(true);
                        break;
                    case "import":
                        menuOperation.setCanImport(true);
                        break;
                    case "export":
                        menuOperation.setCanExport(true);
                        break;
                    case "share":
                        menuOperation.setCanShare(true);
                        break;
                }
            }
        }
        return menuOperation;
    }

    public DataPermission getMenuDataPermission(Long userId, Long applicationId, Long menuId) {
        DataPermission dataPermission = new DataPermission();
        dataPermission.setMenuId(menuId);
        //
        Set<Long> roleIds = getRoleIds(userId, applicationId);
        List<DataGroupDTO> dataGroups = appPermissionApi.findDataGroups(applicationId, roleIds, menuId);
        for (DataGroupDTO dataGroup : dataGroups) {
            DataPermissionItem dataPermissionItem = new DataPermissionItem();
            //TODO
            dataPermission.getItems().add(dataPermissionItem);
        }
        return dataPermission;
    }


    public FieldPermission getMenuFieldPermission(Long userId, Long applicationId, Long menuId) {
        FieldPermission fieldPermission = new FieldPermission();
        //
        Set<Long> roleIds = getRoleIds(userId, applicationId);
        List<PermissionDTO> permissionDTO = appPermissionApi.findPermissions(applicationId, roleIds, menuId);
        for (PermissionDTO permission : permissionDTO) {
            if (Objects.equals(permission.getIsAllFieldsAllowed(), NumberUtils.INTEGER_ONE)) {
                fieldPermission.setAllFieldsAllowed(true);
            }
        }
        if (fieldPermission.isAllFieldsAllowed()) {
            return fieldPermission;
        }
        List<FieldDTO> fields = appPermissionApi.findFields(applicationId, roleIds, menuId);
        return fieldPermission;
    }


}
