// 实体管理服务
import {
  DeleteMethodV2Params,
  DetailMethodV2Params,
  InsertMethodV2Params,
  PageMethodV2Params,
  UpdateMethodV2Params
} from '../types';
import { runtimeMetadataService } from './clients';

export const dataMethodCreateV2 = (tableName: string, menuId: string, params: InsertMethodV2Params) => {
  return runtimeMetadataService.post(`/${tableName}/create?menuId=${menuId}`, params);
};

export const dataMethodUpdateV2 = (tableName: string, menuId: string, params: UpdateMethodV2Params) => {
  return runtimeMetadataService.post(`/${tableName}/update?menuId=${menuId}`, params);
};

export const dataMethodDeleteV2 = (tableName: string, menuId: string, params: DeleteMethodV2Params) => {
  return runtimeMetadataService.post(`/${tableName}/delete?menuId=${menuId}`, params);
};

export const dataMethodDetailV2 = (tableName: string, menuId: string, params: DetailMethodV2Params) => {
  return runtimeMetadataService.get(`/${tableName}/detail?menuId=${menuId}`, params);
};

export const dataMethodPageV2 = (tableName: string, menuId: string, params: PageMethodV2Params) => {
  return runtimeMetadataService.get(`/${tableName}/page?menuId=${menuId}`, params);
};
