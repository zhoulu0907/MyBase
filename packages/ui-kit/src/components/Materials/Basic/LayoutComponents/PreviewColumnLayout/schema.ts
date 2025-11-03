import { baseConfig, baseDefault, widthConfig, type ICommonBaseType } from 'src/components/Materials/common';
import {
    COLUMN_COUNT_OPTIONS,
    COLUMN_COUNT_VALUES,
    CONFIG_TYPES,
    WIDTH_OPTIONS,
    WIDTH_VALUES
} from 'src/components/Materials/constants';
import type {
    IColumnCountConfigType,
    ILabelConfigType,
    ISelectConfigType,
    ITextConfigType,
    IWidthConfigType,
    TRadioDefaultType,
    TSelectDefaultType
} from 'src/components/Materials/types';

export type TColumnCountSelectKeyType = (typeof COLUMN_COUNT_OPTIONS)[keyof typeof COLUMN_COUNT_OPTIONS];
export type TColumnLayouTWidthSelectKeyType = (typeof WIDTH_VALUES)[keyof typeof WIDTH_VALUES];

export type TColumnLayoutEditData = Array<
  | ITextConfigType
  | IColumnCountConfigType<TColumnCountSelectKeyType>
  | IWidthConfigType<TColumnLayouTWidthSelectKeyType>
  | ILabelConfigType
  | ISelectConfigType<TColumnLayouTWidthSelectKeyType | TColumnCountSelectKeyType>
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
  // 页面类型
  pageType?:string;
}

export interface XColumnLayoutSchema {
  editData: TColumnLayoutEditData;
  config: XColumnLayoutConfig;
}

const XColumnLayout: XColumnLayoutSchema = {
  editData: [
    ...baseConfig,
    {
      key: 'colCount',
      name: '列数',
      type: CONFIG_TYPES.COLUMN_COUNT_RADIO,
      range: [
        {
          key: String(COLUMN_COUNT_OPTIONS.ONE),
          text: String(COLUMN_COUNT_OPTIONS.ONE),
          value: COLUMN_COUNT_VALUES[COLUMN_COUNT_OPTIONS.ONE]
        },
        {
          key: String(COLUMN_COUNT_OPTIONS.TWO),
          text: String(COLUMN_COUNT_OPTIONS.TWO),
          value: COLUMN_COUNT_VALUES[COLUMN_COUNT_OPTIONS.TWO]
        },
        {
          key: String(COLUMN_COUNT_OPTIONS.THREE),
          text: String(COLUMN_COUNT_OPTIONS.THREE),
          value: COLUMN_COUNT_VALUES[COLUMN_COUNT_OPTIONS.THREE]
        },
        {
          key: String(COLUMN_COUNT_OPTIONS.FOUR),
          text: String(COLUMN_COUNT_OPTIONS.FOUR),
          value: COLUMN_COUNT_VALUES[COLUMN_COUNT_OPTIONS.FOUR]
        }
      ]
    },
    widthConfig
  ],
  config: {
    ...baseDefault,
    colCount: COLUMN_COUNT_VALUES[COLUMN_COUNT_OPTIONS.TWO],
    width: WIDTH_VALUES[WIDTH_OPTIONS.FULL]
  }
};

export default XColumnLayout;
