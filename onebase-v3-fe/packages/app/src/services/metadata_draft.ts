import { PageParam } from '../types';
import { DeleteDraftParams } from '../types/metadata_draft';
import { runtimeMetadataService } from './clients';

export const createDraft = (tableName: string, menuId: string, data: any) => {
  return runtimeMetadataService.post(`/draft/${tableName}/create?menuId=${menuId}`, data);
};

/**
 * 草稿 查询详情
 * tableName目标表名  
 * traceId 触发链路id  
 * id目标数据主键id  
 * tableName目标表名 
 * containSubTable 是否包含子表
 * containRelation 是否包含关系
 * methodCode 方法编码
 */
export const getDraftDetail = (tableName: string, menuId: string,params: any) => {
  return runtimeMetadataService.post(`/draft/${tableName}/detail?menuId=${menuId}`, params);
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
