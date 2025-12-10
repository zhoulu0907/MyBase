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

export const sm2Decrypt = async (privateKey: string, data: string) => {
  const cipherMode = 1; // 1 - C1C3C2，0 - C1C2C3，默认为1
  let decryptData = sm2.doDecrypt(data, privateKey, cipherMode, {
    asn1: false
  }); // 解密结果
  return decryptData;
};
