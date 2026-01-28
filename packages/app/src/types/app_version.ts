export interface PageApplicationVersionReq {
  applicationId: string;
  pageNo: number;
  pageSize: number;
}

export interface PageAppVersionExportReq {
  exportStatus: string;
  pageNo: number;
  pageSize: number;
} 
export enum OperationType {
  PUBLISH = 1, // 发布版本
  SAVE = 2 // 保存版本
}

export enum VersionExportType {
  DEV = 0, // 开发环境
  PROD = 1 // 正式环境
}

export interface OnlineApplicationReq {
  applicationId: string;
  versionName: string;
  versionNumber: string;
  versionDescription: string;
  environment: string;
  operationType: number;
}

export interface OfflineApplicationReq {
  applicationId: string;
}

export interface RestoreApplicationVersionReq {
  applicationId: string;
  versionId: number;
}

export interface DeleteApplicationVersionReq {
  applicationId: string;
  versionId: number;
}
