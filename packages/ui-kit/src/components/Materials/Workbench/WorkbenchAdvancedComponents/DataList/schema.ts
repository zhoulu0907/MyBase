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
import { ILabelConfigType, IBooleanConfigType, TBooleanDefaultType, TTextDefaultType, IWbCheckInputConfigType, ITableConfigType } from '../../core/types';
import { IDataConfigConfigType } from '../../core/types';
import { labelNameConfig, tableUUidConfig } from '../../config/commonConfig';

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
  | IDataConfigConfigType
  | IWbCheckInputConfigType
  | ITableConfigType
>;

export interface XDataListConfig extends ICommonBaseWorkbenchType {
  componentName: string;
  label: {
    text: TTextDefaultType;
    display: TBooleanDefaultType;
  };
  tableUUid: string;
  status?: TRadioDefaultType<TWorkbenchStatusSelectKeyType>;
  width: TSelectDefaultType<TWorkbenchWidthSelectKeyType>;
}

const XDataList: XDataListSchema = {
  editData: [...workbenchBaseConfig, workbenchStatusConfig, workbenchWidthConfig,
    labelNameConfig,
    tableUUidConfig
  ],
  
  config: {
    ...workbenchBaseDefault,
    componentName: 'DataList',
    label: {
      text: '数据列表',
      display: true
    },
    tableUUid: '',
    width: WORKBENCH_WIDTH_VALUES[WORKBENCH_WIDTH_OPTIONS.FULL],
    status: WORKBENCH_STATUS_VALUES[WORKBENCH_STATUS_OPTIONS.DEFAULT],
  }
};

export default XDataList;

