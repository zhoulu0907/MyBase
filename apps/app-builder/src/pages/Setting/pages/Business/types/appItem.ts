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

export interface updatedParams {
    id: string;
    applicationId: string;
    corpId: string;
    authorizationTime: string;
    expiresTime: string;
}

export interface IAuthorizedAppProps {
    loading: boolean;
    tableData: AppItem[];
    pageination: any;
    className?: string;
    visible: boolean;
    setVisible: (value: boolean) =>void;
    onSearch: (value: string) => void;
    onChange: (pageNo: number, pageSize: number) => void;
    onUpdateTime:(values: updatedParams) => void;
    onRemoveAuthorizedApp: (id: string) => void;
}

export interface statusProps {
  label: string;
  value: string;
  status: number;
}

export interface corpApplicationListProps {
  appName: string;
  appCode: string;
  iconName: string;
  iconColor: string;
}

export interface cropItem {
  id: string;
  address: string;
  corpCode: string;
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
  industryOptions: industryTypeOption[]
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

export interface authorizedAppList extends corpApplicationListProps {
  versionNumber: string;
  createTime: string;
  appId: string;
}

export interface industryTypeOption {
  colorType: string;
  dictType: string;
  id: string;
  label: string;
  sort:number;
  status: number; 
  value: string;
}