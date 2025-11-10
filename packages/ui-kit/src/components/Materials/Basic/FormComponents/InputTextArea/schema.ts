import {
  alignConfig,
  baseConfig,
  baseDefault,
  dataFieldConfig,
  defaultValueConfig,
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
  TTextDefaultType,
  IDefaultValueConfigType
} from '../../../types';

export interface XInputTextAreaSchema {
  editData: TXInputTextAreaEditData;
  config: XInputTextAreaConfig;
}

export type TXInputTextAreaEditData = Array<
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
  | IDefaultValueConfigType
>;

export interface XInputTextAreaConfig extends ICommonBaseType {
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
  defaultValueConfig?: any;

  /**
   * 字段宽度
   */
  width: TSelectDefaultType<TWidthSelectKeyType>;

  /**
   * 数据校验
   * required：是否必填，未填写时提交报错
   * noRepeat：是否不允许重复
   */
  verify: {
    required: TBooleanDefaultType;
    noRepeat?: TBooleanDefaultType;
    lengthLimit?: boolean;
    minLength?: number;
    maxLength?: number;
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
   * 多行文本最小高度
   * 多行文本展示行数
   */
  minRows?: TNumberDefaultType;

  /**
   * 多行文本最大高度
   */
  maxRows?: TNumberDefaultType;

  /**
   * 安全
   * display：开启
   * type：掩码类型
   */
  security: {
    display?: TBooleanDefaultType;
    type?: TTextDefaultType;
  };
}

const XInputTextArea: XInputTextAreaSchema = {
  editData: [
    ...baseConfig,
    {
      key: 'label',
      name: '标题',
      type: CONFIG_TYPES.LABEL_INPUT
    },
    {
      key: 'placeholder',
      name: '占位提示',
      type: CONFIG_TYPES.PLACEHOLDER_INPUT
    },
    {
      key: 'tooltip',
      name: '字段描述',
      type: CONFIG_TYPES.TOOLTIP_INPUT
    },
    //  数据绑定
    ...dataFieldConfig,
    // 默认值
    defaultValueConfig,
    {
      key: 'verify',
      name: '校验',
      type: CONFIG_TYPES.VERIFY
    },
    {
      key: 'minRows',
      name: '文本展示行数',
      type: CONFIG_TYPES.NUMBER_INPUT,
      min: 1,
      max: 10
    },
    // 显示状态
    statusConfig,
    // 对齐方式
    alignConfig,
    // 布局方式
    layoutConfig,
    {
      key: 'security',
      name: '安全',
      type: CONFIG_TYPES.SECURITY
    },
    // 字段宽度
    widthConfig
  ],
  config: {
    ...baseDefault,
    label: {
      text: '多行文本',
      display: true
    },
    dataField: [],
    placeholder: '请输入文字',
    tooltip: '',
    labelColSpan: 200,
    width: WIDTH_VALUES[WIDTH_OPTIONS.HALF],
    status: STATUS_VALUES[STATUS_OPTIONS.DEFAULT],
    defaultValue: '',
    defaultValueConfig:{},
    align: ALIGN_VALUES[ALIGN_OPTIONS.LEFT],
    layout: LAYOUT_VALUES[LAYOUT_OPTIONS.VERTICAL],
    saveWithHidden: false,
    color: '',
    bgColor: '',
    minRows: 3,
    maxRows: 5,
    security: {
      display: false,
      type: ''
    },
    verify: {
      required: false,
      lengthLimit: false,
      minLength: 0,
      maxLength: 2000
    }
  }
};

export default XInputTextArea;
