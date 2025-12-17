import {
    baseConfig,
    baseDefault,
    statusConfig,
    widthConfig,
    type ICommonBaseType,
    type TStatusSelectKeyType,
    type TWidthSelectKeyType
} from '../../../common';
import { STATUS_OPTIONS, STATUS_VALUES, WIDTH_OPTIONS, WIDTH_VALUES } from '../../../constants';
import type {
    IBooleanConfigType,
    IStatusConfigType,
    ITextConfigType,
    IWidthConfigType,
    ICommonConfigType,
    TRadioDefaultType,
    TSelectDefaultType
} from '../../../types';

export interface XCollapseSchema {
  editData: TXCollapseEditData;
  config: XCollapseConfig;
}

export type TXCollapseEditData = Array<
  ITextConfigType | IWidthConfigType<TWidthSelectKeyType> | IStatusConfigType<TStatusSelectKeyType> | IBooleanConfigType | ICommonConfigType
>;

export interface XCollapseConfig extends ICommonBaseType {
  /**
   * 组件状态：可用、隐藏、只读
   * 可选值: 'default' | 'hidden' | 'readonly'
   */
  status?: TRadioDefaultType<TStatusSelectKeyType>;

  /**
   * 字段宽度
   */
  width: TSelectDefaultType<TWidthSelectKeyType>;
}

const XCollapse: XCollapseSchema = {
  editData: [...baseConfig, widthConfig, statusConfig],
  config: {
    ...baseDefault,
    width: WIDTH_VALUES[WIDTH_OPTIONS.FULL],
    status: STATUS_VALUES[STATUS_OPTIONS.DEFAULT]
  }
};

export default XCollapse;
