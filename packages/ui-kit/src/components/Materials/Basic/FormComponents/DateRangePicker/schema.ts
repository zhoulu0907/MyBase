import {
  baseConfig,
  baseDefault,
  dataFieldConfig,
  defaultValueConfig,
  dateRangeConfig,
  layoutConfig,
  statusConfig,
  widthConfig,
  alignConfig,
  type ICommonBaseType,
  type TDateTypeSelectKeyType,
  type TLayoutSelectKeyType,
  type TStatusSelectKeyType,
  type TWidthSelectKeyType,
  type TAlignSelectKeyType,
} from '../../../common';
import {
  ALIGN_VALUES,
  ALIGN_OPTIONS,
  CONFIG_TYPES,
  DATE_OPTIONS,
  DATE_VALUES,
  LAYOUT_OPTIONS,
  LAYOUT_VALUES,
  STATUS_OPTIONS,
  STATUS_VALUES,
  WIDTH_OPTIONS,
  WIDTH_VALUES,
  DEFAULT_VALUE_TYPES,
  DATE_EXTREME_TYPE,
  DATE_DYNAMIC_TYPE,
  DATE_DYNAMIC_CUSTOM_TYPE,
  DATE_DYNAMIC_CUSTOM_VALUE_TYPE
} from '../../../constants';
import type {
  IDataFieldConfigType,
  ILabelConfigType,
  ILayoutConfigType,
  IStatusConfigType,
  ITooltipConfigType,
  IVerifyConfigType,
  IWidthConfigType,
  TBooleanDefaultType,
  TSelectDefaultType,
  TTextAreaDefaultType,
  TTextDefaultType,
  IDateFormatConfigType,
  IPlaceholderConfigType,
  IDefaultValueConfigType,
  IDateRangeConfigType,
  IAlignConfigType,
  ISecurityConfigType,
  TRadioDefaultType
} from '../../../types';
import { ManipulateType } from 'dayjs';

export interface XInputDateRangePickerSchema {
  editData: TXInputDateRangePickerEditData;
  config: XInputDateRangePickerConfig;
}

export type TXInputDateRangePickerEditData = Array<
  | ILabelConfigType
  | IPlaceholderConfigType
  | ITooltipConfigType
  | IDataFieldConfigType
  | IDefaultValueConfigType
  | IDateFormatConfigType
  | IDateRangeConfigType
  | IVerifyConfigType
  | IStatusConfigType<TStatusSelectKeyType>
  | IAlignConfigType<TAlignSelectKeyType>
  | ILayoutConfigType<TLayoutSelectKeyType>
  | ISecurityConfigType
  | IWidthConfigType<TWidthSelectKeyType>
>;

export interface XInputDateRangePickerConfig extends ICommonBaseType {
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
   * TODO 两个字段
   * 数据字段
   */
  dataField: TTextDefaultType[];

  /**
   * TODO
   * 默认值
   */
  startDefaultValueConfig?: any;
  endDefaultValueConfig?: any;

  /**
   * 日期格式： 年、年月、年月日、年月日时
   * 可选值: 'YEAR' | 'MONTH' | 'DATE' | 'FULL'
   */
  dateType: TDateTypeSelectKeyType;

  /**
   * 可选范围
   * 特定星期   全选/星期一/星期二/星期三/星期四/星期五/星期六/星期日
   * 最早可选日期  静态值、动态值、变量
   * 最晚可选日期  静态值、动态值、变量
   */
  dateRange: {
    earliestLimit: boolean;
    earliestType: string;
    earliestStaticValue: string;
    earliestDynamicValue: string;
    earliestCustomType: string;
    earliestCustomValue: number;
    earliestCustomValueType: ManipulateType;
    earliestVariableValue: string;
    latestLimit: boolean;
    latestType: string;
    latestStaticValue: string;
    latestDynamicValue: string;
    latestCustomType: string;
    latestCustomValue: number;
    latestCustomValueType: ManipulateType;
    latestVariableValue: string;
  };

  /**
  * required：是否必填，未填写时提交报错
  * noRepeat：是否不允许重复
  * lengthLimit 长度范围
  * minLength 最小长度
  * maxLength 最大长度
  */
  verify: {
    required: TBooleanDefaultType;
  };

  /**
   * 组件状态：可用、隐藏、只读
   * 可选值: 'default' | 'hidden' | 'readonly'
   */
  status?: TRadioDefaultType<TStatusSelectKeyType>;

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
    display: TBooleanDefaultType;
    type?: TTextDefaultType;
  };

  /**
   * 字段宽度
   */
  width: TRadioDefaultType<TWidthSelectKeyType>;
}

const XDateRangePicker: XInputDateRangePickerSchema = {
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
    {
      key: 'startDefaultValueConfig',
      name: '开始日期默认值',
      type: CONFIG_TYPES.DEFAULT_VALUE,
      valueType: 'date'
    },
    {
      key: 'endDefaultValueConfig',
      name: '结束日期默认值',
      type: CONFIG_TYPES.DEFAULT_VALUE,
      valueType: 'date'
    },
    {
      key: 'dateFormat',
      name: '日期格式',
      type: CONFIG_TYPES.DATE_FORMAT,
      range: [
        { label: '年', value: DATE_VALUES[DATE_OPTIONS.YEAR] },
        { label: '年-月', value: DATE_VALUES[DATE_OPTIONS.MONTH] },
        { label: '年-月-日', value: DATE_VALUES[DATE_OPTIONS.DATE] },
      ]
    },
    dateRangeConfig,
    {
      key: 'verify',
      name: '校验',
      type: CONFIG_TYPES.VERIFY
    },
    // 显示状态
    statusConfig,
    // 对齐方式
    // alignConfig,
    // 布局方式
    layoutConfig,
    {
      key: 'security',
      name: '安全',
      type: CONFIG_TYPES.SECURITY
    },
    // 字段宽度
    widthConfig,
  ],
  config: {
    ...baseDefault,
    label: {
      text: '日期区间',
      display: true
    },
    placeholder: '请选择日期',
    tooltip: '',
    dataField: [],
    startDefaultValueConfig: {
      type: DEFAULT_VALUE_TYPES.CUSTOM,
      customValue: '',
      formulaValue: ''
    },
    endDefaultValueConfig: {
      type: DEFAULT_VALUE_TYPES.CUSTOM,
      customValue: '',
      formulaValue: ''
    },
    dateType: DATE_VALUES[DATE_OPTIONS.DATE],
    dateRange: {
      earliestLimit: false,
      earliestType: DATE_EXTREME_TYPE.DYNAMIC,
      earliestStaticValue: '',
      earliestDynamicValue: DATE_DYNAMIC_TYPE.TODAY,
      earliestCustomType: DATE_DYNAMIC_CUSTOM_TYPE.CURRENT,
      earliestCustomValue: 1,
      earliestCustomValueType: DATE_DYNAMIC_CUSTOM_VALUE_TYPE.DAY,
      earliestVariableValue: '',
      latestLimit: false,
      latestType: DATE_EXTREME_TYPE.DYNAMIC,
      latestStaticValue: '',
      latestDynamicValue: DATE_DYNAMIC_TYPE.TODAY,
      latestCustomType: DATE_DYNAMIC_CUSTOM_TYPE.CURRENT,
      latestCustomValue: 1,
      latestCustomValueType: DATE_DYNAMIC_CUSTOM_VALUE_TYPE.DAY,
      latestVariableValue: ''
    },
    verify: {
      required: false,
    },
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

export default XDateRangePicker;
