export interface ListApplicationMenuReq {
  applicationId: string;
}

export interface ApplicationMenu {
    id: string;
    parentCode?: string;
    menuCode: string;
    menuSort: number;
    menuType: number;
    menuName: string;
    menuIcon: string;
    isVisible: boolean;
    children: ApplicationMenu[];
}

export enum MenuType {
  PAGE = 1,
  GROUP = 2
}

export enum PageType {
  // 普通表单
  NORMAL = 1
}

export const RootParentPage = {
    id: "",
    menuCode: "root",
    parentCode: "",
    menuSort: 0,
    menuType: MenuType.GROUP,
    menuName: "根目录",
    menuIcon: "",
    isVisible: true,
    children: [] as ApplicationMenu[]
};

export interface CreateApplicationMenuReq {
  applicationId: string;
  parentCode?: string;
  pageType?: number;
  menuName: string;
  menuType: MenuType;
  menuIcon: string;
  entityCode?: string;
}

export interface UpdateApplicationMenuNameReq {
  id: string;
  menuName: string;
}

export interface UpdateApplicationMenuOrderReq {
  id: string;
  parentCode?: string;
  ids: number[];
}

export interface UpdateApplicationMenuVisibleReq {
  id: string;
  visible: boolean;
}

export interface CopyApplicationMenuReq {
  id: string;
  menuName: string;
  parentCode?: string;
}

export interface DeleteApplicationMenuReq {
  id: string;
}

export interface GetApplicationMenuReq {
  id: string;
}
