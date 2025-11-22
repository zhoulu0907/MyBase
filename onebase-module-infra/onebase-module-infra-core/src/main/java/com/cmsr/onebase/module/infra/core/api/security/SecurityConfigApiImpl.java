package com.cmsr.onebase.module.infra.core.api.security;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.tenant.core.context.TenantContextHolder;
import com.cmsr.onebase.framework.common.biz.security.SecurityConfigApi;
import com.cmsr.onebase.framework.common.biz.security.dto.LoginFailureResultDTO;
import com.cmsr.onebase.framework.common.biz.security.dto.PasswordExpiryCheckDTO;
import com.cmsr.onebase.module.infra.dal.database.SecurityRecordDataRepository;
import com.cmsr.onebase.module.infra.dal.dataobject.security.SecurityRecordDO;
import com.cmsr.onebase.module.infra.enums.security.SecurityRecordTypeEnum;
import com.cmsr.onebase.module.infra.service.security.AntiBruteForceService;
import com.cmsr.onebase.module.infra.service.security.dto.LoginFailureResult;
import com.cmsr.onebase.module.infra.service.security.manager.PasswordPolicyManager;
import com.cmsr.onebase.module.infra.service.security.validator.PasswordValidator;
import com.cmsr.onebase.module.infra.service.security.validator.config.PasswordPolicyConfig;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
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

    @Resource
    private AntiBruteForceService antiBruteForceService;

    @Resource
    private com.cmsr.onebase.module.infra.service.security.MultiDeviceSessionServiceImpl multiDeviceSessionService;

    @Resource
    private com.cmsr.onebase.module.infra.service.security.SessionIdleService sessionIdleService;

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

    @Override
    @Operation(summary = "检查密码有效期")
    public CommonResult<PasswordExpiryCheckDTO> checkPasswordExpiry(@RequestParam("userId") Long userId) {
        // 获取租户的密码策略配置
        PasswordPolicyConfig config = passwordPolicyManager.getPolicyConfig();
        Integer expiryDays = config.getExpiryDays();

        // 如果expiryDays为null或0，不进行有效期检查
        if (expiryDays == null || expiryDays == 0) {
            return success(PasswordExpiryCheckDTO.builder()
                    .type("valid")
                    .message("密码有效期检查未启用")
                    .build());
        }

        // 查询用户最近一次密码记录
        SecurityRecordDO latestRecord = securityRecordDataRepository.findLatestByUserIdAndType(
                userId, SecurityRecordTypeEnum.PASSWORD_HISTORY.getCode());

        // 如果没有密码历史记录，说明是新用户或从未修改过密码，视为密码有效
        if (latestRecord == null) {
            return success(PasswordExpiryCheckDTO.builder()
                    .type("valid")
                    .message("未找到密码历史记录")
                    .build());
        }

        // 计算密码年龄（天数）
        LocalDateTime passwordCreateTime = latestRecord.getCreateTime();
        LocalDateTime now = LocalDateTime.now();
        long passwordAge = ChronoUnit.DAYS.between(passwordCreateTime, now);

        // 判断密码是否过期
        if (passwordAge >= expiryDays) {
            // 密码已过期
            int daysExpired = (int) (passwordAge - expiryDays);
            return success(PasswordExpiryCheckDTO.builder()
                    .type("expired")
                    .daysExpired(daysExpired)
                    .passwordAge((int) passwordAge)
                    .expiryDays(expiryDays)
                    .message(String.format("您的密码已过期%d天，请尽快修改密码", daysExpired))
                    .build());
        } else {
            // 密码未过期
            return success(PasswordExpiryCheckDTO.builder()
                    .type("valid")
                    .passwordAge((int) passwordAge)
                    .expiryDays(expiryDays)
                    .message("密码有效")
                    .build());
        }
    }

    @Override
    @Operation(summary = "检查账号是否被锁定")
    public CommonResult<Long> checkAccountLocked(@RequestParam("userId") Long userId) {
        // 获取当前租户ID
        Long tenantId = TenantContextHolder.getTenantId();

        // 检查账号锁定状态，如果被锁定则直接抛出异常
        antiBruteForceService.checkAccountLocked(tenantId, userId);

        // 如果未被锁定，返回null
        return success(null);
    }

    @Override
    @Operation(summary = "记录登录失败")
    public CommonResult<LoginFailureResultDTO> recordLoginFailure(@RequestParam("userId") Long userId) {
        // 获取当前租户ID
        Long tenantId = TenantContextHolder.getTenantId();

        // 记录登录失败，如果达到阈值会直接抛出异常
        LoginFailureResult internalResult = antiBruteForceService.recordLoginFailure(tenantId, userId);

        // 转换为API DTO
        LoginFailureResultDTO result = LoginFailureResultDTO.builder()
                .locked(internalResult.getLocked())
                .remainingAttempts(internalResult.getRemainingAttempts())
                .remainingLockSeconds(internalResult.getRemainingLockSeconds())
                .message(internalResult.getMessage())
                .build();

        return success(result);
    }

    @Override
    @Operation(summary = "清除登录失败记录")
    public CommonResult<Boolean> clearLoginFailureRecord(@RequestParam("userId") Long userId) {
        // 获取当前租户ID
        Long tenantId = TenantContextHolder.getTenantId();

        // 清除失败记录
        antiBruteForceService.clearLoginFailureRecord(tenantId, userId);

        return success(Boolean.TRUE);
    }

    @Override
    @Operation(summary = "检查并限制设备数")
    public CommonResult<List<String>> checkAndLimitDevices(@RequestParam("userId") Long userId,
                                                            @RequestParam("deviceId") String deviceId,
                                                            @RequestParam("newAccessToken") String newAccessToken) {
        List<String> removedTokens = multiDeviceSessionService.checkAndLimitDevices(userId, deviceId, newAccessToken);
        return success(removedTokens);
    }

    @Override
    @Operation(summary = "添加Token到在线设备列表")
    public CommonResult<Boolean> addOnlineDevice(@RequestParam("userId") Long userId,
                                                  @RequestParam("deviceId") String deviceId,
                                                  @RequestParam("accessToken") String accessToken) {
        multiDeviceSessionService.addOnlineDevice(userId, deviceId, accessToken);
        return success(Boolean.TRUE);
    }

    @Override
    @Operation(summary = "移除在线设备")
    public CommonResult<Boolean> removeOnlineDevice(@RequestParam(value = "tenantId", required = false) Long tenantId,
                                                     @RequestParam("userId") Long userId,
                                                     @RequestParam("accessToken") String accessToken) {
        multiDeviceSessionService.removeOnlineDevice(tenantId, userId, accessToken);
        return success(Boolean.TRUE);
    }

    @Override
    @Operation(summary = "通过Token反查设备ID")
    public CommonResult<String> findDeviceIdByToken(@RequestParam(value = "tenantId", required = false) Long tenantId,
                                                     @RequestParam("userId") Long userId,
                                                     @RequestParam("accessToken") String accessToken) {
        String deviceId = multiDeviceSessionService.findDeviceIdByToken(tenantId, userId, accessToken);
        return success(deviceId);
    }

    @Override
    @Operation(summary = "创建会话空闲Redis Key")
    public CommonResult<Boolean> createSessionIdleKey(@RequestParam("userId") Long userId,
                                                       @RequestParam("deviceId") String deviceId) {
        sessionIdleService.createRedisIdleKey(userId, deviceId);
        return success(Boolean.TRUE);
    }

    @Override
    @Operation(summary = "更新会话空闲Redis Key")
    public CommonResult<Boolean> updateSessionIdleKey(@RequestParam("tenantId") Long tenantId,
                                                       @RequestParam("userId") Long userId,
                                                       @RequestParam("deviceId") String deviceId) {
        boolean result = sessionIdleService.updateRedisIdleKey(tenantId, userId, deviceId);
        return success(result);
    }
}