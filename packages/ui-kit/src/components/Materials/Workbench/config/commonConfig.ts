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
  IBooleanConfigType,
  IWbCheckInputConfigType,
  IWbColorConfigType,
  IWbRichTextContentConfigType,
  IWbSliderConfigType,
  IWbTextAlignConfigType,
  ITableConfigType
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
 * 查看更多跳转目标配置（关联已有页面 | 跳转外部链接）
 */
export const checkMoreJumpTypeConfig: IStatusConfigType<string> = {
  key: 'jumpType',
  name: '链接类型',
  type: WORKBENCH_CONFIG_TYPES.STATUS_RADIO,
  range: [
    {
      key: 'internal',
      text: '页面',
      value: 'internal'
    },
    {
      key: 'external',
      text: '链接',
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
  type: WORKBENCH_CONFIG_TYPES.NUMBER_INPUT,
  defaultValue: 2,
  min: 0,
  max: 10,
  range: [0, 10]
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

/**
 * 按钮组件-标题配置（与通用“标题名称”文案不同）
 */
export const buttonLabelConfig: ILabelConfigType = {
  key: 'label',
  name: '标题配置',
  type: WORKBENCH_CONFIG_TYPES.LABEL_INPUT
};

/**
 * 按钮组件-背景颜色配置
 */
export const buttonBackgroundColorConfig: IWbColorConfigType = {
  key: 'backgroundColor',
  name: '背景颜色',
  type: WORKBENCH_CONFIG_TYPES.WB_COLOR
};

/**
 * 按钮组件-文本颜色配置
 */
export const buttonTextColorConfig: IWbColorConfigType = {
  key: 'textColor',
  name: '文本颜色',
  type: WORKBENCH_CONFIG_TYPES.WB_COLOR
};

/**
 * 按钮组件-文本大小配置
 */
export const buttonTextSizeConfig: IWbSliderConfigType = {
  key: 'textSize',
  name: '文本大小',
  type: WORKBENCH_CONFIG_TYPES.WB_SLIDER,
  min: 12,
  max: 40,
  step: 1
};

/**
 * 按钮组件-文本对齐配置
 */
export const buttonTextAlignConfig: IWbTextAlignConfigType = {
  key: 'textAlign',
  name: '文本对齐',
  type: WORKBENCH_CONFIG_TYPES.WB_TEXT_ALIGN
};

/**
 * 资讯列表-查看更多开关
 */
export const showMoreConfig: IBooleanConfigType = {
  key: 'showMore',
  name: '查看更多',
  type: WORKBENCH_CONFIG_TYPES.SWITCH_INPUT
};

/**
 * 资讯列表-查看更多链接
 */
export const showMoreLinkConfig: ITextConfigType = {
  key: 'showMoreLink',
  name: '查看更多链接',
  type: WORKBENCH_CONFIG_TYPES.TEXT_INPUT
};

/**
 * 欢迎卡片-欢迎语（带 checkbox）
 */
export const welcomeTextConfig: IWbCheckInputConfigType = {
  key: 'welcomeText',
  name: '欢迎语',
  type: WORKBENCH_CONFIG_TYPES.WB_CHECK_INPUT,
  checkboxLabel: '显示用户名'
};

/**
 * 欢迎卡片-辅助语
 */
export const welcomeDescConfig: ITextConfigType = {
  key: 'welcomeDesc',
  name: '辅助语',
  type: WORKBENCH_CONFIG_TYPES.TEXT_INPUT
};

/**
 * 富文本-背景颜色
 */
export const richTextBgColorConfig: IWbColorConfigType = {
  key: 'Wb_Color',
  name: '背景颜色',
  type: WORKBENCH_CONFIG_TYPES.WB_COLOR
};

/**
 * 富文本-内容
 */
export const richTextContentConfig: IWbRichTextContentConfigType = {
  key: 'Wb_RichTextContent',
  name: '富文本内容',
  type: WORKBENCH_CONFIG_TYPES.WB_RICH_TEXT_CONTENT
};

/**
 * 数据列表-表格配置
 */
export const tableInfoConfig: ITableConfigType = {
  key: 'tableInfo',
  name: '表格配置',
  type: WORKBENCH_CONFIG_TYPES.WB_TABLE_CONFIG
};

