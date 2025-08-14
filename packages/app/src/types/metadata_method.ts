export interface InsertMethodParams {
    entityId: string;
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
}

export interface DeleteMethodParam {
    entityId: string;
    id: string;
}

export interface DataMethodParam {
    entityId: string;
    id: string;
}