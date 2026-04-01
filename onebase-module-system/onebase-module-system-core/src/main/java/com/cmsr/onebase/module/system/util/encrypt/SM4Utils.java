package com.cmsr.onebase.module.system.util.encrypt;

import com.cmsr.onebase.module.system.vo.license.LicenseExportRespVO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.Security;
import java.time.LocalDateTime;
import java.util.Base64;

import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.cmsr.onebase.module.system.enums.ErrorCodeConstants.LICENSE_DECRYPT_ERROR;

@Slf4j
public class SM4Utils {
    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    private static final String LICENSE_SECRET_KEY = "1234567812345678";

    /**
     * 确保密钥为16字节长度
     *
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
     *
     * @param encryptedFilePath 加密文件路径
     * @param key               解密密钥
     * @param outputFilePath    解密后内容保存路径
     * @return 解密是否成功
     */
    public static boolean decryptSm4FileToFile(String encryptedFilePath, String key, String outputFilePath) {
        try {
            // 读取加密文件内容
            byte[] encryptedBytes = Files.readAllBytes(Paths.get(encryptedFilePath));
            String encryptedContent = new String(encryptedBytes, StandardCharsets.UTF_8);

            // 解密内容
            String decryptedContent = sm4Decrypt(encryptedContent, key);
            if (StringUtils.isBlank(decryptedContent)) {
                log.error("解密文件错误, encryptedFilePath:{} ", encryptedFilePath);
                throw exception(LICENSE_DECRYPT_ERROR);
            }
            // 将解密后的内容写入输出文件
            Files.write(Paths.get(outputFilePath), decryptedContent.getBytes(StandardCharsets.UTF_8));
            return true;
        } catch (Exception e) {
            log.error("解密文件发生错误, encryptedFilePath:{} ", encryptedFilePath, e);
            throw exception(LICENSE_DECRYPT_ERROR);
        }
    }

    /**
     * 解密SM4加密文件并保存到指定文件
     *
     * @param encryptedFilePath 加密文件路径
     * @param key               解密密钥
     * @return 解密是否成功
     */
    public static String decryptSm4FileToString(String encryptedFilePath, String key) {
        try {
            // 读取加密文件内容
            byte[] encryptedBytes = Files.readAllBytes(Paths.get(encryptedFilePath));
            String encryptedContent = new String(encryptedBytes, StandardCharsets.UTF_8);

            // 解密内容
            String decryptedContent = sm4Decrypt(encryptedContent, key);
            if (StringUtils.isBlank(decryptedContent)) {
                log.error("解密文件错误, encryptedFilePath:{} ", encryptedFilePath);
                throw exception(LICENSE_DECRYPT_ERROR);
            }
            return decryptedContent;
        } catch (Exception e) {
            log.error("解密文件发生错误, encryptedFilePath:{} ", encryptedFilePath, e);
            throw exception(LICENSE_DECRYPT_ERROR);
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
        System.out.println("密钥长度: " + LICENSE_SECRET_KEY.length());
        try {
            // 测试明文内容
            LicenseExportRespVO licenseExportRespVO = new LicenseExportRespVO();

            licenseExportRespVO.setEnterpriseName("上海移动有限公司");
            licenseExportRespVO.setEnterpriseCode("F200090910001");
            licenseExportRespVO.setEnterpriseAddress("上海市浦东金桥开发区");
            licenseExportRespVO.setPlatformType("私有化部署");
            licenseExportRespVO.setExpireTime(LocalDateTime.parse("2025-12-01 08:01:48"));
            licenseExportRespVO.setStatus("disable");
            licenseExportRespVO.setTenantLimit("100");
            licenseExportRespVO.setUserLimit("300");
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            String jsonContent = objectMapper.writeValueAsString(licenseExportRespVO);

            // 使用正确的密钥进行加密
            String sm4Encrypt = sm4Encrypt(jsonContent, LICENSE_SECRET_KEY);
            if (sm4Encrypt != null) {
                System.out.println("加密后的长度: " + sm4Encrypt.length());

                // 生成加密文件路径（使用项目内相对路径，防止路径遍历）
                String licenseDirPath = System.getProperty("user.dir") + File.separator + "license";
                File licenseDir = new File(licenseDirPath);
                if (!licenseDir.exists()) {
                    licenseDir.mkdirs();
                }
                String now = java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
                // 修复：使用 license 目录下的相对路径，避免硬编码绝对路径
                String sm4FilePath = licenseDirPath + File.separator + "license_temp_" + now + ".sm4";
                String licFilePath = licenseDirPath + File.separator + "license_encrypted__testuser_" + now + ".lic";

                // 写入加密数据到文件
                Files.write(Paths.get(sm4FilePath), sm4Encrypt.getBytes());
                System.out.println("加密文件已生成: " + sm4FilePath);

                // 解密文件并保存到lic文件
                boolean decryptSuccess = decryptSm4FileToFile(sm4FilePath, LICENSE_SECRET_KEY, licFilePath);
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

