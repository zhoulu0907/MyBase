import {
  baseConfig,
  baseDefault,
  keyDataConfig,
  metaDataConfig,
  statusConfig,
  widthConfig,
  type ICommonBaseType,
  type TPagePositionSelectKeyType,
  type TStatusSelectKeyType,
  type TWidthSelectKeyType,
} from "@/components/Materials/common";
import {
  CONFIG_TYPES,
  PAGINATION_POSITION_OPTIONS,
  PAGINATION_POSITION_VALUES,
  STATUS_OPTIONS,
  STATUS_VALUES,
  WIDTH_OPTIONS,
  WIDTH_VALUES,
} from "@/components/Materials/constants";
import type {
  IBooleanConfigType,
  IDynamicSelectConfigType,
  ILabelConfigType,
  ISearchItemListConfigType,
  IStatusConfigType,
  ITableColumnConfigType,
  ITablePagePositionConfigType,
  ITablePageSizeConfigType,
  ITextConfigType,
  IWidthConfigType,
  TBooleanDefaultType,
  TNumberDefaultType,
  TRadioDefaultType,
  TSelectDefaultType,
  TTextDefaultType,
} from "@/components/Materials/types";

export interface XTableSchema {
  editData: TXTableEditData;
  config: XTableConfig;
}

export type TXTableEditData = Array<
  | ITextConfigType
  | ILabelConfigType
  | IWidthConfigType<TWidthSelectKeyType>
  | IStatusConfigType<TStatusSelectKeyType>
  | ITableColumnConfigType
  | IBooleanConfigType
  | ITablePagePositionConfigType<TPagePositionSelectKeyType>
  | ITablePageSizeConfigType
  | IDynamicSelectConfigType
  | ISearchItemListConfigType
>;

export interface XTableConfig extends ICommonBaseType {
  /**
   * 输入框标题
   */
  label: TTextDefaultType;

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
}

const pagePositionConfig: ITablePagePositionConfigType<TPagePositionSelectKeyType> =
  {
    key: "pagePosition",
    name: "分页位置",
    type: CONFIG_TYPES.TABLE_PAGE_POSITION_RADIO,
    range: [
      {
        key: PAGINATION_POSITION_VALUES[PAGINATION_POSITION_OPTIONS.TL],
        text: PAGINATION_POSITION_OPTIONS.TL,
        value: PAGINATION_POSITION_VALUES[PAGINATION_POSITION_OPTIONS.TL],
      },
      {
        key: PAGINATION_POSITION_VALUES[PAGINATION_POSITION_OPTIONS.TOP_CENTER],
        text: PAGINATION_POSITION_OPTIONS.TOP_CENTER,
        value:
          PAGINATION_POSITION_VALUES[PAGINATION_POSITION_OPTIONS.TOP_CENTER],
      },
      {
        key: PAGINATION_POSITION_VALUES[PAGINATION_POSITION_OPTIONS.TR],
        text: PAGINATION_POSITION_OPTIONS.TR,
        value: PAGINATION_POSITION_VALUES[PAGINATION_POSITION_OPTIONS.TR],
      },
      {
        key: PAGINATION_POSITION_VALUES[PAGINATION_POSITION_OPTIONS.BL],
        text: PAGINATION_POSITION_OPTIONS.BL,
        value: PAGINATION_POSITION_VALUES[PAGINATION_POSITION_OPTIONS.BL],
      },
      {
        key: PAGINATION_POSITION_VALUES[
          PAGINATION_POSITION_OPTIONS.BOTTOM_CENTER
        ],
        text: PAGINATION_POSITION_OPTIONS.BOTTOM_CENTER,
        value:
          PAGINATION_POSITION_VALUES[PAGINATION_POSITION_OPTIONS.BOTTOM_CENTER],
      },
      {
        key: PAGINATION_POSITION_VALUES[PAGINATION_POSITION_OPTIONS.BR],
        text: PAGINATION_POSITION_OPTIONS.BR,
        value: PAGINATION_POSITION_VALUES[PAGINATION_POSITION_OPTIONS.BR],
      },
    ],
  };

const XTable: XTableSchema = {
  editData: [
    ...baseConfig,
    {
      key: "label",
      name: "标题",
      type: CONFIG_TYPES.LABEL_INPUT,
    },
    metaDataConfig,
    keyDataConfig,
    {
      key: "columns",
      name: "表头配置",
      type: CONFIG_TYPES.TABLE_COLUMN_LIST,
    },
    {
      key: "searchItems",
      name: "搜索项",
      type: CONFIG_TYPES.SEARCH_ITEM_LIST,
    },
    pagePositionConfig,
    {
      key: "pageSize",
      name: "分页数量",
      type: CONFIG_TYPES.TABLE_PAGE_SIZE,
    },

    {
      key: "border",
      name: "显示边框",
      type: CONFIG_TYPES.SWITCH_INPUT,
    },
    {
      key: "borderCell",
      name: "显示单元格",
      type: CONFIG_TYPES.SWITCH_INPUT,
    },
    {
      key: "showHeader",
      name: "显示表头",
      type: CONFIG_TYPES.SWITCH_INPUT,
    },
    {
      key: "hover",
      name: "鼠标悬浮效果",
      type: CONFIG_TYPES.SWITCH_INPUT,
    },
    {
      key: "stripe",
      name: "开启斑马纹",
      type: CONFIG_TYPES.SWITCH_INPUT,
    },
    {
      key: "showTotal",
      name: "显示表格总数",
      type: CONFIG_TYPES.SWITCH_INPUT,
    },
    {
      key: "showOpearate",
      name: "开启操作项",
      type: CONFIG_TYPES.SWITCH_INPUT,
    },
    {
      key: "fixedOpearate",
      name: "固定操作项",
      type: CONFIG_TYPES.SWITCH_INPUT,
    },
    {
      key: "saveWithHidden",
      name: "隐藏时提交数据",
      type: CONFIG_TYPES.SWITCH_INPUT,
    },
    widthConfig,
    statusConfig,
  ],
  config: {
    ...baseDefault,
    label: "",
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
    pageSize: 5,
    defaultValue: [
      {
        key: "1",
        name: "Jane Doe",
        salary: 23000,
        address: "32 Park Road, London",
        email: "jane.doe@example.com",
        gender: "male",
      },
      {
        key: "2",
        name: "Alisa Ross",
        salary: 25000,
        address: "35 Park Road, London",
        email: "alisa.ross@example.com",
        gender: "male",
      },
      {
        key: "3",
        name: "Kevin Sandra",
        salary: 22000,
        address: "31 Park Road, London",
        email: "kevin.sandra@example.com",
        gender: "male",
      },
      {
        key: "4",
        name: "Kevin Sandra",
        salary: 22000,
        address: "31 Park Road, London",
        email: "kevin.sandra@example.com",
        gender: "male",
      },
      {
        key: "5",
        name: "Kevin Sandra",
        salary: 22000,
        address: "31 Park Road, London",
        email: "kevin.sandra@example.com",
        gender: "male",
      },
      {
        key: "6",
        name: "Kevin Sandra",
        salary: 22000,
        address: "31 Park Road, London",
        email: "kevin.sandra@example.com",
        gender: "male",
      },
    ],
    columns: [
      {
        title: "姓名",
        dataIndex: "name",
        fixed: "left",
        width: 140,
      },
      {
        title: "工资",
        dataIndex: "salary",
      },
      {
        title: "地址",
        dataIndex: "address",
      },
      {
        title: "邮箱",
        dataIndex: "email",
      },
      {
        title: "性别",
        dataIndex: "gender",
      },
    ],
    searchItems: [
      // {
      //     label: '姓名',
      //     value: 'name',
      // },
      // {
      //     label: '邮箱',
      //     value: 'email',
      // },
      // {
      //     label: '性别',
      //     value: 'gender',
      // },
      // {
      //     label: '工资',
      //     value: 'salary',
      // },
    ],
  },
};

export default XTable;
