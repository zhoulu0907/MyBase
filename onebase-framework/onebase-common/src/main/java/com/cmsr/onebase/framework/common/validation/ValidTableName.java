package com.cmsr.onebase.framework.common.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

/**
 * 表名校验注解
 * 校验规则：
 * 1. 不能为空
 * 2. 只能包含英文字母、数字和下划线
 * 3. 必须以字母或下划线开头
 * 4. 不能与PostgreSQL系统表名冲突
 *
 * @author bty418
 * @date 2025-08-14
 */
@Target({
        ElementType.METHOD,
        ElementType.FIELD,
        ElementType.ANNOTATION_TYPE,
        ElementType.CONSTRUCTOR,
        ElementType.PARAMETER,
        ElementType.TYPE_USE
})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(
        validatedBy = ValidTableNameValidator.class
)
public @interface ValidTableName {

    String message() default "表名格式不正确或与系统表名冲突";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
