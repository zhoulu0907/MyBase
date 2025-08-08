import { CreatePageSetReq, DeletePageSetReq, GetPageSetCodeReq, LoadPageSetReq, SavePageSetReq } from '../types';
import appService from './clients/app';

export const getPageSetCode = (params: GetPageSetCodeReq) => {
  return appService.get('/resource/page_set/code', params);
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