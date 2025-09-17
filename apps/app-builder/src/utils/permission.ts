import type { MenuInfo, UserInfo } from '@onebase/platform-center';
import { ALL_PERMISSION_CODE } from '../constants/permission';

export interface UserPermissionInfo {
  user: UserInfo; // 用户信息
  roles: string[]; // 角色标识数组
  permissions: string[]; // 权限标识数组
  menus: MenuInfo[]; // 菜单信息数组
  permissionMap?: Record<string, boolean>; // 权限标识map
}

export class UserPermissionManager {
  private static readonly USER_PERMISSION_INFO_KEY = 'onebase_user_permission_info';

  static setUserPermissionInfo(userPermissionInfo: UserPermissionInfo): void {
    // permissions存为map，提高查询效率
    userPermissionInfo.permissionMap = Array.isArray(userPermissionInfo.permissions)
      ? userPermissionInfo.permissions.reduce((acc: Record<string, boolean>, cur: string) => {
          acc[cur] = true;
          return acc;
        }, {})
      : {};
    Reflect.deleteProperty(userPermissionInfo.permissionMap, '');
    localStorage.setItem(this.USER_PERMISSION_INFO_KEY, JSON.stringify(userPermissionInfo));
  }

  static getUserPermissionInfo(): UserPermissionInfo | null {
    const userPermissionInfo = localStorage.getItem(this.USER_PERMISSION_INFO_KEY);
    return userPermissionInfo ? JSON.parse(userPermissionInfo) : null;
  }

  static clearUserPermissionInfo(): void {
    localStorage.removeItem(this.USER_PERMISSION_INFO_KEY);
  }

  /**
   * 是否具有指定权限
   * @param permission 后端定义的权限code (可从constants/permission中引入）
   * @returns 是否具有指定权限
   */
  static hasPermission(permission: string): boolean {
    const userPermissionInfo = this.getUserPermissionInfo();
    if (!userPermissionInfo || !userPermissionInfo.permissions) return false;

    // 拥有所有权限
    if (userPermissionInfo.permissionMap?.[ALL_PERMISSION_CODE]) return true;

    return !!userPermissionInfo.permissionMap?.[permission];
  }

  /**
   * 是否具有多个指定权限中的任意一个
   * @param permission 权限code, 如'systme:user:create'
   * @returns 是否具有多个指定权限中的任意一个
   */
  static hasAnyPermission(permissions: string[]): boolean {
    return permissions.some((permission) => this.hasPermission(permission));
  }

  /**
   * 是否具有所有指定权限
   * @param permission 权限code, 如'systme:user:create'
   * @returns 是否具有所有指定权限
   */
  static hasAllPermissions(permissions: string[]): boolean {
    return permissions.every((permission) => this.hasPermission(permission));
  }

  /**
   * 是否具有指定菜单
   * @param permission 如 'system:user'
   * @returns 是否具有指定菜单
   */
  static hasMenu(menu: string): boolean {
    // TODO: 目前只解析到第二层
    const [moduleCode] = menu?.split(':');
    const userPermissionInfo = this.getUserPermissionInfo();
    const moduleItem = userPermissionInfo?.menus.find((item) => item.permission === `${moduleCode}`);
    return moduleItem?.children?.some((item) => item.permission === menu) || false;
  }
}

export const hasPermission = (permission: string): boolean => {
  return UserPermissionManager.hasPermission(permission);
};
export const hasAnyPermission = (permissions: string[]): boolean => {
  return UserPermissionManager.hasAnyPermission(permissions);
};
export const hasAllPermissions = (permissions: string[]): boolean => {
  return UserPermissionManager.hasAllPermissions(permissions);
};
export const hasMenu = (menu: string): boolean => {
  return UserPermissionManager.hasMenu(menu);
};
