// 应用类型

import { PUBLISH_MODULE } from '@onebase/common';

export interface Application {
  id: string;
  /**
   * 应用UID
   */
  appUid?: string;
  /**
   * 应用名称
   */
  appName: string;
  /**
   * 应用编码
   */
  appCode: string;
  /**
   * 应用描述
   */
  description?: string;
  /**
   * 图标类型
   */
  iconName?: string | null;
  /**
   * 图标颜色
   */
  iconColor?: string;
  /**
   * 应用状态文本
   */
  appStatusText?: string;
  /**
   * 应用状态, 对应AppStatus
   */
  appStatus: AppStatus;
  /**
   * 标签列表
   */
  tags?: {
    id: string;
    tagName: string;
  }[];
  /**
   * 主题色
   */
  themeColor?: string;
  /**
   * 创建时间
   */
  createTime?: string;
  /**
   * 创建人
   */
  createUser?: string;
  /**
   * 更新时间
   */
  updateTime?: string;
  /**
   * 更新人
   */
  updateUser?: string;
  /**
   * 版本号
   */
  versionNumber?: string;
  /**
   * 应用开发状态
   */
  developStatus?: string;
  /**
   * 发布模式
   */
  publishModel?: string;

  /**
   * 发布人
   */
  publisher?: string;

  /**
   * 发布时间
   */
  publishTime?: string;

  userPhotoList?: developUser[];
}

/**
 * 开发人员列表
 */
export interface developUser {
  id: string;
  avatar: string;
  nickName: string;
}

/**
 * 应用状态枚举
 * 0: 开发中
 * 1: 已发布
 * 2: 已发布编辑中
 */
export enum AppStatus {
  DEVELOPING = 0, // 开发中
  PUBLISHED = 1, // 已发布
  EDITING_AFTER_PUBLISH = 2 // 迭代中
}

export interface CreateApplicationReq {
  /**
   * 应用编码
   */
  appCode: string;
  /**
   * 应用模式
   */
  appMode?: string;
  /**
   * 应用名称
   */
  appName: string;
  /**
   * 应用描述
   */
  description?: string;
  /**
   * 图标颜色
   */
  iconColor: string;
  /**
   * 图标类型
   */
  iconName: string;
  /**
   * 标签ID
   */
  tagIds?: number[];
  /**
   * 主题色
   */
  themeColor?: string;
  /**
   * 数据源信息
   */
  datasourceSaveReq?: DatasourceSaveReqDTO;

  publishModel?: PUBLISH_MODULE;
}

/**
 * 数据源信息
 *
 * DatasourceSaveReqDTO
 */
export interface DatasourceSaveReqDTO {
  /**
   * 数据源编码
   */
  code: string;
  /**
   * 数据源配置信息
   */
  config: MapObject | string;
  /**
   * 数据源名称
   */
  datasourceName: string;
  /**
   * 数据源来源，0：系统默认，1：自有数据源，2：外部数据源
   */
  datasourceOrigin?: number;
  /**
   * 数据源类型
   */
  datasourceType: string;
  /**
   * 描述
   */
  description?: string;
  /**
   * 数据源编号
   */
  id?: string;
  /**
   * 版本锁标识
   */
  lockVersion?: number;
  /**
   * 运行模式
   */
  runMode?: number;
}

/**
 * 数据源配置信息
 *
 * MapObject
 */
export interface MapObject {
  key?: { [key: string]: any };
  [property: string]: any;
}

export interface UpdateApplicationReq {
  /**
   * 应用ID
   */
  id: string;
  /**
   * 应用模式
   */
  appMode?: string;
  /**
   * 应用名称
   */
  appName: string;
  /**
   * 应用编码
   */
  appCode: string;
  /**
   * 应用描述
   */
  description?: string;
  /**
   * 图标颜色
   */
  iconColor: string;
  /**
   * 图标类型
   */
  iconName: string;
  /**
   * 标签ID
   */
  tagIds?: number[];
  /**
   * 主题色
   */
  themeColor?: string;
}

export interface UpdateApplicationNameReq {
  /**
   * 应用ID
   */
  id: string;
  /**
   * 应用名称
   */
  name: string;
}

export interface DeleteApplicationReq {
  /**
   * 应用ID
   */
  id: string;
  /**
   * 应用名称
   */
  name: string;
}

export interface GetApplicationReq {
  /**
   * 应用ID
   */
  id: string;
}

export interface GetAppNavigationConfigReq {
  id: string;
}

export interface UpdateAppNavigationConfigReq {
  id: string;
  webDefaultMenu: string;
  webNavLayout: string;
  mobileDefaultMenu: string;
  mobileNavLayout: string;
}
