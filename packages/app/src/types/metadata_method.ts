export interface InsertMethodParams {
  entityId: string;
  data: object;

  //   TODO(mickey): 处理子表逻辑
  //   subEntities: SubEntityParams[];
}

export interface SubEntityParams {
  subEntityId: string;
  data: object;
}

export interface UpdateMethodParams {
  entityId: string;
  id: string;
  data: object;
}

export interface PageMethodParam {
  entityId: string;
  pageNo: number;
  pageSize: number;
  filters?: any;
  sortField?: string;
  sortDirection?: string;
}

export interface DeleteMethodParam {
  entityId: string;
  id: string;
}

export interface DataMethodParam {
  entityId: string;
  id: string;
}
