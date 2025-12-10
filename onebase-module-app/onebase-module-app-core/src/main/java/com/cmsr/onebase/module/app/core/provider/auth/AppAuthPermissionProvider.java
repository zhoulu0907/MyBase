package com.cmsr.onebase.module.app.core.provider.auth;

import com.cmsr.onebase.module.app.core.dal.database.auth.AppAuthPermissionRepository;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppAuthPermissionDO;
import com.cmsr.onebase.module.app.core.enums.auth.AuthDefaultFactory;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * @Author：huangjie
 * @Date：2025/10/28 13:20
 */
@Slf4j
@Setter
@Service
public class AppAuthPermissionProvider {

    @Autowired
    private AppAuthPermissionRepository appAuthPermissionRepository;

    public List<AppAuthPermissionDO> findPermissions(Long applicationId, Set<Long> roleIds) {
        if (CollectionUtils.isEmpty(roleIds)) {
            return Collections.emptyList();
        }
        List<AppAuthPermissionDO> permissionDOS = appAuthPermissionRepository.findByAppIdAndRoleIds(applicationId, roleIds);
        return permissionDOS.stream().map(permissionDO -> {
            if (permissionDO == null || permissionDO.getId() == null) {
                return AuthDefaultFactory.createDefaultAuthPermissionDO();
            } else {
                return permissionDO;
            }
        }).toList();
    }

    public List<AppAuthPermissionDO> findPermissions(Long applicationId, Set<Long> roleIds, Long menuId) {
        if (CollectionUtils.isEmpty(roleIds)) {
            return Collections.emptyList();
        }
        List<AppAuthPermissionDO> permissionDOS = appAuthPermissionRepository.findByAppIdAndRoleIdsAndMenuId(applicationId, roleIds, menuId);
        return permissionDOS.stream().map(permissionDO -> {
            if (permissionDO == null || permissionDO.getId() == null) {
                return AuthDefaultFactory.createDefaultAuthPermissionDO();
            } else {
                return permissionDO;
            }
        }).toList();
    }


}
