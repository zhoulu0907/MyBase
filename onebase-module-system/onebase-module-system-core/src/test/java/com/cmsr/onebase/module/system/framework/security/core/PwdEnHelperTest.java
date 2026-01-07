package com.cmsr.onebase.module.system.framework.security.core;

import cn.hutool.crypto.asymmetric.SM2;
import com.cmsr.onebase.framework.common.consts.ENConstant;
import org.bouncycastle.crypto.engines.SM2Engine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class PwdEnHelperTest {

    private PwdEnHelper pwdEnHelper;
    // 移除对SM2的Mock，使用真实实例
    private SM2 sm2;

    @Mock
    private StringRedisTemplate stringRedisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        pwdEnHelper = new PwdEnHelper();

        // 设置Redis模板的mock行为
        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("system:security:pwd-encrypt-enable")).thenReturn("true");

        // 构造与生产环境一致的SM2实例，避免encrypt返回null
        sm2 = new SM2(ENConstant.EN_P_SM2KEY, ENConstant.EN_P_SM2KEY_PUBLIC);
        sm2.setMode(SM2Engine.Mode.C1C3C2);

        // 使用反射注入真实SM2与Redis模板
        try {
            java.lang.reflect.Field sm2Field = PwdEnHelper.class.getDeclaredField("sm2");
            sm2Field.setAccessible(true);
            sm2Field.set(pwdEnHelper, sm2);

            java.lang.reflect.Field redisTemplateField = PwdEnHelper.class.getDeclaredField("stringRedisTemplate");
            redisTemplateField.setAccessible(true);
            redisTemplateField.set(pwdEnHelper, stringRedisTemplate);
        } catch (Exception e) {
            fail("Failed to inject mock dependencies: " + e.getMessage());
        }
    }

    @Test
    void encryptDecryptStr() {
        // Given
        String plainPassword = "testPassword";

        // When
        String enText = pwdEnHelper.encryptHexStr(plainPassword);
        String plainText = pwdEnHelper.decryptHexStr(enText);
        System.out.println("Encrypted Text: " + enText);
        System.out.println("Decrypted Text: " + plainText);

        // Then
        assertNotNull(enText);
        assertEquals(plainPassword, plainText);
    }


}