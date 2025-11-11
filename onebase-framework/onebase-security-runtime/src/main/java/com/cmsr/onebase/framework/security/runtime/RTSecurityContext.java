package com.cmsr.onebase.framework.security.runtime;

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
        //TODO 暂时不管
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof RTLoginUser) {
            RTLoginUser loginUser = (RTLoginUser) principal;
            return loginUser;
        } else {
            RTLoginUser rtLoginUser = new RTLoginUser();
            rtLoginUser.setUserId(1L);
            rtLoginUser.setApplicationId(1L);
            return rtLoginUser;
        }
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
        return RTPermissionService.getInstance().checkMenuEntity(applicationId, menuId, entityId);
    }

    public static OperationPermission getMenuOperation(Long menuId) {
        RTLoginUser loginUser = getLoginUser();
        Long userId = loginUser.getUserId();
        Long applicationId = loginUser.getApplicationId();
        return RTPermissionService.getInstance().getMenuOperation(userId, applicationId, menuId);
    }

    public static DataPermission getMenuDataPermission(Long menuId) {
        RTLoginUser loginUser = getLoginUser();
        Long userId = loginUser.getUserId();
        Long applicationId = loginUser.getApplicationId();
        return RTPermissionService.getInstance().getMenuDataPermission(userId, applicationId, menuId);
    }

    public static FieldPermission getMenuFieldPermission(Long menuId) {
        RTLoginUser loginUser = getLoginUser();
        Long userId = loginUser.getUserId();
        Long applicationId = loginUser.getApplicationId();
        return RTPermissionService.getInstance().getMenuFieldPermission(userId, applicationId, menuId);
    }

    public static void mockLoginUser(Long userId, Long applicationId) {
        RTLoginUser loginUser = new RTLoginUser();
        loginUser.setUserId(userId);
        loginUser.setApplicationId(applicationId);
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(loginUser, null, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

}
