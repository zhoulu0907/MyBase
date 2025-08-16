import { ComponentConfig } from "./app_component";

export interface GetPageSetIdReq {
    menuId: string;
}

export interface PageSet {
    id: string;
    pageName: string;
    pageType: string;
    components: ComponentConfig[];
}

export interface SavePageSetReq {
    id: string;
    pageSetName: string;
    pages: PageSet[];
}

export interface LoadPageSetReq {
    id: string;
}

export interface CreatePageSetReq {
    pageSetName: string;
    menuId: string;
    displayName: string;
    description: string;
}

export interface DeletePageSetReq {
    menuId: string;
}

export interface GetAppIdByPageSetIdReq {
    pageSetId: string
}


export interface GetPageSetMainMetaDataReq {
    pageSetId: string;
}