/**
 * 监督插件配置文件
 * 用于配置监督插件的启用状态、平台版本和访问地址
 * 环境: 灵畿开发环境
 */
window.supervision_config = {
  // 是否启用监督插件（默认启用，支持关闭）
  ENABLE: true,

  // 平台版本：01 = IT公司α版本, 02 = 互联网公司β版本
  PLATFORM: '01',

  // 监督平台访问地址
  SUPERVISION_URL: 'http://supervision-web-uat1.4c-uat.cmdevops.cn:20011',

  // 单点登录地址
  SSO_URL: 'http://4c-uat.hq.cmcc/moss/micrologin/#/sso/getauthorizecode'
};
