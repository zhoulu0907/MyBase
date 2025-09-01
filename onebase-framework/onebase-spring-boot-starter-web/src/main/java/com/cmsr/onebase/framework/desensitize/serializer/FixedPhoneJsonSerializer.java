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
public class FixedPhoneJsonSerializer extends JsonSerializer<String> {

    /**
     * [固定电话] 后四位，其他隐藏
     * <例子：****1234>
     */
    @Override
    public void serialize(String value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (StringUtils.isEmpty(value)) {
            gen.writeString(value);
            return;
        }

        int length = value.length();
        if (length <= 4) {
            gen.writeString(StringUtils.repeat("*", length));
        } else {
            String masked = StringUtils.repeat("*", length - 4) + value.substring(length - 4);
            gen.writeString(masked);
        }
    }

}
