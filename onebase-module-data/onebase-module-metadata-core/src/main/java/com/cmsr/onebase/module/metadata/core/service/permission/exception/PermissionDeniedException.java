package com.cmsr.onebase.module.metadata.core.service.permission.exception;

/**
 * 权限拒绝异常
 * 
 * 当用户没有权限执行某个操作时抛出此异常
 *
 * @author zhangxihui
 * @date 2025-10-31
 */
public class PermissionDeniedException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * 权限类型（操作权限、数据权限、字段权限）
     */
    private final String permissionType;

    /**
     * 被拒绝的具体权限标识
     */
    private final String deniedPermission;

    public PermissionDeniedException(String permissionType, String deniedPermission, String message) {
        super(message);
        this.permissionType = permissionType;
        this.deniedPermission = deniedPermission;
    }

    public PermissionDeniedException(String permissionType, String deniedPermission, String message, Throwable cause) {
        super(message, cause);
        this.permissionType = permissionType;
        this.deniedPermission = deniedPermission;
    }

    public String getPermissionType() {
        return permissionType;
    }

    public String getDeniedPermission() {
        return deniedPermission;
    }
}


