export interface CreateDashboardParams {
  /** 应用ID */
  applicationId: string;
  /** 实体编码UUID */
  entityUuid: string;
  /** 菜单图标 */
  menuIcon: string;
  /** 菜单名称 */
  menuName: string;
  /** 菜单类型，固定1 */
  menuType: number;
  /** 页面类型，暂定为4 */
  pageSetType: number;
  /** 创建方式 dashboardNew 新建 dashboardTemplate 模板 dashboardLink 关联已有 */
  parentId: string;
  /** 父菜单id */
  createDashboardType: string;
  /** 如选择从模板创建/已有大屏，这里传模板id/大屏id */
  dashboardId: string;
}