import {
  baseConfig,
  baseDefault,
  labelColSpanConfig,
  statusConfig,
  tableButtonPermissionConfig,
  tableMetaDataConfig,
  tableOperationConfig,
  widthConfig,
  type ICommonBaseType,
  type TButtonSelectKeyType,
  type TPagePositionSelectKeyType,
  type TStatusSelectKeyType,
  type TWidthSelectKeyType
} from '../../../common';
import {
  BUTTON_OPTIONS,
  BUTTON_VALUES,
  CONFIG_TYPES,
  PAGINATION_POSITION_OPTIONS,
  PAGINATION_POSITION_VALUES,
  RedirectMethod,
  STATUS_OPTIONS,
  STATUS_VALUES,
  TableOperationButton,
  WIDTH_OPTIONS,
  WIDTH_VALUES
} from '../../../constants';
import type {
  IBooleanConfigType,
  ILabelConfigType,
  INumberConfigType,
  IStatusConfigType,
  ITableButtonConfigType,
  ITableDataConfigType,
  ITableOperationConfigType,
  ITablePagePositionConfigType,
  ITablePageSizeConfigType,
  ITextConfigType,
  IWidthConfigType,
  TBooleanDefaultType,
  TNumberDefaultType,
  TRadioDefaultType,
  TSelectDefaultType,
  TTextDefaultType
} from '../../../types';

export interface XTableSchema {
  editData: TXTableEditData;
  config: XTableConfig;
}

export type TXTableEditData = Array<
  | ITextConfigType
  | ILabelConfigType
  | IWidthConfigType<TWidthSelectKeyType>
  | IStatusConfigType<TStatusSelectKeyType>
  | IBooleanConfigType
  | ITablePagePositionConfigType<TPagePositionSelectKeyType>
  | ITablePageSizeConfigType
  | ITableDataConfigType
  | INumberConfigType
  | ITableButtonConfigType<TButtonSelectKeyType>
  | ITableOperationConfigType
>;

export interface XTableConfig extends ICommonBaseType {
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
   * 默认值
   */
  defaultValue?: any[];

  /**
   * 搜索项
   */
  searchItems?: any[];

  /**
   * 默认表头
   */
  columns?: any[];

  /**
   * 是否显示边框
   */
  border?: TBooleanDefaultType;

  /**
   * 是否显示边框单元格
   */
  borderCell?: TBooleanDefaultType;

  /**
   * 是否显示斑马线
   */
  stripe?: TBooleanDefaultType;

  /**
   * 是否显示表格总数
   */
  showTotal?: TBooleanDefaultType;

  /**
   * 是否显示表头
   */
  showHeader?: TBooleanDefaultType;

  /**
   * 鼠标悬浮效果
   */
  hover?: TBooleanDefaultType;

  /**
   * 分页位置
   */
  pagePosition?: TSelectDefaultType<TPagePositionSelectKeyType>;

  /**
   * 分页数量
   */
  pageSize?: TNumberDefaultType;

  /**
   * 是否开启操作项
   */
  showOpearate?: TBooleanDefaultType;

  /**
   * 是否固定操作项
   */
  fixedOpearate?: TBooleanDefaultType;

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
   * 隐藏时是否提交数据，开启后隐藏状态仍会保存值
   */
  saveWithHidden?: TBooleanDefaultType;
  /**
   * 表头搜索项标题宽度
   */
  labelColSpan?: TNumberDefaultType;
  metaData: TTextDefaultType;

  /**
   * 行点击跳转
   */
  advancedRowRedirect?: TBooleanDefaultType;
  redirectPageId?: TTextDefaultType;
  redirectMethod?: TTextDefaultType;

  /**
   * 操作按钮显示方式：图标、文字、图标+文字
   * 可选值: 'icon' | 'text' | 'all'
   */
  operationButtonShowType?: TTextDefaultType;

  /**
   * 收入“更多”菜单：数字输入框，默认为4，可输入[1,20]的整数
   */
  operationButtonCollpaseNumber?: TNumberDefaultType;

  operationButton: OperationButtonConfig[];

  /**
   * 按钮权限配置
   * 按钮状态：隐藏、置灰
   * 可选值: 'hidden' | 'disabled'
   */
  advancedButtonPermission?: TSelectDefaultType<TButtonSelectKeyType>;

  /**
   * 排序
   */
  sortByObject?: {
    fieldName: TTextDefaultType;
    sortBy: TNumberDefaultType;
  };
}

export interface OperationButtonConfig {
  type: string;
  buttonName: string;
  buttonIcon: string;
  iconColor: string;
  redirectPageId?: string;
  redirectMethod?: string;
  confirmText?: string;
  deletedAction?: string;
  display: boolean;
}

const pagePositionConfig: ITablePagePositionConfigType<TPagePositionSelectKeyType> = {
  key: 'pagePosition',
  name: '分页位置',
  type: CONFIG_TYPES.TABLE_PAGE_POSITION_RADIO,
  range: [
    {
      key: PAGINATION_POSITION_VALUES[PAGINATION_POSITION_OPTIONS.TL],
      text: PAGINATION_POSITION_OPTIONS.TL,
      value: PAGINATION_POSITION_VALUES[PAGINATION_POSITION_OPTIONS.TL]
    },
    {
      key: PAGINATION_POSITION_VALUES[PAGINATION_POSITION_OPTIONS.TOP_CENTER],
      text: PAGINATION_POSITION_OPTIONS.TOP_CENTER,
      value: PAGINATION_POSITION_VALUES[PAGINATION_POSITION_OPTIONS.TOP_CENTER]
    },
    {
      key: PAGINATION_POSITION_VALUES[PAGINATION_POSITION_OPTIONS.TR],
      text: PAGINATION_POSITION_OPTIONS.TR,
      value: PAGINATION_POSITION_VALUES[PAGINATION_POSITION_OPTIONS.TR]
    },
    {
      key: PAGINATION_POSITION_VALUES[PAGINATION_POSITION_OPTIONS.BL],
      text: PAGINATION_POSITION_OPTIONS.BL,
      value: PAGINATION_POSITION_VALUES[PAGINATION_POSITION_OPTIONS.BL]
    },
    {
      key: PAGINATION_POSITION_VALUES[PAGINATION_POSITION_OPTIONS.BOTTOM_CENTER],
      text: PAGINATION_POSITION_OPTIONS.BOTTOM_CENTER,
      value: PAGINATION_POSITION_VALUES[PAGINATION_POSITION_OPTIONS.BOTTOM_CENTER]
    },
    {
      key: PAGINATION_POSITION_VALUES[PAGINATION_POSITION_OPTIONS.BR],
      text: PAGINATION_POSITION_OPTIONS.BR,
      value: PAGINATION_POSITION_VALUES[PAGINATION_POSITION_OPTIONS.BR]
    }
  ]
};

const XTable: XTableSchema = {
  editData: [
    ...baseConfig,
    {
      key: 'label',
      name: '标题',
      type: CONFIG_TYPES.LABEL_INPUT
    },
    tableMetaDataConfig,
    // keyDataConfig,
    labelColSpanConfig,
    pagePositionConfig,
    {
      key: 'pageSize',
      name: '分页数量',
      type: CONFIG_TYPES.TABLE_PAGE_SIZE
    },
    {
      key: 'border',
      name: '显示边框',
      type: CONFIG_TYPES.SWITCH_INPUT
    },
    {
      key: 'borderCell',
      name: '显示单元格',
      type: CONFIG_TYPES.SWITCH_INPUT
    },
    {
      key: 'showHeader',
      name: '显示表头',
      type: CONFIG_TYPES.SWITCH_INPUT
    },
    {
      key: 'hover',
      name: '鼠标悬浮效果',
      type: CONFIG_TYPES.SWITCH_INPUT
    },
    {
      key: 'stripe',
      name: '开启斑马纹',
      type: CONFIG_TYPES.SWITCH_INPUT
    },
    {
      key: 'showTotal',
      name: '显示表格总数',
      type: CONFIG_TYPES.SWITCH_INPUT
    },
    {
      key: 'showOpearate',
      name: '开启操作项',
      type: CONFIG_TYPES.SWITCH_INPUT
    },
    {
      key: 'fixedOpearate',
      name: '固定操作项',
      type: CONFIG_TYPES.SWITCH_INPUT
    },
    // {
    //   key: 'saveWithHidden',
    //   name: '隐藏时提交数据',
    //   type: CONFIG_TYPES.SWITCH_INPUT
    // },
    widthConfig,
    statusConfig,
    {
      key: 'advancedRowRedirect',
      name: '行点击跳转',
      type: CONFIG_TYPES.TABLE_DATA,
      advanced: true
    },
    tableOperationConfig,
    tableButtonPermissionConfig
  ],
  config: {
    ...baseDefault,
    label: {
      text: '表格',
      display: false
    },
    stripe: true,
    border: true,
    borderCell: true,
    showHeader: true,
    hover: true,
    showTotal: true,
    showOpearate: true,
    fixedOpearate: true,
    saveWithHidden: false,
    width: WIDTH_VALUES[WIDTH_OPTIONS.FULL],
    status: STATUS_VALUES[STATUS_OPTIONS.DEFAULT],
    pagePosition: PAGINATION_POSITION_VALUES[PAGINATION_POSITION_OPTIONS.BR],
    pageSize: 10,
    metaData: '',
    labelColSpan: 100,
    defaultValue: [],
    columns: [],

    searchItems: [],
    advancedRowRedirect: false,
    redirectPageId: '',
    redirectMethod: RedirectMethod.DRAWER,

    // 操作按钮
    operationButton: [
      {
        type: TableOperationButton.EDIT,
        buttonName: '编辑',
        buttonIcon: 'edit',
        iconColor: '#C9CDD4',
        redirectPageId: '',
        redirectMethod: RedirectMethod.NEW_TAB,
        display: true
      },
      {
        type: TableOperationButton.DELETE,
        buttonName: '删除',
        buttonIcon: 'delete',
        iconColor: '#F53F3F',
        confirmText: '确定删除？删除后不可恢复',
        deletedAction: RedirectMethod.REFRESH,
        display: true
      }
    ],

    operationButtonShowType: 'all',
    operationButtonCollpaseNumber: 4,
    advancedButtonPermission: BUTTON_VALUES[BUTTON_OPTIONS.HIDDEN]
  }
};

export default XTable;
