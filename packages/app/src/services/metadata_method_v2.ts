// 实体管理服务
import { InsertMethodV2Params } from '../types/metadata_method_v2';
import { runtimeMetadataService } from './clients';

export const dataMethodCreateV2 = (tableName: string, menuId: string, params: InsertMethodV2Params) => {
  return runtimeMetadataService.post(`/${tableName}/create?menuId=${menuId}`, params);
};

export const dataMethodUpdateV2 = (tableName: string, menuId: string, params: InsertMethodV2Params) => {
  return runtimeMetadataService.post(`/${tableName}/update?menuId=${menuId}`, params);
};
