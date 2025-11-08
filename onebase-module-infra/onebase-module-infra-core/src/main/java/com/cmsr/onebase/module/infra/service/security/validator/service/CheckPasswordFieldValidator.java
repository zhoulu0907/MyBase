package com.cmsr.onebase.module.infra.service.security.validator.service;

import com.cmsr.onebase.framework.common.exception.ServiceException;
import com.cmsr.onebase.module.infra.service.security.PasswordValidationService;
import com.cmsr.onebase.module.infra.service.security.validator.annotation.CheckPasswordField;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @CheckPasswordField注解的校验实现
 * 
 * 用于在Bean Validation框架中支持@CheckPasswordField注解
 * 配合@Valid注解使用，自动对标注字段进行密码强度检查
 *
 * @author chengyuansen
 * @date 2025-11-07
 */
@Slf4j
@Component
public class CheckPasswordFieldValidator implements ConstraintValidator<CheckPasswordField, String> {

    @Autowired(required = false)
    private PasswordValidationService passwordValidationService;

    @Override
    public void initialize(CheckPasswordField constraintAnnotation) {
        // 初始化方法，可以获取注解中的参数
    }

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        try {
            // 如果密码为空，不进行校验（让@NotEmpty等注解处理）
            if (password == null || password.trim().isEmpty()) {
                return true;
            }

            // 如果PasswordValidationService未注入，则跳过校验
            if (passwordValidationService == null) {
                log.warn("PasswordValidationService未注入，跳过密码强度检查");
                return true;
            }

            // 执行密码校验
            passwordValidationService.validatePassword(password);
            return true;
        } catch (ServiceException e) {
            // 禁用默认错误消息，使用自定义错误消息
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(e.getMessage())
                    .addConstraintViolation();
            return false;
        } catch (Exception e) {
            log.error("密码校验过程中发生异常", e);
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("密码校验失败，请稍后重试")
                    .addConstraintViolation();
            return false;
        }
    }

}
