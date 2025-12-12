export interface PageApplicationVersionReq {
  applicationId: string;
  pageNo: number;
  pageSize: number;
}

export enum OperationType {
  PUBLISH = 1, // 发布版本
  SAVE = 2 // 保存版本
}

export interface CreateApplicationVersionReq {
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
