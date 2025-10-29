package com.cmsr.onebase.framework.security.runtime.service;

import com.cmsr.onebase.module.app.api.security.AppAuthSecurityApi;
import com.cmsr.onebase.module.app.api.security.bo.DataPermission;
import com.cmsr.onebase.module.app.api.security.bo.FieldPermission;
import com.cmsr.onebase.module.app.api.security.bo.MenuPermission;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    private AppAuthSecurityApi appAuthSecurityApi;

    public boolean checkMenuEntity(Long applicationId, Long menuId, Long entityId) {
        return appAuthSecurityApi.checkMenuEntity(applicationId, menuId, entityId);
    }

    public MenuPermission getMenuOperation(Long userId, Long applicationId, Long menuId) {
        return appAuthSecurityApi.getMenuPermission(userId, applicationId, menuId);
    }

    public DataPermission getMenuDataPermission(Long userId, Long applicationId, Long menuId) {
        return appAuthSecurityApi.getMenuDataPermission(userId, applicationId, menuId);
    }


    public FieldPermission getMenuFieldPermission(Long userId, Long applicationId, Long menuId) {
        return appAuthSecurityApi.getMenuFieldPermission(userId, applicationId, menuId);
    }


}
