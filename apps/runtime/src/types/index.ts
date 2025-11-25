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

export interface TagProps {
  id: string;
  tagName: string;
  themeColor: string;
}

export interface ApplicationList {
  applicationName: string;
  applicationId: string;
  iconName: string;
  iconColor: string;
  appStatus: number;
  id: string;
  versionNumber: string;
  tags?: TagProps[];
  description: string;
  themeColor: string;
}

export interface authorizedTime {
  authorizationTime: string;
  expiresTime: string;
}

export interface authorizedTimeGroup {
  appTime: authorizedTime
}