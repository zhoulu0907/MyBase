package com.cmsr.onebase.framework.desensitize.serializer;

import cn.hutool.core.util.HexUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.SM2;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import jakarta.annotation.Resource;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Setter
@Component
public class PasswordDecryptoDeserializer extends JsonDeserializer<String> {

    @Resource(name = "pwdSM2")
    private SM2 sm2;

    @Override
    public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String encryptedPwd = p.getValueAsString();
        if (StringUtils.isBlank(encryptedPwd)) {
            throw new IllegalArgumentException("密码为空");
        }
        int pwdLength = encryptedPwd.length();
        // 脱敏则返回null
        if (Strings.CI.equals(encryptedPwd, StringUtils.repeat("*", pwdLength))) {
            return null;
        }
        try {
            byte[] cipherBytes = HexUtil.decodeHex(encryptedPwd);
            byte[] plainBytes = sm2.decrypt(cipherBytes, KeyType.PrivateKey);
            String plainPassword = new String(plainBytes, StandardCharsets.UTF_8);
            return plainPassword;
        } catch (Exception e) {
            throw new IllegalArgumentException("密码错误");
        }
    }
}
