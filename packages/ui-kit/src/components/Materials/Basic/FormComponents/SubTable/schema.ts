import {
  baseConfig,
  baseDefault,
  layoutConfig,
  statusConfig,
  subTableConfig,
  widthConfig,
  type ICommonBaseType,
  type TLayoutSelectKeyType,
  type TStatusSelectKeyType
} from '../../../common';
import {
  COLUMN_COUNT_OPTIONS,
  CONFIG_TYPES,
  LAYOUT_OPTIONS,
  LAYOUT_VALUES,
  STATUS_OPTIONS,
  STATUS_VALUES,
  WIDTH_OPTIONS,
  WIDTH_VALUES
} from '../../../constants';
import type {
  IColumnCountConfigType,
  ILabelConfigType,
  ILayoutConfigType,
  INumberConfigType,
  ISelectConfigType,
  IStatusConfigType,
  ISubTableConfigType,
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

export type TColumnCountSelectKeyType = (typeof COLUMN_COUNT_OPTIONS)[keyof typeof COLUMN_COUNT_OPTIONS];
export type TColumnLayouTWidthSelectKeyType = (typeof WIDTH_VALUES)[keyof typeof WIDTH_VALUES];

export type TColumnLayoutEditData = Array<
  | ITextConfigType
  | IColumnCountConfigType<TColumnCountSelectKeyType>
  | IWidthConfigType<TColumnLayouTWidthSelectKeyType>
  | ILabelConfigType
  | ISelectConfigType<TColumnLayouTWidthSelectKeyType | TColumnCountSelectKeyType>
  | IStatusConfigType<TStatusSelectKeyType>
  | IVerifyConfigType
  | ILayoutConfigType<TLayoutSelectKeyType>
  | ITooltipConfigType
  | INumberConfigType
  | ISubTableConfigType
>;

export interface XSubTableConfig extends ICommonBaseType {
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
  };
  pageType?: string;
}

export interface XSubTableSchema {
  editData: TColumnLayoutEditData;
  config: XSubTableConfig;
}

const XSubTable: XSubTableSchema = {
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
    subTableConfig,
    // labelColSpanConfig,
    // layoutConfig,
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
      text: '子表单',
      display: true
    },
    tooltip: '',
    labelColSpan: 200,
    width: WIDTH_VALUES[WIDTH_OPTIONS.FULL],
    status: STATUS_VALUES[STATUS_OPTIONS.DEFAULT],
    verify: {
      required: false,
      noRepeat: false,
      maxLength: '',
      allowNull: false
    },
  }
};

export default XSubTable;
