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
    setAddAppModalVisible: (visible:boolean) => void;
    onEdit: (name: string)=>void;
}