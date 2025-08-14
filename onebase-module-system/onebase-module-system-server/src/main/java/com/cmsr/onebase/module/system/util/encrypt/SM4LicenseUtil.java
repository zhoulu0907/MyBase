package com.cmsr.onebase.module.system.util.encrypt;

import com.cmsr.onebase.module.system.controller.admin.license.vo.LicenseExportRespVO;
import com.cmsr.onebase.module.system.enums.license.LicenseSecretKeyEnum;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.engines.SM4Engine;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.paddings.PKCS7Padding;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.security.Security;
import java.time.format.DateTimeFormatter;

import static com.cmsr.onebase.module.system.util.encrypt.SM4Utils.sm4Decrypt;
import static com.cmsr.onebase.module.system.util.encrypt.SM4Utils.sm4Encrypt;

/**
 * 国密SM4加解密工具类
 * 用于对license.lic文件进行SM4对称加密和解密
 */
public class SM4LicenseUtil {
    
    static {
        // 添加BouncyCastleProvider支持
        Security.addProvider(new BouncyCastleProvider());
    }
    
    private static final int BLOCK_SIZE = 16;
    
    /**
     * 使用SM4算法加密文件（二进制格式）
     *
     * @param key       密钥
     * @param inputFile 输入文件
     * @param outputFile 输出加密文件
     * @throws Exception 加密异常
     */
    public static void encryptFile(String key, File inputFile, File outputFile) throws Exception {
        byte[] keyBytes = getKeyBytes(key);
        byte[] iv = generateIV();
        
        try (FileInputStream fis = new FileInputStream(inputFile);
             FileOutputStream fos = new FileOutputStream(outputFile)) {
            
            // 先写入IV到输出文件
            fos.write(iv);
            
            // 创建加密器
            PaddedBufferedBlockCipher cipher = getCipher(true, keyBytes, iv);
            
            // 处理文件流
            processStream(cipher, fis, fos);
        }
    }
    
    /**
     * 使用SM4算法解密文件
     *
     * @param key       密钥
     * @param inputFile 输入加密文件
     * @param outputFile 输出解密文件
     * @throws Exception 解密异常
     */
    public static void decryptFile(String key, File inputFile, File outputFile) throws Exception {
        byte[] keyBytes = getKeyBytes(key);
        
        try (FileInputStream fis = new FileInputStream(inputFile);
             FileOutputStream fos = new FileOutputStream(outputFile)) {
            
            // 读取IV
            byte[] iv = new byte[BLOCK_SIZE];
            if (fis.read(iv) != BLOCK_SIZE) {
                throw new IllegalArgumentException("无效的加密文件，缺少IV");
            }
            
            // 创建解密器
            PaddedBufferedBlockCipher cipher = getCipher(false, keyBytes, iv);
            
            // 处理文件流
            processStream(cipher, fis, fos);
        }
    }
    
    /**
     * 获取加解密器实例
     *
     * @param forEncryption 是否为加密模式
     * @param key           密钥
     * @param iv            初始化向量
     * @return 加解密器
     */
    private static PaddedBufferedBlockCipher getCipher(boolean forEncryption, byte[] key, byte[] iv) {
        CBCBlockCipher blockCipher = new CBCBlockCipher(new SM4Engine());
        PaddedBufferedBlockCipher cipher = new PaddedBufferedBlockCipher(blockCipher, new PKCS7Padding());
        CipherParameters params = new ParametersWithIV(new KeyParameter(key), iv);
        cipher.init(forEncryption, params);
        return cipher;
    }
    
    /**
     * 处理输入输出流的加解密
     *
     * @param cipher 加解密器
     * @param in     输入流
     * @param out    输出流
     * @throws Exception 异常
     */
    private static void processStream(PaddedBufferedBlockCipher cipher, InputStream in, OutputStream out) throws Exception {
        byte[] inBuf = new byte[1024];
        byte[] outBuf = new byte[1024 + BLOCK_SIZE];
        int len;
        
        while ((len = in.read(inBuf)) > 0) {
            int outLen = cipher.processBytes(inBuf, 0, len, outBuf, 0);
            if (outLen > 0) {
                out.write(outBuf, 0, outLen);
            }
        }
        
        int outLen = cipher.doFinal(outBuf, 0);
        if (outLen > 0) {
            out.write(outBuf, 0, outLen);
        }
    }
    
    /**
     * 获取16字节密钥
     *
     * @param key 密钥字符串
     * @return 16字节密钥
     */
    private static byte[] getKeyBytes(String key) {
        byte[] keyBytes = new byte[BLOCK_SIZE];
        byte[] src = key.getBytes(StandardCharsets.UTF_8);
        System.arraycopy(src, 0, keyBytes, 0, Math.min(src.length, BLOCK_SIZE));
        return keyBytes;
    }
    
    /**
     * 生成随机IV
     *
     * @return IV字节数组
     */
    private static byte[] generateIV() {
        byte[] iv = new byte[BLOCK_SIZE];
        new SecureRandom().nextBytes(iv);
        return iv;
    }
    
    /**
     * 使用SM4算法加密字符串，返回加密后的字节数组（前16字节为IV，后面为密文）
     *
     * @param key 密钥
     * @param plainText 明文字符串
     * @return 加密后的字节数组
     * @throws Exception 加密异常
     */
    public static byte[] encryptString(String key, String plainText) throws Exception {
        byte[] keyBytes = getKeyBytes(key);
        byte[] iv = generateIV();
        byte[] input = plainText.getBytes(StandardCharsets.UTF_8);
        PaddedBufferedBlockCipher cipher = getCipher(true, keyBytes, iv);
        byte[] output = new byte[input.length + BLOCK_SIZE];
        int outLen = cipher.processBytes(input, 0, input.length, output, 0);
        outLen += cipher.doFinal(output, outLen);
        // 拼接IV和密文
        byte[] result = new byte[iv.length + outLen];
        System.arraycopy(iv, 0, result, 0, iv.length);
        System.arraycopy(output, 0, result, iv.length, outLen);
        return result;
    }

    /**
     * 将字符串加密为SM4加密字节数组（不落盘）
     *
     * @param plainText 明文字符串
     * @return 加密后的字节数组
     * @throws Exception 加密异常
     */
    public static byte[] encryptStringToSm4Bytes(String plainText) throws Exception {
        String key = "admin123";
        return encryptString(key, plainText);
    }

    /**
     * 解密SM4加密文件�����字节数组
     *
     * @param inputFilePath 加密文件路径
     * @return 解密后的字节数组
     * @throws Exception 解密异常
     */
    public static byte[] decryptSm4FileToBytes(String inputFilePath) throws Exception {
        String key = "admin123";
        File file = new File(inputFilePath);
        byte[] allBytes = new byte[(int) file.length()];
        try (FileInputStream fis = new FileInputStream(file)) {
            fis.read(allBytes);
        }
        // 前16字节为IV
        byte[] iv = new byte[BLOCK_SIZE];
        System.arraycopy(allBytes, 0, iv, 0, BLOCK_SIZE);
        byte[] cipherBytes = new byte[allBytes.length - BLOCK_SIZE];
        System.arraycopy(allBytes, BLOCK_SIZE, cipherBytes, 0, cipherBytes.length);
        byte[] keyBytes = getKeyBytes(key);
        PaddedBufferedBlockCipher cipher = getCipher(false, keyBytes, iv);
        byte[] output = new byte[cipherBytes.length + BLOCK_SIZE];
        int outLen = cipher.processBytes(cipherBytes, 0, cipherBytes.length, output, 0);
        outLen += cipher.doFinal(output, outLen);
        byte[] result = new byte[outLen];
        System.arraycopy(output, 0, result, 0, outLen);
        return result;
    }

    /**
     * 解密SM4加密文件并生成明文文件（UTF-8编码）
     *
     * @param inputFilePath  加密文件路径
     * @param outputFilePath 解密后明文文件路径
     * @throws Exception 解密异常
     */
    public static void decryptSm4FileToFile(String inputFilePath, String outputFilePath) throws Exception {
        String key = "admin123";
        File file = new File(inputFilePath);
        
        // 检查文件是否存在
        if (!file.exists()) {
            throw new IllegalArgumentException("加密文件不存在: " + inputFilePath);
        }
        
        // 检查文件大小是否足够（至少需要一个IV块和一个数据块）
        if (file.length() < BLOCK_SIZE * 2) {
            throw new IllegalArgumentException("加密文件太小，可能已损坏: " + inputFilePath);
        }
        
        byte[] allBytes = new byte[(int) file.length()];
        try (FileInputStream fis = new FileInputStream(file)) {
            int bytesRead = fis.read(allBytes);
            if (bytesRead != allBytes.length) {
                throw new IOException("读取文件不完整，期望: " + allBytes.length + ", 实际: " + bytesRead);
            }
        }
        
        // 前16字节为IV
        byte[] iv = new byte[BLOCK_SIZE];
        System.arraycopy(allBytes, 0, iv, 0, BLOCK_SIZE);
        byte[] cipherBytes = new byte[allBytes.length - BLOCK_SIZE];
        System.arraycopy(allBytes, BLOCK_SIZE, cipherBytes, 0, cipherBytes.length);
        
        byte[] keyBytes = getKeyBytes(key);
        PaddedBufferedBlockCipher cipher = getCipher(false, keyBytes, iv);
        
        // 注意：使用PKCS7填充时，不需要严格要求数据长度是块大小的倍数
        // 但为了安全起见，仍然检查长度是否合理
        if (cipherBytes.length < BLOCK_SIZE) {
            throw new IllegalArgumentException("加密数据长度过短");
        }
        
        byte[] output = new byte[cipherBytes.length + BLOCK_SIZE];
        int outLen = cipher.processBytes(cipherBytes, 0, cipherBytes.length, output, 0);
        outLen += cipher.doFinal(output, outLen);
        
        // 写入明文文件，确保UTF-8编码
        try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(outputFilePath), StandardCharsets.UTF_8)) {
            writer.write(new String(output, 0, outLen, StandardCharsets.UTF_8));
        }
    }

    /**
     * 主方法：测试将字符串加密为SM4加密文件，并解密该文件并生成.lic解密文件
     *
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        LicenseSecretKeyEnum.LICENSE_SECRET_KEY.getSecretKey();
        try {
            // 测试明文内容
            // String plainText = "{\"enterpriseName\":\"中移（上海） 产业研究院\",\"enterpriseCode\":\"F200090910000\",\"enterpriseAddress\":\"上海市浦东金桥开发区\",\"platformType\":\"私有化部署\",\"expireTime\":\"2025-10-22 14:53:12\",\"status\":\"enable\",\"tenantLimit\":\"3\",\"userLimit\":\"4\"}";
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
            // 生成加密文件路径
            String licenseDirPath = System.getProperty("user.dir") + File.separator + "license";
            File licenseDir = new File(licenseDirPath);
            if (!licenseDir.exists()) {
                licenseDir.mkdirs();
            }
            String now = java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
            String sm4FilePath = licenseDirPath + File.separator + "license_encrypted__testuser_" + now + ".sm4";
            String licFilePath = licenseDirPath + File.separator + "license_encrypted__testuser_" + now + ".lic";
            // 加密并写入文件
            String sm4Encrypt = sm4Encrypt(LicenseSecretKeyEnum.LICENSE_SECRET_KEY.getSecretKey(), jsonContent);
            System.out.println("加密后的长度: " + sm4Encrypt.length());

            // try (FileOutputStream fos = new FileOutputStream(sm4FilePath)) {
            //     fos.write(bytes);
            // }
            Files.write(Paths.get(sm4FilePath), sm4Encrypt.getBytes());
            System.out.println("加密文件已生成: " + sm4FilePath);
            // 解密并生成.lic解密文件（确保UTF-8编码）
            // decryptSm4FileToFile(sm4FilePath, licFilePath);
            // sm4Decrypt(sm4FilePath, LicenseSecretKeyEnum.LICENSE_SECRET_KEY.getSecretKey());
            // System.out.println("解密文件已生成: " + licFilePath);
            // // 校验解密内容
            // String decrypted = new String(java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(licFilePath)), java.nio.charset.StandardCharsets.UTF_8);
            // System.out.println("解密内容: " + decrypted);
            // if (jsonContent.equals(decrypted)) {
            //     System.out.println("加解密测试通过！");
            // } else {
            //     System.err.println("加解密测试失败！");
            // }
        } catch (Exception e) {
            System.err.println("处理过程中发生错误: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
