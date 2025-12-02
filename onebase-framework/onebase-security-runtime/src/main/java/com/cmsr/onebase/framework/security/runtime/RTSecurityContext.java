package com.cmsr.onebase.framework.security.runtime;

import com.cmsr.onebase.framework.common.security.ApplicationManager;
import com.cmsr.onebase.framework.common.security.SecurityFrameworkUtils;
import com.cmsr.onebase.framework.common.security.dto.RuntimeLoginUser;
import com.cmsr.onebase.framework.security.runtime.service.RTPermissionService;
import com.cmsr.onebase.module.app.api.security.bo.DataPermission;
import com.cmsr.onebase.module.app.api.security.bo.FieldPermission;
import com.cmsr.onebase.module.app.api.security.bo.OperationPermission;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;

/**
 * 为什么给数据模块要用菜单id作为条件查询？
 * 因为一个实体可以配置多个菜单，权限的配置是以菜单来的。用户在不同的菜单可有不同的权限。
 *
 * @Author：huangjie
 * @Date：2025/10/17 12:30
 */
public class RTSecurityContext {

    public static RuntimeLoginUser getLoginUser() {
        return SecurityFrameworkUtils.getLoginUser();
    }

    public static Long getUserId() {
        return getLoginUser() == null ? null : getLoginUser().getId();
    }

    public static boolean checkMenuEntity(Long menuId, String entityUuid) {
        Long applicationId = ApplicationManager.getApplicationId();
        return RTPermissionService.getInstance().checkMenuEntity(applicationId, menuId, entityUuid);
    }

    public static OperationPermission getMenuOperation(Long menuId) {
        RuntimeLoginUser runtimeLoginUser = getLoginUser();
        Long userId = runtimeLoginUser.getId();
        Long applicationId = ApplicationManager.getApplicationId();
        return RTPermissionService.getInstance().getMenuOperation(userId, applicationId, menuId);
    }

    public static DataPermission getMenuDataPermission(Long menuId) {
        RuntimeLoginUser runtimeLoginUser = getLoginUser();
        Long userId = runtimeLoginUser.getId();
        Long applicationId = ApplicationManager.getApplicationId();
        return RTPermissionService.getInstance().getMenuDataPermission(userId, applicationId, menuId);
    }

    public static FieldPermission getMenuFieldPermission(Long menuId) {
        RuntimeLoginUser runtimeLoginUser = getLoginUser();
        Long userId = runtimeLoginUser.getId();
        Long applicationId = ApplicationManager.getApplicationId();
        return RTPermissionService.getInstance().getMenuFieldPermission(userId, applicationId, menuId);
    }

    public static void mockLoginUser(Long userId, Long applicationId) {
        RuntimeLoginUser runtimeLoginUser = new RuntimeLoginUser();
        runtimeLoginUser.setId(userId);
        runtimeLoginUser.setApplicationId(applicationId);
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(runtimeLoginUser, null, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

}
