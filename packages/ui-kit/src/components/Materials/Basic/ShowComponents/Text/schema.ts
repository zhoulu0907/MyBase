import {
  baseConfig,
  baseDefault,
  statusConfig,
  widthConfig,
  type ICommonBaseType,
  type TStatusSelectKeyType,
  type TWidthSelectKeyType
} from '../../../common';
import {
  CONFIG_TYPES,
  STATUS_OPTIONS,
  STATUS_VALUES,
  WIDTH_OPTIONS,
  WIDTH_VALUES
} from '../../../constants';
import type {
  IBooleanConfigType,
  IStatusConfigType,
  ITextConfigType,
  IWidthConfigType,
  TRadioDefaultType,
  TSelectDefaultType,
  TTextDefaultType,
  IColorConfigType
} from '../../../types';

export interface XTextSchema {
  editData: TXTextEditData;
  config: XTextConfig;
}

export type TXTextEditData = Array<
  ITextConfigType | IWidthConfigType<TWidthSelectKeyType> | IStatusConfigType<TStatusSelectKeyType> | IBooleanConfigType | IColorConfigType
>;

export interface XTextConfig extends ICommonBaseType {
  /**
   * 组件状态：可用、隐藏、只读
   * 可选值: 'default' | 'hidden' | 'readonly'
   */
  status?: TRadioDefaultType<TStatusSelectKeyType>;

  /**
   * 字段宽度
   */
  width: TSelectDefaultType<TWidthSelectKeyType>;

  /**
   * 文本内容
   */
  content: TTextDefaultType;

  /**
   * 文本颜色
   */
  color?: TTextDefaultType;
}

const XText: XTextSchema = {
  editData: [
    ...baseConfig,
    {
      key: 'content',
      name: '文本内容',
      type: CONFIG_TYPES.TEXT_INPUT
    },
    {
      key: 'color',
      name: '文本颜色',
      type: CONFIG_TYPES.COLOR
    },
    widthConfig,
    statusConfig
    // TODO(mickey): 补充颜色 背景色配置
  ],
  config: {
    ...baseDefault,
    content: '静态文本内容',
    width: WIDTH_VALUES[WIDTH_OPTIONS.HALF],
    status: STATUS_VALUES[STATUS_OPTIONS.DEFAULT],
    color: ''
  }
};

export default XText;
