package com.cmsr.onebase.module.system.util.encrypt;

import com.cmsr.onebase.module.system.controller.admin.license.vo.LicenseExportRespVO;
import com.cmsr.onebase.module.system.enums.license.LicenseSecretKeyEnum;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.Security;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.Security;
import java.util.Base64;

public class SM4Utils {
    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    /**
     * 确保密钥为16字节长度
     * @param key 原始密钥
     * @return 16字节密钥
     */
    private static byte[] getKeyBytes(String key) {
        byte[] keyBytes = new byte[16]; // 16字节 = 128位
        byte[] inputKeyBytes = key.getBytes(StandardCharsets.UTF_8);
        // 如果输入密钥超过16字节，截取前16字节；如果不足16字节，用0补齐
        System.arraycopy(inputKeyBytes, 0, keyBytes, 0, Math.min(inputKeyBytes.length, 16));
        return keyBytes;
    }
    
    /**
     * 解密SM4加密文件并保存到指定文件
     * @param encryptedFilePath 加密文件路径
     * @param key 解密密钥
     * @param outputFilePath 解密后内容保存路径
     * @return 解密是否成功
     */
    public static boolean decryptSm4FileToFile(String encryptedFilePath, String key, String outputFilePath) {
        try {
            // 读取加密文件内容
            byte[] encryptedBytes = Files.readAllBytes(Paths.get(encryptedFilePath));
            String encryptedContent = new String(encryptedBytes, StandardCharsets.UTF_8);
            
            // 解密内容
            String decryptedContent = sm4Decrypt(encryptedContent, key);
            if (decryptedContent == null) {
                System.err.println("解密失败，解密结果为null");
                return false;
            }
            
            // 将解密后的内容写入输出文件
            Files.write(Paths.get(outputFilePath), decryptedContent.getBytes(StandardCharsets.UTF_8));
            return true;
        } catch (Exception e) {
            System.err.println("解密文件时发生错误: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public static String sm4Encrypt(String plainString, String key) {
        String cipherString = null;
        try {
            // 确保密钥长度为16字节(128位)
            byte[] keyBytes = getKeyBytes(key);
            
            // 指定加密算法
            String algorithm = "SM4";
            // 创建密钥规范
            SecretKey secretKey = new SecretKeySpec(keyBytes, algorithm);
            // 获取Cipher对象实例（BC中SM4默认使用ECB模式和PKCS5Padding填充方式）
            Cipher cipher = Cipher.getInstance(algorithm + "/ECB/PKCS5Padding", "BC");
            // 初始化Cipher为加密模式
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            // 获取加密byte数组
            byte[] cipherBytes = cipher.doFinal(plainString.getBytes(StandardCharsets.UTF_8));
            // 输出为Base64编码
            cipherString = Base64.getEncoder().encodeToString(cipherBytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cipherString;
    }

    public static String sm4Decrypt(String cipherString, String key) {
        String plainString = null;
        try {
            // 指定加密算法
            String algorithm = "SM4";
            // 创建密钥规范
            SecretKey secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), algorithm);
            // 获取Cipher对象实例（BC中SM4默认使用ECB模式和PKCS5Padding填充方式）
            Cipher cipher = Cipher.getInstance(algorithm + "/ECB/PKCS5Padding", "BC");
            // 初始化Cipher为解密模式
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            // 获取加密byte数组
            byte[] cipherBytes = cipher.doFinal(Base64.getDecoder().decode(cipherString));
            // 输出为字符串
            plainString = new String(cipherBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return plainString;
    }

    public static void main(String[] args) {
        System.out.println("密钥长度: " + LicenseSecretKeyEnum.LICENSE_SECRET_KEY.getSecretKey().length());
        try {
            // 测试明文内容
            LicenseExportRespVO licenseExportRespVO = new LicenseExportRespVO();

            licenseExportRespVO.setEnterpriseName("上海移动有限公司");
            licenseExportRespVO.setEnterpriseCode("F200090910001");
            licenseExportRespVO.setEnterpriseAddress("上海市浦东金桥开发区");
            licenseExportRespVO.setPlatformType("私有化部署");
            licenseExportRespVO.setExpireTime("2025-12-01 08:01:48");
            licenseExportRespVO.setStatus("disable");
            licenseExportRespVO.setTenantLimit("100");
            licenseExportRespVO.setUserLimit("300");
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            String jsonContent = objectMapper.writeValueAsString(licenseExportRespVO);
            
            // 使用正确的密钥进行加密
            String sm4Encrypt = sm4Encrypt(jsonContent, LicenseSecretKeyEnum.LICENSE_SECRET_KEY.getSecretKey());
            if (sm4Encrypt != null) {
                System.out.println("加密后的长度: " + sm4Encrypt.length());
                
                // 生成加密文件路径
                String licenseDirPath = System.getProperty("user.dir") + File.separator + "license";
                File licenseDir = new File(licenseDirPath);
                if (!licenseDir.exists()) {
                    licenseDir.mkdirs();
                }
                String now = java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
                String sm4FilePath = "M:\\Device\\F\\onebase-v3-be\\.idea\\httpRequests\\license.lic-5.sm4";
                String licFilePath = licenseDirPath + File.separator + "license_encrypted__testuser_" + now + ".lic";
                
                // 写入加密数据到文件
                Files.write(Paths.get(sm4FilePath), sm4Encrypt.getBytes());
                System.out.println("加密文件已生成: " + sm4FilePath);
                
                // 解密文件并保存到lic文件
                boolean decryptSuccess = decryptSm4FileToFile(sm4FilePath, LicenseSecretKeyEnum.LICENSE_SECRET_KEY.getSecretKey(), licFilePath);
                if (decryptSuccess) {
                    System.out.println("解密文件已生成: " + licFilePath);
                    
                    // 验证解密内容
                    String decrypted = new String(Files.readAllBytes(Paths.get(licFilePath)), StandardCharsets.UTF_8);
                    System.out.println("解密内容: " + decrypted);
                    if (jsonContent.equals(decrypted)) {
                        System.out.println("加解密测试通过！");
                    } else {
                        System.err.println("加解密测试失败！");
                    }
                } else {
                    System.err.println("解密文件失败！");
                }
            } else {
                System.err.println("加密失败，返回结果为null");
            }
        } catch (Exception e) {
            System.err.println("处理过程中发生错误: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

