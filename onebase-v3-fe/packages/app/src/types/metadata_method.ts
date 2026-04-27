export interface InsertMethodParams {
  menuId: string;
  entityId: string;
  data: object;
  subEntities?: SubEntityParams[];
}

export interface SubEntityParams {
  subEntityId: string;
  subData: object;
}

export interface UpdateMethodParams {
  menuId: string;
  entityId: string;
  id: string;
  data: object;
  subEntities?: SubEntityParams[];
}

export interface PageMethodParam {
  menuId: string;
  entityId: string;
  pageNo: number;
  pageSize: number;
  filters?: any;
  sortField?: string;
  sortDirection?: string;
}

export interface DeleteMethodParam {
  menuId: string;
  entityId: string;
  id: string;
}

export interface DataMethodParam {
  menuId: string;
  entityId: string;
  id: string;
}
