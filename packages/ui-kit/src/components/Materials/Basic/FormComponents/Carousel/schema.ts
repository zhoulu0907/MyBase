import {
    baseConfig,
    baseDefault,
    statusConfig,
    widthConfig,
    fillConfig,
    carouselConfig,
    type ICommonBaseType,
    type TStatusSelectKeyType,
    type TWidthSelectKeyType,
    type TFillSelectKeyType,
} from '../../../common';
import { CONFIG_TYPES, STATUS_OPTIONS, STATUS_VALUES, WIDTH_OPTIONS, WIDTH_VALUES, FILL_OPTIONS, FILL_VALUES } from '../../../constants';
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
    ICarouselConfigType
} from '../../../types';

export interface XCarouselSchema {
  editData: TXCarouselEditData;
  config: XCarouselConfig;
}

export type TXCarouselEditData = Array<
  ITextConfigType | IWidthConfigType<TWidthSelectKeyType> | IStatusConfigType<TStatusSelectKeyType> | IBooleanConfigType|
  IStatusConfigType<TFillSelectKeyType> | INumberConfigType | ICarouselConfigType
>;

interface Images {
  image: string;
  text?: string;
  url?: string;
}

export interface XCarouselConfig extends ICommonBaseType {
  /**
   * 组件状态：可用、隐藏、只读
   * 可选值: 'default' | 'hidden' | 'readonly'
   */
  status?: TRadioDefaultType<TStatusSelectKeyType>;

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
}

const XCarousel: XCarouselSchema = {
  editData: [
    ...baseConfig,
    carouselConfig,
    widthConfig,
    statusConfig,
    fillConfig,
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
  ],
  config: {
    ...baseDefault,
    width: WIDTH_VALUES[WIDTH_OPTIONS.FULL],
    status: STATUS_VALUES[STATUS_OPTIONS.DEFAULT],
    fillStyle: FILL_VALUES[FILL_OPTIONS.COVER],
    autoplay: false,
    interval: 3,
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
    ]
  }
};

export default XCarousel;
