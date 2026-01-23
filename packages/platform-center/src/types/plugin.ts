
export interface pluginParams {
  name?: string;
  status?: number | null;
}

export interface pluginPageParams {
  /**
   * 页码，从 1 开始
   */
  pageNo: number;
  /**
   * 每页条数，最大值为 100
   */
  pageSize: number;
  /**
   * 插件名称（模糊匹配）
   */
  pluginName?: string;
  /**
   * 状态（0停用，1启用）
   */
  status?: number;
}

export interface PluginInfoRespVO {
  /**
   * 当前版本记录ID
   */
  id: number;
  /**
   * 插件唯一标识
   */
  pluginId: string;
  /**
   * 插件名称
   */
  pluginName: string;
  /**
   * 插件图标ID
   */
  pluginIcon: number;
  /**
   * 插件描述
   */
  pluginDescription: string;
  /**
   * 当前版本号
   */
  pluginVersion: string;
  /**
   * 当前状态（0停用，1启用）
   */
  status: number;
  /**
   * 版本总数
   */
  versionCount: number;
  /**
   * 首次创建时间
   */
  createTime: string;
  /**
   * 最后更新时间
   */
  updateTime: string;
}

export interface PluginVersionVO {
  id: number;
  pluginId: string;
  version: string;
  description: string;
  createTime: string;
  updateTime: string;
  status: number; // 1: active, 0: inactive
}

export interface PluginDetailRespVO extends PluginInfoRespVO {
  versions: PluginVersionVO[];
}

export interface PageResultPluginInfoRespVO {
  /**
   * 插件列表
   */
  list: PluginInfoRespVO[];
  /**
   * 总量
   */
  total: number;
}

export interface CommonResultPageResultPluginInfoRespVO {
  /**
   * 错误码
   */
  code: number;
  /**
   * 返回数据
   */
  data: PageResultPluginInfoRespVO;
  /**
   * 错误提示，用户可阅读
   */
  msg: string;
}
