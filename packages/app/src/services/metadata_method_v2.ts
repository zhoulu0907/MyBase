// 实体管理服务
import {
  DeleteMethodV2Params,
  DetailMethodV2Params,
  InsertMethodV2Params,
  PageMethodV2Params,
  UpdateMethodV2Params
} from '../types';
import { runtimeMetadataService } from './clients';

export const dataMethodCreateV2 = (
  tableName: string,
  menuId: string,
  params: InsertMethodV2Params,
  draftId?: string
) => {
  // TODO(mickey): 等卞老师接口修复后继续联调
  //   console.log('draftId: ', draftId);
  let url = `/${tableName}/create?menuId=${menuId}`;
  //   if (draftId) {
  //     url += `&draftId=${draftId}`;
  //   }
  return runtimeMetadataService.post(url, params);
};

export const dataMethodUpdateV2 = (tableName: string, menuId: string, params: UpdateMethodV2Params) => {
  return runtimeMetadataService.post(`/${tableName}/update?menuId=${menuId}`, params);
};

export const dataMethodDeleteV2 = (tableName: string, menuId: string, params: DeleteMethodV2Params) => {
  return runtimeMetadataService.post(`/${tableName}/delete?menuId=${menuId}`, params);
};

export const dataMethodDetailV2 = (tableName: string, menuId: string, params: DetailMethodV2Params) => {
  return runtimeMetadataService.post(`/${tableName}/detail?menuId=${menuId}`, params);
};

export const dataMethodPageV2 = (tableName: string, menuId: string, params: PageMethodV2Params) => {
  return runtimeMetadataService.post(`/${tableName}/page?menuId=${menuId}`, params);
};
