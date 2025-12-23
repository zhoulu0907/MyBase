export interface DataItem {
  id: string;
  name: string;
  children?: DataItem[];
  [key: string]: any;
}

export interface UserTableProps {
  selectedDeptId?: string;
  deptTree: DataItem[]; 
  deptLoading: boolean;
  onRefreshDept: () => void;
}

export type CreateSourceType = {
  self: string;
  back: string;
};

export type CreateSourceKey = keyof CreateSourceType;

export interface externalUserRecord {
  id: string;
  nickName: string;
  mobile: string;
  applicationList: userApplicationList[];
  status: number;
  source: CreateSourceType;
}

export interface SelectOptions {
  label: string;
  value: string | number;
}

export interface userApplicationList {
  appName: string;
  appId: string;
  iconColor: string;
  iconName: string;
}
