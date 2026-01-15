package com.cmsr.onebase.framework.desensitize.serializer;

import cn.hutool.core.util.HexUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.SM2;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import jakarta.annotation.Resource;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * 将密码字符串进行加密传输
 *
 */
@Setter
@Component
public class PasswordEncryptoSerializer extends JsonSerializer<String> {

    @Resource(name = "pwdSM2")
    private SM2 sm2;

    @Override
    public void serialize(String value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (StringUtils.isEmpty(value)) {
            gen.writeString(value);
            return;
        }
        // 使用SM2公钥加密
        byte[] plainBytes = value.getBytes(StandardCharsets.UTF_8);
        byte[] cipherBytes = sm2.encrypt(plainBytes, KeyType.PublicKey);
        // 将加密后的字节数组转换为十六进制字符串
        gen.writeString(HexUtil.encodeHexStr(cipherBytes));
    }
}
