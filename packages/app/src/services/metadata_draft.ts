import { PageParam } from '../types';
import { DeleteDraftParams } from '../types/metadata_draft';
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

export const deleteDraft = (tableName: string, menuId: string, params: DeleteDraftParams) => {
  return runtimeMetadataService.post(`/draft/${tableName}/delete?menuId=${menuId}`, params);
};

export const deleteDraftTable = (tableName: string, menuId: string) => {
  return runtimeMetadataService.post(`/draft/${tableName}/deletebytable?menuId=${menuId}`);
};

export const updateDraft = (tableName: string, menuId: string, data: any) => {
  return runtimeMetadataService.post(`/draft/${tableName}/update?menuId=${menuId}`, data);
};
