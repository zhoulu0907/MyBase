import {
  baseConfig,
  baseDefault,
  widthConfig,
  stepsConfig,
  stepsLabelPlacementConfig,
  type ICommonBaseType,
  type TWidthSelectKeyType,
  type TStepsTypeSelectKeyType,
  type TStepsLabelPlacementSelectKeyType
} from '../../../common';
import {
  WIDTH_OPTIONS,
  WIDTH_VALUES,
  STEPS_TYPE_OPTIONS,
  STEPS_LABEL_PLACEMENT_OPTIONS,
  STEPS_LABEL_PLACEMENT_VALUES,
  COLUMN_COUNT_OPTIONS,
} from '../../../constants';
import type {
  IBooleanConfigType,
  INumberConfigType,
  IPlaceholderConfigType,
  ITextAreaConfigType,
  ITextConfigType,
  IWidthConfigType,
  TSelectDefaultType,
  IStepsConfigType,
  IStepsLabelPlacementConfigType,
} from '../../../types';

export interface XStepsLayoutSchema {
  editData: XStepsLayoutEditData;
  config: XStepsLayoutConfig;
}

export type TColumnCountSelectKeyType = (typeof COLUMN_COUNT_OPTIONS)[keyof typeof COLUMN_COUNT_OPTIONS];
export type XStepsLayoutEditData = Array<
  | ITextConfigType
  | IPlaceholderConfigType
  | IWidthConfigType<TWidthSelectKeyType>
  | INumberConfigType
  | ITextAreaConfigType
  | IBooleanConfigType
  | IStepsConfigType
  | IStepsLabelPlacementConfigType<TStepsLabelPlacementSelectKeyType>
>;

export interface XStepsLayoutConfig extends ICommonBaseType {

  defaultValue?: any[];

  width: TSelectDefaultType<TWidthSelectKeyType>;

  type?: TSelectDefaultType<TStepsTypeSelectKeyType>;

  labelPlacement?: TSelectDefaultType<TStepsLabelPlacementSelectKeyType>;

  colCount: number;
}

const defaultValue = [
  {
    title: '步骤1',
    key: '1',
    description: '这是步骤1的描述'
  },
  {
    title: '步骤2',
    key: '2',
    description: '这是步骤2的描述'
  },
  {
    title: '步骤3',
    key: '3',
    description: '这是步骤3的描述'
  }
]

const XStepsLayout: XStepsLayoutSchema = {
  editData: [
    ...baseConfig,
    stepsConfig,
    stepsLabelPlacementConfig,
    widthConfig
  ],
  config: {
    ...baseDefault,
    width: WIDTH_VALUES[WIDTH_OPTIONS.FULL],
    defaultValue,
    type: STEPS_TYPE_OPTIONS.DEFAULT,
    colCount: defaultValue.length,
    labelPlacement: STEPS_LABEL_PLACEMENT_VALUES[STEPS_LABEL_PLACEMENT_OPTIONS.HORIZONTAL]
  }
};

export default XStepsLayout;
