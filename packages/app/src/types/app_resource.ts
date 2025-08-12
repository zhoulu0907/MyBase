import { ComponentConfig } from "./app_component";

export interface GetPageSetCodeReq {
  menuCode: string;
}

export interface PageSet {
    pageCode: string;
    pageName: string;
    pageType: string;
    components: ComponentConfig[];
}

export interface SavePageSetReq {
    pageSetCode: string;
    pageSetName: string;
    pages: PageSet[];
}

export interface LoadPageSetReq {
    pageSetCode: string;
}

export interface CreatePageSetReq {
    pageSetName: string;
    menuId: string;
    displayName: string;
    description: string;
}

export interface DeletePageSetReq {
    pageSetCode: string;
}

export interface GetAppIdByPageSetCodeReq {
    code: string
}
