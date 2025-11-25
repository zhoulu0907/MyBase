package com.cmsr.onebase.module.app.core.provider.auth;

import com.cmsr.onebase.module.app.core.dal.database.AppAuthPermissionRepository;
import com.cmsr.onebase.module.app.core.dal.dataobject.AuthPermissionDO;
import com.cmsr.onebase.module.app.core.enums.auth.AuthDefaultFactory;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public List<AuthPermissionDO> findPermissions(Long applicationId, Set<Long> roleIds) {
        List<AuthPermissionDO> permissionDOS = appAuthPermissionRepository.findByAppIdAndRoleIds(applicationId, roleIds);
        return permissionDOS.stream().map(permissionDO -> {
            if (permissionDO.getId() == null) {
                return AuthDefaultFactory.createAuthPermissionDO();
            } else {
                return permissionDO;
            }
        }).toList();
    }

    public List<AuthPermissionDO> findPermissions(Long applicationId, Set<Long> roleIds, Long menuId) {
        List<AuthPermissionDO> permissionDOS = appAuthPermissionRepository.findByAppIdAndRoleIdsAndMenuId(applicationId, roleIds, menuId);
        return permissionDOS.stream().map(permissionDO -> {
            if (permissionDO.getId() == null) {
                return AuthDefaultFactory.createAuthPermissionDO();
            } else {
                return permissionDO;
            }
        }).toList();
    }


}
