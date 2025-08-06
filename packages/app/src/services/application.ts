// 应用服务

import {
    type CreateApplicationReq,
    type DeleteApplicationReq,
    type ListApplicationReq,
    type UpdateApplicationNameReq,
    type UpdateApplicationReq
} from '../types/application';
import appService from './clients/app';


export const listApplication = (params: ListApplicationReq) => {
  return appService.get('/application/page', params);
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
  return appService.post('/application/delete', params);
}