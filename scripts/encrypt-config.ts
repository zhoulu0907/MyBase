/**
 * 配置文件加密工具
 * 用于加密配置文件，生成加密后的配置字符串
 *
 * 使用方法:
 *   npx tsx scripts/encrypt-config.ts [环境] [configKey]
 *   npx tsx scripts/encrypt-config.ts emit [输出目录] [环境] [configKey1] [configKey2]...
 *
 * 环境选项:
 *   lingji-dev   - 灵畿开发环境
 *   lingji-sit   - 灵畿 SIT 环境
 *   tiangong-dev - 天工开发环境
 *   tiangong-sit - 天工 SIT 环境
 */

import { CONFIG_PRIVATE_KEY, CONFIG_PUBLIC_KEY, sm2Decrypt, sm2Encrypt } from '../packages/common/src/utils/crypto';
import { ENVIRONMENTS, EnvironmentKey, ENVIRONMENT_LIST } from './env-config';

type FrontendConfig = Record<string, unknown>;

declare const require: any;

// ==================== 通用配置 ====================

const FRONTEND_APP_KEY = 'onebase';
const FRONTEND_APP_SECRET =
  'ac47af767231f0d08e3787b7d032443a2c7baedaeee07d596cff4525b94ce6a7';
const FRONTEND_PUBLIC_KEY =
  '045efee7520c3ed4b3c6bb75424a3ae25039e25bd859731a1f6464cb7e5f7dfb419bcba55cc6adfb7f3e224a6e8949709a3664ff2dc4b822f50ee77bbd64ce3946';

// 根据环境配置生成 CONFIG_SOURCES
function buildConfigSources(env: EnvironmentKey): Record<string, FrontendConfig> {
  const envConfig = ENVIRONMENTS[env];

  return {
    'admin-console': {
      THEME: 'lingji',
      ENVIRONMENT: 'platform',
      APP_KEY: FRONTEND_APP_KEY,
      APP_SECRET: FRONTEND_APP_SECRET,
      BASE_URL: `${envConfig.ONEBASESERVER_BASE_URL}/admin-api`,
      PLATFORM_BASE_URL: `${envConfig.ONEBASESERVER_BASE_URL}/platform`,
      RESOURCE_URL: `${envConfig.ONEBASESERVER_BASE_URL}/platform/infra/file/download`,
      PLATFORM_FE_URL: `${envConfig.APP_BUILDER_FE_URL}/`,
      PUBLIC_KEY: FRONTEND_PUBLIC_KEY
    },
    'app-builder': {
      THEME: 'lingji',
      ENVIRONMENT: 'builder',
      APP_KEY: FRONTEND_APP_KEY,
      APP_SECRET: FRONTEND_APP_SECRET,
      BASE_URL: `${envConfig.ONEBASESERVER_BASE_URL}/admin-api`,
      PLATFORM_BASE_URL: `${envConfig.ONEBASESERVER_BASE_URL}/platform`,
      RUNTIME_BASE_URL: `${envConfig.ONEBASERUNTIMESERVER_BASE_URL}/runtime`,
      RESOURCE_URL: `${envConfig.ONEBASESERVER_BASE_URL}/admin-api/infra/file/download`,
      MOBILE_EDITOR_URL: envConfig.APP_MOBILE_BUILDER_FE_URL,
      RUNTIME_URL: envConfig.APP_RUNTIME_FE_URL,
      RUNTIME_MOBILE_URL: envConfig.APP_MOBILE_RUNTIME_FE_URL,
      PLUGIN_URL: envConfig.ONEBASESERVER_BASE_URL,
      APP_BUILDER_DATASET_URL: envConfig.APP_BUILDER_DATASET_URL,
      APP_BUILDER_DASHBOARD_URL: envConfig.APP_BUILDER_DASHBOARD_URL,
      PUBLIC_KEY: FRONTEND_PUBLIC_KEY,
      // 监督插件配置
      SUPERVISION_PLUGIN: {
        ENABLE: envConfig.SUPERVISION_ENABLE,
        PLATFORM: envConfig.SUPERVISION_PLATFORM,
        SUPERVISION_URL: envConfig.SUPERVISION_URL,
        SSO_URL: envConfig.SUPERVISION_SSO_URL,
      }
    },
    // AI 配置（单独加密）
    'app-builder-ai': {
      GENAPP: envConfig.AI_GENAPP_URL,
      COPILOT: envConfig.AI_COPILOT_URL,
    },
    'mobile-runtime': {
      THEME: 'lingji',
      ENVIRONMENT: 'runtime',
      APP_KEY: FRONTEND_APP_KEY,
      APP_SECRET: FRONTEND_APP_SECRET,
      BASE_URL: `${envConfig.ONEBASESERVER_BASE_URL}/admin-api`,
      RUNTIME_BASE_URL: `${envConfig.ONEBASERUNTIMESERVER_BASE_URL}/runtime`,
      RESOURCE_URL: `${envConfig.ONEBASERUNTIMESERVER_BASE_URL}/runtime/infra/file/download`,
      PUBLIC_KEY: FRONTEND_PUBLIC_KEY
    },
    runtime: {
      THEME: 'lingji',
      ENVIRONMENT: 'runtime',
      APP_KEY: FRONTEND_APP_KEY,
      APP_SECRET: FRONTEND_APP_SECRET,
      BASE_URL: `${envConfig.ONEBASESERVER_BASE_URL}/admin-api`,
      RUNTIME_BASE_URL: `${envConfig.ONEBASERUNTIMESERVER_BASE_URL}/runtime`,
      RESOURCE_URL: `${envConfig.ONEBASERUNTIMESERVER_BASE_URL}/runtime/infra/file/download`,
      CORP_RESOURCE_URL: `${envConfig.ONEBASERUNTIMESERVER_BASE_URL}/runtime/infra/file/corp/download`,
      PLUGIN_URL: `${envConfig.ONEBASESERVER_BASE_URL}/plugins`,
      APP_BUILDER_DASHBOARD_URL: envConfig.APP_BUILDER_DASHBOARD_URL,
      CHATBOT_BASE_URL: envConfig.CHATBOT_BASE_URL,
      PUBLIC_KEY: FRONTEND_PUBLIC_KEY
    },
    'mobile-editor': {
      BASE_URL: `${envConfig.ONEBASESERVER_BASE_URL}/admin-api`,
      RUNTIME_BASE_URL: `${envConfig.ONEBASERUNTIMESERVER_BASE_URL}/runtime`,
      RUNTIME_URL: envConfig.APP_RUNTIME_FE_URL
    },
    'app-dashboard': {
      DASHBOARD_URL: envConfig.DASHBOARD_URL,
      PREVIEW_URL: envConfig.PREVIEW_URL,
      DATASET_URL: envConfig.DATASET_URL
    }
  };
}

const CONFIG_KEY_TO_DEPLOY_FILENAME: Record<string, string> = {
  'app-builder': 'builder_config.js',
  'admin-console': 'console_config.js',
  runtime: 'runtime_config.js',
  'mobile-runtime': 'mobile_runtime_config.js',
  'mobile-editor': 'mobile_editor_config.js',
  'app-dashboard': 'dashboard_config.js'
};

function renderPlainConfigJs(obj: FrontendConfig) {
  return `window.global_config = ${JSON.stringify(obj, null, 2)};\n`;
}

async function renderEncryptedConfigJs(obj: FrontendConfig) {
  const encryptedData = await sm2Encrypt(CONFIG_PUBLIC_KEY, `${JSON.stringify(obj)}`);
  return `window.global_config = {\n  CONFIG: ${JSON.stringify(encryptedData)}\n};\n`;
}

// 为 app-builder 生成包含 CONFIG 和 AI_CONFIG 的配置文件
async function renderBuilderConfigJs(configObj: FrontendConfig, aiConfigObj: FrontendConfig) {
  const encryptedConfig = await sm2Encrypt(CONFIG_PUBLIC_KEY, `${JSON.stringify(configObj)}`);
  const encryptedAiConfig = await sm2Encrypt(CONFIG_PUBLIC_KEY, `${JSON.stringify(aiConfigObj)}`);
  return `window.global_config = {
  CONFIG: ${JSON.stringify(encryptedConfig)},
  AI_CONFIG: ${JSON.stringify(encryptedAiConfig)}
};\n`;
}

// 不需要加密的配置 key
const PLAIN_CONFIG_KEYS = ['app-dashboard'];

async function emitConfigFiles(
  env: EnvironmentKey,
  configKeys: string[],
  outDir: string,
  render: (obj: FrontendConfig) => string | Promise<string>
) {
  const fs = require('fs');
  const path = require('path');

  // 确保输出目录存在
  if (!fs.existsSync(outDir)) {
    fs.mkdirSync(outDir, { recursive: true });
  }

  const CONFIG_SOURCES = buildConfigSources(env);
  const uniqueKeys = Array.from(new Set(configKeys));
  const outputs: Array<{ configKey: string; filePath: string }> = [];

  for (const configKey of uniqueKeys) {
    // app-builder 特殊处理：生成包含 CONFIG 和 AI_CONFIG 的配置文件
    if (configKey === 'app-builder') {
      const configObj = CONFIG_SOURCES['app-builder'];
      const aiConfigObj = CONFIG_SOURCES['app-builder-ai'];
      if (!configObj || !aiConfigObj) {
        throw new Error(`app-builder 配置未找到`);
      }
      const fileName = CONFIG_KEY_TO_DEPLOY_FILENAME[configKey];
      const filePath = path.resolve(outDir, fileName);
      const content = await renderBuilderConfigJs(configObj, aiConfigObj);
      fs.writeFileSync(filePath, content, 'utf8');
      outputs.push({ configKey, filePath });

      // 生成额外的配置文件（sso.config.js 和 supervision.config.js）
      await emitAppBuilderExtraConfigs(env, outDir);
      continue;
    }

    // app-builder-ai 不单独生成文件
    if (configKey === 'app-builder-ai') {
      continue;
    }

    const obj = CONFIG_SOURCES[configKey];
    if (!obj) {
      throw new Error(`未知 configKey: ${configKey}，可选：${Object.keys(CONFIG_SOURCES).join(', ')}`);
    }
    const fileName = CONFIG_KEY_TO_DEPLOY_FILENAME[configKey] || `${configKey}_config.js`;
    const filePath = path.resolve(outDir, fileName);
    // app-dashboard 使用明文配置，其他使用加密配置
    const content = PLAIN_CONFIG_KEYS.includes(configKey)
      ? renderPlainConfigJs(obj)
      : await render(obj);
    fs.writeFileSync(filePath, content, 'utf8');
    outputs.push({ configKey, filePath });
  }

  console.log(`已生成配置文件 [${ENVIRONMENTS[env].name}]:`);
  for (const item of outputs) {
    console.log(`- ${item.configKey}: ${item.filePath}`);
  }
}

// 生成 app-builder 的额外配置文件（sso.config.js 和 supervision.config.js）
async function emitAppBuilderExtraConfigs(
  env: EnvironmentKey,
  outDir: string
) {
  const fs = require('fs');
  const path = require('path');
  const envConfig = ENVIRONMENTS[env];

  // 确保输出目录存在
  if (!fs.existsSync(outDir)) {
    fs.mkdirSync(outDir, { recursive: true });
  }

  // 生成 sso.config.js（仅灵畿环境）
  if (envConfig.LINGJI_HOME_URL && envConfig.LINGJI_SSO_SOURCE_ID) {
    const ssoConfigContent = `/**
 * 灵畿 SSO 配置文件
 * 容器部署时可通过挂载此文件注入配置
 * 环境: ${envConfig.name}
 */
window.SSO_CONFIG = {
  // 灵畿前端首页地址（未登录时跳转）
  lingjiHome: '${envConfig.LINGJI_HOME_URL}',

  // 灵畿 SSO 应用ID
  sourceid: '${envConfig.LINGJI_SSO_SOURCE_ID}',

  // 后端 SSO 登录接口（完整路径）
  ssoLoginApi: '${envConfig.ONEBASESERVER_BASE_URL}/admin-api/system/lingji-sso/login',

  // 登录成功后跳转地址前缀（完整路径，tenantId 从后端接口返回）
  successRedirectBase: '${envConfig.APP_BUILDER_FE_URL}'
};
`;
    const ssoConfigPath = path.resolve(outDir, 'sso.config.js');
    fs.writeFileSync(ssoConfigPath, ssoConfigContent, 'utf8');
    console.log(`- sso.config.js: ${ssoConfigPath}`);
  }

  // 生成 supervision.config.js（仅灵畿环境）
  if (envConfig.SUPERVISION_ENABLE) {
    const supervisionConfigContent = `/**
 * 监督插件配置文件
 * 用于配置监督插件的启用状态、平台版本和访问地址
 * 环境: ${envConfig.name}
 */
window.supervision_config = {
  // 是否启用监督插件（默认启用，支持关闭）
  ENABLE: ${envConfig.SUPERVISION_ENABLE},

  // 平台版本：01 = IT公司α版本, 02 = 互联网公司β版本
  PLATFORM: '${envConfig.SUPERVISION_PLATFORM}',

  // 监督平台访问地址
  SUPERVISION_URL: '${envConfig.SUPERVISION_URL}',

  // 单点登录地址
  SSO_URL: '${envConfig.SUPERVISION_SSO_URL}'
};
`;
    const supervisionConfigPath = path.resolve(outDir, 'supervision.config.js');
    fs.writeFileSync(supervisionConfigPath, supervisionConfigContent, 'utf8');
    console.log(`- supervision.config.js: ${supervisionConfigPath}`);
  }
}

const encryptConfig = async (env: EnvironmentKey, configKey: string) => {
  const publicKey = CONFIG_PUBLIC_KEY;
  const privateKey = CONFIG_PRIVATE_KEY;

  const CONFIG_SOURCES = buildConfigSources(env);
  const obj = CONFIG_SOURCES[configKey];
  if (!obj) {
    throw new Error(`未知 configKey: ${configKey}，可选：${Object.keys(CONFIG_SOURCES).join(', ')}`);
  }

  console.log(`环境: ${ENVIRONMENTS[env].name}`);
  console.log('原始配置对象:');
  console.log(JSON.stringify(obj, null, 2));
  console.log('\n开始加密...\n');

  const encryptedData = await sm2Encrypt(publicKey, `${JSON.stringify(obj)}`);

  console.log('加密后的数据:');
  console.log(encryptedData);
  console.log('\n开始解密验证...\n');

  const decryptedData = sm2Decrypt(privateKey, encryptedData);
  console.log('解密后的数据:');
  console.log(decryptedData);

  const parsedData = JSON.parse(decryptedData as string);
  console.log('\n解析后的配置对象:');
  console.log(JSON.stringify(parsedData, null, 2));

  console.log('\n✅ 加密解密验证成功！');
};

function printUsage() {
  console.log(`
使用方法:
  npx tsx scripts/encrypt-config.ts [环境] [configKey]
  npx tsx scripts/encrypt-config.ts emit [输出目录] [环境] [configKey1] [configKey2]...

环境选项:
  lingji-dev   - 灵畿开发环境 (默认)
  lingji-sit   - 灵畿 SIT 环境
  tiangong-dev - 天工开发环境
  tiangong-sit - 天工 SIT 环境

示例:
  npx tsx scripts/encrypt-config.ts lingji-dev app-builder
  npx tsx scripts/encrypt-config.ts lingji-sit app-builder
  npx tsx scripts/encrypt-config.ts tiangong-dev app-builder
  npx tsx scripts/encrypt-config.ts emit deploy/config lingji-dev
  npx tsx scripts/encrypt-config.ts emit deploy/config lingji-sit app-builder runtime
`);
}

async function main() {
  const argv = ((globalThis as any).process?.argv ?? []) as string[];
  const args = argv.slice(2).filter((item) => item !== '--');

  // 显示帮助
  if (args[0] === '-h' || args[0] === '--help') {
    printUsage();
    return;
  }

  // emit 模式: 生成配置文件
  if (args[0] === 'emit') {
    const outDir = args[1] || 'deploy/config';
    const env = (args[2] as EnvironmentKey) || 'lingji-dev';
    if (!ENVIRONMENTS[env]) {
      console.error(`❌ 未知环境: ${env}，可选: ${Object.keys(ENVIRONMENTS).join(', ')}`);
      return;
    }
    const configKeys = args.slice(3);
    const CONFIG_SOURCES = buildConfigSources(env);
    const keys = configKeys.length > 0 ? configKeys : Object.keys(CONFIG_SOURCES);
    await emitConfigFiles(env, keys, outDir, renderEncryptedConfigJs);
    return;
  }

  // emit-plain 模式: 生成明文配置文件
  if (args[0] === 'emit-plain') {
    const outDir = args[1] || 'deploy/config';
    const env = (args[2] as EnvironmentKey) || 'lingji-dev';
    if (!ENVIRONMENTS[env]) {
      console.error(`❌ 未知环境: ${env}，可选: ${Object.keys(ENVIRONMENTS).join(', ')}`);
      return;
    }
    const configKeys = args.slice(3);
    const CONFIG_SOURCES = buildConfigSources(env);
    const keys = configKeys.length > 0 ? configKeys : Object.keys(CONFIG_SOURCES);
    await emitConfigFiles(env, keys, outDir, renderPlainConfigJs);
    return;
  }

  // 默认模式: 加密并验证单个配置
  const env = (args[0] as EnvironmentKey) || 'lingji-dev';
  const configKey = args[1] || 'app-builder';

  // 判断第一个参数是环境还是 configKey
  if (ENVIRONMENTS[env as EnvironmentKey]) {
    await encryptConfig(env, configKey);
  } else {
    // 兼容旧用法：第一个参数是 configKey，使用默认环境
    await encryptConfig('lingji-dev', args[0] || 'app-builder');
  }
}

main().catch((error) => {
  console.error('❌ 执行失败:', error);
});
