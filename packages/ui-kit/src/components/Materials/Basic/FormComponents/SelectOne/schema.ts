import {
  baseConfig,
  baseDefault,
  dataFieldConfig,
  layoutConfig,
  selectOptionsConfig,
  statusConfig,
  widthConfig,
  type ICommonBaseType,
  type TLayoutSelectKeyType,
  type TStatusSelectKeyType,
  type TWidthSelectKeyType
} from '../../../common';
import {
  CONFIG_TYPES,
  DEFAULT_OPTIONS_TYPE,
  LAYOUT_OPTIONS,
  LAYOUT_VALUES,
  STATUS_OPTIONS,
  STATUS_VALUES,
  WIDTH_OPTIONS,
  WIDTH_VALUES
} from '../../../constants';
import type {
  IDataFieldConfigType,
  ILabelConfigType,
  ILayoutConfigType,
  ISelectOptionsConfigType,
  IStatusConfigType,
  ITextConfigType,
  ITooltipConfigType,
  IVerifyConfigType,
  IWidthConfigType,
  TBooleanDefaultType,
  TRadioDefaultType,
  TTextAreaDefaultType,
  TTextDefaultType
} from '../../../types';

export interface XInputSelectOneSchema {
  editData: TXInputSelectOneEditData;
  config: XInputSelectOneConfig;
}

export type TXInputSelectOneEditData = Array<
  | ITextConfigType
  | ILabelConfigType
  | ITooltipConfigType
  | IDataFieldConfigType
  | ISelectOptionsConfigType
  | IVerifyConfigType
  | IStatusConfigType<TStatusSelectKeyType>
  | ILayoutConfigType<TLayoutSelectKeyType>
  | IWidthConfigType<TWidthSelectKeyType>
>;

export interface XInputSelectOneConfig extends ICommonBaseType {
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
   * required：是否必填，未填写时提交报错
   */
  verify: {
    required: TBooleanDefaultType;
  };

  /**
   * 组件状态：可用、隐藏、只读
   * 可选值: 'default' | 'hidden' | 'readonly'
   */
  status?: TRadioDefaultType<TStatusSelectKeyType>;

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

const XSelectOne: XInputSelectOneSchema = {
  editData: [
    ...baseConfig,
    {
      key: 'label',
      name: '标题',
      type: CONFIG_TYPES.LABEL_INPUT
    },
    {
      key: 'tooltip',
      name: '字段描述',
      type: CONFIG_TYPES.TOOLTIP_INPUT
    },
    //  数据绑定
    ...dataFieldConfig,
    // 选项
    selectOptionsConfig,
    // 选项分布方式
    {
      key: 'verify',
      name: '校验',
      type: CONFIG_TYPES.VERIFY
    },
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
      text: '下拉单选',
      display: true
    },
    tooltip: '',
    dataField: [],
    verify: {
      required: false
    },
    status: STATUS_VALUES[STATUS_OPTIONS.DEFAULT],
    layout: LAYOUT_VALUES[LAYOUT_OPTIONS.VERTICAL],
    width: WIDTH_VALUES[WIDTH_OPTIONS.HALF]
  }
};

export default XSelectOne;
