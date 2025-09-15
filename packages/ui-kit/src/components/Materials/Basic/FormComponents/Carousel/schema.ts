import {
  baseConfig,
  baseDefault,
  statusConfig,
  widthConfig,
  fillConfig,
  layoutConfig,
  carouselConfig,
  type ICommonBaseType,
  type TStatusSelectKeyType,
  type TWidthSelectKeyType,
  type TFillSelectKeyType,
  type TLayoutSelectKeyType,
  type TAlignSelectKeyType,
} from '../../../common';
import { CONFIG_TYPES, STATUS_OPTIONS, STATUS_VALUES, WIDTH_OPTIONS, WIDTH_VALUES, FILL_OPTIONS, FILL_VALUES, LAYOUT_OPTIONS, LAYOUT_VALUES, } from '../../../constants';
import type {
  IBooleanConfigType,
  IStatusConfigType,
  ITextConfigType,
  IWidthConfigType,
  TBooleanDefaultType,
  TNumberDefaultType,
  TRadioDefaultType,
  TSelectDefaultType,
  INumberConfigType,
  ICarouselConfigType,
  IVerifyConfigType,
  TTextDefaultType,
  TTextAreaDefaultType,
  ILayoutConfigType,
  ITextAreaConfigType,
  ILabelConfigType,
  ITooltipConfigType
} from '../../../types';

export interface XCarouselSchema {
  editData: TXCarouselEditData;
  config: XCarouselConfig;
}

export type TXCarouselEditData = Array<
  ITextConfigType
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
  image: string;
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
   * 组件状态：可用、隐藏、只读
   * 可选值: 'default' | 'hidden' | 'readonly'
   */
  status?: TRadioDefaultType<TStatusSelectKeyType>;

  /**
   * 描述信息（鼠标悬浮时显示）
   */
  tooltip?: TTextAreaDefaultType;

  /**
   * 字段宽度
   */
  width: TSelectDefaultType<TWidthSelectKeyType>;
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
  }

  /**
   * 表单的布局：水平、垂直（默认）
   * 可选值: 'vertical' | 'horizontal'
   */
  layout?: TLayoutSelectKeyType;

  /**
   * 内容对齐方式：左、中、右
   * 可选值: 'left' | 'center' | 'right'
   */
  align?: TSelectDefaultType<TAlignSelectKeyType>;

  /**
   * 标题宽度
   */
  labelColSpan?: TNumberDefaultType;
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
      name: '描述信息',
      type: CONFIG_TYPES.TOOLTIP_INPUT
    },
    layoutConfig,
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
    {
      key: 'verify',
      name: '校验',
      type: CONFIG_TYPES.VERIFY
    },
    fillConfig,
    statusConfig,
    widthConfig,
  ],
  config: {
    ...baseDefault,
    label: {
      text: '轮播图',
      display: true,
    },
    tooltip: '',
    layout: LAYOUT_VALUES[LAYOUT_OPTIONS.HORIZONTAL],
    width: WIDTH_VALUES[WIDTH_OPTIONS.FULL],
    status: STATUS_VALUES[STATUS_OPTIONS.DEFAULT],
    fillStyle: FILL_VALUES[FILL_OPTIONS.COVER],
    autoplay: false,
    interval: 3,
    labelColSpan: 100,
    carouselConfig: [
      {
        image: 'https://devops.cm-iov.com:9000/system-static/img/annual2.jpg',
        text: '🎑 中秋快乐 🎉',
        url: 'https://devops.cm-iov.com:9000/system-static/img/annual2.jpg',
      },
      {
        image: 'https://devops.cm-iov.com/static/img/bg.dd06daaa.png',
        text: '222',
        url: '#',
      }
    ],
    verify: {
      required: false,
      maxCount: 10,
      maxSize: 5
    }
  }
};

export default XCarousel;
