import { appService, dashboardService } from './clients';
import { DashboardListParams, DashboardTemplateListParams, editDashboardInfoParams, getDashboardIdParams, saveDashboardAsTemplateParams } from '../types/app_dashboard';

// 获取模版列表接口
export const getDashboardTemplateListApi = (params: DashboardTemplateListParams) => {
  return dashboardService.get(`/template/page`, params); 
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

// 大屏另存为模版
export const saveDashboardAsTemplateApi = (params: saveDashboardAsTemplateParams) => {
  return dashboardService.post(`/template/saveOtherDashboardTemplate`, params);
};

// 大屏报表获取大屏id
export const getDashboardIdApi = (params: getDashboardIdParams) => {
  return dashboardService.post(`/create`, params);
};