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
public class MobileJsonSerializer extends JsonSerializer<String> {

    /**
     * [手机号码] 前三位，后四位，其他隐藏
     * <例子:138******1234>
     */
    @Override
    public void serialize(String value, JsonGenerator gen, SerializerProvider serializers) throws IOException {

        // boolean mobileDesensitize = true;
        // if(!mobileDesensitize){
        //     gen.writeString(value);
        //     return;
        // }

        if (StringUtils.isEmpty(value)) {
            gen.writeString(value);
            return;
        }

        int length = value.length();
        if (length < 7) {
            gen.writeString(StringUtils.repeat("*", length));
        } else {
            String masked = value.substring(0, 3) + StringUtils.repeat("*", length - 7) + value.substring(length - 4);
            gen.writeString(masked);
        }
    }

}
