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
  IPlaceholderConfigType,
  ISecurityConfigType,
  IStatusConfigType,
  ITooltipConfigType,
  IVerifyConfigType,
  IWidthConfigType,
  TBooleanDefaultType,
  TSelectDefaultType,
  TTextAreaDefaultType,
  TTextDefaultType,
  TRadioDefaultType,
  IDefaultValueConfigType,
  ITextConfigType,
  IDividerTooltipConfigType,
  IDividerStyleTypeConfigType
} from '../../../types';

export interface XDividerSchema {
  editData: TXDividerEditData;
  config: XDividerConfig;
}

export type TXDividerEditData = Array<
  | ILabelConfigType
  | IDividerTooltipConfigType
  | IDividerStyleTypeConfigType
  | IWidthConfigType<TWidthSelectKeyType>
>;

export interface XDividerConfig extends ICommonBaseType {
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
   * 描述信息
   */
  tooltip?: {
    text: TTextDefaultType;
    display: TBooleanDefaultType;
  };

  /**
   * 样式
   */
  styleType: TTextDefaultType;

  /**
   * 配色
  */
  color: TTextDefaultType;

  /**
   * 标题颜色
  */
  titleColor: TTextDefaultType;

  /**
   * 描述颜色
   */
  descriptionColor: TTextDefaultType;

  /**
   * 字段宽度
   */
  width: TRadioDefaultType<TWidthSelectKeyType>;
}

const XDivider: XDividerSchema = {
  editData: [
    {
      key: 'label',
      name: '标题',
      type: CONFIG_TYPES.LABEL_INPUT
    },
    {
      key: 'tooltip',
      name: '字段描述',
      type: CONFIG_TYPES.DIVIDER_TOOLTIP_INPUT
    },
    {
      key: 'styleType',
      name: '样式',
      type: CONFIG_TYPES.DIVIDER_STYLE_TYPE
    },
    // 字段宽度
    widthConfig,
  ],
  config: {
    ...baseDefault,
    label: {
      text: '分割线',
      display: true
    },
    tooltip: {
      text: '',
      display: true
    },
    styleType: 'style1',
    color: '#C9CDD4',
    titleColor: '#1D2129',
    descriptionColor: '#86909C',
    width: WIDTH_VALUES[WIDTH_OPTIONS.HALF],
  }
};

export default XDivider;
