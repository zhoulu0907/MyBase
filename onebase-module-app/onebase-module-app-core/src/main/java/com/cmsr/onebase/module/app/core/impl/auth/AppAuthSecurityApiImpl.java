package com.cmsr.onebase.module.app.core.impl.auth;

import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import com.cmsr.onebase.module.app.api.security.AppAuthSecurityApi;
import com.cmsr.onebase.module.app.api.security.bo.*;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppAuthPermissionDO;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppMenuDO;
import com.cmsr.onebase.module.app.core.dto.auth.UserRoleDTO;
import com.cmsr.onebase.module.app.core.provider.auth.AppAuthDataGroupProvider;
import com.cmsr.onebase.module.app.core.provider.auth.AppAuthFieldProvider;
import com.cmsr.onebase.module.app.core.provider.auth.AppAuthPermissionProvider;
import com.cmsr.onebase.module.app.core.provider.auth.AppAuthRoleProvider;
import com.cmsr.onebase.module.app.core.provider.menu.AppMenuProvider;
import com.cmsr.onebase.module.app.core.utils.CacheUtils;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.redisson.api.RBucket;
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
    public boolean checkMenuEntity(Long applicationId, Long menuId, String entityUuid) {
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

    @Override
    public boolean isApplicationAdmin(Long userId, Long applicationId) {
        return  false;
        // UserRoleDTO userRoleDTO = appAuthRoleProvider.findUserRoleByApplication(userId, applicationId);
        // return userRoleDTO != null && userRoleDTO.isAdminRole();
    }

    @Override
    public OperationPermission getMenuOperationPermission(Long userId, Long applicationId, Long menuId) {
        String redisKey = CacheUtils.keyForOperationPermission(userId, applicationId, menuId);
        RBucket<OperationPermission> bucket = redissonClient.getBucket(redisKey, CacheUtils.KRYO5_CODEC);
        if (bucket.isExists()) {
            return bucket.get();
        }
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
        Set<Long> roleIds = userRoleDTO.getRoleIds();
        List<AppAuthPermissionDO> permissionDOs = appAuthPermissionProvider.findPermissions(applicationId, roleIds, menuId);
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
        bucket.set(operationPermission, CacheUtils.CACHE_EXPIRE_TIME);
        return operationPermission;
    }

    @Override
    public DataPermission getMenuDataPermission(Long userId, Long applicationId, Long menuId) {
        String redisKey = CacheUtils.keyForDataPermission(userId, applicationId, menuId);
        RBucket<DataPermission> bucket = redissonClient.getBucket(redisKey, CacheUtils.KRYO5_CODEC);
        if (bucket.isExists()) {
            return bucket.get();
        }
        //
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
        Set<Long> roleIds = userRoleDTO.getRoleIds();
        List<DataPermissionGroup> dataGroups = appAuthDataGroupProvider.findDataGroups(applicationId, roleIds, menuId);
        dataPermission.setGroups(dataGroups);
        dataPermission.setAllAllowed(false);
        dataPermission.setAllDenied(false);
        //
        bucket.set(dataPermission, CacheUtils.CACHE_EXPIRE_TIME);
        return dataPermission;
    }

    @Override
    public FieldPermission getMenuFieldPermission(Long userId, Long applicationId, Long menuId) {
        String redisKey = CacheUtils.keyForFieldPermission(userId, applicationId, menuId);
        RBucket<FieldPermission> bucket = redissonClient.getBucket(redisKey, CacheUtils.KRYO5_CODEC);
        if (bucket.isExists()) {
            return bucket.get();
        }
        //
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
        Set<Long> roleIds = userRoleDTO.getRoleIds();
        List<FieldPermissionItem> fields = appAuthFieldProvider.findFields(applicationId, roleIds, menuId);
        fieldPermission.setAllAllowed(false);
        fieldPermission.setAllDenied(false);
        fieldPermission.setFields(fields);
        //
        bucket.set(fieldPermission, CacheUtils.CACHE_EXPIRE_TIME);
        return fieldPermission;
    }


}
