/**
 * 天工平台配置
 */

export const config = {
  platform: 'tiangong',
  platformName: '天工',
  features: {
    supervisionPlugin: false,
    ssoLogin: false,
    oauthLogin: true,
  },
  routes: {
    callback: '/oauth/obbuilder/:appName',
  },
};

export type PlatformConfig = typeof config;