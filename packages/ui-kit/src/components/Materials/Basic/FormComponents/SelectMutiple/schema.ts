import {
  baseConfig,
  baseDefault,
  dataFieldConfig,
  labelColSpanConfig,
  layoutConfig,
  selectOptionsConfig,
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
  ISelectConfigType,
  ISelectOptionsConfigType,
  IStatusConfigType,
  ITextAreaConfigType,
  ITextConfigType,
  ITooltipConfigType,
  IWidthConfigType,
  TBooleanDefaultType,
  TNumberDefaultType,
  TSelectDefaultType,
  TTextAreaDefaultType,
  TTextDefaultType,
  IVerifyConfigType
} from '../../../types';

export interface XInputSelectMutipleSchema {
  editData: TXInputSelectMutipleEditData;
  config: XInputSelectMutipleConfig;
}

export type TXInputSelectMutipleEditData = Array<
  | ITextConfigType
  | ILabelConfigType
  | IPlaceholderConfigType
  | IDescriptionConfigType
  | ITooltipConfigType
  | IStatusConfigType<TStatusSelectKeyType>
  | IWidthConfigType<TWidthSelectKeyType>
  | INumberConfigType
  | ISelectConfigType<TWidthSelectKeyType | TStatusSelectKeyType>
  | ITextAreaConfigType
  | IBooleanConfigType
  | ILayoutConfigType<TLayoutSelectKeyType>
  | IVerifyConfigType
  | IDataFieldConfigType
  | ISelectOptionsConfigType
>;

export interface XInputSelectMutipleConfig extends ICommonBaseType {
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
  defaultValue?: any;

  /**
   * 字段宽度
   */
  width: TSelectDefaultType<TWidthSelectKeyType>;

  /**
   * required：是否必填，未填写时提交报错
   * maxChecked：最大选中数量，默认：3
   */
  verify: {
    required: TBooleanDefaultType;
    maxChecked: TNumberDefaultType;
  }

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
   * 搜索
   */
  showSearch?: TBooleanDefaultType;
}

const XSelectMutiple: XInputSelectMutipleSchema = {
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
    selectOptionsConfig,
    {
      key: 'tooltip',
      name: '提示文字',
      type: CONFIG_TYPES.TOOLTIP_INPUT
    },
    layoutConfig,
    labelColSpanConfig,
    {
      key: 'showSearch',
      name: '开启搜索',
      type: CONFIG_TYPES.SWITCH_INPUT
    },
    {
      key: 'saveWithHidden',
      name: '隐藏时提交数据',
      type: CONFIG_TYPES.SWITCH_INPUT
    },
    {
      key: 'verify',
      name: '校验',
      type: CONFIG_TYPES.VERIFY
    },
    statusConfig,
    widthConfig
  ],
  config: {
    ...baseDefault,
    label: {
      text: '下拉多选',
      display: true,
    },
    dataField: [],
    description: '',
    tooltip: '',
    width: WIDTH_VALUES[WIDTH_OPTIONS.HALF],
    status: STATUS_VALUES[STATUS_OPTIONS.DEFAULT],
    defaultValue: [
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
    layout: LAYOUT_VALUES[LAYOUT_OPTIONS.HORIZONTAL],
    saveWithHidden: false,
    labelColSpan: 100,
    showSearch: true,
    verify: {
      required: false,
      maxChecked: 3
    }
  }
};

export default XSelectMutiple;
