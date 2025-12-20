package com.cmsr.onebase.plugin.demo.hello.http;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.crypto.symmetric.AES;
import com.cmsr.onebase.plugin.api.HttpHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Hutool加密测试接口
 * 用于验证compile scope三方依赖能否被正确打包到ZIP并被PF4J加载
 *
 * @author matianyu
 * @date 2025-12-20
 */
@RestController
public class HutoolCryptoHandler implements HttpHandler {

    /**
     * 测试Hutool加密功能
     * <p>访问路径：GET /plugin/hello-plugin/crypto?text=xxx</p>
     */
    @GetMapping("/plugin/hello-plugin/crypto")
    public Map<String, Object> cryptoTest(@RequestParam(defaultValue = "Hello OneBase Plugin") String text) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 1. MD5加密
            String md5 = DigestUtil.md5Hex(text);
            result.put("md5", md5);
            
            // 2. SHA256加密
            String sha256 = DigestUtil.sha256Hex(text);
            result.put("sha256", sha256);
            
            // 3. AES加密
            String key = "onebase123456789"; // 16位密钥
            AES aes = SecureUtil.aes(key.getBytes(StandardCharsets.UTF_8));
            String encrypted = aes.encryptHex(text);
            result.put("aesEncrypted", encrypted);
            
            // 4. AES解密验证
            String decrypted = aes.decryptStr(encrypted);
            result.put("aesDecrypted", decrypted);
            result.put("aesVerified", text.equals(decrypted));
            
            // 5. 原始文本
            result.put("originalText", text);
            
            // 6. Hutool版本信息
            result.put("hutoolVersion", "5.8.35");
            result.put("hutoolLoaded", true);
            
            // 7. 插件信息
            result.put("pluginId", "hello-plugin");
            result.put("message", "Hutool三方依赖加载成功！AES加密解密验证通过！");
            result.put("success", true);
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", e.getMessage());
            result.put("errorType", e.getClass().getName());
            result.put("hutoolLoaded", false);
        }
        
        return result;
    }
    
    /**
     * 检查Hutool是否可用
     * <p>访问路径：GET /plugin/hello-plugin/check-hutool</p>
     */
    @GetMapping("/plugin/hello-plugin/check-hutool")
    public Map<String, Object> checkHutool() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 尝试加载Hutool类
            Class<?> clazz = Class.forName("cn.hutool.crypto.SecureUtil");
            result.put("hutoolClassLoaded", true);
            result.put("hutoolClassName", clazz.getName());
            result.put("classLoader", clazz.getClassLoader().getClass().getName());
            
            // 测试简单功能
            String testMd5 = DigestUtil.md5Hex("test");
            result.put("testMd5", testMd5);
            result.put("md5Correct", "098f6bcd4621d373cade4e832627b4f6".equals(testMd5));
            
            result.put("message", "Hutool依赖检查通过！");
            result.put("success", true);
            
        } catch (ClassNotFoundException e) {
            result.put("hutoolClassLoaded", false);
            result.put("error", "Hutool类未找到: " + e.getMessage());
            result.put("success", false);
        } catch (Exception e) {
            result.put("error", e.getMessage());
            result.put("errorType", e.getClass().getName());
            result.put("success", false);
        }
        
        return result;
    }
}
