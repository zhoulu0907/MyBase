import {
  workbenchBaseConfig,
  workbenchBaseDefault,
  workbenchStatusConfig,
  workbenchWidthConfig,
  IStatusConfigType,
  ITextConfigType,
  IWidthConfigType,
  TRadioDefaultType,
  TSelectDefaultType,
  type ICommonBaseWorkbenchType,
  type TWorkbenchStatusSelectKeyType,
  type TWorkbenchWidthSelectKeyType
} from '../../config/workbenchShared';
import {
  WORKBENCH_STATUS_OPTIONS,
  WORKBENCH_STATUS_VALUES,
  WORKBENCH_WIDTH_OPTIONS,
  WORKBENCH_WIDTH_VALUES
} from '../../core/constants';
import { ILabelConfigType, IBooleanConfigType, TBooleanDefaultType, TTextDefaultType, IWbCheckInputConfigType, ITableConfigType, INumberConfigType } from '../../core/types';
import { labelNameConfig, tableInfoConfig, dataCountConfig } from '../../config/commonConfig';

export interface XDataListSchema {
  editData: TXDataListEditData;
  config: XDataListConfig;
}

export type TXDataListEditData = Array<
  | ILabelConfigType
  | ITextConfigType
  | IStatusConfigType<TWorkbenchStatusSelectKeyType>
  | IWidthConfigType<TWorkbenchWidthSelectKeyType>
  | IBooleanConfigType
  | IWbCheckInputConfigType
  | ITableConfigType
  | INumberConfigType
>;

export interface XDataListConfig extends ICommonBaseWorkbenchType {
  componentName: string;
  label: {
    text: TTextDefaultType;
    display: TBooleanDefaultType;
  };
  tableInfo: {
    componentId: string; // 组件id
    tableName: string; // 绑定的资产
    metaData: string; // 绑定的资产uuid
    columns: object[]; // 表头内容
  };
  dataCount: number;
  status?: TRadioDefaultType<TWorkbenchStatusSelectKeyType>;
  width: TSelectDefaultType<TWorkbenchWidthSelectKeyType>;
}

const XDataList: XDataListSchema = {
  editData: [...workbenchBaseConfig, workbenchStatusConfig, workbenchWidthConfig,
    labelNameConfig,
    tableInfoConfig,
    dataCountConfig
  ],
  
  config: {
    ...workbenchBaseDefault,
    componentName: 'DataList',
    label: {
      text: '数据列表',
      display: true
    },
    tableInfo: {
      componentId: '',
      tableName: '',
      metaData: '',
      columns: [],
    },
    dataCount: 5,
    width: WORKBENCH_WIDTH_VALUES[WORKBENCH_WIDTH_OPTIONS.FULL],
    status: WORKBENCH_STATUS_VALUES[WORKBENCH_STATUS_OPTIONS.DEFAULT],
  }
};

export default XDataList;

