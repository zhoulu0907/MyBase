/**
 * 灵畿 SSO 配置文件
 * 容器部署时可通过挂载此文件注入配置
 */
window.SSO_CONFIG = {
  // 灵畿前端首页地址（未登录时跳转）
  lingjiHome: 'https://rdcloud.4c-uat.hq.cmcc/cmdevops-platform-desktop-web/home',
//   UAT3: https://4c-uat3.hq.cmcc/cmdevops-platform-desktop-web/home
//   lingjiHome: 'https://rdcloud.4c-uat.hq.cmcc/cmdevops-platform-desktop-web/home',
  
  // 后端 SSO 登录接口（完整路径）
  ssoLoginApi: 'http://onebase.4c-uat.hq.cmcc:20011/observerbuilder/admin-api/system/lingji-sso/login',
  
  // 登录成功后跳转地址前缀（完整路径，teanetId 从后端接口返回）
  successRedirectBase: 'http://onebase.4c-uat.hq.cmcc:20011/appbuilder'
};
