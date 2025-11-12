// 企业
export interface disabledCorpParams {
  id: string;
  status: number;
}

export interface pageParams {
  pageNo: number;
  pageSize: number;
  corpName?: string;
  industryType?: string;
  status?: string;
  beginCreateTime?: string;
  endCreateTime?: string;
}

export interface updateCorpParams {
  id: string;
  corpId: string;
  corpName: string;
  industryType: number;
  address: string;
  userLimit: number;
}

export interface corpStatusParams {
  id: string;
  status: number;
}

export interface AppAuthTimeInfo {
  applicationIdList: string[];
  authorizationTime: string;
  expiresTime: string;
}

export interface CorpAdminInfo {
  username: string;
  email?: string;
  mobile?: string;
  nickname?: string;
}

export interface CorpBasicInfo {
  address?: string;
  corpId?: string;
  corpLogo?: string;
  corpName?: string;
  industryType?: string;
  status?: number;
  userLimit?: number;
}

export interface createCorpParams {
  appAuthTimeReqVO: AppAuthTimeInfo;
  corpAdminReqVO: CorpAdminInfo;
  corpReqVO: CorpBasicInfo;
}

export interface corpListParams {
  pageNo: number;
  pageSize: number
}

/**
 * 返回数据
 *
 * CorpRespVO
 */
export interface CorpDetailResponse {
  /**
   * 地址
   */
  address?: string;
  /**
   * 管理员
   */
  adminName?: string;
  /**
   * 应用个数
   */
  appCount?: number;
  /**
   * 授权应用
   */
  corpApplicationList?: CorpAppVo[];
  /**
   * 企业编码
   */
  corpCode?: string;
  /**
   * 企业Logo
   */
  corpLogo?: string;
  /**
   * 企业名称
   */
  corpName?: string;
  /**
   * 创建时间
   */
  createTime?: string;
  /**
   * 联系人邮箱
   */
  email?: string;
  /**
   * 企业Id
   */
  id?: string;
  /**
   * 行业类型
   */
  industryType?: string;
  /**
   * 联系人电话
   */
  mobile?: string;
  /**
   * 状态
   */
  status?: number;
  /**
   * 用户个数
   */
  userCount?: number;
  /**
   * 用户上限
   */
  userLimit?: number;
  [property: string]: any;
}

/**
 * com.cmsr.onebase.module.system.vo.corp.CorpAppVo
 *
 * CorpAppVo
 */
export interface CorpAppVo {
  /**
   * 应用ID
   */
  appId?: string;
  /**
   * 应用名称
   */
  appName?: string;
  /**
   * 图标颜色
   */
  iconColor?: string;
  /**
   * 应用图标
   */
  iconName?: string;
  [property: string]: any;
}