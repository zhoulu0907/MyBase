import {
  baseConfig,
  baseDefault,
  widthConfig,
  collapsedConfig,
  collapsedStyleConfig,
  labelConfig,
  statusHiddenConfig,
  type ICommonBaseType,
  type TStatusSelectKeyType,
  type TWidthSelectKeyType,
  type TCollapsedSelectKeyType,
  type TLayoutStatusSelectKeyType
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
  ILabelConfigType,
  ISelectConfigType,
  IStatusConfigType,
  ITextConfigType,
  IWidthConfigType,
  ICommonConfigType,
  TBooleanDefaultType,
  TSelectDefaultType,
  TTextDefaultType,
  TRadioDefaultType,
  ICollapsedConfigType,
  ICollapsedStyleConfig
} from '../../../types';

export interface XCollapseLayoutSchema {
  editData: XCollapseLayoutEditData;
  config: XCollapseLayoutConfig;
}

export type TColumnCountSelectKeyType = (typeof COLUMN_COUNT_OPTIONS)[keyof typeof COLUMN_COUNT_OPTIONS];
export type XCollapseLayoutEditData = Array<
  | ITextConfigType
  | ILabelConfigType
  | IStatusConfigType<TLayoutStatusSelectKeyType>
  | IWidthConfigType<TWidthSelectKeyType>
  | ISelectConfigType<TWidthSelectKeyType | TStatusSelectKeyType>
  | ICollapsedConfigType<TCollapsedSelectKeyType>
  | ICollapsedStyleConfig
  | ICommonConfigType
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

  collapseStyle: {
    showBordered: boolean;
    showDivider: boolean;
    titleColor: string;
    shapeColor: string;
  },

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
}

const XLCollapseLayout: XCollapseLayoutSchema = {
  editData: [
    ...baseConfig,
    labelConfig,
    collapsedStyleConfig,
    collapsedConfig,
    statusHiddenConfig,
    widthConfig
  ],
  config: {
    ...baseDefault,
    label: {
      text: '分组布局',
      display: true,
    },
    collapseStyle: {
      showBordered: false,
      showDivider: true,
      titleColor: 'rgb(var(--primary-7))',
      shapeColor: 'rgb(var(--primary-7))'
    },
    colCount: COLUMN_COUNT_VALUES[COLUMN_COUNT_OPTIONS.ONE],
    status: STATUS_VALUES[STATUS_OPTIONS.DEFAULT],
    width: WIDTH_VALUES[WIDTH_OPTIONS.FULL],
    collapsed: COLLAPSED_VALUES[COLLAPSED_OPTIONS.EXPOSED]
  }
};

export default XLCollapseLayout;
