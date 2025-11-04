export interface AppItem {
  key:number,
  appName: string;
  appId: string;
  version: string;
  effectTime: string;
  expireTime: string;
}

export interface AuthorizedAppRef {
  addNewApp: (newData: AppItem) => void;
};

export interface IAuthorizedAppProps {
    className?: string;
    setAddAppModalVisible: (visible:boolean) => void;
    onEdit: (record?: AppItem)=> void;
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
  corpId: number;
}