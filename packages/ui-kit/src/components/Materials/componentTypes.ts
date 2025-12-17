/**
 * 组件类型常量
 * 统一管理所有组件类型字符串，提升代码可维护性
 */

import {
  buildFormComponentTypes,
  buildLayoutComponentTypes,
  buildListComponentTypes,
  buildShowComponentTypes,
  buildAllComponentTypes
} from './registry';

export const FORM_COMPONENT_TYPES = buildFormComponentTypes();

const BASE_LAYOUT_COMPONENT_TYPES = buildLayoutComponentTypes();
export const LAYOUT_COMPONENT_TYPES = {
  ...BASE_LAYOUT_COMPONENT_TYPES,
  PREVIEW_COLUMN_LAYOUT: 'XPreviewColumnLayout',
  PREVIEW_TABS_LAYOUT: 'XPreviewTabsLayout',
  PREVIEW_COLLAPSE_LAYOUT: 'XPreviewCollpaseLayout'
} as Record<string, string> & {
  readonly PREVIEW_COLUMN_LAYOUT: 'XPreviewColumnLayout';
  readonly PREVIEW_TABS_LAYOUT: 'XPreviewTabsLayout';
  readonly PREVIEW_COLLAPSE_LAYOUT: 'XPreviewCollpaseLayout';
};

export const LIST_COMPONENT_TYPES = buildListComponentTypes();

export const SHOW_COMPONENT_TYPES = buildShowComponentTypes();

// 列表组件类型
export const ENTITY_COMPONENT_TYPES = {
  MAIN_ENTITY: 'XMainEntity',
  SUB_ENTITY: 'XSubEntity'
} as const;

export const ALL_COMPONENT_TYPES = buildAllComponentTypes();

export const COMPONENT_TYPE_VALUES = Object.values(ALL_COMPONENT_TYPES);

export type ComponentType = string;
