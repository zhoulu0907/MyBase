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
  WORKBENCH_WIDTH_VALUES,
  WORKBENCH_CONFIG_TYPES,
  DATA_CONFIG_RANGE
} from '../../core/constants';
import { ILabelConfigType, IBooleanConfigType, TBooleanDefaultType, TTextDefaultType } from '../../core/types';
import { IDataConfigConfigType } from '../../core/types';

export interface XWelcomeCardSchema {
  editData: TXWelcomeCardEditData;
  config: XWelcomeCardConfig;
}

export type TXWelcomeCardEditData = Array<
  | ILabelConfigType
  | ITextConfigType
  | IStatusConfigType<TWorkbenchStatusSelectKeyType>
  | IWidthConfigType<TWorkbenchWidthSelectKeyType>
  | IBooleanConfigType
  | IDataConfigConfigType
>;

export interface XWelcomeCardConfig extends ICommonBaseWorkbenchType {
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

const XWelcomeCard: XWelcomeCardSchema = {
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

export default XWelcomeCard;

