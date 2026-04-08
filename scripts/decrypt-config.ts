/**
 * 配置文件解密工具
 * 用于解密由 encrypt-config.ts 生成的加密配置
 *
 * 使用方法:
 *   npx tsx scripts/decrypt-config.ts
 */

import { CONFIG_PRIVATE_KEY, sm2Decrypt } from '../packages/common/src/utils/crypto';

// const config = `
// window.global_config = {
//   CONFIG: 'c5d09b4e88cc4ebc1dbab00e501fbca6135ecb28deec72f8a4dba23854f06f0646f52f338fd0c45609a3e54baff7a9aef553e0851c294cfb4dd81fe353787778c2c4bbb9483083b0a2a0e7c2155e6e55bb0bc6cc359b3a987785a5d3c77df2d9563db4b73fb83f2f04b11ca49aa7480797d4b3fd09f41d0b9f7653258f801a8dfbe5db2aea3ecdb90efbbd69f3450c697b393c329536b2da2f2d39fe5b35a4d9a1bd123be8cda09c486c6f70ea84b337fb2663017ae3179c031a372c7f52dd6a5ffd4a0d08c7a945afd8e3ca256fe9d2b266664631d8ea7c40c4fda1203f9132158a545ff06845ec1d9ec08cf041f7c92cb2db7e99b6e3768e7c57f9d37de8e7b542becaba70e9d1d9b721d693b2b5330fb12bc725ed14522118ba87703dbee416082b7a4a2d3581f3725f89c65aaf242f26118a115d9b11625660dd4a796f9d1b01e288f940dc8c86343d21a6757f8203981d66a9363a69e2023f44983129bbb65873d0313aa320e04ca59df9d6568edec99b9dcbb0fe71025889958066ebf783bf58b5f9c8a642a8822d6f4bf5d3021e17d667965f322b6e478a6a89eed929799087059dde30e48315e835325a5fdef1c77ab14b05cde210c942cd524b5bfc1efee9388fba2e35a3a171b2ec6cc33dd2d3378f08aed0afa868e44889342c67b707e2ee1cf7632ae5b354c127c9e4230e3aa19ed9732e15f46e16c3769f25d98b17bfe6bb794288f1743cef025ee81afa8b5c7657d063f1d0eb7897730af2e2cd94b7d3f58aab501e016af66e9104bae7dacc9c65a456bea3ae7602e26d5b9d785febe439fc1154763ba96d11c2f04990d5b9ea4a1424f69b1d96109aecda7fa32ef7497c5fe282aecf3bd4abc0003861f1c119870039b30068a7fd389f6ef3e8f95e873a157c619da2a457eaadef6d6c29f927438d2f940d336f8dedb386d9ba51ed4c3aa6b3bb783fc86a71dff4fd21114c1ff2273de2c38913382762f5d4bb96d85ce64c46911233f65e3bc7fc060613c0803867027e9a7f77e0909a876e3e90a968d2829e9e1e475b30615d118673fdd0676e756cfbc3cb6413fe9e9132a467c4dc70e326ab1f6b238425f41bcac3197199e9e0f32c71d0515769f3e6e234d64259f2c9ed59bea8b54843262f9aae59aac731b47e057f0a5efd98b32174be86dae63ffe498c055f2ddb578f12af5e4a8ae83649118cd17e87accbeb2223b1918b9717e3e29b3ab0026a200fe4b232fe1e4687df217a93d814c20eb0f615c2cae29d861881488386c37f1c043eabce1a700c1f518bfb32556eee4db7efac5d9b22b566765cde0dcb6f62888ccf98f7d5ae4764e203c3746df790b82c14fc7d6b8d3c2fd3ddf0663e5b5a4e2925848e3bd630f63f749eeb75dfb45330fe383882082e948ffb5657e0466361cccfe0a2cbc49cfdb6cf3f5fe0fadda48388f2703c9522e834f6e4e261'
// };
// `

const config = `
  window.global_config = {
    CONFIG: 'a5ac407affe97715e22ca7f817b4a776c39bc16a57a2446fe772309f6cb1e9aaea945723c880a5d53512da951a393ad9ea5a9a116c0360bc6510cffb6b74d46b99639a5b4a15c5b9af37dadec8acd66935bc3735702475ac2a6f41e4d20ca8f7ac1578647f295b0969df18301c1341150480ca36416dc1574a2d77c722f1d48551a74d8ecdb5e3d1fde4cebc3c50465bc11cfb8c520d1e38b68f37d12c7ab431460ffcba3c123e85ea932c536afc745dd4b8b11fc3e0e7657bb608731e059119485fa83c937fe843f3e58a61379af3f4ab63806777'
  }
`;



function decryptConfig(encryptedData: string, privateKey: string = CONFIG_PRIVATE_KEY): string {
  const decryptedData = sm2Decrypt(privateKey, encryptedData);

  if (!decryptedData) {
    throw new Error('解密失败，请检查密文或私钥是否正确');
  }

  return decryptedData as string;
}

async function main() {
  // 从 config 字符串中提取加密数据
  const match = config.match(/CONFIG:\s*'([^']+)'/);
  if (!match) {
    console.error('❌ 无法从 config 中提取加密数据');
    return;
  }

  const encryptedData = match[1];
  console.log('加密数据:', encryptedData.substring(0, 50) + '...');
  console.log('\n开始解密...\n');

  try {
    const decryptedStr = decryptConfig(encryptedData);
    const decryptedObj = JSON.parse(decryptedStr);

    console.log('解密后的配置:');
    console.log(JSON.stringify(decryptedObj, null, 2));
    console.log('\n✅ 解密成功！');
  } catch (error: any) {
    console.error('❌ 解密失败:', error.message);
  }
}

main();