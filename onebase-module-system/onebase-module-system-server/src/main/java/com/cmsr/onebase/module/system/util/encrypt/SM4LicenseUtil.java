package com.cmsr.onebase.module.system.util.encrypt;

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
import java.security.SecureRandom;
import java.security.Security;

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
     * 主方法：用于加密license.lic文件
     *
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        try {
            String key = "admin123";
            // 使用相对路径表示license文件
            File inputFile = new File("M:\\Device\\F\\onebase-v3-be\\onebase-module-system\\onebase-module-system-server\\src\\main\\resources\\license.lic");
            File encryptedFile = new File("M:\\Device\\F\\onebase-v3-be\\onebase-module-system\\onebase-module-system-server\\src\\main\\resources\\license.lic.sm4");
            File decryptedFile = new File("M:\\Device\\F\\onebase-v3-be\\onebase-module-system\\onebase-module-system-server\\src\\main\\resources\\license_decrypted.lic");
            
            System.out.println("开始加密license文件...");
            encryptFile(key, inputFile, encryptedFile);
            System.out.println("加密完成，加密文件保存为: " + encryptedFile.getAbsolutePath());
            
            System.out.println("开始解密验证...");
            decryptFile(key, encryptedFile, decryptedFile);
            System.out.println("解密完成，解密文件保存为: " + decryptedFile.getAbsolutePath());
            
            System.out.println("验证完成！");
        } catch (Exception e) {
            System.err.println("处理过程中发生错误: " + e.getMessage());
            e.printStackTrace();
        }
    }
}