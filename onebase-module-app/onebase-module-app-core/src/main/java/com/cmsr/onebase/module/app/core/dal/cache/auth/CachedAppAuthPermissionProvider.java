package com.cmsr.onebase.module.app.core.dal.cache.auth;

import com.cmsr.onebase.module.app.core.dal.database.auth.AppAuthPermissionRepository;
import com.cmsr.onebase.module.app.core.dal.database.menu.AppMenuRepository;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * @Author：huangjie
 * @Date：2025/10/28 13:20
 */
@Slf4j
@Setter
@Service
public class CachedAppAuthPermissionProvider {

    @Autowired
    private AppMenuRepository appMenuRepository;

    @Autowired
    private AppAuthPermissionRepository appAuthPermissionRepository;

    public Set<Long> findAccessibleMenuIds(Long applicationId, Long roleId) {
        return null;
    }
}
