import {
    baseConfig,
    baseDefault,
    statusConfig,
    labelConfig,
    canvasCardConfig,
    type ICommonBaseType,
    type TStatusSelectKeyType,
    type TWidthSelectKeyType
} from '../../../common';
import { STATUS_OPTIONS, STATUS_VALUES, WIDTH_OPTIONS, WIDTH_VALUES } from '../../../constants';
import type {
    IStatusConfigType,
    ITextConfigType,
    ICommonConfigType,
    TRadioDefaultType,
    ILabelConfigType,
    TTextDefaultType,
    TBooleanDefaultType,
    TSelectDefaultType,
    ICanvasCardConfigType
} from '../../../types';

export interface XCanvasCardSchema {
  editData: TXCanvasCardEditData;
  config: XCanvasCardConfig;
}

export type TXCanvasCardEditData = Array<
  ITextConfigType | IStatusConfigType<TStatusSelectKeyType> | ILabelConfigType | ICanvasCardConfigType | ICommonConfigType
>;

export interface XCanvasCardConfig extends ICommonBaseType {
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
   * 标题配置
   */
  label: {
    text: TTextDefaultType;
    display: TBooleanDefaultType;
  };

  /**
   * 组件名称
   */
  componentName?: 'CanvasCardType1' | 'CanvasCardType2';

  /**
   * 卡片配置
   */
  config?: {
      /**
       * 图片 URL
       */
      imageUrl?: string;
      
      /**
       * 标签列表
       */
      tags?: string[];
      
      /**
       * 卡片标题
       */
      title?: string;
      
      /**
       * 卡片内容
       */
      content?: string;
      
      /**
       * 来源
       */
      source?: string;
      
      /**
       * 发布日期
       */
      publishDate?: string;
      
      /**
       * 浏览量
       */
      viewCount?: string;
    };
}

const XCanvasCard: XCanvasCardSchema = {
  editData: [...baseConfig, labelConfig, canvasCardConfig, statusConfig],
  config: {
    ...baseDefault,
    label: {
      text: '画布卡片',
      display: false
    },
    componentName: 'CanvasCardType1',
    status: STATUS_VALUES[STATUS_OPTIONS.DEFAULT],
    width: WIDTH_VALUES[WIDTH_OPTIONS.FULL],
    config: {
      imageUrl: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=beautiful%20mountain%20lake%20reflection%20scenery&image_size=landscape_16_9',
      tags: ['标签标签', 'default', 'default'],
      title: '卡片标题字段',
      content: '这是一段长文本这是一段长文本这是一段长文本这是一段长文本这是一段长文本这是一段长文本这是一段长文本这是一段长文本这是一段长文本这是一段长文本这是一段长文本这是一段长文本',
      source: '华尔街日报',
      publishDate: '2026年1月11日 22:22',
      viewCount: '888'
    }
  }
};

export default XCanvasCard;