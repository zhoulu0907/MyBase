package com.cmsr.onebase.framework.security.runtime.service;

import com.cmsr.onebase.module.app.api.security.AppAuthSecurityApi;
import com.cmsr.onebase.module.app.api.security.bo.DataPermission;
import com.cmsr.onebase.module.app.api.security.bo.FieldPermission;
import com.cmsr.onebase.module.app.api.security.bo.OperationPermission;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

/**
 * 实时权限服务类，提供权限检查和获取功能
 *
 * @Author：huangjie
 * @Date：2025/10/24 18:21
 */
@Setter
@Slf4j
@Service
public class RTPermissionService implements ApplicationContextAware {

    private static volatile RTPermissionService INSTANCE;

    @Autowired
    private AppAuthSecurityApi appAuthSecurityApi;

    /**
     * 获取RTPermissionService的Spring Bean实例
     *
     * @return RTPermissionService实例
     */
    public static RTPermissionService getInstance() {
        if (INSTANCE == null) {
            throw new IllegalStateException("RTPermissionService not initialized yet");
        }
        return INSTANCE;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        synchronized (RTPermissionService.class) {
            if (INSTANCE == null) {
                INSTANCE = this;
                log.debug("RTPermissionService instance has been initialized");
            }
        }
    }

    public boolean checkMenuEntity(Long applicationId, Long menuId, Long entityId) {
        return appAuthSecurityApi.checkMenuEntity(applicationId, menuId, entityId);
    }

    public OperationPermission getMenuOperation(Long userId, Long applicationId, Long menuId) {
        return appAuthSecurityApi.getMenuOperationPermission(userId, applicationId, menuId);
    }

    public DataPermission getMenuDataPermission(Long userId, Long applicationId, Long menuId) {
        return appAuthSecurityApi.getMenuDataPermission(userId, applicationId, menuId);
    }

    public FieldPermission getMenuFieldPermission(Long userId, Long applicationId, Long menuId) {
        return appAuthSecurityApi.getMenuFieldPermission(userId, applicationId, menuId);
    }

}
