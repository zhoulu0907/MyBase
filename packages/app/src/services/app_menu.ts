import {
    type CopyApplicationMenuReq,
    type CreateApplicationMenuReq,
    type DeleteApplicationMenuReq,
    type ListApplicationMenuReq,
    type UpdateApplicationMenuNameReq,
    type UpdateApplicationMenuOrderReq,
    type UpdateApplicationMenuVisibleReq
} from '../types/app_menu';
import { appService } from './clients';

export const listApplicationMenu = (params: ListApplicationMenuReq) => {
  return appService.get('/menu/list', params);
};

export const createApplicationMenu = (params: CreateApplicationMenuReq) => {
  return appService.post('/menu/create', params);
};

export const updateApplicationMenuName = (params: UpdateApplicationMenuNameReq) => {
  return appService.post(`/menu/update-name?id=${params.id}&menuName=${params.menuName}`);
};

export const deleteApplicationMenu = (params: DeleteApplicationMenuReq) => {
  return appService.post(`/menu/delete?id=${params.id}`);
};

export const updateApplicationMenuOrder = (params: UpdateApplicationMenuOrderReq) => {
  return appService.post('/menu/update-order', params);
};

export const updateApplicationMenuVisible = (params: UpdateApplicationMenuVisibleReq) => {
  return appService.post('/menu/update-visible', params);
};

export const copyApplicationMenu = (params: CopyApplicationMenuReq) => {
  return appService.post('/menu/copy', params);
};
