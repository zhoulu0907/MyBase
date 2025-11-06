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

export interface AuthorizedAppRef {
  addNewApp: (newData: AppItem) => void;
};

export interface IAuthorizedAppProps {
    loading: boolean;
    tableData: AppItem[];
    pageination: any;
    className?: string;
    onSearch: (value: string) => void;
    onChange: (pageNo: number, pageSize: number) => void;
}

export interface statusProps {
  label: string;
  value: string;
  status: number;
}

export interface corpApplicationListProps {
  appName: string;
  appCount: string;
  iconName: string;
}

export interface cropItem {
  id: string;
  address: string;
  corpId: string;
  corpName: string;
  createTime:number;
  industryType: number;
  status: number;
  userLimit:number;
  adminName: string;
  corpApplicationList: corpApplicationListProps[];
}

export interface OutletContextType {
  currentId: string;
}

export interface authorizedTime {
  authorizationTime: string;
  expiresTime: string;
}
export interface authorizedTimeGroup {
  appTime: authorizedTime
}

export interface ICreateAppModal {
    visible: boolean;
    onCloseAppModal: () =>void;
    onSaveAppData: (data: any)=>void;
}

export interface authorizedAppList {
  corpName: string;
  corpId: string;
  id: string;
}