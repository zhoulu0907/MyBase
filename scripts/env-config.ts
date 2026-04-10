/**
 * 环境配置文件
 * 每个环境定义独立的服务地址配置
 */

export type EnvironmentKey = 'lingji-dev' | 'lingji-sit' | 'tiangong-dev' | 'tiangong-sit';

export interface EnvironmentConfig {
  name: string;
  // OneBase 服务端地址
  ONEBASESERVER_BASE_URL: string;
  ONEBASERUNTIMESERVER_BASE_URL: string;
  // 前端应用地址
  APP_BUILDER_FE_URL: string;
  APP_RUNTIME_FE_URL: string;
  APP_MOBILE_BUILDER_FE_URL: string;
  APP_MOBILE_RUNTIME_FE_URL: string;
  // 数据集和仪表板服务地址
  APP_BUILDER_DATASET_URL: string;
  APP_BUILDER_DASHBOARD_URL: string;
  // Chatbot 服务地址
  CHATBOT_BASE_URL: string;
  // Dashboard 配置
  DASHBOARD_URL: string;
  PREVIEW_URL: string;
  DATASET_URL: string;
  // AI 配置
  AI_GENAPP_URL: string;
  AI_COPILOT_URL: string;
  // 监督插件配置
  SUPERVISION_ENABLE: boolean;
  SUPERVISION_PLATFORM: string;
  SUPERVISION_URL: string;
  SUPERVISION_SSO_URL: string;
  // SSO 配置（灵畿）
  LINGJI_HOME_URL: string;
  LINGJI_SSO_SOURCE_ID: string;
}

export const ENVIRONMENTS: Record<EnvironmentKey, EnvironmentConfig> = {
  // ==================== 灵畿环境 ====================
  'lingji-dev': {
    name: '灵畿开发环境',
    ONEBASESERVER_BASE_URL: 'http://onebase.4c-uat.hq.cmcc:20011/observerbuilder',
    ONEBASERUNTIMESERVER_BASE_URL: 'http://onebase.4c-uat.hq.cmcc:20011/observerruntime',
    APP_BUILDER_FE_URL: 'http://onebase.4c-uat.hq.cmcc:20011/appbuilder',
    APP_RUNTIME_FE_URL: 'http://onebase.4c-uat.hq.cmcc:20011/appruntime',
    APP_MOBILE_BUILDER_FE_URL: 'http://onebase.4c-uat.hq.cmcc:20011/mobilebuilder',
    APP_MOBILE_RUNTIME_FE_URL: 'http://onebase.4c-uat.hq.cmcc:20011/mobileruntime',
    APP_BUILDER_DATASET_URL: 'http://onebase.4c-uat.hq.cmcc:20011/observerbuilder',
    APP_BUILDER_DASHBOARD_URL: 'http://onebase.4c-uat.hq.cmcc:20011/appdashboard/#/',
    CHATBOT_BASE_URL: '',
    DASHBOARD_URL: 'http://onebase.4c-uat.hq.cmcc:20011/observerbuilder',
    PREVIEW_URL: 'http://onebase.4c-uat.hq.cmcc:20011/appdashboard/#/chart/preview',
    DATASET_URL: 'http://10.0.104.38:8100/de2api',
    // AI 配置
    AI_GENAPP_URL: 'http://onebase.4c-uat.hq.cmcc:20011/aigenapp/',
    AI_COPILOT_URL: 'http://onebase.4c-uat.hq.cmcc:20011/aicopilot/',
    // 监督插件配置
    SUPERVISION_ENABLE: true,
    SUPERVISION_PLATFORM: '01',
    SUPERVISION_URL: 'http://supervision-web-uat1.4c-uat.cmdevops.cn:20011',
    SUPERVISION_SSO_URL: 'http://4c-uat.hq.cmcc/moss/micrologin/#/sso/getauthorizecode',
    // SSO 配置（灵畿）
    LINGJI_HOME_URL: 'https://rdcloud.4c-uat.hq.cmcc/cmdevops-platform-desktop-web/home',
    LINGJI_SSO_SOURCE_ID: '5570132830',
  },

  'lingji-sit': {
    name: '灵畿 SIT 环境',
    ONEBASESERVER_BASE_URL: 'http://onebase.4c-uat3.hq.cmcc:20018/observerbuilder',
    ONEBASERUNTIMESERVER_BASE_URL: 'http://onebase.4c-uat3.hq.cmcc:20018/observerruntime',
    APP_BUILDER_FE_URL: 'http://onebase.4c-uat3.hq.cmcc:20018/appbuilder',
    APP_RUNTIME_FE_URL: 'http://onebase.4c-uat3.hq.cmcc:20018/appruntime',
    APP_MOBILE_BUILDER_FE_URL: 'http://onebase.4c-uat3.hq.cmcc:20018/mobilebuilder',
    APP_MOBILE_RUNTIME_FE_URL: 'http://onebase.4c-uat3.hq.cmcc:20018/mobileruntime',
    APP_BUILDER_DATASET_URL: 'http://onebase.4c-uat3.hq.cmcc:20018/observerbuilder',
    APP_BUILDER_DASHBOARD_URL: 'http://onebase.4c-uat3.hq.cmcc:20018/appdashboard/#/',
    CHATBOT_BASE_URL: '',
    DASHBOARD_URL: 'http://onebase.4c-uat3.hq.cmcc:20018/observerbuilder',
    PREVIEW_URL: 'http://onebase.4c-uat3.hq.cmcc:20018/appdashboard/#/chart/preview',
    DATASET_URL: 'http://10.0.104.38:8100/de2api',
    // AI 配置
    AI_GENAPP_URL: 'http://onebase.4c-uat3.hq.cmcc:20018/aigenapp/',
    AI_COPILOT_URL: 'http://onebase.4c-uat3.hq.cmcc:20018/aicopilot/',
    // 监督插件配置
    SUPERVISION_ENABLE: true,
    SUPERVISION_PLATFORM: '01',
    SUPERVISION_URL: 'http://4c-uat3.hq.cmcc/supervision',
    SUPERVISION_SSO_URL: 'http://4c-uat3.hq.cmcc/moss/micrologin/#/sso/getauthorizecode',
    // SSO 配置（灵畿）
    LINGJI_HOME_URL: 'https://4c-uat3.hq.cmcc/cmdevops-platform-desktop-web/home',
    LINGJI_SSO_SOURCE_ID: '5570132830',
  },

  // ==================== 天工环境 ====================
  'tiangong-dev': {
    name: '天工开发环境',
    ONEBASESERVER_BASE_URL: 'https://onebase-sit.artifex-cmcc.com.cn/observerbuilder',
    ONEBASERUNTIMESERVER_BASE_URL: 'https://onebase-sit.artifex-cmcc.com.cn/observerruntime',
    APP_BUILDER_FE_URL: 'https://onebase-sit.artifex-cmcc.com.cn/appbuilder',
    APP_RUNTIME_FE_URL: 'https://onebase-sit.artifex-cmcc.com.cn/appruntime',
    APP_MOBILE_BUILDER_FE_URL: 'https://onebase-sit.artifex-cmcc.com.cn/mobilebuilder',
    APP_MOBILE_RUNTIME_FE_URL: 'https://onebase-sit.artifex-cmcc.com.cn/mobileruntime',
    APP_BUILDER_DATASET_URL: 'https://onebase-sit.artifex-cmcc.com.cn/observerbuilder',
    APP_BUILDER_DASHBOARD_URL: 'https://onebase-sit.artifex-cmcc.com.cn/appdashboard/#/',
    CHATBOT_BASE_URL: '',
    DASHBOARD_URL: 'https://onebase-sit.artifex-cmcc.com.cn/observerbuilder',
    PREVIEW_URL: 'https://onebase-sit.artifex-cmcc.com.cn/appdashboard/#/chart/preview',
    DATASET_URL: 'https://onebase-sit.artifex-cmcc.com.cn/de2api',
    // AI 配置
    AI_GENAPP_URL: 'https://onebase-sit.artifex-cmcc.com.cn/aigenapp/',
    AI_COPILOT_URL: 'https://onebase-sit.artifex-cmcc.com.cn/aicopilot/',
    // 监督插件配置（天工不需要）
    SUPERVISION_ENABLE: false,
    SUPERVISION_PLATFORM: '',
    SUPERVISION_URL: '',
    SUPERVISION_SSO_URL: '',
    // SSO 配置（天工无灵畿 SSO）
    LINGJI_HOME_URL: '',
    LINGJI_SSO_SOURCE_ID: '',
  },

  'tiangong-sit': {
    name: '天工 SIT 环境',
    ONEBASESERVER_BASE_URL: 'https://onebase-sit.artifex-cmcc.com.cn/observerbuilder',
    ONEBASERUNTIMESERVER_BASE_URL: 'https://onebase-sit.artifex-cmcc.com.cn/observerruntime',
    APP_BUILDER_FE_URL: 'https://onebase-sit.artifex-cmcc.com.cn/appbuilder',
    APP_RUNTIME_FE_URL: 'https://onebase-sit.artifex-cmcc.com.cn/appruntime',
    APP_MOBILE_BUILDER_FE_URL: 'https://onebase-sit.artifex-cmcc.com.cn/mobilebuilder',
    APP_MOBILE_RUNTIME_FE_URL: 'https://onebase-sit.artifex-cmcc.com.cn/mobileruntime',
    APP_BUILDER_DATASET_URL: 'https://onebase-sit.artifex-cmcc.com.cn/observerbuilder',
    APP_BUILDER_DASHBOARD_URL: 'https://onebase-sit.artifex-cmcc.com.cn/appdashboard/#/',
    CHATBOT_BASE_URL: '',
    DASHBOARD_URL: 'https://onebase-sit.artifex-cmcc.com.cn/observerbuilder',
    PREVIEW_URL: 'https://onebase-sit.artifex-cmcc.com.cn/appdashboard/#/chart/preview',
    DATASET_URL: 'https://onebase-sit.artifex-cmcc.com.cn/de2api',
    // AI 配置
    AI_GENAPP_URL: 'https://dev-sit.artifex-cmcc.com.cn/aigenapp/',
    AI_COPILOT_URL: 'https://dev-sit.artifex-cmcc.com.cn/aicopilot/',
    // 监督插件配置（天工不需要）
    SUPERVISION_ENABLE: false,
    SUPERVISION_PLATFORM: '',
    SUPERVISION_URL: '',
    SUPERVISION_SSO_URL: '',
    // SSO 配置（天工无灵畿 SSO）
    LINGJI_HOME_URL: '',
    LINGJI_SSO_SOURCE_ID: '',
  },
};

// 环境列表（用于帮助信息）
export const ENVIRONMENT_LIST = Object.keys(ENVIRONMENTS) as EnvironmentKey[];