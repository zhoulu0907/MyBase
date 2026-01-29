import {
  baseConfig,
  baseDefault,
  dataFieldConfig,
  defaultValueModeConfig,
  layoutConfig,
  selectScopeConfig,
  statusConfig,
  widthConfig,
  labelConfig,
  tooltipConfig,
  verifyConfig,
  type ICommonBaseType,
  type TLayoutSelectKeyType,
  type TStatusSelectKeyType,
  type TAlignSelectKeyType,
  type TWidthSelectKeyType
} from '../../../common';
import {
  LAYOUT_OPTIONS,
  LAYOUT_VALUES,
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
  ICommonConfigType,
  TBooleanDefaultType,
  TSelectDefaultType,
  TTextAreaDefaultType,
  TTextDefaultType,
  TRadioDefaultType
} from '../../../types';

export interface XInputDeptSelectSchema {
  editData: TXInputDeptSelectEditData;
  config: XInputDeptSelectConfig;
}

export type TXInputDeptSelectEditData = Array<
  | ITextConfigType
  | ILabelConfigType
  | IPlaceholderConfigType
  | IDataFieldConfigType
  | ITooltipConfigType
  | IStatusConfigType<TStatusSelectKeyType>
  | IWidthConfigType<TWidthSelectKeyType>
  | INumberConfigType
  | ISelectConfigType<TWidthSelectKeyType | TStatusSelectKeyType>
  | ITextAreaConfigType
  | IBooleanConfigType
  | ILayoutConfigType<TLayoutSelectKeyType>
  | ICommonConfigType
>;

export interface XInputDeptSelectConfig extends ICommonBaseType {
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
   * 数据字段
   */
  dataField: TTextDefaultType[];

  /**
   * 默认值方式
   */
  defaultValueMode?: any;

  /**
   * 部门默认值
   */
  defaultDeptValue?: TTextDefaultType;

  /**
  * required：是否必填，未填写时提交报错
  * noRepeat：是否不允许重复
  */
  verify: {
    required: TBooleanDefaultType;
    noRepeat?: TBooleanDefaultType;
  };

  /**
   * 可选范围switch
   */
  isSelectScope?: TBooleanDefaultType;

  /**
   * 可选范围
   */
  selectScope?: TTextDefaultType[];

  /**
   * 组件状态：可用、隐藏、只读
   * 可选值: 'default' | 'hidden' | 'readonly'
   */
  status?: TRadioDefaultType<TStatusSelectKeyType>;

  /**
   * 内容对齐方式：左、中、右
   * 可选值: 'left' | 'center' | 'right'
   */
  align?: TSelectDefaultType<TAlignSelectKeyType>;

  /**
   * 表单的布局：水平、垂直（默认）
   * 可选值: 'vertical' | 'horizontal'
   */
  layout?: TLayoutSelectKeyType;
  /**
   * 字段宽度
   */
  width: TRadioDefaultType<TWidthSelectKeyType>;
}

const XDeptSelect: XInputDeptSelectSchema = {
  editData: [
    ...baseConfig,
    labelConfig,
    tooltipConfig,
    ...dataFieldConfig,
    defaultValueModeConfig,
    selectScopeConfig,
    verifyConfig,
    // 显示状态
    statusConfig,
    // 布局方式
    layoutConfig,
    // 字段宽度
    widthConfig
  ],
  config: {
    ...baseDefault,
    label: {
      text: '部门选择',
      display: true
    },
    dataField: [],
    tooltip: '',
    width: WIDTH_VALUES[WIDTH_OPTIONS.HALF],
    status: STATUS_VALUES[STATUS_OPTIONS.DEFAULT],
    defaultValueMode: {
      type: DEFAULT_VALUE_TYPES.CUSTOM,
      formulaValue: ''
    },
    defaultDeptValue: '',
    isSelectScope: false,
    selectScope: [],
    layout: LAYOUT_VALUES[LAYOUT_OPTIONS.VERTICAL],
    verify: {
      required: false,
      noRepeat: false
    }
  }
};

export default XDeptSelect;
