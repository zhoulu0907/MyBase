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
  INumberFormatConfigType,
  IPlaceholderConfigType,
  ISecurityConfigType,
  ISelectConfigType,
  IStatusConfigType,
  ITextAreaConfigType,
  ITextConfigType,
  ITooltipConfigType,
  IVerifyConfigType,
  IWidthConfigType,
  TBooleanDefaultType,
  TNumberDefaultType,
  TSelectDefaultType,
  TTextAreaDefaultType,
  TTextDefaultType
} from '../../../types';

export interface XInputNumberSchema {
  editData: TXInputNumberEditData;
  config: XInputNumberConfig;
}

export type TXInputNumberEditData = Array<
  | ITextConfigType
  | ILabelConfigType
  | IPlaceholderConfigType
  | ITooltipConfigType
  | IStatusConfigType<TStatusSelectKeyType>
  | IWidthConfigType<TWidthSelectKeyType>
  | INumberConfigType
  | ISelectConfigType<TWidthSelectKeyType | TStatusSelectKeyType>
  | ITextAreaConfigType
  | IBooleanConfigType
  | IStatusConfigType<TAlignSelectKeyType>
  | ILayoutConfigType<TLayoutSelectKeyType>
  | IAlignConfigType<TAlignSelectKeyType>
  | IColorConfigType
  | IDataFieldConfigType
  | ISecurityConfigType
  | IVerifyConfigType
  | INumberFormatConfigType
>;

export interface XInputNumberConfig extends ICommonBaseType {
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
  status?: TSelectDefaultType<TStatusSelectKeyType>;

  /**
   * 默认值
   */
  defaultValue?: TTextDefaultType;

  /**
   * 字段宽度
   */
  width: TSelectDefaultType<TWidthSelectKeyType>;

  /**
   * required：是否必填，未填写时提交报错
   * min：最小值，默认：0
   * max：最大值，默认：100
   */
  verify: {
    required: TBooleanDefaultType;
    min: TNumberDefaultType;
    max: TNumberDefaultType;
  };

  numberFormat: {
    showUnit: TBooleanDefaultType;
    unitValue: TTextDefaultType;
    // 保留小数点位数
    showPrecision: TBooleanDefaultType;
    // 小数点位数
    precision: TNumberDefaultType;
    // 是否显示为百分比
    showPercent: TBooleanDefaultType;
    // 是否应用千分位分隔符
    useThousandsSeparator: TBooleanDefaultType;
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
   * 文本颜色
   */
  color?: TTextDefaultType;

  /**
   * 背景颜色
   */
  bgColor?: TTextDefaultType;

  /**
   * 数字步长：默认1
   */
  step: TNumberDefaultType;

  /**
   * 隐藏时是否提交数据，开启后隐藏状态仍会保存值
   */
  saveWithHidden?: TBooleanDefaultType;

  /**
   * 标题宽度
   */
  labelColSpan?: TNumberDefaultType;

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

const XInputNumber: XInputNumberSchema = {
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
      name: '占位符',
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
      key: 'step',
      name: '数字步长',
      type: CONFIG_TYPES.NUMBER_INPUT
    },
    {
      key: 'verify',
      name: '校验',
      type: CONFIG_TYPES.VERIFY
    },
    {
      key: 'numberFormat',
      name: '格式',
      type: CONFIG_TYPES.NUMBER_FORMAT
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
      text: '数字录入',
      display: true
    },
    dataField: [],
    placeholder: '请输入数字',
    tooltip: '',
    width: WIDTH_VALUES[WIDTH_OPTIONS.HALF],
    status: STATUS_VALUES[STATUS_OPTIONS.DEFAULT],
    defaultValue: '',
    align: ALIGN_VALUES[ALIGN_OPTIONS.LEFT],
    layout: LAYOUT_VALUES[LAYOUT_OPTIONS.VERTICAL],
    step: 1,
    saveWithHidden: false,
    color: '',
    bgColor: '',
    labelColSpan: 200,
    security: {
      display: false,
      type: ''
    },
    verify: {
      required: false,
      min: 0,
      max: Infinity
    },
    numberFormat: {
      showUnit: false,
      unitValue: '',
      showPrecision: false,
      precision: 0,
      showPercent: false,
      useThousandsSeparator: false
    }
  }
};

export default XInputNumber;
