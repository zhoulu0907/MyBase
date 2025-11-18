export enum PlatformTenantStatus {
  /**
   * 已启用
   */
  disabled = 0,
  /**
   * 已禁用
   */
  enabled
}

export enum PlatformTenantSort {
  asc = 'asc',
  desc = 'desc'
}

export enum PlatformTenantPublishMode {
  saas = 'saas',
  inner = 'inner'
}

// 管理员根用户ID
export const ADMIN_ROOT_ID = '1';

/**
 * 平台租户信息
 */
export interface PlatformTenantInfo {
  /**
   * 租户编号
   */
  id?: string;
  /**
   * 访问地址
   */
  accessUrl?: string;
  /**
   * 账号数量
   */
  accountCount?: number;
  /**
   * 应用数量
   */
  appCount?: number;
  /**
   * 企业数
   */
  corpCount?: number;
  /**
   * 创建时间
   */
  createTime?: string;
  /**
   * 已存在用户数量
   */
  existUserCount?: number;
  /**
   * 过期时间
   */
  expireTime?: string;
  /**
   * 用户logo
   */
  logoUrl?: string;
  /**
   * 租户名
   */
  name?: string;
  /**
   * 租户套餐编号
   */
  packageId?: number;
  /**
   * saas功能是否开启
   */
  publishModel?: PlatformTenantPublishMode;
  /**
   * 租户状态
   */
  status?: PlatformTenantStatus;
  /**
   * 管理员集合
   */
  tenantAdminUserList?: TenantAdminUserResVO[];
  /**
   * 租户编码
   */
  tenantCode?: string;
  /**
   * key
   */
  tenantKey?: string;
  /**
   * secret
   */
  tenantSecret?: string;
  /**
   * 域名
   */
  website?: string;
  /**
   * 域名H5
   */
  websiteH5?: string;
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
   * 域名
   */
  website: string;
  /**
   * 分配数量
   */
  accountCount: number;
  /**
   * 访问地址
   */
  accessUrl?: string;
  /**
   * 用户logo
   */
  logoUrl?: string;
  /**
   * saas功能是否开启
   */
  publishModel?: PlatformTenantPublishMode;
  /**
   * 管理员
   */
  tenantAdminUserReqVOList?: TenantAdminUserResVO[];
  tenantAdminUserUpdateReqVOSList?: TenantAdminUserResVO[];
}

/**
 * TenantAdminUserResVO
 */
export interface TenantAdminUserResVO {
  /**
   * 管理员手机
   */
  adminMobile?: string;
  /**
   * 管理员昵称
   */
  adminNickName?: string;
  /**
   * 空间管理员id
   */
  adminUserId?: number;

  /**
   * 平台管理员ID
   */
  platformUserId?: number;
  /**
   * 管理员账号
   */
  adminUserName?: string;
}

export interface UpdateTenantParams extends CreateTenantParams {
  /**
   * 租户ID
   */
  id: string;
}
