package com.cmsr.onebase.framework.common.annotaion;

import java.lang.annotation.*;


/**
 * 不需要HTTP API 签名
 *
 * @author Zhougang
 */
@Inherited
@Documented
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiSignIgnore {
}
