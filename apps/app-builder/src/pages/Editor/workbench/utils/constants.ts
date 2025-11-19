import { WORKBENCH_COMPONENT_TYPES, WIDTH_OPTIONS, WIDTH_VALUES } from '@onebase/ui-kit';

/**
 * 工作台组件默认宽度配置
 */
export const WORKBENCH_DEFAULT_WIDTHS = {
  [WORKBENCH_COMPONENT_TYPES.QUICK_ENTRY]: WIDTH_VALUES[WIDTH_OPTIONS.FULL],
  default: WIDTH_VALUES[WIDTH_OPTIONS.HALF]
} as const;

/**
 * ReactSortable 配置
 */
export const SORTABLE_CONFIG = {
  filter: "[data-resize-handle='true']",
  preventOnFilter: false,
  sort: true,
  forceFallback: true
} as const;

/**
 * 工作台容器最小宽度百分比
 */
export const MIN_WIDTH_PERCENTAGE = 25;
