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
  labelConfig,
} from '../../../common';
import { STATUS_OPTIONS, STATUS_VALUES, WIDTH_OPTIONS, WIDTH_VALUES, FILL_VALUES, FILL_OPTIONS } from '../../../constants';
import type {
  IBooleanConfigType,
  IStatusConfigType,
  ITextConfigType,
  IWidthConfigType,
  ICommonConfigType,
  TNumberDefaultType,
  TRadioDefaultType,
  TSelectDefaultType,
  IImageConfigType,
  TBooleanDefaultType,
  TTextDefaultType
} from '../../../types';
import { jumpTypeConfig, jumpPageIdConfig, jumpExternalUrlConfig } from '../../config/commonConfig';

export interface XImageSchema {
  editData: TXImageEditData;
  config: XImageConfig;
}

export type TXImageEditData = Array<
  ITextConfigType | IWidthConfigType<TWidthSelectKeyType> | IStatusConfigType<TStatusSelectKeyType> | IBooleanConfigType |
  IStatusConfigType<TFillSelectKeyType> | IImageConfigType | ICommonConfigType
>;

export interface XImageConfig extends ICommonBaseType {
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
   * 字段宽度
   */
  width: TSelectDefaultType<TWidthSelectKeyType>;

  /**
   * 填充方式
   */
  fillStyle?: TSelectDefaultType<TFillSelectKeyType>;

  imageConfig: string;
  verify: {
    required: boolean;
    maxSize: TNumberDefaultType;
  }

  /**
   * 跳转
   */
  jumpType: string,
  jumpPageId: string,
  jumpExternalUrl: string
}

const XImageWorkbench: XImageSchema = {
  editData: [...baseConfig, imageConfig, fillConfig, widthConfig, statusConfig, labelConfig,
    jumpTypeConfig,
    jumpPageIdConfig,
    jumpExternalUrlConfig
  ],
  config: {
    ...baseDefault,
    label: {
      text: '图片',
      display: true
    },
    width: WIDTH_VALUES[WIDTH_OPTIONS.HALF],
    status: STATUS_VALUES[STATUS_OPTIONS.DEFAULT],
    fillStyle: FILL_VALUES[FILL_OPTIONS.COVER],
    imageConfig: '',
    verify: {
      required: false,
      maxSize: 5
    },
    jumpType: 'internal',
    jumpPageId: '',
    jumpExternalUrl: ''
  }
};

export default XImageWorkbench;
