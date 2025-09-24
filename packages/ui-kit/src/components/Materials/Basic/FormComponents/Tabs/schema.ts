import {
  baseConfig,
  baseDefault,
  labelColSpanConfig,
  layoutConfig,
  statusConfig,
  widthConfig,
  tabsTypeConfig,
  tabsPositionConfig,
  type ICommonBaseType,
  type TLayoutSelectKeyType,
  type TStatusSelectKeyType,
  type TWidthSelectKeyType,
  type TTabsTypeSelectKeyType,
  type TTabsPositionSelectKeyType
} from '../../../common';
import {
  CONFIG_TYPES,
  LAYOUT_OPTIONS,
  LAYOUT_VALUES,
  STATUS_OPTIONS,
  STATUS_VALUES,
  WIDTH_OPTIONS,
  WIDTH_VALUES,
  TABS_TYPE_OPTIONS,
  TABS_POSITION_OPTIONS,
  TABS_POSITION_VALUES
} from '../../../constants';
import type {
  IBooleanConfigType,
  ILabelConfigType,
  ILayoutConfigType,
  INumberConfigType,
  IPlaceholderConfigType,
  ISelectConfigType,
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
  IVerifyConfigType,
  ITabsTypeConfigType,
  ITabsPositionConfigType,
} from '../../../types';

export interface XTabsSchema {
  editData: XTabsEditData;
  config: XTabsConfig;
}

export type XTabsEditData = Array<
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
  | IVerifyConfigType
  | ITabsTypeConfigType<TTabsTypeSelectKeyType>
  | ITabsPositionConfigType<TTabsPositionSelectKeyType>
>;

export interface XTabsConfig extends ICommonBaseType {
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
   * 组件状态：可用、隐藏、只读
   * 可选值: 'default' | 'hidden' | 'readonly'
   */
  status?: TSelectDefaultType<TStatusSelectKeyType>;

  /**
   * 默认值
   */
  defaultValue?: any[];

  /**
   * 字段宽度
   */
  width: TSelectDefaultType<TWidthSelectKeyType>;

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
   * 页签类型
   */
  type?: TSelectDefaultType<TTabsTypeSelectKeyType>;

  /**
   * 页签位置
   */
  tabPosition?: TSelectDefaultType<TTabsPositionSelectKeyType>;
}

const XTabs: XTabsSchema = {
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
    {
      key: 'defaultValue',
      name: '默认值',
      type: CONFIG_TYPES.SELECT_INPUT
    },
    layoutConfig,
    labelColSpanConfig,
    tabsTypeConfig,
    tabsPositionConfig,
    {
      key: 'saveWithHidden',
      name: '隐藏时提交数据',
      type: CONFIG_TYPES.SWITCH_INPUT
    },
    statusConfig,
    widthConfig
  ],
  config: {
    ...baseDefault,
    label: {
      text: '页签组件',
      display: true,
    },
    tooltip: '',
    width: WIDTH_VALUES[WIDTH_OPTIONS.FULL],
    status: STATUS_VALUES[STATUS_OPTIONS.DEFAULT],
    defaultValue: [
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
    ],
    layout: LAYOUT_VALUES[LAYOUT_OPTIONS.HORIZONTAL],
    labelColSpan: 100,
    saveWithHidden: false,
    type: TABS_TYPE_OPTIONS.LINE,
    tabPosition: TABS_POSITION_VALUES[TABS_POSITION_OPTIONS.TOP]
  }
};

export default XTabs;
