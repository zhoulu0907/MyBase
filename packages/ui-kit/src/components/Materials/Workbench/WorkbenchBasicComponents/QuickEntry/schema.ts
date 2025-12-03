import { quickEntryConfig } from '../../common';
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
  ITextConfigType
} from '../../workbenchShared';
import {
  WORKBENCH_STATUS_OPTIONS,
  WORKBENCH_STATUS_VALUES,
  WORKBENCH_WIDTH_OPTIONS,
  WORKBENCH_WIDTH_VALUES
} from '../../constants';
import { QUICK_ENTRY_THEME_OPTIONS, QUICK_ENTRY_THEME_VALUES } from '../constants';
import type { IQuickEntryConfigType, QuickEntryPropsConfig } from '../../types';
export interface XQuickEntrySchema {
  editData: TXQuickEntryEditData;
  config: XQuickEntryConfig;
}

export type TXQuickEntryEditData = Array<
  | ITextConfigType
  | IQuickEntryConfigType
  | IStatusConfigType<TWorkbenchStatusSelectKeyType>
  | IWidthConfigType<TWorkbenchWidthSelectKeyType>
>;

export interface XQuickEntryConfig extends ICommonBaseWorkbenchType {
  componentName: string;
  props: QuickEntryPropsConfig;
  status?: TRadioDefaultType<TWorkbenchStatusSelectKeyType>;
  width: TSelectDefaultType<TWorkbenchWidthSelectKeyType>;
}

const XQuickEntry: XQuickEntrySchema = {
  editData: [...workbenchBaseConfig, quickEntryConfig, workbenchStatusConfig, workbenchWidthConfig],
  config: {
    ...workbenchBaseDefault,
    componentName: 'QuickEntry',
    props: {
      titleConfig: {
        showTitle: true,
        titleName: '快捷入口',
        showMore: true
      },
      styleConfig: {
        theme: QUICK_ENTRY_THEME_VALUES[QUICK_ENTRY_THEME_OPTIONS.THEME_1]
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
      }
    },
    width: WORKBENCH_WIDTH_VALUES[WORKBENCH_WIDTH_OPTIONS.FULL],
    status: WORKBENCH_STATUS_VALUES[WORKBENCH_STATUS_OPTIONS.DEFAULT]
  }
};

export default XQuickEntry;
