package com.cmsr.onebase.framework.desensitize.annotation;

import com.cmsr.onebase.framework.desensitize.serializer.EMailJsonSerializer;
import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
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
@JsonSerialize(using = EMailJsonSerializer.class)
public @interface EMailDesensitize {

}
