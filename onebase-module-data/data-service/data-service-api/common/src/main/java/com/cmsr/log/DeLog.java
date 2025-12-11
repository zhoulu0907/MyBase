package com.cmsr.log;

import com.cmsr.constant.LogOT;
import com.cmsr.constant.LogST;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DeLog {
    String id() default "";

    String pid() default "";

    LogST st() default LogST.PANEL;

    LogOT ot();

    String stExp() default "";
}
