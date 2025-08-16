// 应用类型

export interface Application {
  id: string;
  /**
   * 应用名称
   */
  appName: string;
  /**
   * 应用编码
   */
  appKey: string;
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
  appStatus: 0 | 1 | 2;
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
}
export interface ListApplicationReq {
  pageNo: number;
  pageSize: number;
  name: string;
  ownerTag?: boolean;
  orderByTime?: 'create' | 'update';
  status?: number | null;
}

/**
 * 应用状态常量
 * 0: 开发中
 * 1: 已发布
 * 2: 已发布编辑中
 */
export const AppStatus = {
  DEVELOPING: 0,      // 开发中
  PUBLISHED: 1,       // 已发布
  EDITING_AFTER_PUBLISH: 2, // 已发布编辑中
} as const;

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
   * 数据源ID
   */
  datasourceId: number;
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
  appKey: string;
  /**
   * 数据源ID
   */
  datasourceId: number;
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
  id: string,
}