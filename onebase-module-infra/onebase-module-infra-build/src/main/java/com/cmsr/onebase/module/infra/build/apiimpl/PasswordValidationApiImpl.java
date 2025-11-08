package com.cmsr.onebase.module.infra.build.apiimpl;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.module.infra.api.security.PasswordValidationApi;
import com.cmsr.onebase.module.infra.service.security.manager.PasswordPolicyManager;
import com.cmsr.onebase.module.infra.service.security.validator.PasswordValidator;
import com.cmsr.onebase.module.infra.service.security.validator.config.PasswordPolicyConfig;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.cmsr.onebase.framework.common.pojo.CommonResult.success;
import static com.cmsr.onebase.module.infra.enums.ErrorCodeConstants.WEAK_PASSWORD_TENANT_EMPTY;

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
    public class PasswordValidationApiImpl implements PasswordValidationApi {

        @Resource
        private PasswordPolicyManager passwordPolicyManager;

        private final PasswordValidator passwordValidator = new PasswordValidator();

        @Override
        @Operation(summary = "校验密码强度")
        public CommonResult<Boolean> validatePassword(@RequestParam("tenantId") Long tenantId, @RequestParam("password") String password) {
            if (tenantId == null) {
                throw exception(WEAK_PASSWORD_TENANT_EMPTY);
            }

            // 获取租户的密码策略配置
            PasswordPolicyConfig config = passwordPolicyManager.getPolicyConfig(tenantId);

            // 检查是否启用了弱密码校验
            if (!Boolean.TRUE.equals(config.getEnableWeakPassword())) {
                log.debug("租户{}未启用弱密码校验", tenantId);
                return success(Boolean.TRUE);
            }

            // 执行密码校验
            passwordValidator.validate(password, config);

            log.debug("租户{}的密码校验通过", tenantId);
            return success(Boolean.TRUE);
        }
    }
