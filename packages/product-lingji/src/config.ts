/**
 * 灵畿平台配置
 */

export const config = {
  platform: 'lingji',
  platformName: '灵畿',
  features: {
    supervisionPlugin: true,
    ssoLogin: true,
    oauthLogin: false,
  },
  routes: {
    callback: '/lingji-callback',
  },
};

export type PlatformConfig = typeof config;