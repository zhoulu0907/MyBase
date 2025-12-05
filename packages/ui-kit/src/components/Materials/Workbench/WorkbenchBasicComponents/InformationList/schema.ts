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
} from '../../workbenchShared';
import {
  WORKBENCH_STATUS_OPTIONS,
  WORKBENCH_STATUS_VALUES,
  WORKBENCH_WIDTH_OPTIONS,
  WORKBENCH_WIDTH_VALUES,
  WORKBENCH_CONFIG_TYPES,
  DATA_CONFIG_RANGE
} from '../../constants';
import { ILabelConfigType, IBooleanConfigType, TBooleanDefaultType, TTextDefaultType } from '../../../types';
import { IDataConfigConfigType } from '../../types';

export interface XInformationListSchema {
  editData: TXInformationListEditData;
  config: XInformationListConfig;
}

export type TXInformationListEditData = Array<
  | ILabelConfigType
  | ITextConfigType
  | IStatusConfigType<TWorkbenchStatusSelectKeyType>
  | IWidthConfigType<TWorkbenchWidthSelectKeyType>
  | IBooleanConfigType
  | IDataConfigConfigType
>;

export interface XInformationListConfig extends ICommonBaseWorkbenchType {
  componentName: string;
  label: {
    text: TTextDefaultType;
    display: TBooleanDefaultType;
  };
  dataConfig: {
      showPending: boolean;
      showCreated: boolean;
      showHandled: boolean;
      showCc: boolean;
    }
  status?: TRadioDefaultType<TWorkbenchStatusSelectKeyType>;
  width: TSelectDefaultType<TWorkbenchWidthSelectKeyType>;
}

const XInformationList: XInformationListSchema = {
  editData: [...workbenchBaseConfig, workbenchStatusConfig, workbenchWidthConfig, {
    key: 'label',
    name: '标题名称',
    type: WORKBENCH_CONFIG_TYPES.LABEL_INPUT
  }, 
  {
    key: 'dataConfig',
    name: '数据内容配置',
    type: WORKBENCH_CONFIG_TYPES.DATA_CONFIG,
    range: DATA_CONFIG_RANGE
  }],
  config: {
    ...workbenchBaseDefault,
    componentName: 'TodoCenter',
    label: {
      text: '待办中心',
      display: true
    },
    dataConfig: {
      showPending: true,
      showCreated: true,
      showHandled: true,
      showCc: true
    },
    width: WORKBENCH_WIDTH_VALUES[WORKBENCH_WIDTH_OPTIONS.FULL],
    status: WORKBENCH_STATUS_VALUES[WORKBENCH_STATUS_OPTIONS.DEFAULT],
  }
};

export default XInformationList;

