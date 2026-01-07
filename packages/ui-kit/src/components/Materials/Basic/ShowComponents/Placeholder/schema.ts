import {
  baseConfig,
  baseDefault,
  widthConfig,
  type ICommonBaseType,
  type TWidthSelectKeyType
} from '../../../common';
import {
  WIDTH_OPTIONS,
  WIDTH_VALUES
} from '../../../constants';
import type {
  IBooleanConfigType,
  ITextConfigType,
  IWidthConfigType,
  ICommonConfigType,
  TSelectDefaultType,
  IColorConfigType
} from '../../../types';

export interface XPlaceholderSchema {
  editData: TPlaceholderEditData;
  config: XPlaceholderConfig;
}

export type TPlaceholderEditData = Array<
  ITextConfigType | IWidthConfigType<TWidthSelectKeyType> | IBooleanConfigType | IColorConfigType | ICommonConfigType
>;

export interface XPlaceholderConfig extends ICommonBaseType {

  /**
   * 字段宽度
   */
  width: TSelectDefaultType<TWidthSelectKeyType>;
}

const XPlaceholder: XPlaceholderSchema = {
  editData: [
    ...baseConfig,
    widthConfig,
  ],
  config: {
    ...baseDefault,
    width: WIDTH_VALUES[WIDTH_OPTIONS.HALF],
  }
};

export default XPlaceholder;
