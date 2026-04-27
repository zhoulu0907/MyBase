import {
  baseConfig,
  baseDefault,
  widthConfig,
  tabsConfig,
  tabsTypeConfig,
  tabsPositionConfig,
  type ICommonBaseType,
  type TWidthSelectKeyType,
  type TTabsTypeSelectKeyType,
  type TTabsPositionSelectKeyType
} from '../../../common';
import {
  WIDTH_OPTIONS,
  WIDTH_VALUES,
  TABS_TYPE_OPTIONS,
  TABS_POSITION_OPTIONS,
  TABS_POSITION_VALUES,
  COLUMN_COUNT_OPTIONS,
} from '../../../constants';
import type {
  IBooleanConfigType,
  INumberConfigType,
  IPlaceholderConfigType,
  ITextAreaConfigType,
  ITextConfigType,
  IWidthConfigType,
  TSelectDefaultType,
  ITabsConfigType,
  ITabsTypeConfigType,
  ITabsPositionConfigType,
} from '../../../types';

export interface XTabsLayoutSchema {
  editData: XTabsLayoutEditData;
  config: XTabsLayoutConfig;
}

export type TColumnCountSelectKeyType = (typeof COLUMN_COUNT_OPTIONS)[keyof typeof COLUMN_COUNT_OPTIONS];
export type XTabsLayoutEditData = Array<
  | ITextConfigType
  | IPlaceholderConfigType
  | IWidthConfigType<TWidthSelectKeyType>
  | INumberConfigType
  | ITextAreaConfigType
  | IBooleanConfigType
  | ITabsConfigType
  | ITabsTypeConfigType<TTabsTypeSelectKeyType>
  | ITabsPositionConfigType<TTabsPositionSelectKeyType>
>;

export interface XTabsLayoutConfig extends ICommonBaseType {

  /**
   * 默认值
   */
  defaultValue?: any[];

  /**
   * 字段宽度
   */
  width: TSelectDefaultType<TWidthSelectKeyType>;

  /**
   * 页签类型
   */
  type?: TSelectDefaultType<TTabsTypeSelectKeyType>;

  /**
   * 页签位置
   */
  tabPosition?: TSelectDefaultType<TTabsPositionSelectKeyType>;

  /**
   * 列数
   */
  colCount: number;
  // 页面类型
  pageType?:string;
}

const defaultValue = [
  {
    title: '标签页1',
    key: 1
  },
  {
    title: '标签页2',
    key: 2
  },
  {
    title: '标签页3',
    key: 3
  }
]

const XTabsLayout: XTabsLayoutSchema = {
  editData: [
    ...baseConfig,
    tabsConfig,
    tabsTypeConfig,
    tabsPositionConfig,
    widthConfig
  ],
  config: {
    ...baseDefault,
    width: WIDTH_VALUES[WIDTH_OPTIONS.FULL],
    defaultValue,
    type: TABS_TYPE_OPTIONS.LINE,
    colCount: defaultValue.length,
    tabPosition: TABS_POSITION_VALUES[TABS_POSITION_OPTIONS.TOP]
  }
};

export default XTabsLayout;
