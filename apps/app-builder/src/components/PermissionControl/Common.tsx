import React from 'react';
import { UserPermissionManager } from '@/utils/permission';

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
  }, [permission, anyPermissions, allPermissions]);
  
  return <>{checkResult ? children : fallback}</>;
};

export default PermissionControl;
