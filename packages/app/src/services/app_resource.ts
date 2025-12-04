import {
  CreatePageSetReq,
  CreatePageViewParams,
  DeletePageSetReq,
  GetAppIdByPageSetIdReq,
  GetComponentListByPageIdReq,
  GetPageListByAppIdReq,
  GetPageMetadataReq,
  GetPageSetIdReq,
  GetPageSetMainMetaDataReq,
  ListPageViewParams,
  LoadPageSetReq,
  SavePageSetReq
} from '../types';
import { appService, runtimeAppService } from './clients';

export const getPageSetId = (params: GetPageSetIdReq, runtime?: boolean) => {
  return (runtime ? runtimeAppService : appService).get('/resource/page_set/id', params);
};

export const savePageSet = (params: SavePageSetReq) => {
  return appService.post('/resource/page_set/save', params);
};

export const loadPageSet = (params: LoadPageSetReq, runtime?: boolean) => {
  return (runtime ? runtimeAppService : appService).post('/resource/page_set/load', params);
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

export const getPageSetMetaData = (params: GetPageSetMainMetaDataReq, runtime?: boolean) => {
  return (runtime ? runtimeAppService : appService).get('/resource/page_set/main_metadata', params);
};

export const getPageListByAppId = (params: GetPageListByAppIdReq) => {
  return appService.post('/resource/page/form/app_id', params);
};

export const getPageMetadata = (params: GetPageMetadataReq) => {
  return appService.post('/resource/page/metadata', params);
};

export const getComponentListByPageId = (params: GetComponentListByPageIdReq) => {
  return appService.post('/resource/component/list', params);
};

export const createPageView = (params: CreatePageViewParams) => {
  return appService.post('/resource/page/view/create', params);
};

export const listPageView = (params: ListPageViewParams, runtime?: boolean) => {
  return (runtime ? runtimeAppService : appService).post('/resource/page/view/list', params);
};
