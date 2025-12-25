/**
 * 配置文件加密工具
 * 用于加密配置文件，生成加密后的配置字符串
 *
 * 使用方法:
 *   npx tsx scripts/encrypt-config.ts
 *   或者
 *   ts-node scripts/encrypt-config.ts
 */

import { CONFIG_PRIVATE_KEY, CONFIG_PUBLIC_KEY, sm2Decrypt, sm2Encrypt } from '../packages/common/src/utils/crypto';

const encryptConfig = async () => {
  const publicKey = CONFIG_PUBLIC_KEY;
  const privateKey = CONFIG_PRIVATE_KEY;

  //   这里放config.js的配置
  const obj = {
    ENVIRONMENT: 'runtime',
    APP_KEY: 'onebase',
    APP_SECRET: 'xxx',
    BASE_URL: 'xxx',
    RUNTIME_BASE_URL: 'xxx',
    FILE_DETAIL_URL: 'xxx',
    PUBLIC_KEY: 'xxx'
  };

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

// 运行加密函数
encryptConfig().catch((error) => {
  console.error('❌ 执行失败:', error);
});
