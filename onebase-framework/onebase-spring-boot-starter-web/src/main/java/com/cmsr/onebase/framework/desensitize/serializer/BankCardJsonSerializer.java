package com.cmsr.onebase.framework.desensitize.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;

/**
 * @Author：huangjie
 * @Date：2025/8/31 9:37
 */
public class BankCardJsonSerializer extends JsonSerializer<String> {

    /**
     * [银行卡号] 前六位，后四位，其他用星号隐藏每位1个星号
     * <例子:6222600**********1234>
     */
    @Override
    public void serialize(String value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (StringUtils.isEmpty(value)) {
            gen.writeString(value);
            return;
        }

        // 如果银行卡号长度小于等于10位，不进行脱敏
        if (value.length() <= 10) {
            gen.writeString(StringUtils.repeat("*", value.length()));
            return;
        }

        // 保留前六位和后四位，中间用星号替代
        String prefix = value.substring(0, 6);
        String suffix = value.substring(value.length() - 4);
        StringBuilder middle = new StringBuilder();
        for (int i = 0; i < value.length() - 10; i++) {
            middle.append('*');
        }
        String maskedValue = prefix + middle + suffix;
        gen.writeString(maskedValue);
    }

}
