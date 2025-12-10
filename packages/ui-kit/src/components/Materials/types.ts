import type { CONFIG_TYPES } from './constants';

// TODO(mickey): 后续扩展配置
export type EditConfig = any;

export type TTextDefaultType = string;
export type TNumberDefaultType = number;
export type TTextAreaDefaultType = string;
export type TSelectDefaultType<KeyType> = KeyType;
export type TRadioDefaultType<KeyType> = KeyType;
export type TBooleanDefaultType = boolean;

/**
 * 默认基础配置
 * */

export interface ICommonConfigType {
   key: string;
   name: string;
   type: string;
   /** 配置通用 key value类型 */
   [key: string]: any;
}

// 文本输入框配置
export interface ITextConfigType {
  key: string;
  name: string;
  type: typeof CONFIG_TYPES.TEXT_INPUT;
  placeholder?: string;
}

// 数字输入框配置
export interface INumberConfigType {
  key: string;
  name: string;
  type: typeof CONFIG_TYPES.NUMBER_INPUT;
  range?: [number, number];
  step?: number;
}

// 文本区域输入框配置
export interface ITextAreaConfigType {
  key: string;
  name: string;
  type: typeof CONFIG_TYPES.TEXT_AREA_INPUT;
}

// 下拉框配置
export interface ISelectConfigType<KeyType> {
  key: string;
  name: string;
  type: typeof CONFIG_TYPES.SELECT_INPUT;
  range?: Array<{
    key: string;
    text: string;
    value: KeyType;
  }>;
}

export interface IDynamicSelectConfigType {
  key: string;
  name: string;
  type: typeof CONFIG_TYPES.DYNAMIC_SELECT_INPUT;
}

export interface IDataFieldConfigType {
  key: string;
  name: string;
  type: typeof CONFIG_TYPES.FIELD_DATA;
}

export interface IRelatedFormDataConfigType {
  key: string;
  name: string;
  type: typeof CONFIG_TYPES.RELATED_FORM_DATA;
}

export interface ITableDataConfigType {
  key: string;
  name: string;
  type: typeof CONFIG_TYPES.TABLE_DATA;
  advanced?: boolean;
}

export interface IRadioDataConfigType {
  key: string;
  name: string;
  type: typeof CONFIG_TYPES.RADIO_DATA;
}

export interface ICheckboxDataConfigType {
  key: string;
  name: string;
  type: typeof CONFIG_TYPES.CHECKBOX_DATA;
}

// TODO(mickey): remove
// export interface ISearchItemListConfigType {
//   key: string;
//   name: string;
//   type: typeof CONFIG_TYPES.SEARCH_ITEM_LIST;
// }

export interface IBooleanConfigType {
  key: string;
  name: string;
  type: typeof CONFIG_TYPES.SWITCH_INPUT;
  advanced?: boolean;
}

/**
 * 自定义配置
 * */

// Label配置
export interface ILabelConfigType {
  key: string;
  name: string;
  type: typeof CONFIG_TYPES.LABEL_INPUT;
  placeholder?: string;
}

// 占位符配置
export interface IPlaceholderConfigType {
  key: string;
  name: string;
  type: typeof CONFIG_TYPES.PLACEHOLDER_INPUT;
  placeholder?: string;
}

// 提示配置
export interface ITooltipConfigType {
  key: string;
  name: string;
  type: typeof CONFIG_TYPES.TOOLTIP_INPUT;
  placeholder?: string;
}

// 状态配置
export interface IStatusConfigType<KeyType> {
  key: string;
  name: string;
  type: typeof CONFIG_TYPES.STATUS_RADIO;
  range: Array<{
    key: string;
    text: string;
    value: KeyType;
  }>;
}

// 数据选择方式配置（下拉框/弹窗）
export interface IDataSelectModeConfigType {
  key: string;
  name: string;
  type: typeof CONFIG_TYPES.DATA_SELECT_MODE;
  range: Array<any>;
}

export interface IImageConfigType {
  key: string;
  name: string;
  type: typeof CONFIG_TYPES.IMAGE;
}
export interface IFileConfigType {
  key: string;
  name: string;
  type: typeof CONFIG_TYPES.FILE;
}

// 宽度配置
export interface IWidthConfigType<KeyType> {
  key: string;
  name: string;
  type: typeof CONFIG_TYPES.WIDTH_RADIO;
  range: Array<{
    key: string;
    text: string;
    value: KeyType;
  }>;
}

// 是否必填配置
export interface IRequiredCheckboxConfigType {
  key: string;
  name: string;
  type: typeof CONFIG_TYPES.REQUIRED_CHECKBOX;
}

// 布局列数配置
export interface IColumnCountConfigType<KeyType> {
  key: string;
  name: string;
  type: typeof CONFIG_TYPES.COLUMN_COUNT_RADIO;
  range: Array<{
    key: string;
    text: string;
    value: KeyType;
  }>;
}

// TODO(mickey): remove
// export interface ITableColumnConfigType {
//   key: string;
//   name: string;
//   type: typeof CONFIG_TYPES.TABLE_COLUMN_LIST;
// }

// 状态配置
export interface ITablePagePositionConfigType<KeyType> {
  key: string;
  name: string;
  type: typeof CONFIG_TYPES.TABLE_PAGE_POSITION_RADIO;
  range: Array<{
    key: string;
    text: string;
    value: KeyType;
  }>;
}

// 表格分页
export interface ITablePageSizeConfigType {
  key: string;
  name: string;
  type: typeof CONFIG_TYPES.TABLE_PAGE_SIZE;
}

// 文件上传大小限制
export interface IUploadSizeConfigType {
  key: string;
  name: string;
  type: typeof CONFIG_TYPES.UPLOAD_SIZE;
}

// 文件上传数量限制
export interface IUploadLimitConfigType {
  key: string;
  name: string;
  type: typeof CONFIG_TYPES.UPLOAD_LIMIT;
}

// 图片压缩率
export interface IUploadCompressConfigType {
  key: string;
  name: string;
  type: typeof CONFIG_TYPES.UPLOAD_COMPRESS;
}

// 日期格式
export interface IDateTypeConfigType<KeyType> {
  key: string;
  name: string;
  type: typeof CONFIG_TYPES.DATE_TYPE;
  range: Array<{
    key: string;
    text: string;
    value: KeyType;
  }>;
}

// 表单布局方式
export interface ILayoutConfigType<KeyType> {
  key: string;
  name: string;
  type: typeof CONFIG_TYPES.FORM_LAYOUT;
  range: Array<{
    key: string;
    text: string;
    value: KeyType;
  }>;
}

// 文本对齐方式
export interface IAlignConfigType<KeyType> {
  key: string;
  name: string;
  type: typeof CONFIG_TYPES.TEXT_ALIGN;
  range: Array<{
    key: string;
    text: string;
    value: KeyType;
  }>;
}

// 颜色配置
export interface IColorConfigType {
  key: string;
  name: string;
  type: typeof CONFIG_TYPES.COLOR;
  placeholder?: string;
}

// 日期
export interface IDateConfigType {
  key: string;
  name: string;
  type: typeof CONFIG_TYPES.DATE_INPUT;
}

// 轮播图配置
export interface ICarouselConfigType {
  key: string;
  name: string;
  type: typeof CONFIG_TYPES.CAROUSEL;
}

// 安全配置
export interface ISecurityConfigType {
  key: string;
  name: string;
  type: typeof CONFIG_TYPES.SECURITY;
}

// 校验配置
export interface IVerifyConfigType {
  key: string;
  name: string;
  type: typeof CONFIG_TYPES.VERIFY;
}

export interface INumberFormatConfigType {
  key: string;
  name: string;
  type: typeof CONFIG_TYPES.NUMBER_FORMAT;
}

// 文本输入框配置
// export interface ISupportFileTypeConfigType {
//   key: string;
//   name: string;
//   type: typeof CONFIG_TYPES.SUPPORT_FILE_TYPE;
//   placeholder?: string;
// }

export interface ISelectOptionsConfigType {
  key: string;
  name: string;
  type: typeof CONFIG_TYPES.SELECT_OPTIONS_INPUT;
  placeholder?: string;
}
export interface IMutipleSelectOptionsConfigType {
  key: string;
  name: string;
  type: typeof CONFIG_TYPES.MUTIPLE_SELECT_OPTIONS_INPUT;
  placeholder?: string;
}

// 数据选择 数据源
export interface ISelectDataSourceConfigType {
  key: string;
  name: string;
  type: typeof CONFIG_TYPES.SELECT_DATA_SOURCE;
}

// 子表子组件配置
export interface ISubTableConfigType {
  key: string;
  name: string;
  type: typeof CONFIG_TYPES.SUB_TABLE;
}

// 页签组件配置
export interface ITabsConfigType {
  key: string;
  name: string;
  type: typeof CONFIG_TYPES.TABS;
}

// 页签组件类型
export interface ITabsTypeConfigType<KeyType> {
  key: string;
  name: string;
  type: typeof CONFIG_TYPES.TABS_TYPE;
  range: Array<{
    key: string;
    label: string;
    value: KeyType;
  }>;
}

// 页签组件位置
export interface ITabsPositionConfigType<KeyType> {
  key: string;
  name: string;
  type: typeof CONFIG_TYPES.TABS_POSITION;
  range: Array<{
    key: string;
    label: string;
    value: KeyType;
  }>;
}

// 折叠配置
export interface ICollapsedConfigType<KeyType> {
  key: string;
  name: string;
  type: typeof CONFIG_TYPES.COLLAPSED;
  range: Array<{
    key: string;
    text: string;
    value: KeyType;
  }>;
}
export interface ICollapsedStyleConfig {
  key: string;
  name: string;
  type: typeof CONFIG_TYPES.COLLAPSED_STYLE;
}

export interface ITableOperationConfigType {
  key: string;
  name: string;
  type: typeof CONFIG_TYPES.TABLE_OPERATION;
  advanced?: boolean;
}

// 按钮权限配置
export interface ITableButtonConfigType<KeyType> {
  key: string;
  name: string;
  type: typeof CONFIG_TYPES.TABLE_BUTTON;
  advanced: boolean;
  range: Array<{
    key: string;
    text: string;
    value: KeyType;
  }>;
}

// 自动编号规则配置
export interface IAutoCodeConfigType {
  key: string;
  name: string;
  type: typeof CONFIG_TYPES.AUTO_CODE_RULES;
}

export interface IImageHandleConfigType {
  key: string;
  name: string;
  type: typeof CONFIG_TYPES.IMAGE_HANDLE;
}

// 日期格式
export interface IDateFormatConfigType {
  key: string;
  name: string;
  type: typeof CONFIG_TYPES.DATE_FORMAT;
  range?: Array<{
    label: string;
    value: KeyType;
  }>;
}

// 日期可选范围
export interface IDateRangeConfigType {
  key: string;
  name: string;
  type: typeof CONFIG_TYPES.DATE_RANGE;
}

// 时间格式
export interface ITimeFormatConfigType {
  key: string;
  name: string;
  type: typeof CONFIG_TYPES.TIME_FORMAT;
  range?: Array<{
    label: string;
    value: KeyType;
  }>;
}
// 时间可选范围
export interface ITimeRangeConfigType {
  key: string;
  name: string;
  type: typeof CONFIG_TYPES.TIME_RANGE;
}


// 填充文本 switch
export interface ISwitchFillTextConfigType {
  key: string;
  name: string;
  type: typeof CONFIG_TYPES.SWITCH_FILL_TEXT;
}

// 默认值
export interface IDefaultValueConfigType {
  key: string;
  name: string;
  type: typeof CONFIG_TYPES.DEFAULT_VALUE;
  valueType?: string;
}
// 电话类型
export interface IPhoneType {
  key: string;
  name: string;
  type: typeof CONFIG_TYPES.PHONE_TYPE;
  range?: Array<{
    label: string;
    value: string;
  }>;
}

// 分割线字段描述
export interface IDividerTooltipConfigType {
  key: string;
  name: string;
  type: typeof CONFIG_TYPES.DIVIDER_TOOLTIP_INPUT;
}

// 分割线样式
export interface IDividerStyleTypeConfigType {
  key: string;
  name: string;
  type: typeof CONFIG_TYPES.DIVIDER_STYLE_TYPE;
}