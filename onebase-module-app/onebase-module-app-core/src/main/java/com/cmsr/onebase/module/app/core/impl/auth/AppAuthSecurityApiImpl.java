package com.cmsr.onebase.module.app.core.impl.auth;

import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import com.cmsr.onebase.module.app.api.security.AppAuthSecurityApi;
import com.cmsr.onebase.module.app.api.security.bo.*;
import com.cmsr.onebase.module.app.core.dal.database.menu.AppMenuRepository;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppAuthPermissionDO;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppMenuDO;
import com.cmsr.onebase.module.app.core.dto.auth.UserRoleDTO;
import com.cmsr.onebase.module.app.core.enums.menu.MenuTypeEnum;
import com.cmsr.onebase.module.app.core.provider.auth.AppAuthDataGroupProvider;
import com.cmsr.onebase.module.app.core.provider.auth.AppAuthFieldProvider;
import com.cmsr.onebase.module.app.core.provider.auth.AppAuthPermissionProvider;
import com.cmsr.onebase.module.app.core.provider.auth.AppAuthRoleProvider;
import com.cmsr.onebase.module.app.core.provider.menu.AppMenuProvider;
import com.cmsr.onebase.module.app.core.utils.CacheUtils;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;
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
public class AppAuthSecurityApiImpl implements AppAuthSecurityApi {

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private AppMenuRepository appMenuRepository;

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
    public void loadAuthCache(Long userId, Long applicationId) {
        List<Long> menuIds = getVisibleMenuIds(userId, applicationId);
        if (CollectionUtils.isEmpty(menuIds)) {
            return;
        }
        for (Long menuId : menuIds) {
            getMenuOperationPermission(userId, applicationId, menuId);
            getMenuDataPermission(userId, applicationId, menuId);
            getMenuFieldPermission(userId, applicationId, menuId);
        }
    }

    @Override
    public void cleanAuthCache(Long userId, Long applicationId) {
        String key = CacheUtils.keyForAuth(userId, applicationId);
        redissonClient.getMapCache(key, CacheUtils.KRYO5_CODEC).clear();
    }

    @Override
    public boolean isApplicationAdmin(Long userId, Long applicationId) {
        return doIsApplicationAdmin(userId, applicationId);
    }

    @Override
    public boolean hasApplicationPermission(Long userId, Long applicationId) {
        String key = CacheUtils.keyForAuth(userId, applicationId);
        RMapCache<String, Boolean> mapCache = redissonClient.getMapCache(key, CacheUtils.KRYO5_CODEC);
        String hashKey = CacheUtils.keyForHasPerm();
        Boolean hasPermission = mapCache.get(hashKey);
        if (hasPermission != null) {
            return hasPermission;
        }
        hasPermission = doHasApplicationPermission(userId, applicationId);
        mapCache.put(hashKey, hasPermission, CacheUtils.CACHE_TTL, CacheUtils.CACHE_TTL_UNIT);
        return hasPermission;
    }

    @Override
    public boolean checkMenuEntity(Long applicationId, Long menuId, String entityUuid) {
        return doCheckMenuEntity(applicationId, menuId, entityUuid);
    }

    @Override
    public List<Long> getVisibleMenuIds(Long userId, Long applicationId) {
        String key = CacheUtils.keyForAuth(userId, applicationId);
        RMapCache<String, List<Long>> mapCache = redissonClient.getMapCache(key, CacheUtils.KRYO5_CODEC);
        String hashKey = CacheUtils.keyForVisibleMenuIds();
        List<Long> menuIds = mapCache.get(hashKey);
        if (menuIds != null) {
            return menuIds;
        }
        menuIds = doGetVisibleMenuIds(userId, applicationId);
        mapCache.put(hashKey, menuIds, CacheUtils.CACHE_TTL, CacheUtils.CACHE_TTL_UNIT);
        return menuIds;
    }

    @Override
    public OperationPermission getMenuOperationPermission(Long userId, Long applicationId, Long menuId) {
        String key = CacheUtils.keyForAuth(userId, applicationId);
        RMapCache<String, OperationPermission> mapCache = redissonClient.getMapCache(key, CacheUtils.KRYO5_CODEC);
        String hashKey = CacheUtils.keyForOperation(menuId);
        OperationPermission operationPermission = mapCache.get(hashKey);
        if (operationPermission != null) {
            return operationPermission;
        }
        operationPermission = doGetMenuOperationPermission(userId, applicationId, menuId);
        mapCache.put(hashKey, operationPermission, CacheUtils.CACHE_TTL, CacheUtils.CACHE_TTL_UNIT);
        return operationPermission;
    }

    @Override
    public DataPermission getMenuDataPermission(Long userId, Long applicationId, Long menuId) {
        String key = CacheUtils.keyForAuth(userId, applicationId);
        RMapCache<String, DataPermission> mapCache = redissonClient.getMapCache(key, CacheUtils.KRYO5_CODEC);
        String hashKey = CacheUtils.keyForData(menuId);
        DataPermission dataPermission = mapCache.get(hashKey);
        if (dataPermission != null) {
            return dataPermission;
        }
        dataPermission = doGetMenuDataPermission(userId, applicationId, menuId);
        mapCache.put(hashKey, dataPermission, CacheUtils.CACHE_TTL, CacheUtils.CACHE_TTL_UNIT);
        return dataPermission;
    }

    @Override
    public FieldPermission getMenuFieldPermission(Long userId, Long applicationId, Long menuId) {
        String key = CacheUtils.keyForAuth(userId, applicationId);
        RMapCache<String, FieldPermission> mapCache = redissonClient.getMapCache(key, CacheUtils.KRYO5_CODEC);
        String hashKey = CacheUtils.keyForField(menuId);
        FieldPermission fieldPermission = mapCache.get(hashKey);
        if (fieldPermission != null) {
            return fieldPermission;
        }
        fieldPermission = doGetMenuFieldPermission(userId, applicationId, menuId);
        mapCache.put(hashKey, fieldPermission, CacheUtils.CACHE_TTL, CacheUtils.CACHE_TTL_UNIT);
        return fieldPermission;
    }


    public boolean doCheckMenuEntity(Long applicationId, Long menuId, String entityUuid) {
        AppMenuDO menuDO = appMenuProvider.findByMenuId(menuId);
        if (menuDO == null) {
            return false;
        }
        if (menuDO.getApplicationId().equals(applicationId)
                && menuDO.getId().equals(menuId)
                && menuDO.getEntityUuid().equals(entityUuid)) {
            return true;
        }
        return false;
    }

    public Boolean doHasApplicationPermission(Long userId, Long applicationId) {
        UserRoleDTO userRoleDTO = appAuthRoleProvider.findUserRoleByApplication(userId, applicationId);
        if (userRoleDTO != null && userRoleDTO.isAdminRole()) {
            return true;
        }
        if (userRoleDTO == null || CollectionUtils.isEmpty(userRoleDTO.getRoleIds()) || CollectionUtils.isEmpty(userRoleDTO.getRoleUuids())) {
            return false;
        }
        return true;
    }

    public boolean doIsApplicationAdmin(Long userId, Long applicationId) {
        UserRoleDTO userRoleDTO = appAuthRoleProvider.findUserRoleByApplication(userId, applicationId);
        return userRoleDTO != null && userRoleDTO.isAdminRole();
    }

    public List<Long> doGetVisibleMenuIds(Long userId, Long applicationId) {
        UserRoleDTO userRoleDTO = appAuthRoleProvider.findUserRoleByApplication(userId, applicationId);
        List<AppMenuDO> menuDOS = appMenuRepository.findVisibleByAppIdAndType(applicationId,
                Set.of(MenuTypeEnum.PAGE.getValue(), MenuTypeEnum.GROUP.getValue()));
        if (userRoleDTO.isAdminRole()) {
            return menuDOS.stream().map(AppMenuDO::getId).toList();
        }
        Set<String> roleUuids = userRoleDTO.getRoleUuids();
        List<AppAuthPermissionDO> permissions = appAuthPermissionProvider.findPermissions(applicationId, roleUuids);
        Set<String> menuUuidBlacklist = new HashSet<>();
        for (AppAuthPermissionDO permission : permissions) {
            if (!NumberUtils.INTEGER_ONE.equals(permission.getIsPageAllowed()) && StringUtils.isNotEmpty(permission.getMenuUuid()))
                menuUuidBlacklist.add(permission.getMenuUuid());
        }
        return menuDOS.stream().filter(v -> !menuUuidBlacklist.contains(v.getMenuUuid())).map(AppMenuDO::getId).toList();
    }

    public OperationPermission doGetMenuOperationPermission(Long userId, Long applicationId, Long menuId) {
        OperationPermission operationPermission = new OperationPermission();
        UserRoleDTO userRoleDTO = appAuthRoleProvider.findUserRoleByApplication(userId, applicationId);
        if (userRoleDTO != null && userRoleDTO.isAdminRole()) {
            operationPermission.allAllow();
            return operationPermission;
        }
        if (userRoleDTO == null || CollectionUtils.isEmpty(userRoleDTO.getRoleIds())) {
            operationPermission.allDeny();
            return operationPermission;
        }
        //
        Set<String> roleUuids = userRoleDTO.getRoleUuids();
        AppMenuDO appMenuDO = appMenuProvider.findByMenuId(menuId);
        String menuUuid = appMenuDO.getMenuUuid();
        //
        List<AppAuthPermissionDO> permissionDOs = appAuthPermissionProvider.findPermissions(applicationId, roleUuids, menuUuid);
        for (AppAuthPermissionDO permissionDO : permissionDOs) {
            if (NumberUtils.INTEGER_ONE.equals(permissionDO.getIsPageAllowed())) {
                operationPermission.setPageAllowed(true);
            }
            if (NumberUtils.INTEGER_ONE.equals(permissionDO.getIsAllViewsAllowed())) {
                operationPermission.setAllViewsAllowed(true);
            }
            if (NumberUtils.INTEGER_ONE.equals(permissionDO.getIsAllFieldsAllowed())) {
                operationPermission.setAllFieldsAllowed(true);
            }
            List<String> operationTags = JsonUtils.parseArray(permissionDO.getOperationTags(), String.class);
            for (String operationTag : operationTags) {
                if (operationTag.equalsIgnoreCase("create")) {
                    operationPermission.setCanCreate(true);
                } else if (operationTag.equalsIgnoreCase("edit")) {
                    operationPermission.setCanEdit(true);
                } else if (operationTag.equalsIgnoreCase("delete")) {
                    operationPermission.setCanDelete(true);
                } else if (operationTag.equalsIgnoreCase("import")) {
                    operationPermission.setCanImport(true);
                } else if (operationTag.equalsIgnoreCase("export")) {
                    operationPermission.setCanExport(true);
                } else if (operationTag.equalsIgnoreCase("share")) {
                    operationPermission.setCanShare(true);
                }
            }
        }
        return operationPermission;
    }


    public DataPermission doGetMenuDataPermission(Long userId, Long applicationId, Long menuId) {
        DataPermission dataPermission = new DataPermission();
        UserRoleDTO userRoleDTO = appAuthRoleProvider.findUserRoleByApplication(userId, applicationId);
        if (userRoleDTO != null && userRoleDTO.isAdminRole()) {
            dataPermission.setAllAllowed(true);
            dataPermission.setAllDenied(false);
            return dataPermission;
        }
        if (userRoleDTO == null || CollectionUtils.isEmpty(userRoleDTO.getRoleIds())) {
            dataPermission.setAllAllowed(false);
            dataPermission.setAllDenied(true);
            return dataPermission;
        }
        //
        Set<String> roleUuids = userRoleDTO.getRoleUuids();
        AppMenuDO appMenuDO = appMenuProvider.findByMenuId(menuId);
        String menuUuid = appMenuDO.getMenuUuid();
        //
        List<DataPermissionGroup> dataGroups = appAuthDataGroupProvider.findDataGroups(applicationId, roleUuids, menuUuid);
        dataPermission.setGroups(dataGroups);
        dataPermission.setAllAllowed(false);
        dataPermission.setAllDenied(false);
        //
        return dataPermission;
    }


    public FieldPermission doGetMenuFieldPermission(Long userId, Long applicationId, Long menuId) {
        FieldPermission fieldPermission = new FieldPermission();
        UserRoleDTO userRoleDTO = appAuthRoleProvider.findUserRoleByApplication(userId, applicationId);
        if (userRoleDTO != null && userRoleDTO.isAdminRole()) {
            fieldPermission.setAllAllowed(true);
            fieldPermission.setAllDenied(false);
            return fieldPermission;
        }
        if (userRoleDTO == null) {
            fieldPermission.setAllAllowed(false);
            fieldPermission.setAllDenied(true);
            return fieldPermission;
        }
        OperationPermission operationPermission = getMenuOperationPermission(userId, applicationId, menuId);
        if (operationPermission.isAllFieldsAllowed()) {
            fieldPermission.setAllAllowed(true);
            fieldPermission.setAllDenied(false);
            return fieldPermission;
        }
        if (CollectionUtils.isEmpty(userRoleDTO.getRoleIds())) {
            fieldPermission.setAllAllowed(false);
            fieldPermission.setAllDenied(true);
            return fieldPermission;
        }
        //
        Set<String> roleUuids = userRoleDTO.getRoleUuids();
        AppMenuDO appMenuDO = appMenuProvider.findByMenuId(menuId);
        String menuUuid = appMenuDO.getMenuUuid();
        //
        List<FieldPermissionItem> fields = appAuthFieldProvider.findFields(applicationId, roleUuids, menuUuid);
        fieldPermission.setAllAllowed(false);
        fieldPermission.setAllDenied(false);
        fieldPermission.setFields(fields);
        //
        return fieldPermission;
    }


}
