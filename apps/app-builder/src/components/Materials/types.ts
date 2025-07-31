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
    range: Array<{
      key: string;
      text: string;
      value: KeyType;
    }>;
}

export interface IBooleanConfigType  {
    key: string;
    name: string;
    type: typeof CONFIG_TYPES.SWITCH_INPUT;
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

// 描述配置
export interface IDescriptionConfigType {
    key: string;
    name: string;
    type: typeof CONFIG_TYPES.DESCRIPTION_INPUT;
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

// 默认值配置
export interface IDefaultValueConfigType {
    key: string;
    name: string;
    type: typeof CONFIG_TYPES.DEFAULT_VALUE_SELECT;
    range: Array<{
        key: string;
        text: string;
        value: KeyType;
    }>;
}

// 默认值输入配置
export interface IDefaultValueInputConfigType {
    key: string;
    name: string;
    type: typeof CONFIG_TYPES.DEFAULT_VALUE_INPUT;
    placeholder?: string;
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

export interface ITableColumnConfigType {
    key: string;
    name: string;
    type: typeof CONFIG_TYPES.TABLE_COLUMN_LIST;
}

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
