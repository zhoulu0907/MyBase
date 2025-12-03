/**
 * 组件类型常量
 * 统一管理所有组件类型字符串，提升代码可维护性
 */

// 表单组件类型
export const FORM_COMPONENT_TYPES = {
  INPUT_TEXT: 'XInputText',
  INPUT_TEXTAREA: 'XInputTextArea',
  INPUT_EMAIL: 'XInputEmail',
  INPUT_PHONE: 'XInputPhone',
  INPUT_NUMBER: 'XInputNumber',
  DATE_PICKER: 'XDatePicker',
  DATE_RANGE_PICKER: 'XDateRangePicker',
  DATE_TIME_PICKER: 'XDateTimePicker',
  TIME_PICKER: 'XTimePicker',
  SWITCH: 'XSwitch',
  RADIO: 'XRadio',
  CHECKBOX: 'XCheckbox',
  SELECT_ONE: 'XSelectOne',
  SELECT_MUTIPLE: 'XSelectMutiple',
  USER_SELECT: 'XUserSelect',
  USER_MULTIPLE_SELECT: 'XUserMultipleSelect',
  DEPT_SELECT: 'XDeptSelect',
  DEPT_MULTIPLE_SELECT: 'XDeptMultipleSelect',
  FILE_UPLOAD: 'XFileUpload',
  IMG_UPLOAD: 'XImgUpload',
  AUTO_CODE: 'XAutoCode',
  RELATED_FORM: 'XRelatedForm',
  STATIC_TEXT: 'XStaticText',
  RICH_TEXT: 'XRichText',
  CAROUSEL_FORM: 'XCarouselForm',
  SUB_TABLE: 'XSubTable',
  DATA_SELECT: 'XDataSelect'
} as const;

// 布局组件类型
export const LAYOUT_COMPONENT_TYPES = {
  COLUMN_LAYOUT: 'XColumnLayout',
  PREVIEW_COLUMN_LAYOUT: 'XPreviewColumnLayout',
  TABS_LAYOUT: 'XTabsLayout',
  PREVIEW_TABS_LAYOUT: 'XPreviewTabsLayout',
  COLLAPSE_LAYOUT: 'XCollpaseLayout',
  PREVIEW_COLLAPSE_LAYOUT: 'XPreviewCollpaseLayout'
} as const;

// 列表组件类型
export const LIST_COMPONENT_TYPES = {
  TABLE: 'XTable',
  CALENDAR: 'XCalendar',
  TIMELINE: 'XTimeline',
  CAROUSEL: 'XCarousel',
  LIST: 'XList',
  COLLAPSE: 'XCollapse'
} as const;

// 展示组件类型
export const SHOW_COMPONENT_TYPES = {
  INFO_NOTICE: 'XInfoNotice',
  IMAGE: 'XImage',
  FILE: 'XFile',
  TEXT: 'XText',
  WEB_VIEW: 'XWebView',
  DIVIDER: 'XDivider',
  PLACEHOLDER: 'XPlaceholder'
} as const;

// 列表组件类型
export const ENTITY_COMPONENT_TYPES = {
  MAIN_ENTITY: 'XMainEntity',
  SUB_ENTITY: 'XSubEntity'
} as const;

// 所有组件类型
export const ALL_COMPONENT_TYPES = {
  ...FORM_COMPONENT_TYPES,
  ...LAYOUT_COMPONENT_TYPES,
  ...LIST_COMPONENT_TYPES,
  ...SHOW_COMPONENT_TYPES
} as const;

// 组件类型值数组（用于类型检查）
export const COMPONENT_TYPE_VALUES = Object.values(ALL_COMPONENT_TYPES);

// 组件类型联合类型
export type ComponentType =
  | (typeof ALL_COMPONENT_TYPES)[keyof typeof ALL_COMPONENT_TYPES]
  | (typeof ENTITY_COMPONENT_TYPES)[keyof typeof ENTITY_COMPONENT_TYPES];
