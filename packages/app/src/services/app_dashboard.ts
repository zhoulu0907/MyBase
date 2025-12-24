import { appService, dashboardService } from './clients';
import { DashboardListParams, DashboardTemplateListParams, editDashboardInfoParams } from '../types/app_dashboard';

// 获取模版列表接口
export const getDashboardTemplateListApi = (params: DashboardTemplateListParams) => {
  return dashboardService.get(`/template/page?hot=${params.hot}&templateType=${params.templateType}&pageNo=${params.pageNo}&pageSize=${params.pageSize}&templateName=${params.templateName}`); 
};

// 获取大屏列表接口 
export const getDashboardListApi = (params: DashboardListParams) => {
  return dashboardService.get('/list', params);
};

// 修改大屏信息
export const editDashboardInfoApi = (params: editDashboardInfoParams) => {
  return dashboardService.post('/edit', params);
};

// 删除大屏
export const deleteDashboardApi = (params: string) => {
  return dashboardService.post(`/delete?id=${params}`);
};

// 获取大屏详情
export const getDashboardDetailApi = (params: string) => {
  return dashboardService.get(`/getScreenDSLData?projectId=${params}`);
};