import type { MenuInfo, UserInfo } from "@onebase/platform-center";

export interface UserPermissionInfo {
  user: UserInfo; // 用户信息
  roles: string[]; // 角色标识数组
  permissions: string[]; // 权限标识数组
  menus: MenuInfo[]; // 菜单信息数组
}

export class UserPermissionManager {
  private static readonly USER_PERMISSION_INFO_KEY =
    "onebase_user_permission_info";

  static setUserPermissionInfo(userPermissionInfo: UserPermissionInfo): void {
    localStorage.setItem(
      this.USER_PERMISSION_INFO_KEY,
      JSON.stringify(userPermissionInfo),
    );
  }

  static getUserPermissionInfo(): UserPermissionInfo | null {
    const userPermissionInfo = localStorage.getItem(
      this.USER_PERMISSION_INFO_KEY,
    );
    return userPermissionInfo ? JSON.parse(userPermissionInfo) : null;
  }

  static clearUserPermissionInfo(): void {
    localStorage.removeItem(this.USER_PERMISSION_INFO_KEY);
  }
}
