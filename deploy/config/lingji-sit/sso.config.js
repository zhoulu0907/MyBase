/**
 * 灵畿 SSO 配置文件
 * 容器部署时可通过挂载此文件注入配置
 * 环境: 灵畿 SIT 环境
 */
window.SSO_CONFIG = {
  // 灵畿前端首页地址（未登录时跳转）
  lingjiHome: 'https://4c-uat3.hq.cmcc/cmdevops-platform-desktop-web/home',

  // 灵畿 SSO 应用ID
  sourceid: '5570132830',

  // 后端 SSO 登录接口（完整路径）
  ssoLoginApi: 'http://onebase.4c-uat3.hq.cmcc:20018/observerbuilder/admin-api/system/lingji-sso/login',

  // 登录成功后跳转地址前缀（完整路径，tenantId 从后端接口返回）
  successRedirectBase: 'http://onebase.4c-uat3.hq.cmcc:20018/appbuilder'
};
