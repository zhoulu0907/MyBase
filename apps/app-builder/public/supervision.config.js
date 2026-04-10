/**
 * 监督插件配置文件
 * 用于配置监督插件的启用状态、平台版本和访问地址
 */
window.supervision_config = {
  // 是否启用监督插件（默认启用，支持关闭）
  ENABLE: true,

  // 平台版本：01 = IT公司α版本, 02 = 互联网公司β版本
  PLATFORM: '01',

  // 监督平台访问地址
  // IT公司UAT1: http://supervision-web-uat1.4c-uat.cmdevops.cn:20011
  // IT公司UAT3: http://4c-uat3.hq.cmcc/supervision
  // IT公司UAT4: http://4c.hq.cmcc/supervision
  SUPERVISION_URL: 'http://supervision-web-uat1.4c-uat.cmdevops.cn:20011',

  // 单点登录地址
  // IT公司UAT1: http://4c-uat.hq.cmcc/moss/micrologin/#/sso/getauthorizecode
  // IT公司UAT3: http://4c-uat3.hq.cmcc/moss/micrologin/#/sso/getauthorizecode
  // IT公司UAT4: http://4c.hq.cmcc/moss/micrologin/#/sso/getauthorizecode
  SSO_URL: 'http://4c-uat.hq.cmcc/moss/micrologin/#/sso/getauthorizecode',

  // 模块编码（固定的编码）
  // 根据《监督插件集成方案》文档格式
  MODULE_CODE: 'M_ONEBASE_001',

  // 菜单编码映射（一级菜单）
  // 一级菜单切换时生效，遵循文档格式
  MENU_CODES: {
    // 设置页面侧边栏菜单
    'application': 'C_APPLICATION_001',
    'user': 'C_USER_002',
    'role': 'C_ROLE_003',
    'organization': 'C_ORGANIZATION_004',
    'spaceInfo': 'C_SPACE_INFO_005',
    'system-dict': 'C_SYSTEM_DICT_006',
    'security': 'C_SECURITY_007',
    'profile': 'C_PROFILE_008',
    'plugin': 'C_PLUGIN_009',
    'externalUser': 'C_EXTERNAL_USER_010',
    'enterprise': 'C_ENTERPRISE_011',
    'agent': 'C_AGENT_012',
    'copilotdoc': 'C_COPILOT_DOC_013',
    'wxmini': 'C_WXMINI_014'
  },

  // 默认菜单编码
  DEFAULT_MENU_CODE: 'C_APPLICATION_001'
};