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
  WORKBENCH_THEME_OPTIONS
} from '../../core/constants';
import { ILabelConfigType, IBooleanConfigType, TBooleanDefaultType, TTextDefaultType, IThemeConfigType, IWbCheckInputConfigType } from '../../core/types';
import { IDataConfigConfigType } from '../../core/types';
import { themeConfig, welcomeDescConfig, welcomeTextConfig } from '../../config/commonConfig';

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
  | IThemeConfigType
  | IWbCheckInputConfigType
>;

export interface XWelcomeCardConfig extends ICommonBaseWorkbenchType {
  componentName: string;
  welcomeText: {
    text: TTextDefaultType;
    display: TBooleanDefaultType;
  };
  theme: string;
  userAvatar: TTextDefaultType;
  userName: TTextDefaultType;
  welcomeDesc: TTextDefaultType;
  status?: TRadioDefaultType<TWorkbenchStatusSelectKeyType>;
  width: TSelectDefaultType<TWorkbenchWidthSelectKeyType>;
}

const XWelcomeCard: XWelcomeCardSchema = {
  editData: [
    ...workbenchBaseConfig,
    workbenchStatusConfig,
    workbenchWidthConfig,
    welcomeTextConfig,
    themeConfig,
    welcomeDescConfig
  ],
  
  config: {
    ...workbenchBaseDefault,
    componentName: 'TodoCenter',
    welcomeText: {
      text: '下午好！',
      display: true
    },
    userAvatar: '',
    userName: '',
    theme: WORKBENCH_THEME_OPTIONS.THEME_1,
    width: WORKBENCH_WIDTH_VALUES[WORKBENCH_WIDTH_OPTIONS.QUARTER],
    status: WORKBENCH_STATUS_VALUES[WORKBENCH_STATUS_OPTIONS.DEFAULT],
    welcomeDesc: '开心工作，认真生活'
  }
};

export default XWelcomeCard;

