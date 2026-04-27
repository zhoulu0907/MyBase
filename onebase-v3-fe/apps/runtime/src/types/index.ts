export interface AppItem {
  id: string;
  applicationName: string;
  applicationId?: string;
  applicationUid?: string;
  applicationCode: string;
  authorizationTime: number;
  versionNumber:string;
  expiresTime: number;
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
  showStatus?: number;
}

export interface authorizedTime {
  authorizationTime: number;
  expiresTime: number;
}

export interface authorizedTimeGroup {
  appTime: authorizedTime
}