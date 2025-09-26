import {
  baseConfig,
  baseDefault,
  statusConfig,
  widthConfig,
  fillConfig,
  imageConfig,
  type ICommonBaseType,
  type TStatusSelectKeyType,
  type TWidthSelectKeyType,
  type TFillSelectKeyType,
} from '../../../common';
import { STATUS_OPTIONS, STATUS_VALUES, WIDTH_OPTIONS, WIDTH_VALUES, FILL_VALUES, FILL_OPTIONS } from '../../../constants';
import type {
  IBooleanConfigType,
  IStatusConfigType,
  ITextConfigType,
  IWidthConfigType,
  TNumberDefaultType,
  TRadioDefaultType,
  TSelectDefaultType,
  IImageConfigType
} from '../../../types';

export interface XImageSchema {
  editData: TXImageEditData;
  config: XImageConfig;
}

export type TXImageEditData = Array<
  ITextConfigType | IWidthConfigType<TWidthSelectKeyType> | IStatusConfigType<TStatusSelectKeyType> | IBooleanConfigType |
  IStatusConfigType<TFillSelectKeyType> | IImageConfigType
>;

interface Images {
  image: string;
  text?: string;
  url?: string;
}

export interface XImageConfig extends ICommonBaseType {
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
   * 填充方式
   */
  fillStyle?: TSelectDefaultType<TFillSelectKeyType>;

  /**
   * 最大限制高度（px）
   */
  maxHeight?: TNumberDefaultType;
  imageConfig: string;
  verify: {
    required: boolean;
    maxSize: TNumberDefaultType;
  }
}

const XImage: XImageSchema = {
  editData: [...baseConfig, imageConfig, fillConfig, widthConfig, statusConfig],
  config: {
    ...baseDefault,
    width: WIDTH_VALUES[WIDTH_OPTIONS.HALF],
    status: STATUS_VALUES[STATUS_OPTIONS.DEFAULT],
    fillStyle: FILL_VALUES[FILL_OPTIONS.COVER],
    imageConfig: '',
    maxHeight: undefined,
    verify: {
      required: false,
      maxSize: 5
    }
  }
};

export default XImage;
