export interface AppItem {
  key:number,
  applicationName: string;
  applicationCode: string;
  applicationId: number;
  authorizationTime: string;
  versionNumber:string;
  expiresTime: string;
  status: number;
}

export interface AuthorizedAppRef {
  addNewApp: (newData: AppItem) => void;
};

export interface IAuthorizedAppProps {
    loading: boolean;
    tableData: AppItem[];
    pageination: any;
    className?: string;
    onEdit: (id: number) =>void;
    onSearch: (value: string) => void;
    onChange: (pageNo: number, pageSize: number) => void;
    setAddAppModalVisible: (visible:boolean) => void;
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
  id: number;
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
  currentId: number;
}