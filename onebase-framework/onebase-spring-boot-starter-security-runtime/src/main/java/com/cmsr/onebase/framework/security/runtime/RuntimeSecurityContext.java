package com.cmsr.onebase.framework.security.runtime;

import java.util.List;
import java.util.Set;

/**
 * @Author：huangjie
 * @Date：2025/10/17 12:30
 */
public class RuntimeSecurityContext {

    public static RuntimeLoginUser getLoginUser() {
        return null;
    }

    public static Long getApplicationId() {
        return null;
    }

    public static boolean hasApplicationPermission() {
        return true;
    }

    public static Set<Long> getAccessibleMenus() {
        return null;
    }

    public static Set<Long> getAccessibleEntities() {
        return null;
    }

    public static Set<OperationEnum> getPagePermissions(Long pageId) {
        return null;
    }

    public static List<DataPermission> getPageDataPermissions(Long pageId) {
        return null;
    }

    public static List<FieldPermission> getPageFieldPermissions(Long pageId) {
        return null;
    }
}
