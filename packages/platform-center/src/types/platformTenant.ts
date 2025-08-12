export enum PlatformTenantStatus {
  /**
   * 已启用
   */
  enabled = 0,
  /**
   * 已禁用
   */
  disabled = 1,
  /**
   * 全部
   */
  all = 2,

}

// 管理员根用户ID
export const ADMIN_ROOT_ID = 1;

/**
 * 平台租户信息
 */
export interface PlatformTenantInfo {
  id: string | number,
  /**
   * 租户名称
   */
  name: string,
  /**
   * 管理员名称
   */
  contactName: string,
  /**
   * 租户编码
   */
  tenantCode: string,
  /**
   * 管理员手机号
   */
  contactMobile: string,
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
   * 网站
   */
  website?: string,
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
   * 管理员名称
   */
  contactName: string;
  /**
   * 租户状态
   */
  status: 0 | 1;
  /**
   * 分配数量
   */
  accountCount: number;
}