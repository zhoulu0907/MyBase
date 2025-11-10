import {
  alignConfig,
  baseConfig,
  baseDefault,
  dataFieldConfig,
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
  IColorConfigType,
  IDataFieldConfigType,
  ILabelConfigType,
  ILayoutConfigType,
  INumberConfigType,
  IPlaceholderConfigType,
  ISecurityConfigType,
  IStatusConfigType,
  ITextAreaConfigType,
  ITextConfigType,
  ITooltipConfigType,
  IVerifyConfigType,
  IWidthConfigType,
  TBooleanDefaultType,
  TNumberDefaultType,
  TRadioDefaultType,
  TSelectDefaultType,
  TTextAreaDefaultType,
  TTextDefaultType
} from '../../../types';

// 输入框组件的schema
export interface XInputTextSchema {
  // 可配置项
  editData: TXInputTextEditData;
  // 默认配置
  config: XInputTextConfig;
}

// 输入框组件的可配置项
export type TXInputTextEditData = Array<
  | ITextConfigType
  | ILabelConfigType
  | IPlaceholderConfigType
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
  | ISecurityConfigType
  | IVerifyConfigType
>;

export interface XInputTextConfig extends ICommonBaseType {
  /**
   * 输入框标题
   * text：标题
   * display：是否显示
   */
  label: {
    text: TTextDefaultType;
    display: TBooleanDefaultType;
  };

  /**
   * 数据字段
   */
  dataField: TTextDefaultType[];

  /**
   * 占位符
   */
  placeholder: TTextDefaultType;

  /**
   * 描述信息（鼠标悬浮时显示）
   */
  tooltip?: TTextAreaDefaultType;

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
   * required：是否必填，未填写时提交报错
   * noRepeat：是否不允许重复
   */
  verify: {
    required: TBooleanDefaultType;
    noRepeat?: TBooleanDefaultType;
  };

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

  /**
   * 文本最大长度
   */
  maxLength?: TNumberDefaultType;

  /**
   * 安全
   * display：开启
   * type：掩码类型
   */
  security: {
    display: TBooleanDefaultType;
    type?: TTextDefaultType;
  };
}

const XInputText: XInputTextSchema = {
  editData: [
    ...baseConfig,
    {
      key: 'label',
      name: '标题',
      type: CONFIG_TYPES.LABEL_INPUT
    },
    ...dataFieldConfig,
    {
      key: 'placeholder',
      name: '占位提示',
      type: CONFIG_TYPES.PLACEHOLDER_INPUT
    },
    {
      key: 'tooltip',
      name: '描述信息',
      type: CONFIG_TYPES.TOOLTIP_INPUT
    },
    {
      key: 'defaultValue',
      name: '默认值',
      type: CONFIG_TYPES.TEXT_INPUT
    },
    layoutConfig,
    labelColSpanConfig,
    {
      key: 'maxLength',
      name: '文本最大长度',
      type: CONFIG_TYPES.NUMBER_INPUT
    },
    // {
    //   key: 'saveWithHidden',
    //   name: '隐藏时提交数据',
    //   type: CONFIG_TYPES.SWITCH_INPUT
    // },
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
    {
      key: 'verify',
      name: '校验',
      type: CONFIG_TYPES.VERIFY
    },
    statusConfig,
    alignConfig,
    {
      key: 'security',
      name: '安全',
      type: CONFIG_TYPES.SECURITY
    },
    widthConfig
  ],
  config: {
    ...baseDefault,
    label: {
      text: '单行文本',
      display: true
    },
    dataField: [],
    placeholder: '请输入文字',
    tooltip: '',
    width: WIDTH_VALUES[WIDTH_OPTIONS.HALF],
    status: STATUS_VALUES[STATUS_OPTIONS.DEFAULT],
    defaultValue: '',
    align: ALIGN_VALUES[ALIGN_OPTIONS.LEFT],
    layout: LAYOUT_VALUES[LAYOUT_OPTIONS.VERTICAL],
    saveWithHidden: false,
    color: '',
    bgColor: '',
    labelColSpan: 200,
    maxLength: 200,
    security: {
      display: false,
      type: ''
    },
    verify: {
      required: false,
      noRepeat: false
    }
  }
};

export default XInputText;
