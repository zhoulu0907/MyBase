import {
  baseConfig,
  baseDefault,
  dataFieldConfig,
  layoutConfig,
  statusConfig,
  widthConfig,
  alignConfig,
  timeRangeConfig,
  labelConfig,
  placeholderConfig,
  tooltipConfig,
  defaultTimeValueConfig,
  timeFormatConfig,
  verifyConfig,
  securityConfig,
  type ICommonBaseType,
  type TTimeTypeSelectKeyType,
  type TLayoutSelectKeyType,
  type TStatusSelectKeyType,
  type TAlignSelectKeyType,
  type TWidthSelectKeyType
} from '../../../common';
import {
  ALIGN_VALUES,
  ALIGN_OPTIONS,
  CONFIG_TYPES,
  LAYOUT_OPTIONS,
  LAYOUT_VALUES,
  STATUS_OPTIONS,
  STATUS_VALUES,
  WIDTH_OPTIONS,
  WIDTH_VALUES,
  TIME_VALUES,
  TIME_OPTIONS,
  DEFAULT_VALUE_TYPES
} from '../../../constants';
import type {
  IBooleanConfigType,
  ILabelConfigType,
  ILayoutConfigType,
  INumberConfigType,
  IPlaceholderConfigType,
  ISelectConfigType,
  IStatusConfigType,
  ITextAreaConfigType,
  ITextConfigType,
  ITooltipConfigType,
  IVerifyConfigType,
  IWidthConfigType,
  ICommonConfigType,
  TBooleanDefaultType,
  TNumberDefaultType,
  TSelectDefaultType,
  TTextAreaDefaultType,
  TTextDefaultType,
  ITimeFormatConfigType,
  TRadioDefaultType
} from '../../../types';

export interface XInputTimePickerSchema {
  editData: TXInputTimePickerEditData;
  config: XInputTimePickerConfig;
}

export type TXInputTimePickerEditData = Array<
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
  | ILayoutConfigType<TLayoutSelectKeyType>
  | IVerifyConfigType
  | ITimeFormatConfigType<TTimeTypeSelectKeyType>
  | ICommonConfigType
>;

export interface XInputTimePickerConfig extends ICommonBaseType {
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

  // 时间格式
  dateType: TTimeTypeSelectKeyType;
  use24Hours?: boolean;

  // 可选范围
  timeRange: {
    earliestLimit: boolean;
    earliestValue: string;
    latestLimit: boolean;
    latestValue: string;
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

const XTimePicker: XInputTimePickerSchema = {
  editData: [
    ...baseConfig,
    labelConfig,
    placeholderConfig,
    tooltipConfig,
    //  数据绑定
    ...dataFieldConfig,
    // 默认值
    defaultTimeValueConfig,
    timeFormatConfig,
    timeRangeConfig,
    verifyConfig,
    // 显示状态
    statusConfig,
    // 对齐方式
    // alignConfig,
    // 布局方式
    layoutConfig,
    securityConfig,
    // 字段宽度
    widthConfig
  ],
  config: {
    ...baseDefault,
    label: {
      text: '时间选择',
      display: true
    },
    placeholder: '请输入时间选择',
    tooltip: '',
    dataField: [],
    defaultValueConfig: {
      type: DEFAULT_VALUE_TYPES.CUSTOM,
      customValue: '',
      formulaValue: ''
    },
    dateType: TIME_VALUES[TIME_OPTIONS.SECOND],
    use24Hours: true,
    timeRange: {
      earliestLimit: false,
      earliestValue: '',
      latestLimit: false,
      latestValue: ''
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

export default XTimePicker;
