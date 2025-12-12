
import { runtimeMetadataService } from './clients';

interface UploadProgressCallback {
  (progressEvent: ProgressEvent): void;
}

/**
 * @param tableName 
 * @param params 
 * @returns 
 */
export const attachmentUpload = (tableName: string, params: any, onProgress?: UploadProgressCallback) => {
  return runtimeMetadataService.post(`/${tableName}/attachment/upload`, params, {
    headers: {
      'Content-Type': 'multipart/form-data'
    },
    onUploadProgress: onProgress
  });
};

/**
 * @param tableName 
 * @param params `menuId`、`id`、`fieldName`、`fileId`
 * @returns 
 */
export const attachmentDownload = (tableName: string, params: any) => {
  return runtimeMetadataService.get(`/${tableName}/attachment/download`, params);
};


