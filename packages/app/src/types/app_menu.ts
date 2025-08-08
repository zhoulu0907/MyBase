export interface ListApplicationMenuReq {
  applicationId: string;
}

export interface ApplicationMenu {
    id: string;
    parentId?: string;
    menuCode: string;
    menuSort: number;
    menuType: string;
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
    id: "0",
    menuCode: "",
    menuSort: 0,
    menuType: "",
    menuName: "根目录",
    menuIcon: "",
    isVisible: true,
    children: [] as ApplicationMenu[]
};

export interface CreateApplicationMenuReq {
  applicationId: string;
  parentId?: string;
  pageType?: number;
  menuName: string;
  menuType: MenuType;
  menuIcon: string;
}

export interface UpdateApplicationMenuNameReq {
  id: string;
  menuName: string;
}

export interface UpdateApplicationMenuOrderReq {
  id: string;
  parentUuid?: string;
  ids: number[];
}

export interface UpdateApplicationMenuVisibleReq {
  id: string;
  visible: boolean;
}

export interface CopyApplicationMenuReq {
  id: string;
  menuName: string;
  parentUuid?: string;
}

export interface DeleteApplicationMenuReq {
  id: string;
}

export interface GetApplicationMenuReq {
  id: string;
}
