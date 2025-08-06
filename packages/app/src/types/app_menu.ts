export interface ListApplicationMenuReq {
    applicationId: number;
}

export interface CreateApplicationMenuReq {
    applicationId: number;
    parentUuid?: string;
    menuName: string;
}

export interface UpdateApplicationMenuNameReq {
    id: number;
    menuName: string;
}

export interface UpdateApplicationMenuOrderReq {
    id: number;
    parentUuid?: string;
    ids: number[]
}

export interface UpdateApplicationMenuVisibleReq {
    id: number;
    visible: boolean;
}

export interface CopyApplicationMenuReq {
    id: number;
    menuName: string;
    parentUuid?: string;
}

export interface DeleteApplicationMenuReq {
    id: number;
}

export interface GetApplicationMenuReq {
    id: number;
}
