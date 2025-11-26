export interface ListApplicationMenuReq {
  applicationId: string;
  name?: string;
}

export interface ApplicationMenu {
  id: string;
  parentId?: string;
  menuCode: string;
  menuSort: number;
  menuType: number;
  menuName: string;
  menuIcon: string;
  isVisible: number;
  children: ApplicationMenu[];
}

export enum MenuType {
  PAGE = 1,
  GROUP = 2,
  BPM = 3
}

export enum VisibleType {
  SHOW = 1,
  HIDDEN = 0
}

export enum PageType {
  // 普通表单
  NORMAL = 1,
  // 流程表单
  BPM = 2,
  // 工作台
  WORKBENCH = 3
}

export enum CATEGORY_TYPE {
  NAVIGATE = 'navigate',
  LAYOUT = 'layout',
  FORM = 'form',
  LIST = 'list',
  SHOW = 'show'
}

export const RootParentPage = {
  id: '0',
  menuCode: 'root',
  parentId: '0',
  menuSort: 0,
  menuType: MenuType.GROUP,
  menuName: '根目录',
  menuIcon: '',
  isVisible: 1,
  children: [] as ApplicationMenu[]
};

export interface CreateApplicationMenuReq {
  applicationId: string;
  parentId?: string;
  pageSetType?: number;
  menuName: string;
  menuType: MenuType;
  menuIcon: string;
  entityId?: string;
  pageType?: string;
}

export interface UpdateApplicationMenuNameReq {
  id: string;
  menuName: string;
  menuIcon?: string;
}

export interface UpdateApplicationMenuOrderReq {
  id?: string;
  parentId?: string;
  /**
   * 菜单顺序树结构
   */
  menuTree?: MenuOrderNode[];
}

export interface MenuOrderNode {
  id?: string;
  children?: MenuOrderNode[];
  [property: string]: any;
}

export interface UpdateApplicationMenuVisibleReq {
  id: string;
  visible: number;
}

export interface CopyApplicationMenuReq {
  id: string;
  menuName: string;
  parentId?: string;
}

export interface DeleteApplicationMenuReq {
  id: string;
}

export interface GetApplicationMenuReq {
  id: string;
}
