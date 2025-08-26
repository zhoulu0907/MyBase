import {
    alignConfig,
    baseConfig,
    baseDefault,
    labelColSpanConfig,
    layoutConfig,
    statusConfig,
    widthConfig,
    type ICommonBaseType,
    type TAlignSelectKeyType,
    type TLayoutSelectKeyType,
    type TStatusSelectKeyType,
    type TWidthSelectKeyType
} from '../../../common';
import {
    ALIGN_OPTIONS,
    ALIGN_VALUES,
    CONFIG_TYPES,
    LAYOUT_OPTIONS,
    LAYOUT_VALUES,
    STATUS_OPTIONS,
    STATUS_VALUES,
    WIDTH_OPTIONS,
    WIDTH_VALUES
} from '../../../constants';
import type {
    IAlignConfigType,
    IBooleanConfigType,
    ILabelConfigType,
    ILayoutConfigType,
    INumberConfigType,
    IStatusConfigType,
    ITextAreaConfigType,
    ITextConfigType,
    ITooltipConfigType,
    IWidthConfigType,
    TNumberDefaultType,
    TRadioDefaultType,
    TSelectDefaultType,
    TTextDefaultType
} from '../../../types';

// 输入框组件的schema
export interface XInputTextSchema {
  // 可配置项
  editData: TXInputTextEditData;
  // 默认配置
  config: XDividerConfig;
}

// 输入框组件的可配置项
export type TXInputTextEditData = Array<
  | ITextConfigType
  | ILabelConfigType
  | ITooltipConfigType
  | IStatusConfigType<TStatusSelectKeyType>
  | IWidthConfigType<TWidthSelectKeyType>
  | INumberConfigType
  | ITextAreaConfigType
  | IBooleanConfigType
  | IStatusConfigType<TAlignSelectKeyType>
  | ILayoutConfigType<TLayoutSelectKeyType>
  | IAlignConfigType<TAlignSelectKeyType>
>;

export interface XDividerConfig extends ICommonBaseType {
  /**
   * 输入框标题
   */
  label: TTextDefaultType;

  /**
   * 提示文字（鼠标悬浮时显示）
   */
  tooltip?: TTextDefaultType;

  /**
   * 组件状态：可用、隐藏、只读
   * 可选值: 'default' | 'hidden' | 'readonly'
   */
  status?: TRadioDefaultType<TStatusSelectKeyType>;

  /**
   * 分割线文案
   */
  defaultValue?: TTextDefaultType;

  /**
   * 字段宽度
   */
  width: TRadioDefaultType<TWidthSelectKeyType>;

  /**
   * 表单的布局：水平、垂直（默认）
   * 可选值: 'vertical' | 'horizontal'
   */
  layout?: TLayoutSelectKeyType;

  /**
   * 内容对齐方式：左、中、右
   * 可选值: 'left' | 'center' | 'right'
   */
  align?: TSelectDefaultType<TAlignSelectKeyType>;

  /**
   * 标题宽度
   */
  labelColSpan?: TNumberDefaultType;

  /**
   * 上下间距
   */
  margin?: TNumberDefaultType;
}

const XInputText: XInputTextSchema = {
  editData: [
    ...baseConfig,
    {
      key: 'label',
      name: '标题',
      type: CONFIG_TYPES.LABEL_INPUT
    },
    {
      key: 'defaultValue',
      name: '分割线文案',
      type: CONFIG_TYPES.TEXT_INPUT
    },
    {
      key: 'tooltip',
      name: '提示文字',
      type: CONFIG_TYPES.TOOLTIP_INPUT
    },
    layoutConfig,
    labelColSpanConfig,
    {
      key: 'margin',
      name: '上下间距',
      type: CONFIG_TYPES.NUMBER_INPUT
    },
    statusConfig,
    widthConfig,
    alignConfig
  ],
  config: {
    ...baseDefault,
    label: '分割线',
    tooltip: '',
    width: WIDTH_VALUES[WIDTH_OPTIONS.HALF],
    status: STATUS_VALUES[STATUS_OPTIONS.DEFAULT],
    defaultValue: '',
    align: ALIGN_VALUES[ALIGN_OPTIONS.CENTER],
    layout: LAYOUT_VALUES[LAYOUT_OPTIONS.HORIZONTAL],
    labelColSpan: 100,
    margin: 0
  }
};

export default XInputText;
