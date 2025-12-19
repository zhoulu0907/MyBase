package com.cmsr.onebase.module.system.framework.security.core;

import cn.hutool.core.util.HexUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.SM2;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.cmsr.onebase.module.system.enums.ErrorCodeConstants.AUTH_LOGIN_BAD_CREDENTIALS;

@Component
@Slf4j
public class PwdEnHelper {

    // see SecurityConfiguration#sm2()
    @Resource(name = "pwdSM2")
    private SM2 sm2;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    public boolean isEecryptEnabled() {
        return true;
        // // 从redis中读取配置
        // String enable = stringRedisTemplate.opsForValue().get("system:security:pwd-encrypt-enable");
        // // 非false(含空、其他值)即为打开
        // return !"false".equalsIgnoreCase(enable);
    }

    public String decryptHexStr(String pwd) {
        if (StringUtils.isBlank(pwd)) {
            return null;
        }
        if (!isEecryptEnabled()) {
            return pwd;
        }
        try {
            // 测试用，临时解密字符串，实际使用时请从前端获取
            byte[] cipherBytes = HexUtil.decodeHex(pwd);
            // 将十六进制字符串转换为字节数组
            byte[] plainBytes = sm2.decrypt(cipherBytes, KeyType.PrivateKey);
            String plainPassword = new String(plainBytes, java.nio.charset.StandardCharsets.UTF_8);
            return plainPassword;
        } catch (Exception e) {
            log.error("decryptHexStr SM2 解密失败", e);
            // 为避免泄露加解密细节，对外统一表现为账号或密码错误
            throw exception(AUTH_LOGIN_BAD_CREDENTIALS, "无效密码");
        }
    }


    public String encryptHexStr(String pwd) {
        if (StringUtils.isBlank(pwd)) {
            return null;
        }
        if (!isEecryptEnabled()) {
            return pwd;
        }
        try {
            byte[] encryptBytes = sm2.encrypt(pwd.getBytes(StandardCharsets.UTF_8), KeyType.PublicKey);
            return HexUtil.encodeHexStr(encryptBytes);
        } catch (Exception e) {
            log.error("encryptHexStr SM2 加密失败", e);
            // 为避免泄露加解密细节，对外统一表现为账号或密码错误
            throw exception(AUTH_LOGIN_BAD_CREDENTIALS, "加密失败");
        }
    }

}