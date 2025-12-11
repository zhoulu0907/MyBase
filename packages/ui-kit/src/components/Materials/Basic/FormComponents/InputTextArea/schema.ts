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
  WIDTH_VALUES,
  DEFAULT_VALUE_TYPES
} from '../../../constants';
import type {
  IAlignConfigType,
  IDataFieldConfigType,
  ILabelConfigType,
  ILayoutConfigType,
  INumberConfigType,
  IPlaceholderConfigType,
  ISecurityConfigType,
  IStatusConfigType,
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
  | ILabelConfigType
  | IPlaceholderConfigType
  | ITooltipConfigType
  | IDataFieldConfigType
  | IDefaultValueConfigType
  | IVerifyConfigType
  | INumberConfigType
  | IStatusConfigType<TStatusSelectKeyType>
  | IAlignConfigType<TAlignSelectKeyType>
  | ILayoutConfigType<TLayoutSelectKeyType>
  | ISecurityConfigType
  | IWidthConfigType<TWidthSelectKeyType>
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
   * 占位符
   */
  placeholder: TTextDefaultType;

  /**
   * 描述信息（鼠标悬浮时显示）
   */
  tooltip?: TTextAreaDefaultType;

  /**
   * 数据字段
   */
  dataField: TTextDefaultType[];

  /**
  * 默认值
  */
  defaultValueConfig?: any;

  /**
   * 数据校验
   * required：是否必填，未填写时提交报错
   * noRepeat：是否不允许重复
   * lengthLimit 长度范围
   * minLength 最小长度
   * maxLength 最大长度
   */
  verify: {
    required: TBooleanDefaultType;
    noRepeat?: TBooleanDefaultType;
    lengthLimit?: boolean;
    minLength?: number;
    maxLength?: number;
  };

  /**
  * 多行文本最小高度
  * 多行文本展示行数
  */
  minRows?: TNumberDefaultType;

  /**
   * 组件状态：可用、隐藏、只读
   * 可选值: 'default' | 'hidden' | 'readonly'
   */
  status?: TSelectDefaultType<TStatusSelectKeyType>;

  /**
   * 内容对齐方式：左、中、右
   * 可选值: 'left' | 'center' | 'right'
   */
  align?: TSelectDefaultType<TAlignSelectKeyType>;


  /**
   * 表单的布局：水平、垂直（默认）
   * 可选值: 'vertical' | 'horizontal'
   */
  layout?: TLayoutSelectKeyType;

  /**
  * 安全
  * display：开启
  * type：掩码类型
  */
  security: {
    display?: TBooleanDefaultType;
    type?: TTextDefaultType;
  };
  
  /**
   * 字段宽度
   */
  width: TSelectDefaultType<TWidthSelectKeyType>;
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
      max: 10,
      step: 1,
      precision: 0
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
    placeholder: '请输入文字',
    tooltip: '',
    dataField: [],
    defaultValueConfig: {
      type: DEFAULT_VALUE_TYPES.CUSTOM,
      customValue: '',
      formulaValue: ''
    },
    verify: {
      required: false,
      noRepeat: false,
      lengthLimit: false,
      minLength: 0,
      maxLength: 2000
    },
    minRows: 3,
    status: STATUS_VALUES[STATUS_OPTIONS.DEFAULT],
    align: ALIGN_VALUES[ALIGN_OPTIONS.LEFT],
    layout: LAYOUT_VALUES[LAYOUT_OPTIONS.VERTICAL],
    security: {
      display: false,
      type: ''
    },
    width: WIDTH_VALUES[WIDTH_OPTIONS.HALF],
  }
};

export default XInputTextArea;
