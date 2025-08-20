
import { infraService } from './clients';

export interface UploadProgressCallback {
  (progressEvent: ProgressEvent): void;
}

export const uploadFile = (data: any, onProgress?: UploadProgressCallback) => {
    return infraService.post('/file/upload', data, {
        headers: {
            'Content-Type': 'multipart/form-data'
        },
        onUploadProgress: onProgress
    });
};