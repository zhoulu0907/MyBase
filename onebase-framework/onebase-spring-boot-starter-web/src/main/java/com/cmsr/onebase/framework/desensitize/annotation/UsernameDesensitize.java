package com.cmsr.onebase.framework.desensitize.annotation;

import com.cmsr.onebase.framework.desensitize.serializer.UsernameJsonSerializer;
import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.lang.annotation.*;

/**
 * 用户名脱敏注解
 *
 * 用户名脱敏规则：前1位 + **（如：w**）
 */
@Documented
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotationsInside
@JsonSerialize(using = UsernameJsonSerializer.class)
public @interface UsernameDesensitize {

}