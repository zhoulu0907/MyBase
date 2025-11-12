export interface CorpAppParams {
    corpId: string;
    applicationIdList: string[];
    authorizationTime: string;
    expiresTime: string;
}

export interface corpAppListParams {
    pageNo: number;
    pageSize: number;
    corpId: string;
}

export interface updateAppParams {
    id: string;
    applicationId: string;
    corpId: string;
    authorizationTime: string;
    expiresTime: string;
}