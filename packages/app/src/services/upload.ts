import { DownloadAttachmentParams } from '../types';
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
 * @returns 返回 Blob URL 用于预览
 */
export const attachmentDownload = (tableName: string, params: DownloadAttachmentParams): Promise<string> => {
  return runtimeMetadataService.download(
    `/${tableName}/attachment/download?menuId=${params.menuId}&id=${params.id}&fieldName=${params.fieldName}&fileId=${params.fileId}`,
    undefined,
    undefined,
    true // returnUrl = true，返回 Blob URL 而不是直接下载
  ) as Promise<string>;
};
