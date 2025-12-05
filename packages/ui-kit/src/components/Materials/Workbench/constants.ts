import {
  CONFIG_TYPES,
  LAYOUT_OPTIONS,
  LAYOUT_VALUES,
  STATUS_OPTIONS,
  STATUS_VALUES,
  WIDTH_OPTIONS,
  WIDTH_VALUES
} from '../constants';
import type { IBooleanConfigType } from '../types';

/**
 * Workbench 专属配置类型
 * 优先复用 Materials 的通用定义，只添加 Workbench 特有的配置类型
 */
export const WORKBENCH_CONFIG_TYPES = {
  TEXT_INPUT: CONFIG_TYPES.TEXT_INPUT,
  SWITCH_INPUT: CONFIG_TYPES.SWITCH_INPUT,
  LABEL_INPUT: CONFIG_TYPES.LABEL_INPUT,
  TOOLTIP_INPUT: CONFIG_TYPES.TOOLTIP_INPUT,
  STATUS_RADIO: CONFIG_TYPES.STATUS_RADIO,
  WIDTH_RADIO: CONFIG_TYPES.WIDTH_RADIO,
  FORM_LAYOUT: CONFIG_TYPES.FORM_LAYOUT,
  // 工作台专属配置类型
  QUICK_ENTRY: 'QuickEntry' as const,
  CAROUSEL_CONTENT: 'CarouselContent' as const,
  WB_COLOR: 'WbColor' as const,
  WB_RICH_TEXT_CONTENT: 'WbRichTextContent' as const,
  DATA_CONFIG: 'DataConfig' as const,
} as const;

/**
 * 直接复用 Materials 通用常量，避免重复维护
 * 使用别名导出，保持命名一致性
 */
export {
  STATUS_OPTIONS as WORKBENCH_STATUS_OPTIONS,
  STATUS_VALUES as WORKBENCH_STATUS_VALUES,
  WIDTH_OPTIONS as WORKBENCH_WIDTH_OPTIONS,
  WIDTH_VALUES as WORKBENCH_WIDTH_VALUES,
  LAYOUT_OPTIONS as WORKBENCH_LAYOUT_OPTIONS,
  LAYOUT_VALUES as WORKBENCH_LAYOUT_VALUES
};

/**
 * 待办中心-数据内容配置选项
 */
export const DATA_CONFIG_OPTIONS = {
  SHOW_PENDING: '待我处理',
  SHOW_CREATED: '我创建的',
  SHOW_HANDLED: '我已处理',
  SHOW_CC: '抄送我的'
} as const;

export const DATA_CONFIG_RANGE: IBooleanConfigType[] = [
  {
    key: 'showPending',
    name: DATA_CONFIG_OPTIONS.SHOW_PENDING,
    type: WORKBENCH_CONFIG_TYPES.SWITCH_INPUT
  },
  {
    key: 'showCreated',
    name: DATA_CONFIG_OPTIONS.SHOW_CREATED,
    type: WORKBENCH_CONFIG_TYPES.SWITCH_INPUT
  },
  {
    key: 'showHandled',
    name: DATA_CONFIG_OPTIONS.SHOW_HANDLED,
    type: WORKBENCH_CONFIG_TYPES.SWITCH_INPUT
  },
  {
    key: 'showCc',
    name: DATA_CONFIG_OPTIONS.SHOW_CC,
    type: WORKBENCH_CONFIG_TYPES.SWITCH_INPUT
  }
];


