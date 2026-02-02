/**
 * Workbench 独有配置
 */
import { DATA_CONFIG_RANGE, WORKBENCH_CONFIG_TYPES } from '../core/constants';
import type { 
  IEntryGroupConfigType, 
  IDataConfigConfigType, 
  IEntryTitleConfigType, 
  IThemeConfigType,
  IStatusConfigType,
  ITextConfigType,
  IWbMenuSelectorConfigType,
  ILabelConfigType,
  INumberConfigType,
  IBooleanConfigType
} from '../core/types';

export const entryGroupConfig: IEntryGroupConfigType = {
  key: 'groupConfig',
  name: '入口配置',
  type: WORKBENCH_CONFIG_TYPES.WB_ENTRY_GROUP
};

export const entryStyleConfig: IThemeConfigType = {
  key: 'styleConfig',
  name: '样式库',
  type: WORKBENCH_CONFIG_TYPES.WB_THEME_SELECTOR
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

/**
 * 跳转目标配置（关联已有页面 | 跳转外部链接）
 */
export const jumpTypeConfig: IStatusConfigType<string> = {
  key: 'jumpType',
  name: '跳转目标',
  type: WORKBENCH_CONFIG_TYPES.STATUS_RADIO,
  range: [
    {
      key: 'internal',
      text: '关联已有页面',
      value: 'internal'
    },
    {
      key: 'external',
      text: '跳转外部链接',
      value: 'external'
    }
  ]
};

/**
 * 选择页面配置（当跳转类型为关联已有页面时使用）
 */
export const jumpPageIdConfig: IWbMenuSelectorConfigType = {
  key: 'jumpPageId',
  name: '选择页面',
  type: WORKBENCH_CONFIG_TYPES.WB_MENU_SELECTOR
};

/**
 * 外部链接配置（当跳转类型为跳转外部链接时使用）
 */
export const jumpExternalUrlConfig: ITextConfigType = {
  key: 'jumpExternalUrl',
  name: '外部链接',
  type: WORKBENCH_CONFIG_TYPES.TEXT_INPUT
};

/**
 * 标题名称配置（通用标题配置）
 */
export const labelNameConfig: ILabelConfigType = {
  key: 'label',
  name: '标题名称',
  type: WORKBENCH_CONFIG_TYPES.LABEL_INPUT
};

/**
 * 样式库配置（主题选择器）
 */
export const themeConfig: IThemeConfigType = {
  key: 'theme',
  name: '样式库',
  type: WORKBENCH_CONFIG_TYPES.WB_THEME_SELECTOR
};

/**
 * 数据条数配置
 */
export const dataCountConfig: INumberConfigType = {
  key: 'dataCount',
  name: '数据条数',
  type: WORKBENCH_CONFIG_TYPES.NUMBER_INPUT
};

/**
 * 自动轮播配置
 */
export const autoplayConfig: IBooleanConfigType = {
  key: 'autoplay',
  name: '自动轮播',
  type: WORKBENCH_CONFIG_TYPES.SWITCH_INPUT
};

/**
 * 轮播间隔配置
 */
export const intervalConfig: INumberConfigType = {
  key: 'interval',
  name: '轮播间隔',
  type: WORKBENCH_CONFIG_TYPES.NUMBER_INPUT
};

