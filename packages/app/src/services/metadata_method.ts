// 实体管理服务
import { DataMethodParam, DeleteMethodParam, InsertMethodParams, PageMethodParam, UpdateMethodParams } from '../types';
import { runtimeMetadataService } from './clients';

export const dataMethodInsert = (params: InsertMethodParams) => {
  return runtimeMetadataService.post(`/data-method/insert`, params);
};

export const dataMethodPage = (params: PageMethodParam) => {
  return runtimeMetadataService.post(`/data-method/data/page`, params);
};

export const dataMethodDelete = (params: DeleteMethodParam) => {
  return runtimeMetadataService.post(`/data-method/delete`, params);
};

export const dataMethodData = (params: DataMethodParam) => {
  return runtimeMetadataService.post(`/data-method/data`, params);
};

export const dataMethodUpdate = (params: UpdateMethodParams) => {
  return runtimeMetadataService.post(`/data-method/update`, params);
};
