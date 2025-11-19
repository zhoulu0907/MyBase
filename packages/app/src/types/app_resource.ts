import { ComponentConfig } from './app_component';
import { InteractionRule } from './view';

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
  pageSetId: string;
}

export interface GetPageSetMainMetaDataReq {
  pageSetId: string;
}

export interface GetPageListByAppIdReq {
  appId: string;
}

export interface GetPageMetadataReq {
  pageId: string;
}

export interface GetComponentListByPageIdReq {
  pageId: string;
}

export interface CreatePageViewParams {
  pageSetId: string;

  viewType: string;

  viewName: string;
}

export interface ListPageViewParams {
  pageSetId: string;
}
export enum ViewType {
  MIX = 'mix',
  EDIT = 'edit',
  DETAIL = 'detail',
  UNKNOWN = 'unknown'
}

export interface PageView {
  id: string;
  pageName: string;
  pageType: string;
  editViewMode: number;
  detailViewMode: number;
  isDefaultEditViewMode: number;
  isDefaultDetailViewMode: number;

  // 是否是新增的视图
  created?: boolean;
  //   是否最新更新的视图
  isLatestUpdated?: number;

  interactionRules?: InteractionRule[];
}
