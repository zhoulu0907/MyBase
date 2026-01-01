import { sm2 } from 'sm-crypto-v2';

export const generateSm2KeyPair = () => {
  let keypair = sm2.generateKeyPairHex();

  const publicKey = keypair.publicKey; // 公钥
  const privateKey = keypair.privateKey; // 私钥

  return { publicKey, privateKey };
};

export const sm2Encrypt = async (publicKey: string, data: string) => {
  const compressedPublicKey = sm2.compressPublicKeyHex(publicKey); // compressedPublicKey 和 publicKey 等价
  sm2.comparePublicKeyHex(publicKey, compressedPublicKey); // 判断公钥是否等价

  // 初始化随机数池，在某些场景下可能会用到
  await sm2.initRNGPool();

  let verifyResult = sm2.verifyPublicKey(publicKey); // 验证公钥
  verifyResult = sm2.verifyPublicKey(compressedPublicKey); // 验证公钥

  // 加密解密

  const cipherMode = 1; // 1 - C1C3C2，0 - C1C2C3，默认为1
  // 支持使用 asn1 对加密结果进行编码，在 options 参数中传入 { asn1: true } 即可，默认不开启

  let encryptData = sm2.doEncrypt(data, publicKey, cipherMode, {
    asn1: false
  }); // 加密结果

  return encryptData;
};

export const sm2Decrypt = (privateKey: string, data: string) => {
  const cipherMode = 1; // 1 - C1C3C2，0 - C1C2C3，默认为1
  let decryptData = sm2.doDecrypt(data, privateKey, cipherMode, {
    asn1: false
  }); // 解密结果
  return decryptData;
};

// 配置环境加密用
export const CONFIG_PUBLIC_KEY =
  '04d63308f82df17ee6b8c87aded985d97e39820e49162ccd21f0bbdeefc8ad550561d293dbc108f16fd1860c42627f801707b8efab41a43865d23f12def43b8b31';

export const CONFIG_PRIVATE_KEY = 'bd55dd5a2fdc3c5915e064983fb05d44a2d6c2ea941411af31455fe53875f076';
