import { baseConfig, baseDefault, widthConfig, columnCountConfig, type ICommonBaseType } from '../../../common';
import {
    COLUMN_COUNT_OPTIONS,
    COLUMN_COUNT_VALUES,
    WIDTH_OPTIONS,
    WIDTH_VALUES
} from '../../../constants';
import type {
    IColumnCountConfigType,
    ILabelConfigType,
    ISelectConfigType,
    ITextConfigType,
    IWidthConfigType,
    ICommonConfigType,
    TRadioDefaultType,
    TSelectDefaultType
} from '../../../types';

export type TColumnCountSelectKeyType = (typeof COLUMN_COUNT_OPTIONS)[keyof typeof COLUMN_COUNT_OPTIONS];
export type TColumnLayouTWidthSelectKeyType = (typeof WIDTH_VALUES)[keyof typeof WIDTH_VALUES];

export type TColumnLayoutEditData = Array<
  | ITextConfigType
  | IColumnCountConfigType<number>
  | IWidthConfigType<TColumnLayouTWidthSelectKeyType>
  | ILabelConfigType
  | ISelectConfigType<TColumnLayouTWidthSelectKeyType | TColumnCountSelectKeyType>
  | ICommonConfigType
>;

export interface XColumnLayoutConfig extends ICommonBaseType {
  /**
   * 列数
   */
  colCount: TRadioDefaultType<TColumnCountSelectKeyType>;
  /**
   * 布局宽度
   */
  width: TSelectDefaultType<TColumnLayouTWidthSelectKeyType>;

}

export interface XColumnLayoutSchema {
  editData: TColumnLayoutEditData;
  config: XColumnLayoutConfig;
}

const XColumnLayout: XColumnLayoutSchema = {
  editData: [
    ...baseConfig,
    columnCountConfig,
    widthConfig
  ],
  config: {
    ...baseDefault,
    colCount: COLUMN_COUNT_VALUES[COLUMN_COUNT_OPTIONS.TWO],
    width: WIDTH_VALUES[WIDTH_OPTIONS.FULL]
  }
};

export default XColumnLayout;
