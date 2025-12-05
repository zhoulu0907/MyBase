import { DATA_CONFIG_RANGE, WORKBENCH_CONFIG_TYPES } from './constants';
import type { IQuickEntryConfigType, IDataConfigConfigType } from './types';

/**
 * Workbench 独有配置
 */
export const quickEntryConfig: IQuickEntryConfigType = {
  key: 'props',
  name: '快捷入口配置',
  type: WORKBENCH_CONFIG_TYPES.QUICK_ENTRY
};

export const dataConfigConfig: IDataConfigConfigType = {
  key: 'dataConfig',
  name: '数据内容配置',
  type: WORKBENCH_CONFIG_TYPES.DATA_CONFIG,
  range: DATA_CONFIG_RANGE
};
