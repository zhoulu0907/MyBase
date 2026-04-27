export interface PlatformInfoReq {
  /**
   * 页码
   */
  pageNo: number;
  /**
   * 每页数量
   */
  pageSize: number;
}
/**
 * 认证记录
 */
export interface AuthRecord {
  id: number;
  /**
   * 公司名称
   */
  enterpriseName: string;
  /**
   * 认证记录
   */
  record: string;
  /**
   * 认证状态
   */
  status: number;
  /**
   * 到期时间
   */
  expireTime: string;
}

/**
 * 平台信息
 */
export interface LicenseInfo {
    /**
     * 企业名称
     */
    enterpriseName: string;
     /**
     * 公司编码
     */
    enterpriseCode: string;
    /**
     * 公司地址
     */
    enterpriseAddress: string;
    /**
     * 平台类型
     */
    platformType: string;
    /**
     * 创建时间
     */
    createTime: number;
    /**
     * 过期时间
     */
    expireTime: number;
    /**
     * 实际租户数量
     */
    actualTenantCount: number;
    /**
     * 创建人
     */
    creator: number;
    /**
     * 管理员账号
     */
    adminUser: string;
    /**
     * 状态
     */
    status: 'enable' | 'disable';
    /**
     * 租户数量限制
     */
    tenantLimit: number;
    /**
     * 用户数量限制
     */
    userLimit: number;
}

export interface LicenseInfoList extends LicenseInfo { 
  id: string
}
