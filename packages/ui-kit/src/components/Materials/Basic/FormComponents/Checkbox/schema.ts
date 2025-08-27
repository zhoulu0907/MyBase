import {
    baseConfig,
    baseDefault,
    dataFieldConfig,
    labelColSpanConfig,
    layoutConfig,
    statusConfig,
    widthConfig,
    type ICommonBaseType,
    type TLayoutSelectKeyType,
    type TStatusSelectKeyType,
    type TWidthSelectKeyType
} from '../../../common';
import {
    CONFIG_TYPES,
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
    IDescriptionConfigType,
    ILabelConfigType,
    ILayoutConfigType,
    INumberConfigType,
    IPlaceholderConfigType,
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

export interface XInputCheckboxSchema {
  editData: TXInputCheckboxEditData;
  config: XInputCheckboxConfig;
}

export type TXInputCheckboxEditData = Array<
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
  | ILayoutConfigType<TLayoutSelectKeyType>
  | IDataFieldConfigType
>;

export interface XInputCheckboxConfig extends ICommonBaseType {
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
   * 可选项
   */
  options: { label: string; value: string }[];

  /**
   * 全选 or 反选
   */
  allChecked?: TBooleanDefaultType;

  /**
   * 最大选中数量
   */
  maxChecked?: TNumberDefaultType;
}

const XCheckbox: XInputCheckboxSchema = {
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
    {
      key: 'allChecked',
      name: '全选',
      type: CONFIG_TYPES.SWITCH_INPUT
    },
    {
      key: 'maxChecked',
      name: '最大选中数量',
      type: CONFIG_TYPES.NUMBER_INPUT
    },
    statusConfig,
    widthConfig
  ],
  config: {
    ...baseDefault,
    label: '复选框',
    dataField: [],
    description: '',
    tooltip: '',
    width: WIDTH_VALUES[WIDTH_OPTIONS.HALF],
    status: STATUS_VALUES[STATUS_OPTIONS.DEFAULT],
    defaultValue: '',
    required: false,
    layout: LAYOUT_VALUES[LAYOUT_OPTIONS.HORIZONTAL],
    saveWithHidden: false,
    labelColSpan: 100,
    options: [
      {
        label: '选项一',
        value: '1'
      },
      {
        label: '选项二',
        value: '2'
      },
      {
        label: '选项三',
        value: '3'
      }
    ],
    allChecked: false,
    maxChecked: 9
  }
};

export default XCheckbox;
