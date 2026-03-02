import type {
  ILabelConfigType,
  TTextDefaultType,
  TBooleanDefaultType,
  TNumberDefaultType,
  TRadioDefaultType,
  TSelectDefaultType,
  ICommonConfigType,
  ITextConfigType,
  ICardDataConfigType,
  IDataSortByConfigType,
  IDataFilterConfigType,
  ICoverImageConfigType,
  IGroupFilterConfigType,
  IStatusConfigType,
  ILayoutConfigType,
  IWidthConfigType
} from '../../../types';
import {
  baseConfig,
  labelConfig,
  baseDefault,
  cardMetaDataConfig,
  dataSortByConfig,
  dataFilterConfig,
  coverImageConfig,
  groupFilterConfig,
  statusConfig,
  layoutConfig,
  cardWidthConfig,
  rowRedirectConfig,
  type ICommonBaseType,
  type TStatusSelectKeyType,
  type TLayoutSelectKeyType,
  type TWidthSelectKeyType,
  type TFillSelectKeyType
} from '../../../common';
import {
  LAYOUT_OPTIONS,
  LAYOUT_VALUES,
  STATUS_OPTIONS,
  STATUS_VALUES,
  WIDTH_OPTIONS,
  WIDTH_VALUES,
  RedirectMethod
} from '../../../constants';

export interface XCardSchema {
  editData: TXCardEditData;
  config: XCardConfig;
}

export type TXCardEditData = Array<
  | ICommonConfigType
  | ILabelConfigType
  | ITextConfigType
  | ICardDataConfigType
  | IDataSortByConfigType
  | IDataFilterConfigType
  | ICoverImageConfigType
  | IGroupFilterConfigType
  | IStatusConfigType<TStatusSelectKeyType>
  | ILayoutConfigType<TLayoutSelectKeyType>
  | IWidthConfigType<TWidthSelectKeyType>
>;

export interface XCardConfig extends ICommonBaseType {
  /**
   * 输入框标题
   * text：标题
   * display：是否显示
   */
  label: {
    text: TTextDefaultType;
    display: TBooleanDefaultType;
  };

  // 数据绑定
  metaData: TTextDefaultType;
  tableName: TTextDefaultType;

  /**
   * 显示字段
   */
  showFields: TBooleanDefaultType;
  columns?: any[];

  // 卡片标题字段
  titleField?: TTextDefaultType;

  /**
   * 搜索项
   */
  searchItems?: any[];

  /**
   * 封面图片
   */
  coverField: TTextDefaultType;
  imageFill: TFillSelectKeyType;

  /**
   * 排序
   */
  sortBy?: {
    fieldName: TTextDefaultType;
    rule: TNumberDefaultType;
  }[];

  /**
   * 数据选择过滤条件
   */
  filterCondition?: any;

  /**
   * 绑定分组筛选
   */
  groupFilter?: TTextDefaultType;

  /**
   * 显示状态：可用、隐藏、只读
   * 可选值: 'default' | 'hidden' | 'readonly'
   */
  status?: TRadioDefaultType<TStatusSelectKeyType>;

  /**
   * 字段布局方式：水平、垂直（默认）
   * 可选值: 'vertical' | 'horizontal'
   */
  layout?: TLayoutSelectKeyType;

  /**
   * 宽度
   */
  width: TSelectDefaultType<TWidthSelectKeyType>;

  /**
   * 卡片宽度
   */
  cardWidth: TSelectDefaultType<TWidthSelectKeyType>;

  /**
   * 行点击跳转
   */
  advancedRowRedirect?: TBooleanDefaultType;
  redirectPageId?: TTextDefaultType;
  redirectMethod?: TTextDefaultType;
}

const XCard: XCardSchema = {
  editData: [
    ...baseConfig,
    labelConfig,
    // 卡片数据配置
    cardMetaDataConfig,
    // 封面图片
    coverImageConfig,
    // 数据排序规则
    // dataSortByConfig,
    // 数据过滤
    // dataFilterConfig,
    // 绑定分组筛选
    groupFilterConfig,
    // 显示状态
    statusConfig,
    // 字段布局方式
    layoutConfig,
    // 宽度
    cardWidthConfig,
    // 点击跳转
    {
      key: 'advancedRowRedirect',
      name: '行点击跳转',
      type: 'TableData',
      advanced: true,
      showOpearate: false
    },
  ],
  config: {
    ...baseDefault,
    label: {
      text: '卡片',
      display: false
    },
    metaData: '',
    tableName: '',
    showFields: true,
    columns: [],
    titleField: '',
    searchItems: [],
    coverField: '',
    imageFill: 'fill',
    sortBy: [],
    filterCondition: [],
    groupFilter: '',
    status: STATUS_VALUES[STATUS_OPTIONS.DEFAULT],
    layout: LAYOUT_VALUES[LAYOUT_OPTIONS.VERTICAL],
    width: WIDTH_VALUES[WIDTH_OPTIONS.FULL],
    cardWidth: WIDTH_VALUES[WIDTH_OPTIONS.QUARTER],
    advancedRowRedirect: true,
    redirectPageId: '',
    redirectMethod: RedirectMethod.DRAWER,
  }
};

export default XCard;
