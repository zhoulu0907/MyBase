import {
  type CreateApplicationVersionReq,
  type DeleteApplicationVersionReq,
  type ListApplicationVersionReq,
  type RestoreApplicationVersionReq
} from '../types/app_version';
import appService from './clients/app';

export const listApplicationVersion = (params: ListApplicationVersionReq) => {
  return appService.get('/version/list', params);
};

export const createApplicationVersion = (params: CreateApplicationVersionReq) => {
  return appService.post('/version/create', params);
};

export const restoreApplicationVersion = (params: RestoreApplicationVersionReq) => {
  return appService.post('/version/restore', params);
};

export const deleteApplicationVersion = (params: DeleteApplicationVersionReq) => {
  return appService.post('/version/delete', params);
};
