package com.cmsr.onebase.module.infra.build.security;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.tenant.core.context.TenantContextHolder;
import com.cmsr.onebase.module.infra.api.security.SecurityConfigApi;
import com.cmsr.onebase.module.infra.dal.database.SecurityRecordDataRepository;
import com.cmsr.onebase.module.infra.dal.dataobject.security.SecurityRecordDO;
import com.cmsr.onebase.module.infra.enums.security.SecurityRecordTypeEnum;
import com.cmsr.onebase.module.infra.service.security.manager.PasswordPolicyManager;
import com.cmsr.onebase.module.infra.service.security.validator.PasswordValidator;
import com.cmsr.onebase.module.infra.service.security.validator.config.PasswordPolicyConfig;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.cmsr.onebase.framework.common.pojo.CommonResult.success;
import static com.cmsr.onebase.module.infra.enums.ErrorCodeConstants.PASSWORD_IN_HISTORY;

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

        @Resource
        private SecurityRecordDataRepository securityRecordDataRepository;

        @Resource
        private PasswordEncoder passwordEncoder;

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

        @Override
        @Operation(summary = "校验密码历史")
        public CommonResult<Boolean> validatePasswordHistory(@RequestParam("userId") Long userId,
                                                              @RequestParam("password") String password) {
            // 获取租户的密码策略配置
            PasswordPolicyConfig config = passwordPolicyManager.getPolicyConfig();
            Integer historyLimit = config.getHistoryLimit();

            // 如果historyLimit=0，跳过历史密码校验
            if (historyLimit == null || historyLimit == 0) {
                return success(Boolean.TRUE);
            }

            // 查询用户的历史密码记录（按创建时间倒序，限制数量为historyLimit）
            List<SecurityRecordDO> historyRecords = securityRecordDataRepository.findByTenantUserType(
                     userId, SecurityRecordTypeEnum.PASSWORD_HISTORY.getCode(), historyLimit);

            // 检查新密码是否与历史密码重复
            for (SecurityRecordDO record : historyRecords) {
                if (passwordEncoder.matches(password, record.getRecordValue())) {
                    // 抛出业务异常
                    throw exception(PASSWORD_IN_HISTORY, historyLimit);
                }
            }

            return success(Boolean.TRUE);
        }

        @Override
        @Operation(summary = "保存密码历史")
        public CommonResult<Boolean> savePasswordHistory(@RequestParam("userId") Long userId,
                                                          @RequestParam("encodedPassword") String encodedPassword) {
            // 获取当前租户ID
            Long tenantId = TenantContextHolder.getTenantId();

            // 获取租户的密码策略配置
            PasswordPolicyConfig config = passwordPolicyManager.getPolicyConfig();

            // 创建新的历史记录
            SecurityRecordDO newRecord = SecurityRecordDO.builder()
                    .tenantId(tenantId)
                    .userId(userId)
                    .recordType(SecurityRecordTypeEnum.PASSWORD_HISTORY.getCode())
                    .recordValue(encodedPassword)
                    .build();
            securityRecordDataRepository.insert(newRecord);

            return success(Boolean.TRUE);
        }
    }
