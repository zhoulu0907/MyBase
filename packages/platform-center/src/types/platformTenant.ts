export enum PlatformTenantStatus {
  /**
   * 已启用
   */
  disabled = 0,
  /**
   * 已禁用
   */
  enabled,
}

// 管理员根用户ID
export const ADMIN_ROOT_ID = '1';

/**
 * 平台租户信息
 */
export interface PlatformTenantInfo {
  id: string,
  /**
   * 租户名称
   */
  name: string,
  /**
   * 管理员名称
   */
  adminUserName: string,
  /**
   * 管理员昵称
   */
  adminNickName?: string,
  /**
   * 租户编码
   */
  tenantCode: string,
  /**
   * 管理员手机号
   */
  adminMobile: string,
  /**
   * 创建时间
   */
  createTime: string,
  /**
   * 分配人员数量
   */
  accountCount: number,
  /**
   * 租户状态
   */
  status: PlatformTenantStatus,
  /**
   * 过期时间
   */
  expireTime?: string,
  /**
   * 套餐ID
   */
  packageId?: number,
  /**
   * 访问地址
   */
  accessUrl: string;
  /**
   * 用户logo
   */
  logoUrl?: string;
  /**
   * saas功能是否开启默认0，开启1
   */
  saasEnabled?: PlatformTenantStatus;
  /**
   * 管理员id
   */
  adminUserId?: string[];
}

export interface CreateTenantParams {
  /**
   * 租户名称
   */
  name: string;
  /**
   * 租户编码
   */
  tenantCode: string;
  /**
   * 租户状态
   */
  status: PlatformTenantStatus;
  /**
   * 分配数量
   */
  accountCount: number;
  /**
   * 访问地址
   */
  accessUrl: string;
  /**
   * 用户logo
   */
  logoUrl?: string;
  /**
   * saas功能是否开启默认0，开启1
   */
  saasEnabled?: PlatformTenantStatus;
  /**
   * 管理员id
   */
  adminUserId?: AdminUsers[];
}

interface AdminUsers {
  /**
   * 管理员名称
   */
  adminUserName?: string;
  /**
   * 管理员昵称
   */
  adminNickName?: string;
  /**
   * 管理员电话
   */
  adminMobile?: string;
}

export interface UpdateTenantParams extends CreateTenantParams {
  /**
   * 租户ID
   */
  id: string;
}