import { appService, dashboardService } from './clients';
import { DashboardListParams, DashboardTemplateListParams } from '../types/app_dashboard';

// 获取模版列表接口
export const getDashboardTemplateListApi = (params: DashboardTemplateListParams) => {
  return dashboardService.get(`/template/page?hot=${params.hot}&templateType=${params.templateType}&page_no=${params.page_no}&page_size=${params.page_size}`);
};

// 获取大屏列表接口 
export const getDashboardListApi = (params: DashboardListParams) => {
  return dashboardService.get('/list', params);
};

