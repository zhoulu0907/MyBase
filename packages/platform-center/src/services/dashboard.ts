import { DataSetParamsType,DashboardTemplateParamsType } from '../types';
import { dashboardDataSetService ,dashboardService} from './clients';
export const DataSetList = (params?: any) => {
  return dashboardDataSetService.post('/datasetTree/tree', params);
};
//数据集编辑
export const EditDataSet= (id?: string) => {
  return dashboardDataSetService.post(`/datasetTree/barInfo/${id}`);
};
//数据集删除
export const DelDataSetList = (id?: string) => {
  return dashboardDataSetService.post(`/datasetTree/delete/${id}`);
};
//
// 数据集列表\数据集分页
export const DataSetParams = (params: DataSetParamsType) => {
  return dashboardDataSetService.post(`/datasetTree/page`,params);
};

//模板列表
export const DashboardTemplateParams = (params: DashboardTemplateParamsType) => {
  return dashboardService.get(`/template/page?templateType=${params.type}&pageNo=${params.pageNo}&pageSize=${params.pageSize}&templateName=${params.templateName}`);
};
//模板列表删除
export const DelDashboardTemplate = (id?: string) => {
  return dashboardService.post(`/template/delete?id=${id}`);
};
//修改
export const upLoadDashboardTemplate = (params:{id:string,templateName:string,remarks:string}) => {
  return dashboardService.post(`/template/update`,params);
};
//新建模板
export const createDashboardTemplate = (params:{templateType:string,appId:string | undefined}) => {
  return dashboardService.post(`/template/create`,params);
};
