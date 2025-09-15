import {
  baseConfig, baseDefault, widthConfig, statusConfig, layoutConfig, childrenTableConfig, labelColSpanConfig, type ICommonBaseType,
  type TStatusSelectKeyType,
  type TLayoutSelectKeyType,

} from '../../../common';
import {
  COLUMN_COUNT_OPTIONS,
  COLUMN_COUNT_VALUES,
  CONFIG_TYPES,
  WIDTH_OPTIONS,
  WIDTH_VALUES,
  STATUS_OPTIONS,
  STATUS_VALUES,
  LAYOUT_OPTIONS,
  LAYOUT_VALUES,
} from '../../../constants';
import type {
  IColumnCountConfigType,
  ILabelConfigType,
  ISelectConfigType,
  ITextConfigType,
  IWidthConfigType,
  TRadioDefaultType,
  TSelectDefaultType,
  TTextDefaultType,
  TNumberDefaultType,
  TBooleanDefaultType,
  IStatusConfigType,
  TTextAreaDefaultType,
  IVerifyConfigType,
  ILayoutConfigType,
  ITooltipConfigType,
  INumberConfigType,
  IChildrenTableConfigType,
} from '../../../types';

export type TColumnCountSelectKeyType = (typeof COLUMN_COUNT_OPTIONS)[keyof typeof COLUMN_COUNT_OPTIONS];
export type TColumnLayouTWidthSelectKeyType = (typeof WIDTH_VALUES)[keyof typeof WIDTH_VALUES];

export type TColumnLayoutEditData = Array<
  | ITextConfigType
  | IColumnCountConfigType<TColumnCountSelectKeyType>
  | IWidthConfigType<TColumnLayouTWidthSelectKeyType>
  | ILabelConfigType
  | ISelectConfigType<TColumnLayouTWidthSelectKeyType
    | TColumnCountSelectKeyType>
  | IStatusConfigType<TStatusSelectKeyType>
  | IVerifyConfigType
  | ILayoutConfigType<TLayoutSelectKeyType>
  | ITooltipConfigType
  | INumberConfigType
  | IChildrenTableConfigType
>;

export interface XChildrenTableConfig extends ICommonBaseType {
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
   * 描述信息（鼠标悬浮时显示）
   */
  tooltip?: TTextAreaDefaultType;

  /**
   * 列数
   */
  colCount: TRadioDefaultType<TColumnCountSelectKeyType>;

  /**
   * 布局宽度
   */
  width: TSelectDefaultType<TColumnLayouTWidthSelectKeyType>;

  /**
   * 标题宽度
   */
  labelColSpan?: TNumberDefaultType;

  /**
   * 组件状态：可用、隐藏、只读
   * 可选值: 'default' | 'hidden' | 'readonly'
   */
  status?: TSelectDefaultType<TStatusSelectKeyType>;

  /**
   * required：是否必填，未填写时提交报错
   * noRepeat: 不允许重复
   * maxLength：子字段长度
   * allowNull：子字段空行校验
   */
  verify: {
    required: TBooleanDefaultType;
    noRepeat: TBooleanDefaultType;
    maxLength: TTextDefaultType;
    allowNull: TBooleanDefaultType;
  }

  /**
   * 表单的布局：水平、垂直（默认）
   * 可选值: 'vertical' | 'horizontal'
   */
  layout?: TLayoutSelectKeyType;
}

export interface XChildrenTableSchema {
  editData: TColumnLayoutEditData;
  config: XChildrenTableConfig;
}

const XChildrenTable: XChildrenTableSchema = {
  editData: [
    ...baseConfig,
    {
      key: 'label',
      name: '标题',
      type: CONFIG_TYPES.LABEL_INPUT
    },
    {
      key: 'tooltip',
      name: '描述信息',
      type: CONFIG_TYPES.TOOLTIP_INPUT
    },
    childrenTableConfig,
    labelColSpanConfig,
    layoutConfig,
    {
      key: 'verify',
      name: '校验',
      type: CONFIG_TYPES.VERIFY
    },
    // {
    //   key: 'colCount',
    //   name: '列数',
    //   type: CONFIG_TYPES.COLUMN_COUNT_RADIO,
    //   range: [
    //     {
    //       key: String(COLUMN_COUNT_OPTIONS.ONE),
    //       text: String(COLUMN_COUNT_OPTIONS.ONE),
    //       value: COLUMN_COUNT_VALUES[COLUMN_COUNT_OPTIONS.ONE]
    //     },
    //     {
    //       key: String(COLUMN_COUNT_OPTIONS.TWO),
    //       text: String(COLUMN_COUNT_OPTIONS.TWO),
    //       value: COLUMN_COUNT_VALUES[COLUMN_COUNT_OPTIONS.TWO]
    //     },
    //     {
    //       key: String(COLUMN_COUNT_OPTIONS.THREE),
    //       text: String(COLUMN_COUNT_OPTIONS.THREE),
    //       value: COLUMN_COUNT_VALUES[COLUMN_COUNT_OPTIONS.THREE]
    //     },
    //     {
    //       key: String(COLUMN_COUNT_OPTIONS.FOUR),
    //       text: String(COLUMN_COUNT_OPTIONS.FOUR),
    //       value: COLUMN_COUNT_VALUES[COLUMN_COUNT_OPTIONS.FOUR]
    //     }
    //   ]
    // },
    statusConfig,
    widthConfig
  ],
  config: {
    ...baseDefault,
    label: {
      text: '子表单',
      display: true,
    },
    tooltip: '',
    colCount: COLUMN_COUNT_VALUES[COLUMN_COUNT_OPTIONS.ONE],
    layout: LAYOUT_VALUES[LAYOUT_OPTIONS.HORIZONTAL],
    labelColSpan: 100,
    width: WIDTH_VALUES[WIDTH_OPTIONS.FULL],
    status: STATUS_VALUES[STATUS_OPTIONS.DEFAULT],
    verify: {
      required: false,
      noRepeat: false,
      maxLength: '',
      allowNull: false
    }
  }
};

export default XChildrenTable;
