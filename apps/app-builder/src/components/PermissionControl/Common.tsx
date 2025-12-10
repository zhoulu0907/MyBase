import { userPermissionSignal } from '@/store/singals/user_permission';
import { UserPermissionManager } from '@onebase/common';
import { useSignals } from '@preact/signals-react/runtime';
import React, { useEffect } from 'react';

export interface PermissionControlProps {
  permission?: string; // 单个指定权限名称
  anyPermissions?: string[]; // 多个指定权限具有其中任意一个
  allPermissions?: string[]; // 多个指定权限具有所有权限
  fallback?: React.ReactNode; // 无权限时显示的内容
  children: React.ReactNode;
}

/**
 * 权限控制组件
 * 根据用户权限控制子元素的显示或隐藏
 */
const PermissionControl: React.FC<PermissionControlProps> = ({
  permission,
  anyPermissions,
  allPermissions,
  fallback = null,
  children
}) => {
  // 启用 signal 的响应式更新
  useSignals();

  // 直接访问 signal 的值，useSignals() 会确保组件在 signal 变化时重新渲染
  const permissionInfo = userPermissionSignal.permissionInfo.value;

  useEffect(() => {
    if (permissionInfo) {
      UserPermissionManager.setUserPermissionInfo(permissionInfo);
    }
  }, [permissionInfo]);

  const checkResult = React.useMemo(() => {
    if (!permission && !anyPermissions && !allPermissions) {
      return true;
    }

    if (permission) {
      return UserPermissionManager.hasPermission(permission);
    }

    if (anyPermissions && anyPermissions.length > 0) {
      return UserPermissionManager.hasAnyPermission(anyPermissions);
    }

    if (allPermissions && allPermissions.length > 0) {
      return UserPermissionManager.hasAllPermissions(allPermissions);
    }

    return false;
  }, [permission, anyPermissions, allPermissions, UserPermissionManager.getUserPermissionInfo()]);

  return <>{checkResult ? children : fallback}</>;
};

export default PermissionControl;
