package com.cmsr.onebase.module.app.core.biz.auth;

import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import com.cmsr.onebase.module.app.api.security.AppAuthSecurityApi;
import com.cmsr.onebase.module.app.api.security.bo.*;
import com.cmsr.onebase.module.app.core.dal.dataobject.auth.AuthPermissionDO;
import com.cmsr.onebase.module.app.core.dal.dataobject.menu.MenuDO;
import com.cmsr.onebase.module.app.core.dal.provider.auth.AppAuthDataGroupProvider;
import com.cmsr.onebase.module.app.core.dal.provider.auth.AppAuthFieldProvider;
import com.cmsr.onebase.module.app.core.dal.provider.auth.AppAuthPermissionProvider;
import com.cmsr.onebase.module.app.core.dal.provider.auth.AppAuthRoleProvider;
import com.cmsr.onebase.module.app.core.dal.provider.menu.AppMenuProvider;
import com.cmsr.onebase.module.app.core.dto.auth.UserRole;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * @Author：huangjie
 * @Date：2025/10/27 14:06
 */
@Setter
@Service
public class AppAuthSecurityApiImpl implements AppAuthSecurityApi {

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private AppAuthRoleProvider appAuthRoleProvider;

    @Autowired
    private AppMenuProvider appMenuProvider;

    @Autowired
    private AppAuthPermissionProvider appAuthPermissionProvider;

    @Autowired
    private AppAuthDataGroupProvider appAuthDataGroupProvider;

    @Autowired
    private AppAuthFieldProvider appAuthFieldProvider;

    @Override
    public boolean checkMenuEntity(Long applicationId, Long menuId, Long entityId) {
        List<MenuDO> menuDTOS = appMenuProvider.findByApplicationId(applicationId);
        for (MenuDO menuDO : menuDTOS) {
            if (menuDO.getId().equals(menuId) && menuDO.getEntityId().equals(entityId)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public MenuPermission getMenuPermission(Long userId, Long applicationId, Long menuId) {
        MenuPermission menuPermission = new MenuPermission();
        UserRole userRole = appAuthRoleProvider.findByUserIdAndApplicationId(userId, applicationId);
        if (userRole != null && userRole.isAdminRole()) {
            menuPermission.allAllow();
            return menuPermission;
        }
        if (userRole == null || CollectionUtils.isEmpty(userRole.getRoleIds())) {
            menuPermission.allDeny();
            return menuPermission;
        }
        Set<Long> roleIds = userRole.getRoleIds();
        List<AuthPermissionDO> permissionDOs = appAuthPermissionProvider.findPermissions(applicationId, roleIds, menuId);
        for (AuthPermissionDO permissionDO : permissionDOs) {
            if (NumberUtils.INTEGER_ONE.equals(permissionDO.getIsPageAllowed())) {
                menuPermission.setPageAllowed(true);
            }
            if (NumberUtils.INTEGER_ONE.equals(permissionDO.getIsAllViewsAllowed())) {
                menuPermission.setAllViewsAllowed(true);
            }
            if (NumberUtils.INTEGER_ONE.equals(permissionDO.getIsAllFieldsAllowed())) {
                menuPermission.setAllFieldsAllowed(true);
            }
            List<String> operationTags = JsonUtils.parseArray(permissionDO.getOperationTags(), String.class);
            for (String operationTag : operationTags) {
                if (operationTag.equalsIgnoreCase("create")) {
                    menuPermission.setCanCreate(true);
                } else if (operationTag.equalsIgnoreCase("edit")) {
                    menuPermission.setCanEdit(true);
                } else if (operationTag.equalsIgnoreCase("delete")) {
                    menuPermission.setCanDelete(true);
                } else if (operationTag.equalsIgnoreCase("import")) {
                    menuPermission.setCanImport(true);
                } else if (operationTag.equalsIgnoreCase("export")) {
                    menuPermission.setCanExport(true);
                } else if (operationTag.equalsIgnoreCase("share")) {
                    menuPermission.setCanShare(true);
                }
            }
        }
        return menuPermission;
    }

    @Override
    public DataPermission getMenuDataPermission(Long userId, Long applicationId, Long menuId) {
        DataPermission dataPermission = new DataPermission();
        UserRole userRole = appAuthRoleProvider.findByUserIdAndApplicationId(userId, applicationId);
        if (userRole != null && userRole.isAdminRole()) {
            dataPermission.setAllAllowed(true);
            dataPermission.setAllDenied(false);
            return dataPermission;
        }
        if (userRole == null || CollectionUtils.isEmpty(userRole.getRoleIds())) {
            dataPermission.setAllAllowed(false);
            dataPermission.setAllDenied(true);
            return dataPermission;
        }
        //
        Set<Long> roleIds = userRole.getRoleIds();
        List<DataPermissionGroup> dataGroups = appAuthDataGroupProvider.findDataGroups(applicationId, roleIds, menuId);
        dataPermission.setGroups(dataGroups);
        dataPermission.setAllAllowed(false);
        dataPermission.setAllDenied(false);
        return dataPermission;
    }

    @Override
    public FieldPermission getMenuFieldPermission(Long userId, Long applicationId, Long menuId) {
        FieldPermission fieldPermission = new FieldPermission();
        UserRole userRole = appAuthRoleProvider.findByUserIdAndApplicationId(userId, applicationId);
        if (userRole != null && userRole.isAdminRole()) {
            fieldPermission.setAllAllowed(true);
            fieldPermission.setAllDenied(false);
            return fieldPermission;
        }
        if (userRole == null) {
            fieldPermission.setAllAllowed(false);
            fieldPermission.setAllDenied(true);
            return fieldPermission;
        }
        MenuPermission menuPermission = getMenuPermission(userId, applicationId, menuId);
        if (menuPermission.isAllFieldsAllowed()) {
            fieldPermission.setAllAllowed(true);
            fieldPermission.setAllDenied(false);
            return fieldPermission;
        }
        if (CollectionUtils.isEmpty(userRole.getRoleIds())) {
            fieldPermission.setAllAllowed(false);
            fieldPermission.setAllDenied(true);
            return fieldPermission;
        }
        Set<Long> roleIds = userRole.getRoleIds();
        List<FieldPermissionItem> fields = appAuthFieldProvider.findFields(applicationId, roleIds, menuId);
        fieldPermission.setFields(fields);
        return fieldPermission;
    }


}
