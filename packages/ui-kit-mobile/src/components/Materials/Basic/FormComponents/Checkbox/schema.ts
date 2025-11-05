import {
  baseConfig,
  baseDefault,
  checkboxDataConfig,
  dataFieldConfig,
  directionConfig,
  labelColSpanConfig,
  layoutConfig,
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
  STATUS_OPTIONS,
  STATUS_VALUES,
  WIDTH_OPTIONS,
  WIDTH_VALUES
} from '../../../constants';
import type {
  IBooleanConfigType,
  ICheckboxDataConfigType,
  IDataFieldConfigType,
  ILabelConfigType,
  ILayoutConfigType,
  INumberConfigType,
  IPlaceholderConfigType,
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

export interface XInputCheckboxSchema {
  editData: TXInputCheckboxEditData;
  config: XInputCheckboxConfig;
}

export type TXInputCheckboxEditData = Array<
  | ITextConfigType
  | ILabelConfigType
  | IPlaceholderConfigType
  | ITooltipConfigType
  | IStatusConfigType<TStatusSelectKeyType>
  | IWidthConfigType<TWidthSelectKeyType>
  | INumberConfigType
  | ITextAreaConfigType
  | IBooleanConfigType
  | ILayoutConfigType<TLayoutSelectKeyType>
  | IDataFieldConfigType
  | ICheckboxDataConfigType
  | IVerifyConfigType
>;

export interface XInputCheckboxConfig extends ICommonBaseType {
  label: {
    text: TTextDefaultType;
    display: TBooleanDefaultType;
  };
  dataField: TTextDefaultType[];
  tooltip?: TTextAreaDefaultType;
  status?: TSelectDefaultType<TStatusSelectKeyType>;
  defaultValue?: { label: string; value: string; [property: string]: any }[];
  width: TSelectDefaultType<TWidthSelectKeyType>;
  verify: {
    required: TBooleanDefaultType;
    maxChecked: TNumberDefaultType;
  };
  layout?: TLayoutSelectKeyType;
  labelColSpan?: TNumberDefaultType;
  saveWithHidden?: TBooleanDefaultType;
  allChecked?: TBooleanDefaultType;
  direction?: TLayoutSelectKeyType;
}

const XCheckbox: XInputCheckboxSchema = {
  editData: [
    ...baseConfig,
    { key: 'label', name: '标题', type: CONFIG_TYPES.LABEL_INPUT },
    ...dataFieldConfig,
    { key: 'tooltip', name: '描述信息', type: CONFIG_TYPES.TOOLTIP_INPUT },
    labelColSpanConfig,
    layoutConfig,
    directionConfig,
    { key: 'saveWithHidden', name: '隐藏时提交数据', type: CONFIG_TYPES.SWITCH_INPUT },
    checkboxDataConfig,
    { key: 'allChecked', name: '全选', type: CONFIG_TYPES.SWITCH_INPUT },
    { key: 'verify', name: '校验', type: CONFIG_TYPES.VERIFY },
    statusConfig,
    widthConfig
  ],
  config: {
    ...baseDefault,
    label: { text: '复选框', display: true },
    dataField: [],
    tooltip: '',
    width: WIDTH_VALUES[WIDTH_OPTIONS.HALF],
    status: STATUS_VALUES[STATUS_OPTIONS.DEFAULT],
    layout: LAYOUT_VALUES[LAYOUT_OPTIONS.VERTICAL],
    direction: LAYOUT_VALUES[LAYOUT_OPTIONS.HORIZONTAL],
    saveWithHidden: false,
    labelColSpan: 200,
    defaultValue: [
      { label: '选项一', value: '选项一' },
      { label: '选项二', value: '选项二' },
      { label: '选项三', value: '选项三' }
    ],
    allChecked: false,
    verify: { required: false, maxChecked: 3 }
  }
};

export default XCheckbox;


