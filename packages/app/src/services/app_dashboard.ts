import { appService } from './clients';
import { CreateDashboardParams } from '../types/app_dashboard';

/**
 * 创建页面大屏参数
 * @param applicationId 应用ID
 * @param entityUuid 实体编码UUID
 * @param menuIcon 菜单图标
 * @param menuName 菜单名称
 * @param menuType 菜单类型，固定1
 * @param pageSetType 页面类型，暂定为4
 * @param parentId 父菜单id
 * @param createType 大屏创建方式 dashboardNew 新建 dashboardTemplate 模板 dashboardLink 关联已有
 * @param dashboardId 如选择从模板创建/已有大屏，这里传模板id/大屏id
 */
export const createPageDashboardApi = (params: CreateDashboardParams) => {
  return appService.post('page-dashboard/create', params);
}