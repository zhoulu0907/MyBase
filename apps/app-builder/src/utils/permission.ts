import type { MenuInfo, UserInfo } from '@onebase/platform-center';
import {
  MODULE_MAP,
  MENU_MAP,
  ACTION_MAP,
  ALL_PERMISSION_CODE,
  type PermissionKey,
  type MenuKey
} from '../constants/permission';

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
    userPermissionInfo.permissionMap = Array.isArray(userPermissionInfo.permissions) ? 
      userPermissionInfo.permissions.reduce((acc: Record<string, boolean>, cur:string) => {
        acc[cur] = true;
        return acc;
      }, {})
      :
      {};
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
   * 将前端定义的权限标识转换为后端接口返回的权限标识
   * @param permission 如 'SYSTEM:USER:CREATE' 的键值(constants/permission中查询常量键名）
   * @returns 将permission自动映射为后端权限code: 'system:user:create'
   */
  static getPermissionCode = (permission: PermissionKey | MenuKey | string): string => {
    if (!permission) {
      return '';
    }
    
    const parts = permission.split(':');
    if (parts.length === 1) {
      return permission;
    }
    
    const [moduleName, menuName, actionName] = parts.slice(0, 3);
    
    const moduleCode = MODULE_MAP[moduleName as keyof typeof MODULE_MAP];
    const menuCode = MENU_MAP[menuName as keyof typeof MENU_MAP];
    const actionCode = actionName ? ACTION_MAP[actionName as keyof typeof ACTION_MAP] : undefined;
    
    if (!moduleCode || !menuCode || (actionName && !actionCode)) {
      return permission;
    }
    
    return actionName ? `${moduleCode}:${menuCode}:${actionCode}` : `${moduleCode}:${menuCode}`;
  };

  /**
   * 是否具有指定权限
   * @param permission 如 'SYSTEM:USER:CREATE' 的键值(constants/permission中查询常量键名）
   * @returns 是否具有指定权限
   */
  static hasPermission(permission: PermissionKey): boolean {
    const permissionCode = this.getPermissionCode(permission);
    const userPermissionInfo = this.getUserPermissionInfo();
    if (!userPermissionInfo || !userPermissionInfo.permissions) return false;

    // 拥有所有权限
    if (userPermissionInfo.permissionMap?.[ALL_PERMISSION_CODE]) return true;

    return !!userPermissionInfo.permissionMap?.[permissionCode];
  }

  /**
   * 是否具有多个指定权限中的任意一个
   * @param permission 如 'SYSTEM:USER:CREATE' 的键值
   * @returns 是否具有多个指定权限中的任意一个
   */
  static hasAnyPermission(permissions: PermissionKey[]): boolean {
    return permissions.some((permission) => this.hasPermission(permission));
  }

  /**
   * 是否具有所有指定权限
   * @param permission 如 'SYSTEM:USER:CREATE' 的键值
   * @returns 是否具有所有指定权限
   */
  static hasAllPermissions(permissions: PermissionKey[]): boolean {
    return permissions.every((permission) => this.hasPermission(permission));
  }

  /**
   * 是否具有指定菜单
   * @param permission 如 'SYSTEM:USER' 的键值(constants/permission中查询常量键名）
   * @returns 是否具有指定菜单
   */
  static hasMenu(menu: MenuKey): boolean {
    // TODO: 目前只解析到第二层
    const [moduleName, menuName] = menu?.split(':') as [keyof typeof MODULE_MAP, keyof typeof MENU_MAP];
    const moduleCode = MODULE_MAP[moduleName];
    const menuCode = MENU_MAP[menuName];
    const userPermissionInfo = this.getUserPermissionInfo();
    const moduleItem = userPermissionInfo?.menus.find((item) => item.path === `/${moduleCode}`);
    return moduleItem?.children?.some((item) => item.path === menuCode) || false;
  }
}

export const hasPermission = (permission: PermissionKey): boolean => {
  return UserPermissionManager.hasPermission(permission);
}
export const hasAnyPermission = (permissions: PermissionKey[]): boolean => {
  return UserPermissionManager.hasAnyPermission(permissions);
}
export const hasAllPermissions = (permissions: PermissionKey[]): boolean => {
  return UserPermissionManager.hasAllPermissions(permissions);
}
export const hasMenu = (menu: MenuKey): boolean => {
  return UserPermissionManager.hasMenu(menu);
}
