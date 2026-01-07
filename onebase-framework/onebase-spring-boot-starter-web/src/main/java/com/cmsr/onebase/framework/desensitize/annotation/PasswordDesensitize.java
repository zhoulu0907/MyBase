package com.cmsr.onebase.framework.desensitize.annotation;

import com.cmsr.onebase.framework.desensitize.serializer.PasswordDesensitizeDeserializer;
import com.cmsr.onebase.framework.desensitize.serializer.PasswordDesensitizeSerializer;
import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.lang.annotation.*;

/**
 * 密码
 *
 */
@Documented
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotationsInside
@JsonSerialize(using = PasswordDesensitizeSerializer.class)
@JsonDeserialize(using = PasswordDesensitizeDeserializer.class)
public @interface PasswordDesensitize {

}
