import {
    alignConfig,
    baseConfig,
    baseDefault,
    dataFieldConfig,
    labelColSpanConfig,
    layoutConfig,
    relatedFormdataFieldConfig,
    statusConfig,
    widthConfig,
    type ICommonBaseType,
    type TAlignSelectKeyType,
    type TLayoutSelectKeyType,
    type TStatusSelectKeyType,
    type TWidthSelectKeyType
} from 'src/components/Materials/common';
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
} from 'src/components/Materials/constants';
import type {
    IAlignConfigType,
    IBooleanConfigType,
    IColorConfigType,
    IDataFieldConfigType,
    IDescriptionConfigType,
    ILabelConfigType,
    ILayoutConfigType,
    INumberConfigType,
    IPlaceholderConfigType,
    IRelatedFormDataConfigType,
    IStatusConfigType,
    ITextAreaConfigType,
    ITextConfigType,
    ITooltipConfigType,
    IWidthConfigType,
    TBooleanDefaultType,
    TNumberDefaultType,
    TRadioDefaultType,
    TSelectDefaultType,
    TTextAreaDefaultType,
    TTextDefaultType
} from 'src/components/Materials/types';

// 输入框组件的schema
export interface XRelatedFormSchema {
  // 可配置项
  editData: TXRelatedFormEditData;
  // 默认配置
  config: XRelatedFormConfig;
}

// 输入框组件的可配置项
export type TXRelatedFormEditData = Array<
  | ITextConfigType
  | ILabelConfigType
  | IPlaceholderConfigType
  | IDescriptionConfigType
  | ITooltipConfigType
  | IStatusConfigType<TStatusSelectKeyType>
  | IWidthConfigType<TWidthSelectKeyType>
  | INumberConfigType
  | ITextAreaConfigType
  | IBooleanConfigType
  | IStatusConfigType<TAlignSelectKeyType>
  | ILayoutConfigType<TLayoutSelectKeyType>
  | IAlignConfigType<TAlignSelectKeyType>
  | IColorConfigType
  | IDataFieldConfigType
  | IRelatedFormDataConfigType
>;

export interface XRelatedFormConfig extends ICommonBaseType {
  /**
   * 输入框标题
   */
  label: TTextDefaultType;

  /**
   * 数据字段
   */
  dataField: TTextDefaultType[];

  /**
   * 关联表单数据字段
   */
  relatedFormDataField: TTextDefaultType[];

  /**
   * 占位符
   */
  placeholder: TTextDefaultType;

  /**
   * 描述信息（显示在输入框下方，辅助说明）
   */
  description: TTextAreaDefaultType;

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
   * 默认值
   */
  defaultValue?: TTextDefaultType;

  /**
   * 字段宽度
   */
  width: TRadioDefaultType<TWidthSelectKeyType>;

  /**
   * 是否必填，未填写时提交报错
   */
  required: TBooleanDefaultType;

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
   * 隐藏时是否提交数据，开启后隐藏状态仍会保存值
   */
  saveWithHidden?: TBooleanDefaultType;

  /**
   * 文本颜色
   */
  color?: TTextDefaultType;

  /**
   * 背景颜色
   */
  bgColor?: TTextDefaultType;

  /**
   * 标题宽度
   */
  labelColSpan?: TNumberDefaultType;
}

const XRelatedForm: XRelatedFormSchema = {
  editData: [
    ...baseConfig,
    {
      key: 'label',
      name: '标题',
      type: CONFIG_TYPES.LABEL_INPUT
    },
    {
      key: 'placeholder',
      name: '占位符',
      type: CONFIG_TYPES.PLACEHOLDER_INPUT
    },
    {
      key: 'description',
      name: '描述信息',
      type: CONFIG_TYPES.DESCRIPTION_INPUT
    },
    ...dataFieldConfig,
    ...relatedFormdataFieldConfig,
    {
      key: 'tooltip',
      name: '提示文字',
      type: CONFIG_TYPES.TOOLTIP_INPUT
    },
    layoutConfig,
    labelColSpanConfig,
    {
      key: 'required',
      name: '开启必填',
      type: CONFIG_TYPES.SWITCH_INPUT
    },
    {
      key: 'saveWithHidden',
      name: '隐藏时提交数据',
      type: CONFIG_TYPES.SWITCH_INPUT
    },
    {
      key: 'color',
      name: '文本颜色',
      type: CONFIG_TYPES.COLOR
    },
    {
      key: 'bgColor',
      name: '背景颜色',
      type: CONFIG_TYPES.COLOR
    },
    statusConfig,
    widthConfig,
    alignConfig
  ],
  config: {
    ...baseDefault,
    label: '关联表单',
    dataField: [],
    relatedFormDataField: [],
    placeholder: '请选择关联表单',
    description: '',
    tooltip: '',
    width: WIDTH_VALUES[WIDTH_OPTIONS.HALF],
    status: STATUS_VALUES[STATUS_OPTIONS.DEFAULT],
    defaultValue: '',
    required: false,
    align: ALIGN_VALUES[ALIGN_OPTIONS.LEFT],
    layout: LAYOUT_VALUES[LAYOUT_OPTIONS.HORIZONTAL],
    saveWithHidden: false,
    color: '',
    bgColor: '',
    labelColSpan: 100
  }
};

export default XRelatedForm;
