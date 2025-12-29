import type { MenuInfo, UserInfo } from '@onebase/common';

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

  /**
   * 设备id
   */
  deviceId: string;
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
  /**
   * 设备id
   */
  deviceId: string;
}

export interface RuntimeAccountLoginRequest {
  /**
   * 应用id
   */
  appId: string;

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
  /**
   * 设备id
   */
  deviceId: string;
}

export interface RuntimeCorpLoginRequest {
  /**
   * 手机号
   */
  mobile: string;
  /**
   * 密码
   */
  password: string;
  /**
   * 验证码
   */
  captchaVerification?: string | null;

  /**
   * 设备id
   */
  deviceId: string;
}

export interface RuntimeThirdLoginRequest {
    /**
     * 应用ID
     */
    appId: string;
    /**
     * ========== 图片验证码相关 ==========
     * 验证码，验证码开启时，需要传递
     */
    captchaVerification?: string;
    /**
     * ========== 设备标识相关 ==========
     * 设备ID，用于多设备管理和限制
     */
    deviceId: string;
    /**
     * 密码/验证码
     */
    loginType: string;
    /**
     * 手机号
     */
    mobile: string;
    /**
     * 密码
     */
    password?: string;
    /**
     * 验证码
     */
    verifyCode?: string;
}

export interface RuntimeMobileLoginRequest {
  /**
   * 应用id
   */
  appId: string;

  /**
   * 手机号
   */
  mobile: string;
  /**
   * 密码
   */
  password: string;
  /**
   * 验证码
   */
  captchaVerification?: string | null;

  /**
   * 设备id
   */
  deviceId: string;
}

export interface LoginResponse {
  userId: string; // 用户ID
  accessToken: string; // 访问令牌
  refreshToken: string; // 刷新令牌
  expiresTime: number; // 令牌过期时间（时间戳，毫秒）
  tenantId: string; // 租户id
  tenantWebsite: string; // 租户网址
  corpId: string; // 企业id
  loginSource: string //来源
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

export interface ThirdUserLoginResponse {
  userId: string; // 用户ID
  accessToken: string; // 访问令牌
  refreshToken: string; // 刷新令牌
  expiresTime: number; // 令牌过期时间（时间戳，毫秒）
  tenantId: string; // 租户id
  tenantWebsite: string; // 租户网址
  corpId?: string; // 企业id
  loginSource: string //来源
  userUnRegistFlag: boolean;
  userAppRelationFlag?: boolean; //是否关联应用
  email?: string;
  nickName?: string;
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

export interface SendVerifyCodeRequest {
  /**
   * 用户名
   */
  userName?: string;
  /**
   * 用户手机号
   */
  userMobile?: string;

  sendType: string;
}

/**
 * 登录的租户id
 */
export type Headers = Record<'X-Tenant-Id', string>;
