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