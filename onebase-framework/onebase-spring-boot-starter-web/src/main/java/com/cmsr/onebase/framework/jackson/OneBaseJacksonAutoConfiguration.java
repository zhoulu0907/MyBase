package com.cmsr.onebase.framework.jackson;

import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import com.cmsr.onebase.framework.common.util.json.databind.TimestampLocalDateTimeDeserializer;
import com.cmsr.onebase.framework.common.util.json.databind.TimestampLocalDateTimeSerializer;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.TimeZone;

@AutoConfiguration
@Slf4j
public class OneBaseJacksonAutoConfiguration {

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jsonCustomizer() {
        return builder -> {
            builder.serializerByType(Long.class, ToStringSerializer.instance)
                    .serializerByType(BigInteger.class, ToStringSerializer.instance)
                    .serializerByType(LocalTime.class, LocalTimeSerializer.INSTANCE)
                    .deserializerByType(LocalTime.class, LocalTimeDeserializer.INSTANCE)
                    .serializerByType(LocalDate.class, LocalDateSerializer.INSTANCE)
                    .deserializerByType(LocalDate.class, LocalDateDeserializer.INSTANCE)
                    .serializerByType(LocalDateTime.class, TimestampLocalDateTimeSerializer.INSTANCE)
                    .deserializerByType(LocalDateTime.class, TimestampLocalDateTimeDeserializer.INSTANCE)
                    .failOnEmptyBeans(false)
                    .failOnUnknownProperties(false)
                    .serializationInclusion(JsonInclude.Include.NON_NULL)
                    .modules(new JavaTimeModule())
                    .timeZone(TimeZone.getDefault());
        };
    }

    //TODO 这种做法不科学，JsonUtils有自己的定义，然后很隐晦的又来了一次定义。
    @Bean
    @Deprecated
    public JsonUtils jsonUtils(ObjectMapper objectMapper) {
        JsonUtils.init(objectMapper);
        log.info("[init][初始化 JsonUtils 成功]");
        return new JsonUtils();
    }

}
