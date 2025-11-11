package com.cmsr.onebase.module.infra.api.security;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.module.infra.service.security.manager.PasswordPolicyManager;
import com.cmsr.onebase.module.infra.service.security.validator.PasswordValidator;
import com.cmsr.onebase.module.infra.service.security.validator.config.PasswordPolicyConfig;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.cmsr.onebase.framework.common.pojo.CommonResult.success;

/**
     * 密码校验API实现类
     *
     * 提供密码强度校验的REST端点，供其他模块通过Feign调用
     *
     * @author chengyuansen
     * @date 2025-11-07
     */
    @Slf4j
    @RestController
    public class SecurityConfigApiImpl implements SecurityConfigApi {

        @Resource
        private PasswordPolicyManager passwordPolicyManager;

        private final PasswordValidator passwordValidator = new PasswordValidator();

        @Override
        @Operation(summary = "校验密码强度")
        public CommonResult<Boolean> validatePassword(@RequestParam("password") String password) {

            // 获取租户的密码策略配置
            PasswordPolicyConfig config = passwordPolicyManager.getPolicyConfig();

            // 检查是否启用了弱密码校验
            if (!Boolean.TRUE.equals(config.getEnableWeakPassword())) {
                return success(Boolean.TRUE);
            }
            // 执行密码校验
            passwordValidator.validate(password, config);

            return success(Boolean.TRUE);
        }
    }
