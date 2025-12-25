import { getResourceURL, isPlatformEnv, isRuntimeEnv } from '@onebase/common';
import { BatchUpdateSecurityConfigsParams, GetTenantSecurityConfigParams } from '../types';
import { infraService, platformInfraService, runtimeInfraService } from './clients';

export interface UploadProgressCallback {
  (progressEvent: ProgressEvent): void;
}

/**
 * 文件上传 formdata
 * file 文件附件
 * directory 文件目录
 * visitMode 文件保存标识 public-公开访问，authen-文件需登录鉴权,permission-内部调用
 */
const envService = isRuntimeEnv() ? runtimeInfraService : isPlatformEnv() ? platformInfraService : infraService;
export const uploadFile = (data: any, onProgress?: UploadProgressCallback) => {
  return envService.post('/file/upload', data, {
    headers: {
      'Content-Type': 'multipart/form-data'
    },
    onUploadProgress: onProgress
  });
};
// 根据文件 ID 列表获取文件详情列表
export const getFileListByIds = (ids: string[]) => {
  return envService.get(`/file/list-by-ids?ids=${ids}`);
};

// 获取文件内容
export const getFileUrlById = (id: string) => {
  const resourceUrl = getResourceURL();
  return `${resourceUrl}/${id}`;
};

export const getSecurityConfigCategories = () => {
  return infraService.get('/security-config/categories');
};

export const getSecurityConfigItems = (categoryId: string) => {
  return infraService.get(`/security-config/items?categoryId=${categoryId}`);
};

export const batchUpdateSecurityConfigs = (params: BatchUpdateSecurityConfigsParams) => {
  return infraService.post('/security-config/batch-update', params);
};

export const getTenantSecurityConfig = (params: GetTenantSecurityConfigParams) => {
  return envService.post('/security-config/get-tenant-items', params);
};
