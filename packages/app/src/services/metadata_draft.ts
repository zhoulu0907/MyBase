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

// TODO(mickey): 等天宇提供真实接口，目前mock
export const deleteDraft = (tableName: string, menuId: string, id: string) => {
  return runtimeMetadataService.post(`/draft/${tableName}/delete?menuId=${menuId}&id=${id}`);
};

export const batchDeleteDraft = (tableName: string, menuId: string) => {
  return runtimeMetadataService.post(`/draft/${tableName}/delete?menuId=${menuId}`);
};
