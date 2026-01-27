import {
  baseConfig,
  baseDefault,
  labelConfig,
  statusConfig,
  widthConfig,
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
  IBooleanConfigType,
  ICommonConfigType,
  ILabelConfigType,
  INumberConfigType,
  IStatusConfigType,
  ITableDataConfigType,
  ITextConfigType,
  IWidthConfigType,
  TBooleanDefaultType,
  TNumberDefaultType,
  TRadioDefaultType,
  TSelectDefaultType,
  TTextDefaultType
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
  | IBooleanConfigType
  | ITableDataConfigType
  | INumberConfigType
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

  border?: TBooleanDefaultType;

  showLine?: TBooleanDefaultType;

  hover?: TBooleanDefaultType;

  status?: TRadioDefaultType<TStatusSelectKeyType>;

  width: TSelectDefaultType<TWidthSelectKeyType>;

  saveWithHidden?: TBooleanDefaultType;
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
    {
      key: 'metaData',
      name: '数据绑定',
      type: 'TableData',
      advanced: false
    },
    {
      key: 'treeFields',
      name: '目录字段',
      type: 'TreeFields',
      advanced: false
    },
    {
      key: 'defaultExpandLevel',
      name: '默认展开层级',
      type: 'NumberInput',
      range: [1, 5],
      step: 1,
      advanced: false
    },
    {
      key: 'border',
      name: '显示边框',
      type: 'SwitchInput',
      advanced: true
    },
    {
      key: 'showLine',
      name: '显示连接线',
      type: 'SwitchInput',
      advanced: true
    },
    {
      key: 'hover',
      name: '鼠标悬浮效果',
      type: 'SwitchInput',
      advanced: true
    },
    widthConfig,
    statusConfig
  ],
  config: {
    ...baseDefault,
    label: {
      text: '树结构',
      display: false
    },
    metaData: '',
    tableName: '',
    treeFields: [],
    defaultExpandLevel: 2,
    border: true,
    showLine: true,
    hover: true,
    saveWithHidden: false,
    width: WIDTH_VALUES[WIDTH_OPTIONS.FULL],
    status: STATUS_VALUES[STATUS_OPTIONS.DEFAULT],
    defaultValue: []
  }
};

export default XTree;
