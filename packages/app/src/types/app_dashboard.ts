export  enum IsHot {
  /** 不是热门模版 */
  NO = 0,
  /** 是热门模版 */
  YES = 1,
}

export interface DashboardTemplateListParams {
  /** 模版类型 */
  templateType?: string;
  /** 热门模版 hot */
  hot?: IsHot;
  /** 页码 */
  pageNo: number;
  /** 页大小 */
  pageSize: number;
  /** 搜索关键字 */
  templateName?: string;
}

export interface DashboardListParams {
  /** 大屏页码 */
  page: number;
  /** 大屏页大小 */
  limit: number;

}
/** 修改大屏信息 */
export interface editDashboardInfoParams {
  /** 大屏id */
  id: string;
  /** 大屏名称 */
  projectName: string;
  /** 大屏描述 */
  remarks: string;
  /** 大屏状态 */
  state?: number;
  /** 大屏缩略图 */
  indexImage?: string;
}

// 大屏另存为模版
export interface saveDashboardAsTemplateParams {
  /** 大屏id */
  id: string;
  /** appId */
  appId: string;
}

export interface getDashboardIdParams {
  /** 大屏名称 */
  projectName: string;
  /** 租户id */
  tenantId: string;
  /** appId */
  appId: string;
}