import {
  baseConfig,
  baseDefault,
  dataFieldConfig,
  alignConfig,
  layoutConfig,
  selectOptionsConfig,
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
  ALIGN_VALUES,
  ALIGN_OPTIONS,
  CONFIG_TYPES,
  LAYOUT_OPTIONS,
  LAYOUT_VALUES,
  STATUS_OPTIONS,
  STATUS_VALUES,
  WIDTH_OPTIONS,
  WIDTH_VALUES,
  DEFAULT_OPTIONS_TYPE
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
  ICommonConfigType,
  TBooleanDefaultType,
  TSelectDefaultType,
  TTextAreaDefaultType,
  TRadioDefaultType,
  IAlignConfigType,
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
  | IAlignConfigType<TAlignSelectKeyType>
  | ILayoutConfigType<TLayoutSelectKeyType>
  | IWidthConfigType<TWidthSelectKeyType>
  | any
  | ICommonConfigType
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
   * 选项
   */
  defaultOptionsConfig?: {
    type: string;
    disabled?: boolean,
    dictTypeId?: string;
    defaultOptions: { label: string; value: any;[property: string]: any }[];
  }

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

const XSelectOne: XInputSelectOneSchema = {
  editData: [
    ...baseConfig,
    labelConfig,
    tooltipConfig,
    //  数据绑定
    ...dataFieldConfig,
    // 选项
    selectOptionsConfig,
    // 选项分布方式
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
      text: '下拉单选',
      display: true
    },
    tooltip: '',
    dataField: [],
    defaultOptionsConfig: {
      type: DEFAULT_OPTIONS_TYPE.CUSTOM,
      disabled: false,
      dictTypeId: '',
      defaultOptions: [
        {
          label: '选项一',
          colorType: '',
          isChosen: false,
          value: '选项一'
        },
        {
          label: '选项二',
          colorType: '',
          isChosen: false,
          value: '选项二'
        },
        {
          label: '选项三',
          colorType: '',
          isChosen: false,
          value: '选项三'
        }
      ],
    },
    verify: {
      required: false,
    },
    status: STATUS_VALUES[STATUS_OPTIONS.DEFAULT],
    align: ALIGN_VALUES[ALIGN_OPTIONS.LEFT],
    layout: LAYOUT_VALUES[LAYOUT_OPTIONS.VERTICAL],
    width: WIDTH_VALUES[WIDTH_OPTIONS.HALF],
  },
};

export default XSelectOne;
