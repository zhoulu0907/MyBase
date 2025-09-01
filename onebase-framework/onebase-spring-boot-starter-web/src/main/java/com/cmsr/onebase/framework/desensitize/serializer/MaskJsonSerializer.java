package com.cmsr.onebase.framework.desensitize.serializer;

import com.cmsr.onebase.framework.desensitize.annotation.MaskDesensitize;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.Objects;

/**
 * @Author：huangjie
 * @Date：2025/8/31 8:07
 */
public class MaskJsonSerializer extends JsonSerializer<String> implements ContextualSerializer {

    private int prefixKeep;

    private int suffixKeep;

    private String replacer;

    @Override
    public void serialize(String value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (StringUtils.isBlank(value)) {
            gen.writeString(value);
            return;
        }
        if (prefixKeep < 0 || suffixKeep < 0 || prefixKeep + suffixKeep > value.length()) {
            gen.writeString(value);
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(value, 0, prefixKeep);
        for (int i = prefixKeep; i < value.length() - suffixKeep; i++) {
            sb.append(replacer);
        }
        sb.append(value, value.length() - suffixKeep, value.length());
        gen.writeString(sb.toString());
    }

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider prov, BeanProperty property) throws JsonMappingException {
        if (property == null) {
            return prov.getDefaultNullValueSerializer();
        }
        if (!Objects.equals(property.getType().getRawClass(), String.class)) {
            return prov.findValueSerializer(property.getType(), property);
        }
        // 定义支持的注解类型数组
        MaskDesensitize annotation = property.getAnnotation(MaskDesensitize.class);
        if (annotation == null) {
            return prov.findValueSerializer(property.getType(), property);
        }
        MaskJsonSerializer serializer = new MaskJsonSerializer();
        serializer.prefixKeep = annotation.prefixKeep();
        serializer.suffixKeep = annotation.suffixKeep();
        serializer.replacer = annotation.replacer();
        return serializer;
    }
}
