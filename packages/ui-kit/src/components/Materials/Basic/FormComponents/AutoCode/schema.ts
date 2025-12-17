import {
  baseConfig,
  baseDefault,
  dataFieldConfig,
  layoutConfig,
  statusConfig,
  widthConfig,
  autoCodeConfig,
  labelConfig,
  placeholderConfig,
  tooltipConfig,
  type ICommonBaseType,
  type TAlignSelectKeyType,
  type TLayoutSelectKeyType,
  type TStatusSelectKeyType,
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
  IAutoCodeConfigType,
  ICommonConfigType,
  IDataFieldConfigType,
  ILabelConfigType,
  ILayoutConfigType,
  IPlaceholderConfigType,
  IStatusConfigType,
  ITooltipConfigType,
  IWidthConfigType,
  TBooleanDefaultType,
  TRadioDefaultType,
  TTextAreaDefaultType,
  TTextDefaultType,
} from '../../../types';

// 输入框组件的schema
export interface XautoCodeSchema {
  // 可配置项
  editData: TXautoCodeEditData;
  // 默认配置
  config: XautoCodeConfig;
}

// 输入框组件的可配置项
export type TXautoCodeEditData = Array<
  | ILabelConfigType
  | IPlaceholderConfigType
  | ITooltipConfigType
  | IDataFieldConfigType
  | IAutoCodeConfigType
  | IStatusConfigType<TStatusSelectKeyType>
  | ILayoutConfigType<TLayoutSelectKeyType>
  | IWidthConfigType<TWidthSelectKeyType>
  | ICommonConfigType
>;

export interface XautoCodeConfig extends ICommonBaseType {
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
   * 占位符
   */
  placeholder: TTextDefaultType;

  /**
   * 描述信息（鼠标悬浮时显示）
   */
  tooltip?: TTextAreaDefaultType;

  /**
   * 数据字段
   */
  dataField: TTextDefaultType[];

   // 编码规则
  autoCodeConfig?: any,
  autoCodeDisabled?: boolean,

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

const XautoCode: XautoCodeSchema = {
  editData: [
    ...baseConfig,
    labelConfig,
    placeholderConfig,
    tooltipConfig,
    ...dataFieldConfig,
    autoCodeConfig,
     statusConfig,
    layoutConfig,
    widthConfig
  ],
  config: {
    ...baseDefault,
    label: {
      text: '自动编号',
      display: true
    },
    placeholder: '自动生成无需填写',
    tooltip: '',
    dataField: [],
    autoCodeConfig: {},
    autoCodeDisabled: false,
    status: STATUS_VALUES[STATUS_OPTIONS.DEFAULT],
    layout: LAYOUT_VALUES[LAYOUT_OPTIONS.VERTICAL],
    width: WIDTH_VALUES[WIDTH_OPTIONS.HALF],
  }
};

export default XautoCode;
