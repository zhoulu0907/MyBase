import {
  baseConfig,
  baseDefault,
  carouselConfig,
  fillConfig,
  layoutConfig,
  statusConfig,
  widthConfig,
  type ICommonBaseType,
  type TFillSelectKeyType,
  type TLayoutSelectKeyType,
  type TStatusSelectKeyType,
  type TWidthSelectKeyType
} from '../../../common';
import {
  CONFIG_TYPES,
  FILL_OPTIONS,
  FILL_VALUES,
  LAYOUT_OPTIONS,
  LAYOUT_VALUES,
  STATUS_OPTIONS,
  STATUS_VALUES,
  WIDTH_OPTIONS,
  WIDTH_VALUES
} from '../../../constants';
import type {
  IBooleanConfigType,
  ICarouselConfigType,
  ILabelConfigType,
  ILayoutConfigType,
  INumberConfigType,
  IStatusConfigType,
  ITextAreaConfigType,
  ITextConfigType,
  ITooltipConfigType,
  IVerifyConfigType,
  IWidthConfigType,
  TBooleanDefaultType,
  TNumberDefaultType,
  TRadioDefaultType,
  TSelectDefaultType,
  TTextAreaDefaultType,
  TTextDefaultType
} from '../../../types';

export interface XCarouselSchema {
  editData: TXCarouselEditData;
  config: XCarouselConfig;
}

export type TXCarouselEditData = Array<
  | ITextConfigType
  | IBooleanConfigType
  | INumberConfigType
  | ICarouselConfigType
  | IVerifyConfigType
  | TTextDefaultType
  | IWidthConfigType<TWidthSelectKeyType>
  | IStatusConfigType<TStatusSelectKeyType>
  | IStatusConfigType<TFillSelectKeyType>
  | ILayoutConfigType<TLayoutSelectKeyType>
  | ITextAreaConfigType
  | ILabelConfigType
  | ITooltipConfigType
>;

interface Images {
  fileId: string;
  text?: string;
  url?: string;
}

export interface XCarouselConfig extends ICommonBaseType {
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
   * 自动轮播
   */
  autoplay?: TBooleanDefaultType;
  /**
   * 轮播间隔，单位秒
   */
  interval?: TNumberDefaultType;
  /**
   * 填充方式
   */
  fillStyle?: TSelectDefaultType<TFillSelectKeyType>;
  /**
   * 图片列表
   */
  carouselConfig: Images[];

  /**
   * required：是否必填，未填写时提交报错
   * maxCount：最大上传数量，默认：10
   * maxSize：最大图片大小单位MB，默认：5
   */
  verify: {
    required: TBooleanDefaultType;
    maxCount: TNumberDefaultType;
    maxSize: TNumberDefaultType;
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
  width: TSelectDefaultType<TWidthSelectKeyType>;
}

const XCarousel: XCarouselSchema = {
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
    carouselConfig,
    {
      key: 'autoplay',
      name: '自动轮播',
      type: CONFIG_TYPES.SWITCH_INPUT
    },
    {
      key: 'interval',
      name: '轮播间隔',
      type: CONFIG_TYPES.NUMBER_INPUT
    },
    fillConfig,
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
      text: '轮播图',
      display: true
    },
    tooltip: '',
    carouselConfig: [],
    autoplay: false,
    interval: 3,
    fillStyle: FILL_VALUES[FILL_OPTIONS.COVER],
    verify: {
      required: false,
      maxCount: 10,
      maxSize: 5
    },
    width: WIDTH_VALUES[WIDTH_OPTIONS.FULL],
    layout: LAYOUT_VALUES[LAYOUT_OPTIONS.VERTICAL],
    status: STATUS_VALUES[STATUS_OPTIONS.DEFAULT]
  }
};

export default XCarousel;
