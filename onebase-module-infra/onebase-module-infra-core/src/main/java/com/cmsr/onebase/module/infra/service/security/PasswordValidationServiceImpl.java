package com.cmsr.onebase.module.infra.service.security;

import com.cmsr.onebase.framework.tenant.core.context.TenantContextHolder;
import com.cmsr.onebase.module.infra.service.security.manager.PasswordPolicyManager;
import com.cmsr.onebase.module.infra.service.security.validator.PasswordValidator;
import com.cmsr.onebase.module.infra.service.security.validator.config.PasswordPolicyConfig;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 密码校验服务实现类
 *
 * @author chengyuansen
 * @date 2025-11-07
 */
@Slf4j
@Service
public class PasswordValidationServiceImpl implements PasswordValidationService {

    @Resource
    private PasswordPolicyManager passwordPolicyManager;

    private final PasswordValidator passwordValidator = new PasswordValidator();

    @Override
    public void validatePassword(String password) {
        try {
            // 获取当前租户ID
            Long tenantId = TenantContextHolder.getTenantId();
            if (tenantId == null) {
                log.warn("未获取到租户上下文，跳过密码校验");
                return;
            }

            // 获取租户的密码策略配置
            PasswordPolicyConfig config = passwordPolicyManager.getPolicyConfig(tenantId);

            // 检查是否启用了弱密码校验
            if (!Boolean.TRUE.equals(config.getEnableWeakPassword())) {
                log.debug("租户{}未启用弱密码校验", tenantId);
                return;
            }

            // 执行密码校验
            passwordValidator.validate(password, config);

            log.debug("租户{}的密码校验通过", tenantId);
        } catch (Exception e) {
            log.error("密码校验过程中发生异常", e);
            throw e;
        }
    }

}
