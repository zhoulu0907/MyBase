import {
  alignConfig,
  baseConfig,
  baseDefault,
  widthConfig,
  type ICommonBaseType,
  type TAlignSelectKeyType,
  type TWidthSelectKeyType
} from '../../../common';
import {
  ALIGN_OPTIONS,
  ALIGN_VALUES,
  CONFIG_TYPES,
  WIDTH_OPTIONS,
  WIDTH_VALUES
} from '../../../constants';
import type {
  IAlignConfigType,
  IBooleanConfigType,
  ILabelConfigType,
  INumberConfigType,
  ITextAreaConfigType,
  ITextConfigType,
  ITooltipConfigType,
  IWidthConfigType,
  TNumberDefaultType,
  TRadioDefaultType,
  TSelectDefaultType,
  TTextDefaultType,
  TTextAreaDefaultType,
} from '../../../types';

// 输入框组件的schema
export interface XDividerSchema {
  // 可配置项
  editData: TXDividerEditData;
  // 默认配置
  config: XDividerConfig;
}

// 输入框组件的可配置项
export type TXDividerEditData = Array<
  | ITextConfigType
  | ILabelConfigType
  | ITooltipConfigType
  | IWidthConfigType<TWidthSelectKeyType>
  | INumberConfigType
  | ITextAreaConfigType
  | IBooleanConfigType
  | IAlignConfigType<TAlignSelectKeyType>
  | TTextAreaDefaultType
>;

export interface XDividerConfig extends ICommonBaseType {

  /**
   * 分隔符文案
   */
  content?: TTextDefaultType;

  /**
   * 字段宽度
   */
  width: TRadioDefaultType<TWidthSelectKeyType>;

  /**
   * 内容对齐方式：左、中、右
   * 可选值: 'left' | 'center' | 'right'
   */
  align?: TSelectDefaultType<TAlignSelectKeyType>;

  /**
   * 上下间距
   */
  margin?: TNumberDefaultType;
}

const XDivider: XDividerSchema = {
  editData: [
    ...baseConfig,
    {
      key: 'content',
      name: '分隔符文案',
      type: CONFIG_TYPES.TEXT_INPUT
    },
    {
      key: 'margin',
      name: '上下间距',
      type: CONFIG_TYPES.NUMBER_INPUT
    },
    alignConfig,
    widthConfig,
  ],
  config: {
    ...baseDefault,
    width: WIDTH_VALUES[WIDTH_OPTIONS.FULL],
    content: '分隔符',
    align: ALIGN_VALUES[ALIGN_OPTIONS.CENTER],
    margin: 0
  }
};

export default XDivider;
