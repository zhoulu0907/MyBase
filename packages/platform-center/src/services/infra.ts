import { isRuntimeEnv } from '@onebase/common';
import { BatchUpdateSecurityConfigsParams } from '../types';
import { infraService, platformInfraService, runtimeInfraService } from './clients';

export interface UploadProgressCallback {
  (progressEvent: ProgressEvent): void;
}

// build
export const uploadFile = (data: any, onProgress?: UploadProgressCallback) => {
  return (isRuntimeEnv() ? runtimeInfraService : infraService).post('/file/upload', data, {
    headers: {
      'Content-Type': 'multipart/form-data'
    },
    onUploadProgress: onProgress
  });
};

export const platformUploadFile = (data: any, onProgress?: UploadProgressCallback) => {
  return platformInfraService.post('/file/upload', data, {
    headers: {
      'Content-Type': 'multipart/form-data'
    },
    onUploadProgress: onProgress
  });
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
