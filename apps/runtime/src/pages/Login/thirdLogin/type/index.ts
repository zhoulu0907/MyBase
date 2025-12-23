export interface IRegisterProps {
  appId: string;
  tenantId: string;
  isRelatedApp: boolean;
  mobile: string;
  onGoBack: () => void;
}

export interface registerInfo {
  id: string;
  userName: string;
  email?: string;
}