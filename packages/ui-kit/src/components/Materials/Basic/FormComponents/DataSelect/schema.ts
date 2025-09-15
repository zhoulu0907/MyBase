import {
    baseConfig,
    baseDefault,
    dataFieldConfig,
    labelColSpanConfig,
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
    ISelectDataSourceConfigType
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
>;

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
  }
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
   * 选择的数据源
   */
  selectedDataSource?: TTextDefaultType;

   /**
   * 选择的数据源 属性类型待定
   */
  isSetted: TBooleanDefaultType;
  displayFields: {
    label: TTextDefaultType;
    value: TTextDefaultType;
  }[];
  fillFormField: TBooleanDefaultType;
  selectDataFields: TTextDefaultType[];
  filterData: TBooleanDefaultType;
  sortDataRule: TTextDefaultType[];
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
    ...dataFieldConfig,
    {
      key: 'tooltip',
      name: '描述信息',
      type: CONFIG_TYPES.TOOLTIP_INPUT
    },
    layoutConfig,
    labelColSpanConfig,
    {
      key: 'saveWithHidden',
      name: '隐藏时提交数据',
      type: CONFIG_TYPES.SWITCH_INPUT
    },
    selectDataResourceConfig,
    {
      key: 'verify',
      name: '校验',
      type: CONFIG_TYPES.VERIFY
    },
    statusConfig,
    widthConfig
  ],
  config: {
    ...baseDefault,
    label: {
      text: '选择数据',
      display: true,
    },
    dataField: [],
    description: '',
    tooltip: '',
    width: WIDTH_VALUES[WIDTH_OPTIONS.HALF],
    status: STATUS_VALUES[STATUS_OPTIONS.DEFAULT],
    defaultValue: '选择数据',
    layout: LAYOUT_VALUES[LAYOUT_OPTIONS.HORIZONTAL],
    labelColSpan: 100,
    saveWithHidden: false,
    verify: {
      required: false,
      noRepeat: false
    },
    selectedDataSource: '',

    // 选择数据属性 待定
    isSetted: false,
    displayFields: [],
    fillFormField: false,
    selectDataFields: [],
    filterData: false,
    sortDataRule: [],
    operationAuth: false,
    fastFilter: false,
    dynamicTableConfig: {
      ...baseDefault,
        label: '',
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
        labelColSpan: 100,
        defaultValue: [],
        columns: [],
        searchItems: []}
  }
};

export default XDataSelect;
