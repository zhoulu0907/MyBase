export interface InsertMethodV2Params {
  [key: string]: any;
}

export interface UpdateMethodV2Params {
  id: string;
  [key: string]: any;
}

export interface DeleteMethodV2Params {
  id: string;
  [key: string]: any;
}

export interface DetailMethodV2Params {
  id: string;
}

export interface PageMethodV2Params {
  data?: any;
  pageNo?: number;
  pageSize?: number;
  sortField?: string;
  /** 排序方向，仅支持 'ASC' 或 'DESC' */
  sortDirection?: 'ASC' | 'DESC';
  filters?: any;
}
