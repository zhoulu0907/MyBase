import {
  baseConfig,
  baseDefault,
  dataFieldConfig,
  layoutConfig,
  selectDataResourceConfig,
  statusConfig,
  widthConfig,
  type ICommonBaseType,
  type TLayoutSelectKeyType,
  type TStatusSelectKeyType,
  type TWidthSelectKeyType
} from '../../../common';
import {
  CONFIG_TYPES,
  LAYOUT_OPTIONS,
  LAYOUT_VALUES,
  PAGINATION_POSITION_OPTIONS,
  PAGINATION_POSITION_VALUES,
  STATUS_OPTIONS,
  STATUS_VALUES,
  WIDTH_OPTIONS,
  WIDTH_VALUES
} from '../../../constants';
import type {
  IBooleanConfigType,
  IDataFieldConfigType,
  IDataSelectModeConfigType,
  ILabelConfigType,
  ILayoutConfigType,
  INumberConfigType,
  IPlaceholderConfigType,
  ISelectConfigType,
  ISelectDataSourceConfigType,
  IStatusConfigType,
  ITextAreaConfigType,
  ITextConfigType,
  ITooltipConfigType,
  IVerifyConfigType,
  IWidthConfigType,
  TBooleanDefaultType,
  TNumberDefaultType,
  TSelectDefaultType,
  TTextAreaDefaultType,
  TTextDefaultType
} from '../../../types';
import { XTableConfig } from '../../ListComponents/Table/schema';

export interface XDataSelectSchema {
  editData: TXDataSelectEditData;
  config: XDataSelectConfig;
}

export type TXDataSelectEditData = Array<
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
  | IDataFieldConfigType
  | IVerifyConfigType
  | ISelectDataSourceConfigType
  | IDataSelectModeConfigType
>;

export type TSelectMethodKeyType = 'dropdown' | 'modal';

export interface XDataSelectConfig extends ICommonBaseType {
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
   * 数据字段
   */
  dataField: TTextDefaultType[];

  /**
   * 描述信息（显示在输入框下方，辅助说明）
   */
  description: TTextAreaDefaultType;

  /**
   * 提示文字（鼠标悬浮时显示）
   */
  tooltip?: TTextDefaultType;

  /**
   * 组件状态：可用、隐藏、只读
   * 可选值: 'default' | 'hidden' | 'readonly'
   */
  status?: TSelectDefaultType<TStatusSelectKeyType>;

  /**
   * 默认值
   */
  defaultValue?: TTextDefaultType;

  /**
   * 字段宽度
   */
  width: TSelectDefaultType<TWidthSelectKeyType>;

  /**
   * required：是否必填，未填写时提交报错
   * noRepeat：是否不允许重复
   */
  verify: {
    required: TBooleanDefaultType;
    noRepeat?: TBooleanDefaultType;
  };
  /**
   * 表单的布局：水平、垂直（默认）
   * 可选值: 'vertical' | 'horizontal'
   */
  layout?: TLayoutSelectKeyType;

  /**
   * 数据选择方式：下拉框（默认）/ 弹窗
   * 可选值: 'dropdown' | 'modal'
   */
  selectMethod?: TSelectDefaultType<TSelectMethodKeyType>;

  /**
   * 标题宽度
   */
  labelColSpan?: TNumberDefaultType;

  /**
   * 隐藏时是否提交数据，开启后隐藏状态仍会保存值
   */
  saveWithHidden?: TBooleanDefaultType;

  /**
   * 选择的数据源
   */
  selectedDataSource?: {
    entityUuid: TTextDefaultType;
    entityName: TTextDefaultType;
    tableName: TTextDefaultType;
  };

  /**
   * 选择的数据源 属性类型待定
   */
  isSetted: TBooleanDefaultType;
  displayFields: {
    label: TTextDefaultType;
    value: TTextDefaultType;
    dataValue?: TTextDefaultType;
  }[];
  displayFieldsOptions: any[];
  fillFormFieldOptions: any[];
  fillRuleSetting: any[];
  dataFields: any[];
  selectDataFields: TTextDefaultType[];
  filterData: TBooleanDefaultType;
  filterCondition: any[];
  operationAuth: TBooleanDefaultType;
  fastFilter: TBooleanDefaultType;

  /**
   * 选择数据过程动态表单配置
   */
  dynamicTableConfig: XTableConfig;
}

const XDataSelect: XDataSelectSchema = {
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
    ...dataFieldConfig,
    {
      key: 'selectMethod',
      name: '数据选择方式',
      type: CONFIG_TYPES.DATA_SELECT_MODE,
      range: [
        { key: 'dropdown', text: '下拉框', value: 'dropdown', default: true },
        { key: 'modal', text: '弹窗', value: 'modal' }
      ]
    },
    selectDataResourceConfig,
    {
      key: 'verify',
      name: '校验',
      type: CONFIG_TYPES.VERIFY
    },
    statusConfig,
    layoutConfig,
    widthConfig
  ],
  config: {
    ...baseDefault,
    label: {
      text: '数据选择',
      display: true
    },
    dataField: [],
    description: '',
    tooltip: '',
    width: WIDTH_VALUES[WIDTH_OPTIONS.HALF],
    status: STATUS_VALUES[STATUS_OPTIONS.DEFAULT],
    defaultValue: '数据选择',
    selectMethod: 'dropdown',
    layout: LAYOUT_VALUES[LAYOUT_OPTIONS.VERTICAL],
    labelColSpan: 200,
    saveWithHidden: false,
    verify: {
      required: false,
      noRepeat: false
    },
    selectedDataSource: {
      entityUuid: '',
      entityName: '',
      tableName: ''
    },

    // 选择数据属性 待定
    isSetted: false,
    displayFields: [],
    displayFieldsOptions: [],
    fillFormFieldOptions: [],
    fillRuleSetting: [],
    dataFields: [],
    selectDataFields: [],
    filterData: false,
    filterCondition: [],
    operationAuth: false,
    fastFilter: false,
    dynamicTableConfig: {
      ...baseDefault,
      label: {
        text: '',
        display: true
      },
      tableName: '',
      filterCondition: {},
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
      metaData: '',
      labelColSpan: 200,
      defaultValue: [],
      columns: [],
      searchItems: [],
      sortByObject: {
        fieldName: '',
        sortBy: 1
      },
      operationButton: []
    }
  }
};

export default XDataSelect;
