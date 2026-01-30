import {
  baseConfig,
  baseDefault,
  labelConfig,
  statusConfig,
  widthConfig,
  treeDataConfig,
  type ICommonBaseType,
  type TStatusSelectKeyType,
  type TWidthSelectKeyType
} from '../../../common';
import {
  STATUS_OPTIONS,
  STATUS_VALUES,
  WIDTH_OPTIONS,
  WIDTH_VALUES
} from '../../../constants';
import type {
  ICommonConfigType,
  ILabelConfigType,
  IStatusConfigType,
  ITextConfigType,
  ITreeDataConfigType,
  IWidthConfigType,
  TRadioDefaultType,
  TSelectDefaultType,
  TTextDefaultType,
  TBooleanDefaultType,
  TNumberDefaultType
} from '../../../types';

export interface XTreeSchema {
  editData: TXTreeEditData;
  config: XTreeConfig;
}

export type TXTreeEditData = Array<
  | ITextConfigType
  | ILabelConfigType
  | IWidthConfigType<TWidthSelectKeyType>
  | IStatusConfigType<TStatusSelectKeyType>
  | ITreeDataConfigType
  | ICommonConfigType
>;

export interface XTreeConfig extends ICommonBaseType {
  pageSetType?: number;

  label: {
    text: TTextDefaultType;
    display: TBooleanDefaultType;
  };

  defaultValue?: any[];

  metaData: TTextDefaultType;
  tableName: TTextDefaultType;

  treeFields?: TreeFieldConfig[];

  defaultExpandLevel?: TNumberDefaultType;

  status?: TRadioDefaultType<TStatusSelectKeyType>;

  width: TSelectDefaultType<TWidthSelectKeyType>;

  saveWithHidden?: TBooleanDefaultType;

  enableMinHeight?: TBooleanDefaultType;
  enableMaxHeight?: TBooleanDefaultType;
  minHeight?: TNumberDefaultType;
  maxHeight?: TNumberDefaultType;
}

export interface TreeFieldConfig {
  level: number;
  fieldName: string;
  displayName: string;
  fieldType: string;
}

const XTree: XTreeSchema = {
  editData: [
    ...baseConfig,
    labelConfig,
    treeDataConfig,
    widthConfig,
    statusConfig
  ],
  config: {
    ...baseDefault,
    label: {
      text: '树形目录',
      display: false
    },
    metaData: '',
    tableName: '',
    treeFields: [],
    defaultExpandLevel: 2,
    saveWithHidden: false,
    width: WIDTH_VALUES[WIDTH_OPTIONS.FULL],
    status: STATUS_VALUES[STATUS_OPTIONS.DEFAULT],
    defaultValue: []
  }
};

export default XTree;
