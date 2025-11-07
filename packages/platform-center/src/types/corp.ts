// 企业
export interface disabledCorpParams {
  id: string;
  status: number; 
}

export interface pageParams {
    pageNo: number;
    pageSize: number;
    corpName?: string;
    industryType?: string;
    status?: string;
    beginCreateTime?: string;
    endCreateTime?: string;
}

export interface updateCorpParams {
    id: string;
    corpId: string;
    corpName: string;
    industryType: number;
    address: string;
    userLimit: number;
}

export interface corpStatusParams {
    id: string;
    status: number;
}

export interface AppAuthTimeInfo {
    applicationIdList: string[];
    authorizationTime: string;
    expiresTime: string;
}

export interface CorpAdminInfo {
    username: string;
    email?: string;
    mobile?: string;
    nickname?: string;
}

export interface CorpBasicInfo {
    address?: string;
    corpId?: string;
    corpLogo?: string;
    corpName?: string;
    industryType?: string;
    status?: number;
    userLimit?: number;
}

export interface createCorpParams {
    appAuthTimeReqVO: AppAuthTimeInfo;
    corpAdminReqVO: CorpAdminInfo;
    corpReqVO: CorpBasicInfo;
}

export interface corpListParams {
    pageNo: number;
    pageSize: number
}