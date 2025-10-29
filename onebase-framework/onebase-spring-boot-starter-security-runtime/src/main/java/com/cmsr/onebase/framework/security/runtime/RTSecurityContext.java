package com.cmsr.onebase.framework.security.runtime;

import com.cmsr.onebase.framework.security.runtime.service.RTPermissionService;
import com.cmsr.onebase.module.app.api.security.bo.DataPermission;
import com.cmsr.onebase.module.app.api.security.bo.FieldPermission;
import com.cmsr.onebase.module.app.api.security.bo.MenuPermission;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * @Author：huangjie
 * @Date：2025/10/17 12:30
 */
public class RTSecurityContext {

    public static RTLoginUser getLoginUser() {
        RTLoginUser loginUser = (RTLoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return loginUser;
    }

    public static Long getApplicationId() {
        return getLoginUser().getApplicationId();
    }

    public static Long getUserId() {
        return getLoginUser().getUserId();
    }

    public static boolean checkMenuEntity(Long menuId, Long entityId) {
        RTLoginUser loginUser = getLoginUser();
        Long applicationId = loginUser.getApplicationId();
        return RTPermissionService.instance.checkMenuEntity(applicationId, menuId, entityId);
    }

    public static MenuPermission getMenuOperation(Long menuId) {
        RTLoginUser loginUser = getLoginUser();
        Long userId = loginUser.getUserId();
        Long applicationId = loginUser.getApplicationId();
        return RTPermissionService.instance.getMenuOperation(userId, applicationId, menuId);
    }

    public static DataPermission getMenuDataPermission(Long menuId) {
        RTLoginUser loginUser = getLoginUser();
        Long userId = loginUser.getUserId();
        Long applicationId = loginUser.getApplicationId();
        return RTPermissionService.instance.getMenuDataPermission(userId, applicationId, menuId);
    }

    public static FieldPermission getMenuFieldPermission(Long menuId) {
        RTLoginUser loginUser = getLoginUser();
        Long userId = loginUser.getUserId();
        Long applicationId = loginUser.getApplicationId();
        return RTPermissionService.instance.getMenuFieldPermission(userId, applicationId, menuId);
    }

}
