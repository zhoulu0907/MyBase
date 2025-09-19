export enum SortType {
  // 不排序
  NONE = 'none',
  // 升序
  ASC = 'asc',
  DESC = 'desc'
}

export interface Sort {
  id: string;
  sortType: string;
  sortField: string;
}
