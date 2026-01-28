import {
  OfflineApplicationReq,
  OnlineApplicationReq,
  type DeleteApplicationVersionReq,
  type PageApplicationVersionReq,
  type RestoreApplicationVersionReq,
  type PageAppVersionExportReq
} from '../types/app_version';
import { appService } from './clients';

export const pageApplicationVersion = (params: PageApplicationVersionReq) => {
  return appService.get('/version/page', params);
};

export const onlineApplication = (params: OnlineApplicationReq) => {
  return appService.post('/version/online', params);
};

export const offlineApplication = (params: OfflineApplicationReq) => {
  return appService.post('/version/offline', params);
};

export const restoreApplicationVersion = (params: RestoreApplicationVersionReq) => {
  return appService.post('/version/restore', params);
};

export const deleteApplicationVersion = (params: DeleteApplicationVersionReq) => {
  return appService.post('/version/delete', params);
};

// 获取导出应用资源 应用下载
export const exportAppVersionFile = (params: { exportId: string }, fileName:string) => {
  return appService.download('/version/export/file', fileName, {params}, true) as Promise<string>;;
};

// 导出应用 0开发  1生产  版本id
export const exportAppVersion = (params: { versionId: string }) => {
  return appService.get('/version/export', params);
};

// 导入应用 file  applicationId
export const importAppVersion = (params: FormData) => {
  return appService.post('/version/import', params, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  });
};

// 导出记录分页查询
export const pageExportAppVersion = (params: PageAppVersionExportReq) => {
  return appService.get('/version/export/page', params);
};

// 删除导出记录
export const deleteExportAppVersion = (params: { exportId: string }) => {
  return appService.post('/version/export/delete', params);
};

// 重试导出
export const retryExportAppVersion = (params: { exportId: string }) => {
  return appService.get('/version/export/retry', params);
};

// 获取导出应用状态
export const getExportAppVersionStatus = (params: { exportId: string }) => {
  return appService.get('/version/export/status', params);
};
