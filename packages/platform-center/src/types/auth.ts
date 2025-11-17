// 获取验证码请求参数
/**
 * 登录请求参数
 */
export interface LoginRequest {
  /**
   * 账号
   */
  username: string;
  /**
   * 密码
   */
  password: string;
  /**
   * 验证码
   */
  captchaVerification?: string | null;
}

export interface TenantLoginRequest {
  /**
   * 账号
   */
  username: string;
  /**
   * 密码
   */
  password: string;
  /**
   * 验证码
   */
  captchaVerification?: string | null;
}

export interface LoginResponse {
  userId: string; // 用户ID
  accessToken: string; // 访问令牌
  refreshToken: string; // 刷新令牌
  expiresTime: number; // 令牌过期时间（时间戳，毫秒）
  tenantId: string; // 租户id
  tenantWebsite: string; // 租户网址
}

export interface TenantLoginResponse {
  userId: string; // 用户ID
  accessToken: string; // 访问令牌
  refreshToken: string; // 刷新令牌
  expiresTime: number; // 令牌过期时间（时间戳，毫秒）
  tenantId: string; // 租户id
  tenantWebsite: string; // 租户网址
  corpId: string; // 企业id
}

/**
 * 当前登录用户的权限信息响应
 */
export interface GetPermissionInfoResponse {
  user: UserInfo; // 用户信息
  roles: string[]; // 角色标识数组
  permissions: string[]; // 权限标识数组
  menus: MenuInfo[]; // 菜单信息数组
}

export interface UserInfo {
  id: number; // 用户ID
  nickname: string; // 昵称
  avatar: string; // 头像URL
  deptId: number; // 部门ID
  username: string; // 用户名
  email: string; // 邮箱
}

/**
 * 菜单信息结构
 */
export interface MenuInfo {
  id: number;
  parentId: number;
  name: string;
  path: string;
  component: string | null;
  componentName: string | null;
  icon: string;
  visible: boolean;
  keepAlive: boolean;
  alwaysShow: boolean;
  permission?: string;
  children: MenuInfo[] | null;
}

/**
 * 登录的租户id
 */
export type Headers = Record<'Tenant-Id', string>;
