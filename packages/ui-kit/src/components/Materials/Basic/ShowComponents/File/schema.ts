import {
  baseConfig,
  baseDefault,
  statusConfig,
  widthConfig,
  fillConfig,
  fileConfig,
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
  IFileConfigType
} from '../../../types';

export interface XFileSchema {
  editData: TXFileEditData;
  config: XFileConfig;
}

export type TXFileEditData = Array<
  ITextConfigType | IWidthConfigType<TWidthSelectKeyType> | IStatusConfigType<TStatusSelectKeyType> | IBooleanConfigType |
  IStatusConfigType<TFillSelectKeyType> | IFileConfigType
>;

interface Files {
  name: string;
  fileId: string;
}

export interface XFileConfig extends ICommonBaseType {
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
  fileConfig: Files[];
  verify: {
    required: boolean;
    maxCount: TNumberDefaultType;
    maxSize: TNumberDefaultType;
  }
}

const XFile: XFileSchema = {
  editData: [...baseConfig, fileConfig, fillConfig, widthConfig, statusConfig],
  config: {
    ...baseDefault,
    width: WIDTH_VALUES[WIDTH_OPTIONS.HALF],
    status: STATUS_VALUES[STATUS_OPTIONS.DEFAULT],
    fillStyle: FILL_VALUES[FILL_OPTIONS.COVER],
    fileConfig:[],
    maxHeight: undefined,
    verify: {
      required: false,
      maxSize: 5,
      maxCount: 10
    }
  }
};

export default XFile;
