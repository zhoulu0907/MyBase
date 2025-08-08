import { type CreateApplicationTagReq, type ListTagReq } from '../types/tag';
import appService from './clients/app';

export const listApplicationTag = (params: ListTagReq) => {
  return appService.get('/tag/list', params);
};

export const createApplicationTag = (params: CreateApplicationTagReq) => {
  return appService.post('/tag/create', params);
};
