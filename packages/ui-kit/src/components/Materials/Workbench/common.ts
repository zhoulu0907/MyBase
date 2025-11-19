import { WORKBENCH_CONFIG_TYPES } from './constants';
import type { IQuickEntryConfigType } from './types';

/**
 * Workbench 独有配置
 */
export const quickEntryConfig: IQuickEntryConfigType = {
  key: 'props',
  name: '快捷入口配置',
  type: WORKBENCH_CONFIG_TYPES.QUICK_ENTRY
};
