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
public class ChineseNameJsonSerializer extends JsonSerializer<String> {

    /**
     * [中文姓名] 只显示第一个汉字，其他隐藏为2个星号
     * <例子：李**>
     */
    @Override
    public void serialize(String value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (StringUtils.isEmpty(value)) {
            gen.writeString(value);
            return;
        }
        String begin = value.substring(0, 1);
        String stars = "**";
        gen.writeString(begin + stars);
    }

}
