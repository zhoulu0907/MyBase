import {
  baseConfig,
  baseDefault,
  statusConfig,
  widthConfig,
  collapsedConfig,
  type ICommonBaseType,
  type TStatusSelectKeyType,
  type TWidthSelectKeyType,
  type TCollapsedSelectKeyType
} from '../../../common';
import {
  COLUMN_COUNT_OPTIONS,
  COLUMN_COUNT_VALUES,
  CONFIG_TYPES,
  STATUS_OPTIONS,
  STATUS_VALUES,
  WIDTH_OPTIONS,
  WIDTH_VALUES,
  COLLAPSED_OPTIONS,
  COLLAPSED_VALUES
} from '../../../constants';
import type {
  IBooleanConfigType,
  ILabelConfigType,
  INumberConfigType,
  ISelectConfigType,
  IStatusConfigType,
  ITextAreaConfigType,
  ITextConfigType,
  IWidthConfigType,
  TBooleanDefaultType,
  TSelectDefaultType,
  TTextDefaultType,
  TRadioDefaultType,
  ICollapsedConfigType
} from '../../../types';

export interface XCollapseLayoutSchema {
  editData: XCollapseLayoutEditData;
  config: XCollapseLayoutConfig;
}

export type TColumnCountSelectKeyType = (typeof COLUMN_COUNT_OPTIONS)[keyof typeof COLUMN_COUNT_OPTIONS];
export type XCollapseLayoutEditData = Array<
  | ITextConfigType
  | ILabelConfigType
  | IStatusConfigType<TStatusSelectKeyType>
  | IWidthConfigType<TWidthSelectKeyType>
  | INumberConfigType
  | ISelectConfigType<TWidthSelectKeyType | TStatusSelectKeyType>
  | ITextAreaConfigType
  | IBooleanConfigType
  | ICollapsedConfigType<TCollapsedSelectKeyType>
>;

export interface XCollapseLayoutConfig extends ICommonBaseType {
  /**
   * 输入框标题
   * text：标题
   * display：是否显示
   */
  label: {
    text: TTextDefaultType;
    display: TBooleanDefaultType | null;
  };

  /**
   * 组件状态：可用、隐藏、只读
   * 可选值: 'default' | 'hidden' | 'readonly'
   */
  status?: TSelectDefaultType<TStatusSelectKeyType>;

  /**
   * 字段宽度
   */
  width: TSelectDefaultType<TWidthSelectKeyType>;

  /**
   * 列数
   */
  colCount: TRadioDefaultType<TColumnCountSelectKeyType>;

  /**
   * 默认展示样式
   */
  collapsed: TSelectDefaultType<TCollapsedSelectKeyType>;
  // 页面类型
  pageType?:string;
}

const XLCollapseLayout: XCollapseLayoutSchema = {
  editData: [
    ...baseConfig,
    {
      key: 'label',
      name: '标题',
      type: CONFIG_TYPES.LABEL_INPUT
    },
    collapsedConfig,
    statusConfig,
    widthConfig
  ],
  config: {
    ...baseDefault,
    label: {
      text: '折叠布局',
      display: null,
    },
    colCount: COLUMN_COUNT_VALUES[COLUMN_COUNT_OPTIONS.ONE],
    status: STATUS_VALUES[STATUS_OPTIONS.DEFAULT],
    width: WIDTH_VALUES[WIDTH_OPTIONS.FULL],
    collapsed: COLLAPSED_VALUES[COLLAPSED_OPTIONS.EXPOSED]
  }
};

export default XLCollapseLayout;
