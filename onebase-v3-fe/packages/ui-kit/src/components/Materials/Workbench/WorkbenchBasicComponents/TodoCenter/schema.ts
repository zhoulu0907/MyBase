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
import { IDataConfigConfigType, IThemeConfigType } from '../../core/types';
import { labelNameConfig, themeConfig, dataConfigConfig } from '../../config/commonConfig';

export interface XTodoCenterSchema {
  editData: TXTodoCenterEditData;
  config: XTodoCenterConfig;
}

export type TXTodoCenterEditData = Array<
  | ILabelConfigType
  | ITextConfigType
  | IStatusConfigType<TWorkbenchStatusSelectKeyType>
  | IWidthConfigType<TWorkbenchWidthSelectKeyType>
  | IBooleanConfigType
  | IDataConfigConfigType
  | IThemeConfigType
>;

export interface XTodoCenterConfig extends ICommonBaseWorkbenchType {
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
  status?: TRadioDefaultType<TWorkbenchStatusSelectKeyType>;
  width: TSelectDefaultType<TWorkbenchWidthSelectKeyType>;
}

const XTodoCenter: XTodoCenterSchema = {
  editData: [...workbenchBaseConfig, workbenchStatusConfig, workbenchWidthConfig,
    labelNameConfig,
    dataConfigConfig,
    themeConfig
  ],
  config: {
    ...workbenchBaseDefault,
    componentName: 'TodoCenter',
    theme: WORKBENCH_THEME_OPTIONS.THEME_1,
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

export default XTodoCenter;

