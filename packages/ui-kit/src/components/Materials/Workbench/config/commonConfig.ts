/**
 * Workbench 独有配置
 */
import { DATA_CONFIG_RANGE, WORKBENCH_CONFIG_TYPES } from '../core/constants';
import type { IEntryGroupConfigType, IDataConfigConfigType, IEntryStyleConfigType, IEntryTitleConfigType } from '../core/types';

export const entryGroupConfig: IEntryGroupConfigType = {
  key: 'groupConfig',
  name: '入口配置',
  type: WORKBENCH_CONFIG_TYPES.WB_ENTRY_GROUP
};

export const entryStyleConfig: IEntryStyleConfigType = {
  key: 'styleConfig',
  name: '样式库',
  type: WORKBENCH_CONFIG_TYPES.WB_ENTRY_STYLE
};

export const entryTitleConfig: IEntryTitleConfigType = {
  key: 'titleConfig',
  name: '标题配置',
  type: WORKBENCH_CONFIG_TYPES.WB_ENTRY_TITLE
};

export const dataConfigConfig: IDataConfigConfigType = {
  key: 'dataConfig',
  name: '数据内容配置',
  type: WORKBENCH_CONFIG_TYPES.WB_DATA_CONFIG,
  range: DATA_CONFIG_RANGE
};

