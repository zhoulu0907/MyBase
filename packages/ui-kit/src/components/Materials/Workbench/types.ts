/**
 * Workbench 类型定义
 * 优先复用 Materials 的通用类型，只定义 Workbench 特有的类型
 */
import { WORKBENCH_CONFIG_TYPES } from './constants';

/**
 * Workbench 特有类型定义
 */

// 默认值类型
export type TWbColorDefaultType = string;

// 颜色配置
export interface IWbColorConfigType {
  key: string;
  name: string;
  type: typeof WORKBENCH_CONFIG_TYPES.WB_COLOR;
}

// 富文本内容配置
export interface IWbRichTextContentConfigType {
  key: string;
  name: string;
  type: typeof WORKBENCH_CONFIG_TYPES.WB_RICH_TEXT_CONTENT;
}

/**
 * 快捷入口标题配置
 */
export interface QuickEntryTitleConfig {
  showTitle: boolean;
  titleName: string;
  showMore: boolean;
}

/**
 * 快捷入口样式配置
 */
export interface QuickEntryStyleConfig {
  theme: string;
}

/**
 * 快捷入口项配置
 */
export interface QuickEntryEntryConfig {
  entryName: string;
  entryIcon?: string;
  entryType?: string;
  menuId?: string;
  linkAddress?: string;
  group?: string;
  entryDesc?: string;
}

/**
 * 快捷入口分组项配置
 */
export interface QuickEntryGroupItemConfig {
  groupName: string;
  entries: QuickEntryEntryConfig[];
}

/**
 * 快捷入口分组配置
 */
export interface QuickEntryGroupConfig {
  enableGroup: boolean;
  groups: QuickEntryGroupItemConfig[];
}

/**
 * 快捷入口属性配置
 */
export interface QuickEntryPropsConfig {
  titleConfig: QuickEntryTitleConfig;
  styleConfig: QuickEntryStyleConfig;
  groupConfig: QuickEntryGroupConfig;
}

/**
 * 快捷入口配置类型
 */
export interface IQuickEntryConfigType {
  key: string;
  name: string;
  type: typeof WORKBENCH_CONFIG_TYPES.QUICK_ENTRY;
}

// 工作台轮播内容配置
export interface ICarouselContentConfigType {
  key: string;
  name: string;
  type: typeof WORKBENCH_CONFIG_TYPES.CAROUSEL_CONTENT;
  meta?: Record<string, any>;
}