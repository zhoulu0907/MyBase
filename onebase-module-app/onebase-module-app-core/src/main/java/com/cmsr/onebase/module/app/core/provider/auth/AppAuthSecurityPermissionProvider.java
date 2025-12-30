package com.cmsr.onebase.module.app.core.provider.auth;

import com.cmsr.onebase.framework.common.security.ApplicationManager;
import com.cmsr.onebase.module.app.core.dal.database.auth.AppAuthPermissionRepository;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppAuthPermissionDO;
import com.cmsr.onebase.module.app.core.enums.auth.AuthDefaultFactory;
import com.mybatisflex.core.tenant.TenantManager;
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
public class AppAuthSecurityPermissionProvider {

    @Autowired
    private AppAuthPermissionRepository appAuthPermissionRepository;

    public List<AppAuthPermissionDO> findPermissions(Long applicationId, Set<String> roleUuids) {
        if (CollectionUtils.isEmpty(roleUuids)) {
            return Collections.emptyList();
        }
        List<AppAuthPermissionDO> permissionDOS = TenantManager.withoutTenantCondition(() -> ApplicationManager.withoutApplicationCondition(() ->
                appAuthPermissionRepository.findByAppIdAndRoleIds(applicationId, roleUuids)
        ));
        if (CollectionUtils.isEmpty(permissionDOS)) {
            return List.of(AuthDefaultFactory.createDefaultAuthPermissionDO());
        }
        return permissionDOS;
    }

    public List<AppAuthPermissionDO> findPermissions(Long applicationId, Set<String> roleUuids, String menuUuid) {
        if (CollectionUtils.isEmpty(roleUuids)) {
            return Collections.emptyList();
        }
        List<AppAuthPermissionDO> permissionDOS = TenantManager.withoutTenantCondition(() -> ApplicationManager.withoutApplicationCondition(() ->
                appAuthPermissionRepository.findByAppIdAndRoleIdsAndMenuId(applicationId, roleUuids, menuUuid)
        ));
        if (CollectionUtils.isNotEmpty(permissionDOS)) {
            return permissionDOS;
        }
        return List.of(AuthDefaultFactory.createDefaultAuthPermissionDO());
    }


}
