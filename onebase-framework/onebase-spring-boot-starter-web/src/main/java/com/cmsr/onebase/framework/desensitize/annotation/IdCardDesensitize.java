package com.cmsr.onebase.framework.desensitize.annotation;

import com.cmsr.onebase.framework.desensitize.serializer.IdCardJsonSerializer;
import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.lang.annotation.*;

/**
 * 身份证
 *
 */
@Documented
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotationsInside
@JsonSerialize(using = IdCardJsonSerializer.class)
public @interface IdCardDesensitize {


}
