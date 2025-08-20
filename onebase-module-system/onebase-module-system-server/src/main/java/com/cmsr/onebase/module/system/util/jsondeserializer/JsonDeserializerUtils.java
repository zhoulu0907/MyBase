package com.cmsr.onebase.module.system.util.jsondeserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * 自定义LocalDateTime反序列化器
 * 支持多种日期时间格式的解析
 *
 * @author matianyu
 */
public class JsonDeserializerUtils extends JsonDeserializer<LocalDateTime> {
    
    /**
     * 使用 volatile 关键字防止指令重排
     * volatile 保证了多线程环境下的可见性和有序性
     */
    private static volatile JsonDeserializerUtils INSTANCE;
    
    /**
     * 支持的日期时间格式列表
     */
    private static final List<DateTimeFormatter> FORMATTERS = new ArrayList<>();
    
    static {
        // 添加支持的日期时间格式
        FORMATTERS.add(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        FORMATTERS.add(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
        FORMATTERS.add(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }
    
    /**
     * 私有构造函数，防止外部实例化
     */
    private JsonDeserializerUtils() {
        // 防止通过反射破坏单例
        if (INSTANCE != null) {
            throw new RuntimeException("不允许通过反射创建实例");
        }
    }

    @Override
    public LocalDateTime deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        String text = jsonParser.getText();
        if (text == null || text.isEmpty()) {
            return null;
        }
        
        // 尝试使用不同的格式解析日期时间
        for (DateTimeFormatter formatter : FORMATTERS) {
            try {
                return LocalDateTime.parse(text, formatter);
            } catch (DateTimeParseException e) {
                // 继续尝试下一个格式
            }
        }
        
        // 如果所有格式都失败，则抛出异常
        throw new JsonMappingException(jsonParser, "无法解析日期时间字符串: " + text);
    }

    /**
     * 获取单例实例
     * 使用双重检查锁定（Double-Check Locking）模式确保线程安全
     * 
     * @return JsonDeserializer 单例实例
     */
    public static JsonDeserializerUtils getInstance() {
        // 第一次检查，避免不必要的同步
        if (INSTANCE == null) {
            // 同步块，确保多线程环境下只有一个线程能创建实例
            synchronized (JsonDeserializerUtils.class) {
                // 第二次检查，确保实例只被创建一次
                if (INSTANCE == null) {
                    INSTANCE = new JsonDeserializerUtils();
                }
            }
        }
        return INSTANCE;
    }
    
    /**
     * 防止反序列化破坏单例
     * @return 已存在的单例实例
     */
    private Object readResolve() {
        return INSTANCE;
    }
}