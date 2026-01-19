import {
  alignConfig,
  baseConfig,
  baseDefault,
  dataFieldConfig,
  defaultValueConfig,
  layoutConfig,
  statusConfig,
  widthConfig,
  labelConfig,
  tooltipConfig,
  verifyConfig,
  type ICommonBaseType,
  type TAlignSelectKeyType,
  type TLayoutSelectKeyType,
  type TStatusSelectKeyType,
  type TWidthSelectKeyType
} from '../../../common';
import {
  ALIGN_OPTIONS,
  ALIGN_VALUES,
  LAYOUT_OPTIONS,
  LAYOUT_VALUES,
  CONFIG_TYPES,
  STATUS_OPTIONS,
  STATUS_VALUES,
  WIDTH_OPTIONS,
  WIDTH_VALUES,
  DEFAULT_VALUE_TYPES
} from '../../../constants';
import type {
  IAlignConfigType,
  IDataFieldConfigType,
  ILabelConfigType,
  ILayoutConfigType,
  IStatusConfigType,
  ITooltipConfigType,
  IVerifyConfigType,
  IWidthConfigType,
  ICommonConfigType,
  TBooleanDefaultType,
  TRadioDefaultType,
  TSelectDefaultType,
  TTextAreaDefaultType,
  TTextDefaultType,
  IDefaultValueConfigType
} from '../../../types';

// 组件的schema
export interface XRateSchema {
  // 可配置项
  editData: TXRateEditData;
  // 默认配置
  config: XRateConfig;
}

// 组件的可配置项
export type TXRateEditData = Array<
  | ILabelConfigType
  | ITooltipConfigType
  | IDataFieldConfigType
  | IDefaultValueConfigType
  | IVerifyConfigType
  | IStatusConfigType<TStatusSelectKeyType>
  | IAlignConfigType<TAlignSelectKeyType>
  | ILayoutConfigType<TLayoutSelectKeyType>
  | IWidthConfigType<TWidthSelectKeyType>
  | ICommonConfigType
>;

export interface XRateConfig extends ICommonBaseType {
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
   * 默认值
   */
  defaultValueConfig?: any;

  /**
   * 样式：显示图标、形状、颜色
   * 数量：最大值、允许半星
   * 文字提示：显示选中结果、是否自定义评分文案、自定义评分文案
   */
  rateConfig: {
    showIcon: boolean;
    iconName: string;
    iconColor: string;
    max: number;
    allowHalf: boolean;
    showResult: boolean;
    showTooltips: boolean;
    tooltips: string[];
  };

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

const XRate: XRateSchema = {
  editData: [
    ...baseConfig,
    labelConfig,
    tooltipConfig,
    //  数据绑定
    ...dataFieldConfig,
    // 默认值
    defaultValueConfig,
    {
      key: 'rateConfig',
      name: '评分',
      type: CONFIG_TYPES.RATE_CONFIG,
    },
    verifyConfig,
    // 显示状态
    statusConfig,
    // 对齐方式
    alignConfig,
    // 布局方式
    layoutConfig,
    // 字段宽度
    widthConfig
  ],
  config: {
    ...baseDefault,
    label: {
      text: '评分',
      display: true
    },
    tooltip: '',
    dataField: [],
    defaultValueConfig: {
      type: DEFAULT_VALUE_TYPES.CUSTOM,
      customValue: 0,
      formulaValue: 0
    },
    rateConfig:{
      showIcon: true,
      iconName: 'IconStar',
      iconColor: '#FFD32D',
      max: 5,
      allowHalf: false,
      showResult: false,
      showTooltips: false,
      tooltips: []
    },
    verify: {
      required: false,
    },
    status: STATUS_VALUES[STATUS_OPTIONS.DEFAULT],
    align: ALIGN_VALUES[ALIGN_OPTIONS.LEFT],
    layout: LAYOUT_VALUES[LAYOUT_OPTIONS.VERTICAL],
    width: WIDTH_VALUES[WIDTH_OPTIONS.HALF],
  }
};

export default XRate;