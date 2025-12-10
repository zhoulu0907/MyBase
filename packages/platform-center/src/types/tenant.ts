export interface TenantInfo {
  id: number;
  name: string;
  adminNickName: string;
  contactMobile: string;
  status: number;
  creator: string;
  password: string;
  expireTime: string;
  accountCount: number;
  existUserCount: number;
  appCount: number;
  createTime: string;
  tenantCode: string;
  tenantKey: string;
  tenantSecret: string;
  website: string;
  websiteH5: string;
}

export interface updateTenantParams {
  id: string;
  name: string;
}