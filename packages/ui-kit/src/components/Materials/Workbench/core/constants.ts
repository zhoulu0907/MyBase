/**
 * Workbench 常量定义
 * 只包含 Workbench 特有的常量，通用常量从 Materials 引用
 */
import type { IBooleanConfigType } from './types';

// ========== 从 Materials/constants 引用通用常量 ==========
import {
  CONFIG_TYPES,
  STATUS_OPTIONS,
  STATUS_VALUES,
  WIDTH_OPTIONS,
  WIDTH_VALUES,
  LAYOUT_OPTIONS,
  LAYOUT_VALUES,
  DEFAULT_VALUE_TYPES
} from '../../constants';

// 重新导出通用常量
export {
  CONFIG_TYPES,
  STATUS_OPTIONS,
  STATUS_VALUES,
  WIDTH_OPTIONS,
  WIDTH_VALUES,
  LAYOUT_OPTIONS,
  LAYOUT_VALUES,
  DEFAULT_VALUE_TYPES
};

// ========== Workbench 专属配置类型 ==========

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
  NUMBER_INPUT: CONFIG_TYPES.NUMBER_INPUT,
  TEXT_ALIGN: CONFIG_TYPES.TEXT_ALIGN,
  COLOR: CONFIG_TYPES.COLOR,
  RADIO_DATA: CONFIG_TYPES.RADIO_DATA,
  // 工作台专属配置类型（以WB_开头）
  // 公共
  WB_SLIDER: 'Wb_Slider' as const,
  WB_COLOR: 'Wb_Color' as const,
  WB_TEXT_ALIGN: 'Wb_TextAlign' as const,
  WB_MENU_SELECTOR: 'Wb_MenuSelector' as const,
  // 快捷入口
  WB_ENTRY_GROUP: 'Wb_EntryGroup' as const,
  WB_ENTRY_STYLE: 'Wb_EntryStyle' as const,
  WB_ENTRY_TITLE: 'Wb_EntryTitle' as const,
  // 轮播图
  WB_CAROUSEL_CONTENT: 'Wb_CarouselContent' as const,
  WB_RICH_TEXT_CONTENT: 'Wb_RichTextContent' as const,
  WB_DATA_CONFIG: 'Wb_DataConfig' as const,
  // 按钮组件
  WB_JUMP_CONFIG: 'Wb_JumpConfig' as const,
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
 * 快捷入口样式选项
 */
export const QUICK_ENTRY_THEME_OPTIONS = {
  THEME_1: '样式一',
  THEME_2: '样式二',
  THEME_3: '样式三'
} as const;

export const QUICK_ENTRY_THEME_VALUES = {
  [QUICK_ENTRY_THEME_OPTIONS.THEME_1]: 'theme-one',
  [QUICK_ENTRY_THEME_OPTIONS.THEME_2]: 'theme-two',
  [QUICK_ENTRY_THEME_OPTIONS.THEME_3]: 'theme-three'
} as const;

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

// 垂直对齐方式
export const VERTICAL_ALIGN_OPTIONS = {
  TOP: 'top',
  MIDDLE: 'middle',
  BOTTOM: 'bottom'
} as const;
