import { entryGroupConfig, entryStyleConfig, labelNameConfig, entryTitleConfig } from '../../config/commonConfig';
import {
  workbenchBaseConfig,
  workbenchBaseDefault,
  workbenchStatusConfig,
  workbenchWidthConfig,
  IStatusConfigType,
  IWidthConfigType,
  TRadioDefaultType,
  TSelectDefaultType,
  type ICommonBaseWorkbenchType,
  type TWorkbenchStatusSelectKeyType,
  type TWorkbenchWidthSelectKeyType,
  ITextConfigType,
  ILabelConfigType,
  IBooleanConfigType
} from '../../config/workbenchShared';
import {
  WORKBENCH_STATUS_OPTIONS,
  WORKBENCH_STATUS_VALUES,
  WORKBENCH_WIDTH_OPTIONS,
  WORKBENCH_WIDTH_VALUES,
  WORKBENCH_THEME_OPTIONS
} from '../../core/constants';
import type {
  IEntryGroupConfigType,
  IThemeConfigType,
  IWbMenuSelectorConfigType,
  QuickEntryTitleConfig,
  QuickEntryStyleConfig,
  QuickEntryGroupConfig,
  TBooleanDefaultType,
  TTextDefaultType,
  IEntryTitleConfigType
} from '../../core/types';

export interface XQuickEntrySchema {
  editData: TXQuickEntryEditData;
  config: XQuickEntryConfig;
}

export type TXQuickEntryEditData = Array<
  | ITextConfigType
  | ILabelConfigType
  | IBooleanConfigType
  | IEntryGroupConfigType
  | IThemeConfigType
  | IEntryTitleConfigType
  | IStatusConfigType<TWorkbenchStatusSelectKeyType>
  | IWidthConfigType<TWorkbenchWidthSelectKeyType>
>;

export interface XQuickEntryConfig extends ICommonBaseWorkbenchType {
  componentName: string;
  label: {
    text: TTextDefaultType;
    display: TBooleanDefaultType;
  };
  titleConfig: QuickEntryTitleConfig;
  styleConfig: QuickEntryStyleConfig;
  groupConfig: QuickEntryGroupConfig;
  status?: TRadioDefaultType<TWorkbenchStatusSelectKeyType>;
  width: TSelectDefaultType<TWorkbenchWidthSelectKeyType>;
}

const XQuickEntry: XQuickEntrySchema = {
  editData: [
    ...workbenchBaseConfig,
    labelNameConfig,
    entryTitleConfig,
    entryStyleConfig,
    entryGroupConfig,
    workbenchStatusConfig,
    workbenchWidthConfig
  ],
  config: {
    ...workbenchBaseDefault,
    componentName: 'QuickEntry',
    label: {
      text: '快捷入口',
      display: true
    },
    titleConfig: {
      showMore: true,
      jumpType: 'internal',
      jumpPageId: '',
      jumpExternalUrl: '',
      enableGroup: false
    },
    styleConfig: {
      theme: WORKBENCH_THEME_OPTIONS.THEME_1
    },
    groupConfig: {
      enableGroup: false,
      groups: [
        {
          groupName: '分组1',
          entries: [
            {
              entryName: '客户管理',
              entryIcon: '',
              entryType: '应用菜单',
              menuId: '客户信息',
              linkAddress: 'https://example.com',
              group: '分组1',
              entryDesc: '客户管理'
            },
            {
              entryName: '工时管理',
              entryIcon: '',
              entryType: '应用菜单',
              menuId: '工时信息',
              linkAddress: 'https://example.com',
              group: '分组1',
              entryDesc: '工时管理'
            },
            {
              entryName: '项目管理',
              entryIcon: '',
              entryType: '应用菜单',
              menuId: '项目信息',
              linkAddress: 'https://example.com',
              group: '分组1',
              entryDesc: '项目管理'
            }
          ]
        },
        {
          groupName: '分组2',
          entries: [
            {
              entryName: '客户管理1',
              entryIcon: '',
              entryType: '应用菜单',
              menuId: '客户信息',
              linkAddress: 'https://example.com',
              group: '分组2'
            }
          ]
        }
      ]
    },
    width: WORKBENCH_WIDTH_VALUES[WORKBENCH_WIDTH_OPTIONS.FULL],
    status: WORKBENCH_STATUS_VALUES[WORKBENCH_STATUS_OPTIONS.DEFAULT]
  }
};

export default XQuickEntry;
