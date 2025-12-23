import { PageParam } from '../types';
import { runtimeMetadataService } from './clients';

export const createDraft = (tableName: string, menuId: string, data: any) => {
  return runtimeMetadataService.post(`/draft/${tableName}/create?menuId=${menuId}`, data);
};

export const getDraftDetail = (tableName: string, menuId: string) => {
  return runtimeMetadataService.post(`/draft/${tableName}/detail?menuId=${menuId}`);
};

export const getDraftPage = (tableName: string, menuId: string, pageParam: PageParam) => {
  return runtimeMetadataService.post(`/draft/${tableName}/page?menuId=${menuId}`, pageParam);
};
