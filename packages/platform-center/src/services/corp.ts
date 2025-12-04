//企业管理
import {
  checkCorpAdminUserParams,
  checkCorpParams,
  CorpDetailResponse,
  corpListParams,
  corpStatusParams,
  createCorpParams,
  updateCorpParams
} from '../types';
import { runtimeCorpService, systemService } from './clients';

// 创建企业
export const createCorpApi = (data: createCorpParams) => systemService.post('/corp/create', data);

// 验证企业基本信息
export const checkCorpApi = (data: checkCorpParams) => systemService.post('/corp/check-corp', data);

// 验证企业管理员
export const checkCorpAdminUserApi = (data: checkCorpAdminUserParams) =>
  systemService.post('/corp/check-corp-admin-user', data);

//更新企业
export const updateCorpApi = (data: updateCorpParams, runtime?: boolean) =>
  (runtime ? runtimeCorpService : systemService).post('/corp/update', data);

//禁用/启用企业
export const disabledCorpApi = (data: corpStatusParams) =>
  systemService.post(`/corp/update-status?id=${data.id}&status=${data.status}`);

// 删除企业
export const deleteCorpApi = (id: string) => systemService.post(`/corp/delete?id=${id}`);

//获取企业列表-分页
export const getCorpListApi = (data: corpListParams) => systemService.get('/corp/page', data);

//获取企业精简信息列表-不分页
export const getCorpSimpleDetailsListApi = () => systemService.get('/corp/simple-list');

//获得详情
export const getCorpDetailByIdApi = (id: number): CorpDetailResponse => systemService.get(`/corp/get?id=${id}`);
