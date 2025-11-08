package com.cmsr.onebase.module.infra.service.security.validator.annotation;

import com.cmsr.onebase.module.infra.service.security.validator.service.CheckPasswordFieldValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 密码字段校验注解，用于标注需要进行密码强度检查的字段
 * 
 * 使用示例：
 * <pre>
 * public class LoginDto {
 *     private String phone;
 *
 *     @CheckPasswordField
 *     private String password;
 * }
 * </pre>
 *
 * @author chengyuansen
 * @date 2025-11-07
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CheckPasswordFieldValidator.class)
@Documented
public @interface CheckPasswordField {

    /**
     * 校验失败时的默认错误消息
     */
    String message() default "密码强度不符合要求";

    /**
     * 分组，用于指定在哪个分组下进行校验
     */
    Class<?>[] groups() default {};

    /**
     * 载荷，用于通过载荷提供者扩展约束校验器
     */
    Class<? extends Payload>[] payload() default {};

}
