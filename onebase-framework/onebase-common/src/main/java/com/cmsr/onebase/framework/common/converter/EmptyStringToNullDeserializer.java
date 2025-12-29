package com.cmsr.onebase.framework.common.converter;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;

/**
 * 空字符串转Null反序列化器
 *
 * <p>用于处理前端传递空字符串的情况，自动将空字符串转换为null。
 * 这样可以避免后端处理空字符串时出现异常或逻辑错误。</p>
 *
 * @author matianyu
 * @date 2025-12-29
 */
public class EmptyStringToNullDeserializer extends JsonDeserializer<String> {

    @Override
    public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String value = p.getValueAsString();
        // 如果是空字符串或仅包含空白字符，返回null
        return StringUtils.isBlank(value) ? null : value;
    }
}
