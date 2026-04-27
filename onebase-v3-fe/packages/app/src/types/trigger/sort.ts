export enum SortType {
  // 不排序
  NONE = 'none',
  // 升序
  ASC = 'asc',
  DESC = 'desc'
}

export interface SortData {
  id: string;
  sortType: string;
  sortField: string;
}
