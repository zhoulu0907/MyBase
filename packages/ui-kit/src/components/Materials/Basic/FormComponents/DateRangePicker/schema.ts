import {
  baseConfig,
  baseDefault,
  dataFieldConfig,
  dateTypeConfig,
  labelColSpanConfig,
  layoutConfig,
  statusConfig,
  widthConfig,
  dateFormatConfig,
  type ICommonBaseType,
  type TDateTypeSelectKeyType,
  type TLayoutSelectKeyType,
  type TStatusSelectKeyType,
  type TWidthSelectKeyType
} from '../../../common';
import {
  CONFIG_TYPES,
  DATE_OPTIONS,
  DATE_VALUES,
  LAYOUT_OPTIONS,
  LAYOUT_VALUES,
  STATUS_OPTIONS,
  STATUS_VALUES,
  WIDTH_OPTIONS,
  WIDTH_VALUES
} from '../../../constants';
import type {
  IBooleanConfigType,
  IDataFieldConfigType,
  IDateConfigType,
  IDateTypeConfigType,
  ILabelConfigType,
  ILayoutConfigType,
  INumberConfigType,
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
  IDateFormatConfigType
} from '../../../types';

export interface XInputDateRangePickerSchema {
  editData: TXInputDateRangePickerEditData;
  config: XInputDateRangePickerConfig;
}

export type TXInputDateRangePickerEditData = Array<
  | ITextConfigType
  | ILabelConfigType
  | ITooltipConfigType
  | IStatusConfigType<TStatusSelectKeyType>
  | IWidthConfigType<TWidthSelectKeyType>
  | INumberConfigType
  | ISelectConfigType<TWidthSelectKeyType | TStatusSelectKeyType>
  | ITextAreaConfigType
  | IBooleanConfigType
  | ILayoutConfigType<TLayoutSelectKeyType>
  | IStatusConfigType<TDateTypeSelectKeyType>
  | IDateTypeConfigType<TDateTypeSelectKeyType>
  | IDateConfigType
  | IVerifyConfigType
  | IDataFieldConfigType
  | IDateFormatConfigType
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
   * 数据字段
   */
  dataField: TTextDefaultType[];

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
   */
  verify: {
    required: TBooleanDefaultType;
  };

  /**
   * 表单的布局：水平、垂直（默认）
   * 可选值: 'vertical' | 'horizontal'
   */
  layout?: TLayoutSelectKeyType;

  /**
   * 标题宽度
   */
  labelColSpan?: TNumberDefaultType;

  /**
   * 隐藏时是否提交数据，开启后隐藏状态仍会保存值
   */
  saveWithHidden?: TBooleanDefaultType;

  /**
   * 日期类型： 年、年月、年月日、年月日时
   * 可选值: 'YEAR' | 'MONTH' | 'DATE' | 'FULL'
   */
  dateType: TDateTypeSelectKeyType;

  /**
   * 开始时间
   */
  startTime: TTextDefaultType;

  /**
   * 结束时间
   */
  endTime: TTextDefaultType;
}

const XDateRangePicker: XInputDateRangePickerSchema = {
  editData: [
    ...baseConfig,
    {
      key: 'label',
      name: '标题',
      type: CONFIG_TYPES.LABEL_INPUT
    },
    ...dataFieldConfig,
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
    dateFormatConfig,
    // {
    //   key: 'saveWithHidden',
    //   name: '隐藏时提交数据',
    //   type: CONFIG_TYPES.SWITCH_INPUT
    // },
    {
      key: 'verify',
      name: '校验',
      type: CONFIG_TYPES.VERIFY
    },
    {
      key: 'startTime',
      name: '开始时间',
      type: CONFIG_TYPES.DATE_INPUT
    },
    {
      key: 'endTime',
      name: '结束时间',
      type: CONFIG_TYPES.DATE_INPUT
    },
    statusConfig,
    dateTypeConfig,
    widthConfig
  ],
  config: {
    ...baseDefault,
    label: {
      text: '日期区间',
      display: true
    },
    dataField: [],
    tooltip: '',
    width: WIDTH_VALUES[WIDTH_OPTIONS.HALF],
    status: STATUS_VALUES[STATUS_OPTIONS.DEFAULT],
    defaultValue: '',
    layout: LAYOUT_VALUES[LAYOUT_OPTIONS.VERTICAL],
    saveWithHidden: false,
    labelColSpan: 200,
    dateType: DATE_VALUES[DATE_OPTIONS.DATE],
    startTime: '',
    endTime: '',
    verify: {
      required: false
    }
  }
};

export default XDateRangePicker;
