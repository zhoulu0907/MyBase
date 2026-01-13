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
  DATA_CONFIG_RANGE,
  WORKBENCH_THEME_OPTIONS
} from '../../core/constants';
import { ILabelConfigType, IBooleanConfigType, TBooleanDefaultType, TTextDefaultType } from '../../core/types';
import { IDataConfigConfigType, IThemeConfigType, INumberConfigType } from '../../core/types';

export interface XTodoListSchema {
  editData: TXTodoListEditData;
  config: XTodoListConfig;
}

export type TXTodoListEditData = Array<
  | ILabelConfigType
  | ITextConfigType
  | IStatusConfigType<TWorkbenchStatusSelectKeyType>
  | IWidthConfigType<TWorkbenchWidthSelectKeyType>
  | IBooleanConfigType
  | IDataConfigConfigType
  | IThemeConfigType
  | INumberConfigType
>;

export interface XTodoListConfig extends ICommonBaseWorkbenchType {
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
  theme: string;
  dataCount: number;
  userAvatar: TTextDefaultType;
  userName: TTextDefaultType;
  status?: TRadioDefaultType<TWorkbenchStatusSelectKeyType>;
  width: TSelectDefaultType<TWorkbenchWidthSelectKeyType>;
}

const XTodoList: XTodoListSchema = {
  editData: [...workbenchBaseConfig, workbenchStatusConfig, workbenchWidthConfig, {
    key: 'label',
    name: '标题名称',
    type: WORKBENCH_CONFIG_TYPES.LABEL_INPUT
  }, 
  {
    key: 'dataConfig',
    name: '数据内容配置',
    type: WORKBENCH_CONFIG_TYPES.WB_DATA_CONFIG,
    range: DATA_CONFIG_RANGE
  },
  {
    key: 'theme',
    name: '样式库',
    type: WORKBENCH_CONFIG_TYPES.WB_THEME_SELECTOR
  }, {
    key: 'dataCount',
    name: '数据条数',
    type: WORKBENCH_CONFIG_TYPES.NUMBER_INPUT,
  }],
  config: {
    ...workbenchBaseDefault,
    componentName: 'TodoList',
    theme: WORKBENCH_THEME_OPTIONS.THEME_1,
    label: {
      text: '待办列表',
      display: true
    },
    dataConfig: {
      showPending: true,
      showCreated: true,
      showHandled: true,
      showCc: true
    },
    dataCount: 5,
    userAvatar: '',
    userName: '',
    width: WORKBENCH_WIDTH_VALUES[WORKBENCH_WIDTH_OPTIONS.FULL],
    status: WORKBENCH_STATUS_VALUES[WORKBENCH_STATUS_OPTIONS.DEFAULT],
  }
};

export default XTodoList;

