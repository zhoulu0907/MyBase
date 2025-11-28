// 应用服务

import {
  type CreateApplicationReq,
  type DeleteApplicationReq,
  type GetApplicationReq,
  type UpdateApplicationNameReq,
  type UpdateApplicationReq
} from '../types/application';
import { type PageParam } from '../types/common';
import { appService, runtimeAppService } from './clients';

export const listApplication = (params: PageParam) => {
  return appService.get('/application/page', params);
};

export const getApplication = (params: GetApplicationReq) => {
  return appService.get(`/application/get?id=${params.id}`);
};

export const runtimeGetApplication = (params: GetApplicationReq) => {
  return runtimeAppService.get(`/application/get?id=${params.id}`);
};

export const createApplication = (params: CreateApplicationReq) => {
  return appService.post('/application/create', params);
};

export const updateApplication = (params: UpdateApplicationReq) => {
  return appService.post('/application/update', params);
};

export const updateApplicationName = (params: UpdateApplicationNameReq) => {
  return appService.post('/application/update-name', params);
};

export const deleteApplication = (params: DeleteApplicationReq) => {
  const { id, name } = params;
  return appService.post(`/application/delete?id=${id}&name=${name}`);
};

export const generateId = () => {
  return appService.get('/application/id/generate');
};

// 获取应用精简信息列表-不分页
export const getApplicationSimple = (ownerTag: number, appName: string) => {
  return appService.get(`/application/simple-list-by-name?ownerTag=${ownerTag}&appName=${appName}`);
};
