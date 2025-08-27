import {
    baseConfig,
    baseDefault,
    dataFieldConfig,
    dateTypeConfig,
    labelColSpanConfig,
    layoutConfig,
    statusConfig,
    widthConfig,
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
    IDateTypeConfigType,
    IDescriptionConfigType,
    ILabelConfigType,
    ILayoutConfigType,
    INumberConfigType,
    ISelectConfigType,
    IStatusConfigType,
    ITextAreaConfigType,
    ITextConfigType,
    ITooltipConfigType,
    IWidthConfigType,
    TBooleanDefaultType,
    TNumberDefaultType,
    TSelectDefaultType,
    TTextAreaDefaultType,
    TTextDefaultType
} from '../../../types';

export interface XInputDatePickerSchema {
  editData: TXInputDatePickerEditData;
  config: XInputDatePickerConfig;
}

export type TXInputDatePickerEditData = Array<
  | ITextConfigType
  | ILabelConfigType
  | IDescriptionConfigType
  | ITooltipConfigType
  | IStatusConfigType<TStatusSelectKeyType>
  | IWidthConfigType<TWidthSelectKeyType>
  | INumberConfigType
  | ISelectConfigType<TWidthSelectKeyType | TStatusSelectKeyType>
  | ITextAreaConfigType
  | IBooleanConfigType
  | IStatusConfigType<TDateTypeSelectKeyType>
  | IDateTypeConfigType<TDateTypeSelectKeyType>
  | ILayoutConfigType<TLayoutSelectKeyType>
  | IDataFieldConfigType
>;

export interface XInputDatePickerConfig extends ICommonBaseType {
  /**
   * 输入框标题
   */
  label: TTextDefaultType;

  /**
   * 数据字段
   */
  dataField: TTextDefaultType[];

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
   * 是否必填，未填写时提交报错
   */
  required: TBooleanDefaultType;

  /**
   * 日期类型： 年、年月、年月日、年月日时
   * 可选值: 'YEAR' | 'MONTH' | 'DATE' | 'FULL'
   */
  dateType: TDateTypeSelectKeyType;

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
}

const XDatePicker: XInputDatePickerSchema = {
  editData: [
    ...baseConfig,
    {
      key: 'label',
      name: '标题',
      type: CONFIG_TYPES.LABEL_INPUT
    },
    {
      key: 'description',
      name: '描述信息',
      type: CONFIG_TYPES.DESCRIPTION_INPUT
    },
    ...dataFieldConfig,
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
    statusConfig,
    widthConfig,
    dateTypeConfig
  ],
  config: {
    ...baseDefault,
    label: '日期选择',
    dataField: [],
    description: '',
    tooltip: '',
    width: WIDTH_VALUES[WIDTH_OPTIONS.HALF],
    status: STATUS_VALUES[STATUS_OPTIONS.DEFAULT],
    defaultValue: '',
    required: false,
    dateType: DATE_VALUES[DATE_OPTIONS.DATE],
    layout: LAYOUT_VALUES[LAYOUT_OPTIONS.HORIZONTAL],
    saveWithHidden: false,
    labelColSpan: 100
  }
};

export default XDatePicker;
