import {
  CreatePageSetReq,
  DeletePageSetReq,
  GetAppIdByPageSetIdReq,
  GetPageListByAppIdReq,
  GetPageSetIdReq,
  GetPageSetMainMetaDataReq,
  LoadPageSetReq,
  SavePageSetReq
} from '../types';
import { appService } from './clients';

export const getPageSetId = (params: GetPageSetIdReq) => {
  return appService.get('/resource/page_set/id', params);
};

export const savePageSet = (params: SavePageSetReq) => {
  return appService.post('/resource/page_set/save', params);
};

export const loadPageSet = (params: LoadPageSetReq) => {
  return appService.post('/resource/page_set/load', params);
};

export const createPageSet = (params: CreatePageSetReq) => {
  return appService.post('/resource/page_set/create', params);
};

export const deletePageSet = (params: DeletePageSetReq) => {
  return appService.post('/resource/page_set/delete', params);
};

export const getAppIdByPageSetId = (params: GetAppIdByPageSetIdReq) => {
  return appService.get('/resource/page_set/app_id', params);
};

export const getPageSetMetaData = (params: GetPageSetMainMetaDataReq) => {
  return appService.get('/resource/page_set/main_metadata', params);
};

export const getPageListByAppId = (params: GetPageListByAppIdReq) => {
  return appService.post('/resource/page/form/app_id', params);
};
