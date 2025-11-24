package com.cmsr.onebase.framework.security.runtime;

import com.cmsr.onebase.framework.common.security.SecurityFrameworkUtils;
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
public class RTSecurityContext {

    public static RTLoginUser getLoginUser() {
        return SecurityFrameworkUtils.getLoginUser();
    }

    public static Long getApplicationId() {
        return getLoginUser().getApplicationId();
    }

    public static Long getUserId() {
        return getLoginUser().getId();
    }

    public static boolean checkMenuEntity(Long menuId, Long entityId) {
        RTLoginUser RTLoginUser = getLoginUser();
        Long applicationId = RTLoginUser.getApplicationId();
        return RTPermissionService.getInstance().checkMenuEntity(applicationId, menuId, entityId);
    }

    public static OperationPermission getMenuOperation(Long menuId) {
        RTLoginUser RTLoginUser = getLoginUser();
        Long userId = RTLoginUser.getId();
        Long applicationId = RTLoginUser.getApplicationId();
        return RTPermissionService.getInstance().getMenuOperation(userId, applicationId, menuId);
    }

    public static DataPermission getMenuDataPermission(Long menuId) {
        RTLoginUser RTLoginUser = getLoginUser();
        Long userId = RTLoginUser.getId();
        Long applicationId = RTLoginUser.getApplicationId();
        return RTPermissionService.getInstance().getMenuDataPermission(userId, applicationId, menuId);
    }

    public static FieldPermission getMenuFieldPermission(Long menuId) {
        RTLoginUser RTLoginUser = getLoginUser();
        Long userId = RTLoginUser.getId();
        Long applicationId = RTLoginUser.getApplicationId();
        return RTPermissionService.getInstance().getMenuFieldPermission(userId, applicationId, menuId);
    }

    public static void mockLoginUser(Long userId, Long applicationId) {
        RTLoginUser RTLoginUser = new RTLoginUser();
        RTLoginUser.setId(userId);
        RTLoginUser.setApplicationId(applicationId);
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(RTLoginUser, null, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

}
