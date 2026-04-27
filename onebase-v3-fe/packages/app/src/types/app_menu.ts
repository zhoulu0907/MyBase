export interface ListApplicationMenuReq {
  applicationId: string;
  name?: string;
  isDev?: boolean;
}

export interface ApplicationMenu {
  id?: string;
  parentId?: string;
  menuCode: string;
  menuSort: number;
  menuType: number;
  menuName: string;
  pagesetType: number;
  menuIcon: string;
  menuUuid?: string;
  isVisiblePc: number;
  isVisibleMobile: number;
  children?: ApplicationMenu[];
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
  WORKBENCH = 3,
  // 仪表盘
  DASHBOARD = 4,
  // iframe 嵌入
  IFRAME = 5
}

/**
 * 页面管理-创建菜单分类
 * 覆盖：普通表单、流程表单、工作台、大屏、分组
 */
export const CREATE_MENU_CATEGORIES = {
  NORMAL_FORM: 'normal-page',
  BPM_FORM: 'bpm-page',
  WORKBENCH: 'workbench',
  SCREEN: 'screen',
  GROUP: 'group'
} as const;

export type CreateMenuCategory = typeof CREATE_MENU_CATEGORIES[keyof typeof CREATE_MENU_CATEGORIES];

export const CreateMenuCategoryLabelMap: Record<CreateMenuCategory, string> = {
  'normal-page': '新建普通表单',
  'bpm-page': '新建流程表单',
  'workbench': '新建工作台',
  'screen': '新建大屏',
  'group': '新建分组'
};

export enum CATEGORY_TYPE {
  NAVIGATE = 'navigate',
  LAYOUT = 'layout',
  FORM = 'form',
  LIST = 'list',
  SHOW = 'show',
  WORKBENCH = 'workbench'
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
  //   entityId?: string;
  entityUuid?: string;
  pageType?: string;
  createDashboardType?: string;
  dashboardId?: string;
  iframeUrl?: string;
}

export interface UpdateApplicationMenuNameReq {
  id: string;
  menuName: string;
  menuIcon?: string;
  iframeUrl?: string;
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
