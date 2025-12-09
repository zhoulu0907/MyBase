export interface CorpAppParams {
  corpId: string;
  applicationIdList: string[];
  authorizationTime: number;
  expiresTime: number;
}

export interface corpAppListParams {
  pageNo: number;
  pageSize: number;
  status?: number;
  corpId?: string;
}

export interface updateAppParams {
  id: string;
  applicationId: string;
  corpId: string;
  authorizationTime: number;
  expiresTime: number;
}

export interface authAppStatusParams {
  id: string;
  status: string;
}