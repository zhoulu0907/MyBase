package com.cmsr.onebase.framework.desensitize.annotation;

import com.cmsr.onebase.framework.desensitize.serializer.MobileJsonSerializer;
import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.lang.annotation.*;

/**
 * 手机号
 *
 */
@Documented
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotationsInside
@JsonSerialize(using = MobileJsonSerializer.class)
public @interface MobileDesensitize {


}
