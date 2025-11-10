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
  IVerifyConfigType,
  IWidthConfigType,
  TBooleanDefaultType,
  TNumberDefaultType,
  TSelectDefaultType,
  TTextAreaDefaultType,
  TTextDefaultType
} from '../../../types';

export interface XInputSelectOneSchema {
  editData: TXInputSelectOneEditData;
  config: XInputSelectOneConfig;
}

export type TXInputSelectOneEditData = Array<
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
  | IDataFieldConfigType
  | IVerifyConfigType
  | ISelectOptionsConfigType
>;

export interface XInputSelectOneConfig extends ICommonBaseType {
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
  defaultValue?: any;
  defaultOptions: { label: string; value: any; [property: string]: any }[];

  /**
   * 字段宽度
   */
  width: TSelectDefaultType<TWidthSelectKeyType>;

  /**
   * required：是否必填，未填写时提交报错
   * noRepeat：是否允许重复值
   */
  verify: {
    required: TBooleanDefaultType;
    noRepeat: TBooleanDefaultType;
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
   * 搜索
   */
  showSearch?: TBooleanDefaultType;
}

const XSelectOne: XInputSelectOneSchema = {
  editData: [
    ...baseConfig,
    {
      key: 'label',
      name: '标题',
      type: CONFIG_TYPES.LABEL_INPUT
    },
    ...dataFieldConfig,
    selectOptionsConfig,
    {
      key: 'tooltip',
      name: '描述信息',
      type: CONFIG_TYPES.TOOLTIP_INPUT
    },
    layoutConfig,
    labelColSpanConfig,
    {
      key: 'showSearch',
      name: '开启搜索',
      type: CONFIG_TYPES.SWITCH_INPUT
    },
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
    statusConfig,
    widthConfig
  ],
  config: {
    ...baseDefault,
    label: {
      text: '下拉单选',
      display: true
    },
    dataField: [],
    tooltip: '',
    width: WIDTH_VALUES[WIDTH_OPTIONS.HALF],
    status: STATUS_VALUES[STATUS_OPTIONS.DEFAULT],
    defaultValue: undefined,
    defaultOptions: [
      {
        label: '已完成',
        value: '已完成'
      },
      {
        label: '进行中',
        value: '进行中'
      },
      {
        label: '待办',
        value: '待办'
      }
    ],
    layout: LAYOUT_VALUES[LAYOUT_OPTIONS.VERTICAL],
    saveWithHidden: false,
    labelColSpan: 200,
    showSearch: true,
    verify: {
      required: false,
      noRepeat: false
    }
  }
};

export default XSelectOne;
