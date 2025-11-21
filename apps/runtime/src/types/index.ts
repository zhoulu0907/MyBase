export interface AppItem {
  id: string;
  applicationName: string;
  applicationId?: string;
  applicationUid?: string;
  applicationCode: string;
  authorizationTime: string;
  versionNumber:string;
  expiresTime: string;
  statusDesc: string;
}

export interface authorizedTime {
  authorizationTime: string;
  expiresTime: string;
}

export interface authorizedTimeGroup {
  appTime: authorizedTime
}