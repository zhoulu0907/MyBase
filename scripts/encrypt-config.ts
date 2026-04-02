/**
 * 配置文件加密工具
 * 用于加密配置文件，生成加密后的配置字符串
 *
 * 使用方法:
 *   npx tsx scripts/encrypt-config.ts [configKey]
 *   或者
 *   ts-node scripts/encrypt-config.ts [configKey]
 */

import { CONFIG_PRIVATE_KEY, CONFIG_PUBLIC_KEY, sm2Decrypt, sm2Encrypt } from '../packages/common/src/utils/crypto';

type FrontendConfig = Record<string, unknown>;

declare const require: any;

const ONEBASESERVER_BASE_URL = `http://onebase.4c-uat.hq.cmcc:20011/observerbuilder`;
const ONEBASERUNTIMESERVER_BASE_URL = `http://onebase.4c-uat.hq.cmcc:20011/observerruntime`;

const APP_BUILDER_FE_URL = 'http://onebase.4c-uat.hq.cmcc:20011/appbuilder';
const APP_RUNTIME_FE_URL = 'http://onebase.4c-uat.hq.cmcc:20011/appruntime';
const APP_MOBILE_BUILDER_FE_URL = 'http://onebase.4c-uat.hq.cmcc:20011/mobilebuilder';
const APP_MOBILE_RUNTIME_FE_URL = 'http://onebase.4c-uat.hq.cmcc:20011/mobileruntime';

// 数据集和仪表板服务地址
const APP_BUILDER_DATASET_URL = 'http://onebase.4c-uat.hq.cmcc:20011/observerbuilder';
const APP_BUILDER_DASHBOARD_URL = 'http://onebase.4c-uat.hq.cmcc:20011/appdashboard/#/';
// Chatbot 服务地址
const CHATBOT_BASE_URL = '';
// App Dashboard 配置 (明文配置)
const DASHBOARD_URL = 'http://onebase.4c-uat.hq.cmcc:20011/observerbuilder';
const PREVIEW_URL = 'http://onebase.4c-uat.hq.cmcc:20011/appdashboard/#/chart/preview';
const DATASET_URL = 'http://10.0.104.38:8100/de2api';

const FRONTEND_APP_KEY = 'onebase';
const FRONTEND_APP_SECRET =
  'ac47af767231f0d08e3787b7d032443a2c7baedaeee07d596cff4525b94ce6a7';
const FRONTEND_PUBLIC_KEY =
  '045efee7520c3ed4b3c6bb75424a3ae25039e25bd859731a1f6464cb7e5f7dfb419bcba55cc6adfb7f3e224a6e8949709a3664ff2dc4b822f50ee77bbd64ce3946';

const CONFIG_SOURCES: Record<string, FrontendConfig> = {
  'admin-console': {
    THEME: 'lingji',
    ENVIRONMENT: 'platform',
    APP_KEY: FRONTEND_APP_KEY,
    APP_SECRET: FRONTEND_APP_SECRET,
    BASE_URL: `${ONEBASESERVER_BASE_URL}/admin-api`,
    PLATFORM_BASE_URL: `${ONEBASESERVER_BASE_URL}/platform`,
    RESOURCE_URL:
      `${ONEBASESERVER_BASE_URL}/platform/infra/file/download`,
    PLATFORM_FE_URL: `${APP_BUILDER_FE_URL}/`,
    PUBLIC_KEY: FRONTEND_PUBLIC_KEY
  },
  'app-builder': {
    THEME: 'lingji',
    ENVIRONMENT: 'builder',
    APP_KEY: FRONTEND_APP_KEY,
    APP_SECRET: FRONTEND_APP_SECRET,
    BASE_URL: `${ONEBASESERVER_BASE_URL}/admin-api`,
    PLATFORM_BASE_URL: `${ONEBASESERVER_BASE_URL}/platform`,
    RUNTIME_BASE_URL: `${ONEBASERUNTIMESERVER_BASE_URL}/runtime`,
    RESOURCE_URL:
      `${ONEBASESERVER_BASE_URL}/admin-api/infra/file/download`,
    MOBILE_EDITOR_URL: APP_MOBILE_BUILDER_FE_URL,
    RUNTIME_URL: APP_RUNTIME_FE_URL,
    RUNTIME_MOBILE_URL: APP_MOBILE_RUNTIME_FE_URL,
    PLUGIN_URL: ONEBASESERVER_BASE_URL,
    APP_BUILDER_DATASET_URL: APP_BUILDER_DATASET_URL,
    APP_BUILDER_DASHBOARD_URL: APP_BUILDER_DASHBOARD_URL,
    PUBLIC_KEY: FRONTEND_PUBLIC_KEY
  },
  'mobile-runtime': {
    THEME: 'lingji',
    ENVIRONMENT: 'runtime',
    APP_KEY: FRONTEND_APP_KEY,
    APP_SECRET: FRONTEND_APP_SECRET,
    BASE_URL: `${ONEBASESERVER_BASE_URL}/admin-api`,
    RUNTIME_BASE_URL: `${ONEBASERUNTIMESERVER_BASE_URL}/runtime`,
    RESOURCE_URL:
      `${ONEBASERUNTIMESERVER_BASE_URL}/runtime/infra/file/download`,
    PUBLIC_KEY: FRONTEND_PUBLIC_KEY
  },
  runtime: {
    THEME: 'lingji',
    ENVIRONMENT: 'runtime',
    APP_KEY: FRONTEND_APP_KEY,
    APP_SECRET: FRONTEND_APP_SECRET,
    BASE_URL: `${ONEBASESERVER_BASE_URL}/admin-api`,
    RUNTIME_BASE_URL: `${ONEBASERUNTIMESERVER_BASE_URL}/runtime`,
    RESOURCE_URL:
      `${ONEBASERUNTIMESERVER_BASE_URL}/runtime/infra/file/download`,
    CORP_RESOURCE_URL:
      `${ONEBASERUNTIMESERVER_BASE_URL}/runtime/infra/file/corp/download`,
    PLUGIN_URL: `${ONEBASESERVER_BASE_URL}/plugins`,
    APP_BUILDER_DASHBOARD_URL,
    CHATBOT_BASE_URL,
    PUBLIC_KEY: FRONTEND_PUBLIC_KEY
  },
  'mobile-editor': {
    BASE_URL: `${ONEBASESERVER_BASE_URL}/admin-api`,
    RUNTIME_BASE_URL: `${ONEBASERUNTIMESERVER_BASE_URL}/runtime`,
    RUNTIME_URL: APP_RUNTIME_FE_URL
  },
  'app-dashboard': {
    DASHBOARD_URL,
    PREVIEW_URL,
    DATASET_URL
  }
};

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

// 不需要加密的配置 key
const PLAIN_CONFIG_KEYS = ['app-dashboard'];

async function emitConfigFiles(
  configKeys: string[],
  outDir: string,
  render: (obj: FrontendConfig) => string | Promise<string>
) {
  const fs = require('fs');
  const path = require('path');

  const uniqueKeys = Array.from(new Set(configKeys));
  const outputs: Array<{ configKey: string; filePath: string }> = [];

  for (const configKey of uniqueKeys) {
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

  console.log('已生成配置文件:');
  for (const item of outputs) {
    console.log(`- ${item.configKey}: ${item.filePath}`);
  }
}

const encryptConfig = async (configKey: string) => {
  const publicKey = CONFIG_PUBLIC_KEY;
  const privateKey = CONFIG_PRIVATE_KEY;

  //   这里放config.js的配置
  const obj = CONFIG_SOURCES[configKey];
  if (!obj) {
    throw new Error(`未知 configKey: ${configKey}，可选：${Object.keys(CONFIG_SOURCES).join(', ')}`);
  }

  console.log('原始配置对象:');
  console.log(JSON.stringify(obj, null, 2));
  console.log('\n开始加密...\n');

  const encryptedData = await sm2Encrypt(publicKey, `${JSON.stringify(obj)}`);

  // const encryptedData = `848b9db1f00f263f839f25749eab902e79cf6b1efa8e9cb65565722ad9a4616615bf0bdc1efd059e9e1ec894016d87080480190ce16120106fbfe9a031afcdf2b076d1f012e2f6a9b94e1962a3689c235b65e9d604331d57c7187801931775461e35fada2771a3e5eb3e969fca0dd6af9f22de3d48f71f6e2a3f14bb1870c81d03e1944ab2b05082dc95d7b9e7ba3b0a57b027d039c45998dbb7e4a791683ec3a58cc53a39c97fe50bfce1015c87f1a8e98307a42edd24e7d8f553dacef5b0dbc43fd06d9084fea25974eac0eec4852aff937aca1b776dd6f7d951712f5e1ba423b5fdd1cfd08348dee689110ef3403e7e404cb5712d9b4fa80df04b7561a14cbb3400a7ce2462f79cb19260cbd15033047718578b3e69ac2f4e1cc1c4684b8ee4fe75451452f963e740575e09211a70edcdf02602eed97ffdb890f701607cbed01eb033170dd1234aa7a7d1f96a74dfc856324e564cec66d522da945fceac31de3c5fde09befb274a8e69f2f8ee7df763f6561430612b1d6fc5ef9cc1f4a4efdaf4618912a897a7c1b4de7094cd6750c1ad901374d1f5e139d5dbbef194defb219ff0df4b998c5e0241f374a58d45ec7bb2d39815adfc1a9f105c71f468c86b28264ac0150e72fee9ec058d89d1027d070852024c7bd3a774de12e2b6e30c303e68375ff4a07eab73d8e021ac87393f0221d010afeb866f32b5f3102bd092f3182ce934cb502eb8554fa4fabe3ba21071356e0dbe52c01d4a72ee2ca91b1b5ce8c8258cd6bc082f4002600ac2876866212774e3a6ecb335e5ce54b7a4b819808d419f062885c0cc90272ae796290694d792bfcbb1b9a831dd813a3df46200c28e63082406a1c1b0a1cf5024ffb9d4d1f882df97368565a25af9cf0e0be8bc3d79a49fe1e95619791ad08e74dc52427936b9edf0b478e83a9b21d8943101366204b82fb8f9e4d185e85a7dccda430e9a0e15595a61808a98e9468e90f89364ca320c8cc36023b243c96ee1cb6266cbb7687d2552b4afa3b1c166921c745b51e8c77a4cb78953c1596c6a076ba2599d7a9756987cf89f31e2d06797e3a4beaff05eac0671408305dbeefa873b683c8b2f4595e6317cd2543a41786d76`;

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

async function main() {
  const argv = ((globalThis as any).process?.argv ?? []) as string[];
  const args = argv.slice(2).filter((item) => item !== '--');
  const modeOrConfigKey = args[0] || 'app-builder';

  if (modeOrConfigKey === 'emit') {
    const outDir = args[1] || 'deploy/config';
    const configKeys = args.slice(2);
    const keys = configKeys.length > 0 ? configKeys : Object.keys(CONFIG_SOURCES);
    await emitConfigFiles(keys, outDir, renderEncryptedConfigJs);
    return;
  }

  if (modeOrConfigKey === 'emit-plain') {
    const outDir = args[1] || 'deploy/config';
    const configKeys = args.slice(2);
    const keys = configKeys.length > 0 ? configKeys : Object.keys(CONFIG_SOURCES);
    await emitConfigFiles(keys, outDir, renderPlainConfigJs);
    return;
  }

  await encryptConfig(modeOrConfigKey);
}

main().catch((error) => {
  console.error('❌ 执行失败:', error);
});
