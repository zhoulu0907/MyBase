package com.cmsr.onebase.framework.security.runtime.service;

import com.cmsr.onebase.framework.security.runtime.dto.*;
import com.cmsr.onebase.module.app.api.auth.AppAuthApi;
import com.cmsr.onebase.module.app.api.auth.dto.*;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

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
    private AppAuthApi appAuthApi;

    public UserRole getUserRole(Long userId, Long applicationId) {
        return appAuthApi.findRoles(applicationId, userId);
    }

    public AccessibleMenus getAccessibleMenus(Long userId, Long applicationId) {
        AccessibleMenus accessibleMenus = new AccessibleMenus();
        UserRole userRole = getUserRole(userId, applicationId);
        if (userRole == null) {
            accessibleMenus.setAllAllowed(false);
            accessibleMenus.setAllDenied(true);
            accessibleMenus.setMenuIds(Collections.emptySet());
            return accessibleMenus;
        }
        if (userRole.isAdminRole()) {
            accessibleMenus.setAllAllowed(true);
            accessibleMenus.setAllDenied(false);
            accessibleMenus.setMenuIds(Collections.emptySet());
            return accessibleMenus;
        }
        Set<Long> roleIds = userRole.getRoleIds();
        Set<Long> menuIds = appAuthApi.findAccessibleMenuIds(applicationId, roleIds);
        accessibleMenus.setAllAllowed(false);
        accessibleMenus.setAllDenied(false);
        accessibleMenus.setMenuIds(menuIds);
        return accessibleMenus;
    }

    public AccessibleViews getAccessibleViews(Long userId, Long applicationId, Long menuId) {
        AccessibleViews accessibleMenus = new AccessibleViews();
        UserRole userRole = getUserRole(userId, applicationId);
        if (userRole == null) {
            accessibleMenus.setAllAllowed(false);
            accessibleMenus.setAllDenied(true);
            accessibleMenus.setViewIds(Collections.emptySet());
            return accessibleMenus;
        }
        if (userRole.isAdminRole()) {
            accessibleMenus.setAllAllowed(true);
            accessibleMenus.setAllDenied(false);
            accessibleMenus.setViewIds(Collections.emptySet());
            return accessibleMenus;
        }
        Set<Long> roleIds = userRole.getRoleIds();
        Set<Long> viewIds = new HashSet<>();
        boolean isAllViewsAllowed = false;
        List<PermissionDTO> permissions = appAuthApi.findPermissions(applicationId, roleIds, menuId);
        for (PermissionDTO permission : permissions) {
            if (Objects.equals(permission.getIsAllViewsAllowed(), NumberUtils.INTEGER_ONE)) {
                isAllViewsAllowed = true;
            } else {
                viewIds.addAll(permission.getViewIds());
            }
        }
        if (isAllViewsAllowed) {
            accessibleMenus.setAllAllowed(true);
            accessibleMenus.setAllDenied(false);
            accessibleMenus.setViewIds(Collections.emptySet());
        } else {
            accessibleMenus.setAllAllowed(false);
            accessibleMenus.setAllDenied(false);
            accessibleMenus.setViewIds(viewIds);
        }
        return accessibleMenus;
    }

    public boolean checkMenuEntity(Long applicationId, Long menuId, Long entityId) {
        MenuDTO menuDTO = appAuthApi.findMenuById(menuId);
        if (menuDTO.getApplicationId().equals(applicationId) && menuDTO.getEntityId().equals(entityId)) {
            return true;
        } else {
            return false;
        }
    }

    public MenuOperation getMenuOperation(Long userId, Long applicationId, Long menuId) {
        MenuOperation menuOperation = new MenuOperation();
        UserRole userRole = getUserRole(userId, applicationId);
        if (userRole == null) {
            menuOperation.allDeny();
            return menuOperation;
        }
        if (userRole.isAdminRole()) {
            menuOperation.allAllow();
            return menuOperation;
        }
        Set<Long> roleIds = userRole.getRoleIds();
        List<PermissionDTO> permissionDTOs = appAuthApi.findPermissions(applicationId, roleIds, menuId);
        for (PermissionDTO permissionDTO : permissionDTOs) {
            if (Objects.equals(permissionDTO.getIsPageAllowed(), NumberUtils.INTEGER_ONE)) {
                menuOperation.setAccessible(true);
            }
            for (String operationTag : permissionDTO.getOperationTags()) {
                if (operationTag.equalsIgnoreCase("create")) {
                    menuOperation.setCanCreate(true);
                } else if (operationTag.equalsIgnoreCase("edit")) {
                    menuOperation.setCanEdit(true);
                } else if (operationTag.equalsIgnoreCase("delete")) {
                    menuOperation.setCanDelete(true);
                } else if (operationTag.equalsIgnoreCase("import")) {
                    menuOperation.setCanImport(true);
                } else if (operationTag.equalsIgnoreCase("export")) {
                    menuOperation.setCanExport(true);
                } else if (operationTag.equalsIgnoreCase("share")) {
                    menuOperation.setCanShare(true);
                }
            }
        }
        return menuOperation;
    }

    public DataPermission getMenuDataPermission(Long userId, Long applicationId, Long menuId) {
        DataPermission dataPermission = new DataPermission();
        UserRole userRole = getUserRole(userId, applicationId);
        if (userRole == null) {
            dataPermission.setAllAllowed(false);
            dataPermission.setAllDenied(true);
            return dataPermission;
        }
        if (userRole.isAdminRole()) {
            dataPermission.setAllAllowed(true);
            dataPermission.setAllDenied(false);
            return dataPermission;
        }
        //
        Set<Long> roleIds = userRole.getRoleIds();
        List<DataGroupDTO> dataGroups = appAuthApi.findDataGroups(applicationId, roleIds, menuId);
        for (DataGroupDTO dataGroup : dataGroups) {
            DataPermissionItem dataPermissionItem = new DataPermissionItem();
            dataPermissionItem.setScopTags(DataPermissionTag.createTags(dataGroup.getScopeTags()));
            dataPermissionItem.setScopeFieldId(dataGroup.getScopeFieldId());
            dataPermissionItem.setScopeLevel(DataPermissionLevel.valueOf(dataGroup.getScopeLevel()));
            dataPermissionItem.setScopeValue(dataGroup.getScopeValue());
            dataPermissionItem.setFilters(convertDataFilterLists(dataGroup.getDataFilters()));
            dataPermissionItem.setCanEdit(dataGroup.getOperationTags().contains("edit"));
            dataPermissionItem.setCanDelete(dataGroup.getOperationTags().contains("view"));
            dataPermission.getItems().add(dataPermissionItem);
        }
        dataPermission.setAllAllowed(false);
        dataPermission.setAllDenied(false);
        return dataPermission;
    }

    private List<List<DataPermissionFilter>> convertDataFilterLists(List<List<DataFilterDTO>> dataFilters) {
        if (dataFilters == null || dataFilters.isEmpty()) {
            return Collections.emptyList();
        }
        List<List<DataPermissionFilter>> result = new ArrayList<>();
        for (List<DataFilterDTO> dtoList : dataFilters) {
            if (dtoList == null || dtoList.isEmpty()) {
                continue;
            }
            List<DataPermissionFilter> filterList = convertDataFilterList(dtoList);
            if (CollectionUtils.isNotEmpty(filterList)) {
                result.add(filterList);
            }
        }
        return result;
    }

    private static List<DataPermissionFilter> convertDataFilterList(List<DataFilterDTO> dtoList) {
        List<DataPermissionFilter> filterList = new ArrayList<>();
        for (DataFilterDTO dto : dtoList) {
            if (dto != null) {
                DataPermissionFilter filter = new DataPermissionFilter();
                filter.setFieldId(dto.getFieldId());
                filter.setFieldOperator(dto.getFieldOperator());
                filter.setFieldValueType(dto.getFieldValueType());
                filter.setFieldValue(dto.getFieldValue());
                filterList.add(filter);
            }
        }
        return filterList;
    }

    public FieldPermission getMenuFieldPermission(Long userId, Long applicationId, Long menuId) {
        FieldPermission fieldPermission = new FieldPermission();
        UserRole userRole = getUserRole(userId, applicationId);
        if (userRole == null) {
            fieldPermission.setAllAllowed(false);
            fieldPermission.setAllDenied(true);
            return fieldPermission;
        }
        if (userRole.isAdminRole()) {
            fieldPermission.setAllAllowed(true);
            fieldPermission.setAllDenied(false);
            return fieldPermission;
        }
        //
        Set<Long> roleIds = userRole.getRoleIds();
        List<PermissionDTO> permissionDTO = appAuthApi.findPermissions(applicationId, roleIds, menuId);
        for (PermissionDTO permission : permissionDTO) {
            if (Objects.equals(permission.getIsAllFieldsAllowed(), NumberUtils.INTEGER_ONE)) {
                fieldPermission.setAllAllowed(true);
            }
        }
        if (fieldPermission.isAllAllowed()) {
            return fieldPermission;
        }
        List<FieldDTO> fields = appAuthApi.findFields(applicationId, roleIds, menuId);
        for (FieldDTO field : fields) {
            FieldPermissionItem fieldPermissionItem = fieldPermission.getItems().get(field.getFieldId());
            if (fieldPermissionItem == null) {
                fieldPermissionItem = new FieldPermissionItem();
                fieldPermissionItem.setFieldId(field.getFieldId());
                fieldPermission.getItems().put(field.getFieldId(), fieldPermissionItem);
            }
            if (Objects.equals(field.getIsCanRead(), NumberUtils.INTEGER_ONE)) {
                fieldPermissionItem.setCanRead(true);
            }
            if (Objects.equals(field.getIsCanEdit(), NumberUtils.INTEGER_ONE)) {
                fieldPermissionItem.setCanEdit(true);
            }
            if (Objects.equals(field.getIsCanDownload(), NumberUtils.INTEGER_ONE)) {
                fieldPermissionItem.setCanDownload(true);
            }
        }
        return fieldPermission;
    }


}
