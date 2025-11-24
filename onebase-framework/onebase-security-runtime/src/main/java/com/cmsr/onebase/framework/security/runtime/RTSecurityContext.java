package com.cmsr.onebase.framework.security.runtime;

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
 * @Author：huangjie
 * @Date：2025/10/17 12:30
 */
public class RuntimeSecurityContext {

    public static RuntimeLoginUser getLoginUser() {
        return SecurityFrameworkUtils.getLoginUser();
    }

    public static Long getApplicationId() {
        return getLoginUser().getApplicationId();
    }

    public static Long getUserId() {
        return getLoginUser().getId();
    }

    public static boolean checkMenuEntity(Long menuId, Long entityId) {
        RuntimeLoginUser RuntimeLoginUser = getLoginUser();
        Long applicationId = RuntimeLoginUser.getApplicationId();
        return RTPermissionService.getInstance().checkMenuEntity(applicationId, menuId, entityId);
    }

    public static OperationPermission getMenuOperation(Long menuId) {
        RuntimeLoginUser RuntimeLoginUser = getLoginUser();
        Long userId = RuntimeLoginUser.getId();
        Long applicationId = RuntimeLoginUser.getApplicationId();
        return RTPermissionService.getInstance().getMenuOperation(userId, applicationId, menuId);
    }

    public static DataPermission getMenuDataPermission(Long menuId) {
        RuntimeLoginUser RuntimeLoginUser = getLoginUser();
        Long userId = RuntimeLoginUser.getId();
        Long applicationId = RuntimeLoginUser.getApplicationId();
        return RTPermissionService.getInstance().getMenuDataPermission(userId, applicationId, menuId);
    }

    public static FieldPermission getMenuFieldPermission(Long menuId) {
        RuntimeLoginUser RuntimeLoginUser = getLoginUser();
        Long userId = RuntimeLoginUser.getId();
        Long applicationId = RuntimeLoginUser.getApplicationId();
        return RTPermissionService.getInstance().getMenuFieldPermission(userId, applicationId, menuId);
    }

    public static void mockLoginUser(Long userId, Long applicationId) {
        RuntimeLoginUser RuntimeLoginUser = new RuntimeLoginUser();
        RuntimeLoginUser.setId(userId);
        RuntimeLoginUser.setApplicationId(applicationId);
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(RuntimeLoginUser, null, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

}
