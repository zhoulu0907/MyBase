package com.cmsr.onebase.module.infra.service.security;

import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.cmsr.onebase.module.infra.dal.redis.RedisKeyConstants.REDIS_KEY_FAIL_COUNT;
import static com.cmsr.onebase.module.infra.dal.redis.RedisKeyConstants.REDIS_KEY_FAIL_SHARED_LOCK;
import static com.cmsr.onebase.module.infra.dal.redis.RedisKeyConstants.REDIS_KEY_LOCK;

import com.cmsr.onebase.module.infra.dal.database.SecurityRecordDataRepository;
import com.cmsr.onebase.module.infra.service.security.dto.LoginFailureResult;
import com.cmsr.onebase.module.infra.dal.dataobject.security.SecurityRecordDO;
import com.cmsr.onebase.module.infra.dal.vo.security.SecurityConfigItemRespVO;
import com.cmsr.onebase.module.infra.enums.security.SecurityConfigKey;
import com.cmsr.onebase.module.infra.enums.security.SecurityRecordTypeEnum;
import com.cmsr.onebase.module.infra.enums.ErrorCodeConstants;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 防暴力破解服务实现类
 *
 * @author chengyuansen
 * @date 2025-11-14
 */
@Slf4j
@Service
public class AntiBruteForceServiceImpl implements AntiBruteForceService {
    /**
     * 默认失败锁定阈值
     */
    private static final int DEFAULT_FAILED_LOCK_THRESHOLD = 5;

    /**
     * 默认锁定时长（分钟）
     */
    private static final int DEFAULT_LOCK_DURATION = 30;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private SecurityConfigService securityConfigService;

    @Resource
    private SecurityRecordDataRepository securityRecordDataRepository;

    @Override
    public Long checkAccountLocked(Long tenantId, Long userId) {
        String lockKey = buildLockKey(tenantId, userId);
        
        // 先检查 key 是否存在
        Boolean exists = stringRedisTemplate.hasKey(lockKey);
        if (Boolean.TRUE.equals(exists)) {
            Long ttl = stringRedisTemplate.getExpire(lockKey, TimeUnit.SECONDS);
            
            // ttl == -1 表示 key 存在但没有过期时间（手动解锁方式）
            // ttl > 0 表示 key 存在且有过期时间（自动解锁方式）
            if (ttl != null && (ttl == -1 || ttl > 0)) {
                log.debug("账号已锁定，tenantId: {}, userId: {}, TTL: {}", tenantId, userId, ttl);
                
                // 格式化锁定时间提示
                String timeHint;
                if (ttl == -1) {
                    // 手动解锁方式，没有过期时间
                    timeHint = "请联系管理员解锁";
                } else {
                    // 自动解锁方式，显示剩余时间
                    timeHint = formatLockTimeHint(ttl);
                }
                
                // 抛出异常，包含锁定时间提示
                throw exception(ErrorCodeConstants.AUTH_LOGIN_ACCOUNT_LOCKED, timeHint);
            }
        }

        return null;
    }

    @Override
    public LoginFailureResult recordLoginFailure(Long tenantId, Long userId) {
        return recordLoginFailureInternal(tenantId, userId, 3);
    }
    
    /**
     * 带重试次数限制的登录失败记录
     */
    private LoginFailureResult recordLoginFailureInternal(Long tenantId, Long userId, int maxRetries) {
        if (maxRetries <= 0) {
            log.error("记录登录失败超过最大重试次数，tenantId: {}, userId: {}", tenantId, userId);
            throw exception(ErrorCodeConstants.AUTH_LOGIN_ACCOUNT_LOCKED, "系统繁忙，请稍后重试");
        }
        
        log.info("记录登录失败，tenantId: {}, userId: {}, 剩余重试次数: {}", tenantId, userId, maxRetries);

        // 获取租户防暴力破解配置
        AntiBruteForceConfig config = getAntiBruteForceConfig(tenantId);

        String failKey = buildFailKey(tenantId, userId);
        String lockKey = buildLockKey(tenantId, userId);
        
        // 使用分布式锁防止并发竞态条件
        String lockIdentifier = String.format(REDIS_KEY_FAIL_SHARED_LOCK, tenantId, userId);
        Boolean acquired = stringRedisTemplate.opsForValue().setIfAbsent(lockIdentifier, "1", 5, TimeUnit.SECONDS);
        if (Boolean.FALSE.equals(acquired)) {
            // 其他线程正在处理，短暂等待后重试
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw exception(ErrorCodeConstants.AUTH_LOGIN_ACCOUNT_LOCKED, "系统繁忙，请稍后重试");
            }
            // 直接递归重试，不检查锁定状态（避免异常干扰）
            return recordLoginFailureInternal(tenantId, userId, maxRetries - 1);
        }
        
        try {
            return doRecordLoginFailure(tenantId, userId, config, failKey, lockKey);
        } finally {
            stringRedisTemplate.delete(lockIdentifier);
        }
    }
    
    /**
     * 执行登录失败记录（内部方法，已加锁保护）
     */
    private LoginFailureResult doRecordLoginFailure(Long tenantId, Long userId, AntiBruteForceConfig config, String failKey, String lockKey) {
        // 增加失败次数（已有分布式锁保护，无需再检查锁定状态）
        Long failCount = stringRedisTemplate.opsForValue().increment(failKey);
        
        // 如果是第一次失败，立即设置过期时间（防止内存泄漏）
        if (failCount == 1) {
            stringRedisTemplate.expire(failKey, config.lockDuration(), TimeUnit.MINUTES);
        }
        
        log.debug("当前失败次数: {}, 阈值: {}", failCount, config.failedLockThreshold());

        // 判断是否需要锁定
        if (failCount >= config.failedLockThreshold()) {
            // 执行锁定操作
            lockAccount(tenantId, userId, config.lockDuration(), config.unlockMethod(), lockKey, failKey);

            // 保存锁定记录到数据库
            saveLockRecord(tenantId, userId, config.lockDuration());

            long lockSeconds = config.lockDuration() * 60L;
            
            // 格式化锁定时间提示
            String timeHint = formatLockTimeHint(lockSeconds);
            
            // 达到阈值，直接抛出异常
            throw exception(ErrorCodeConstants.AUTH_LOGIN_ACCOUNT_LOCKED, timeHint);
        } else {
            // 未锁定，返回剩余次数
            int remainingAttempts = config.failedLockThreshold() - failCount.intValue();

            return LoginFailureResult.builder()
                    .locked(false)
                    .remainingAttempts(remainingAttempts)
                    .remainingLockSeconds(null)
                    .message(String.format("用户名或密码错误，还剩%d次尝试机会", remainingAttempts))
                    .build();
        }
    }

    @Override
    public void clearLoginFailureRecord(Long tenantId, Long userId) {
        log.info("清除登录失败记录，tenantId: {}, userId: {}", tenantId, userId);

        String failKey = buildFailKey(tenantId, userId);
        String lockKey = buildLockKey(tenantId, userId);

        // 清除失败次数记录
        stringRedisTemplate.delete(failKey);
        
        // 只清除自动解锁方式的锁定状态（有TTL的），手动锁定需要管理员解锁
        Boolean lockExists = stringRedisTemplate.hasKey(lockKey);
        if (Boolean.TRUE.equals(lockExists)) {
            Long ttl = stringRedisTemplate.getExpire(lockKey, TimeUnit.SECONDS);
            // 只有自动解锁方式（ttl > 0）才能通过登录成功自动清除
            if (ttl != null && ttl > 0) {
                stringRedisTemplate.delete(lockKey);
                log.debug("已清除自动解锁方式的锁定状态");
            } else if (ttl != null && ttl == -1) {
                log.info("账号处于手动锁定状态，需要管理员解锁，tenantId: {}, userId: {}", tenantId, userId);
            }
        }

        log.debug("已清除失败记录");
    }

    /**
     * 锁定账号
     *
     * @param tenantId     租户ID
     * @param userId       用户ID
     * @param lockDuration 锁定时长（分钟）
     * @param unlockMethod 解锁方式
     * @param lockKey      锁定Key
     * @param failKey      失败次数Key
     */
    private void lockAccount(Long tenantId, Long userId, int lockDuration, String unlockMethod, String lockKey, String failKey) {
        long lockSeconds = lockDuration * 60L;

        // 如果 unlockMethod 为空或无效，使用默认值 auto
        String effectiveUnlockMethod = unlockMethod;
        if (effectiveUnlockMethod == null || effectiveUnlockMethod.trim().isEmpty()) {
            effectiveUnlockMethod = SecurityConfigKey.UnlockMethodOption.auto.getKey();
            log.warn("unlockMethod 为空，使用默认值: auto, tenantId: {}, userId: {}", tenantId, userId);
        }

        // 判断解锁方式，决定是否设置Redis过期时间
        if (SecurityConfigKey.UnlockMethodOption.auto.getKey().equals(effectiveUnlockMethod)) {
            // 自动解锁方式：设置Redis过期时间，时间到期后自动解锁
            stringRedisTemplate.opsForValue().set(lockKey, String.valueOf(System.currentTimeMillis()), lockSeconds, TimeUnit.SECONDS);
            log.info("账号已锁定（自动解锁方式），tenantId: {}, userId: {}, 锁定时长: {}分钟", tenantId, userId, lockDuration);
        } else {
            // 其他解锁方式（如manual等）：不设置过期时间，需要手动解锁
            stringRedisTemplate.opsForValue().set(lockKey, String.valueOf(System.currentTimeMillis()));
            log.info("账号已锁定（{}解锁方式），tenantId: {}, userId: {}", effectiveUnlockMethod, tenantId, userId);
        }

        // 清除失败次数记录
        stringRedisTemplate.delete(failKey);
    }

    /**
     * 保存锁定记录到数据库
     *
     * @param tenantId     租户ID
     * @param userId       用户ID
     * @param lockDuration 锁定时长（分钟）
     */
    private void saveLockRecord(Long tenantId, Long userId, int lockDuration) {
        SecurityRecordDO record = SecurityRecordDO.builder()
                .tenantId(tenantId)
                .userId(userId)
                .recordType(SecurityRecordTypeEnum.LOGIN_LOCKED.getCode())
                .recordValue(String.valueOf(lockDuration))
                .build();

        securityRecordDataRepository.insert(record);
        log.debug("已保存锁定记录到数据库，tenantId: {}, userId: {}", tenantId, userId);
    }

    /**
     * 保存解锁记录到数据库
     *
     * 该方法将在实现手动解锁功能时使用。
     * 目前仅实现了自动解锁（Redis TTL过期），预留此方法用于扩展。
     *
     * @param tenantId      租户ID
     * @param userId        用户ID
     * @param unlockMethod  解锁方式（如：manual手动解锁、admin管理员解锁等）
     */
    @SuppressWarnings("unused")
    private void saveUnlockRecord(Long tenantId, Long userId, String unlockMethod) {
        SecurityRecordDO record = SecurityRecordDO.builder()
                .tenantId(tenantId)
                .userId(userId)
                .recordType(SecurityRecordTypeEnum.LOGIN_UNLOCKED.getCode())
                .recordValue(unlockMethod)
                .build();

        securityRecordDataRepository.insert(record);
        log.debug("已保存解锁记录到数据库，tenantId: {}, userId: {}, 解锁方式: {}", tenantId, userId, unlockMethod);
    }

    /**
     * 获取租户防暴力破解配置
     *
     * @param tenantId 租户ID
     * @return 防暴力破解配置
     */
    private AntiBruteForceConfig getAntiBruteForceConfig(Long tenantId) {
        // 从缓存中获取租户所有安全配置项
        List<SecurityConfigItemRespVO> configItems = securityConfigService.getSecurityConfigsByTenant(tenantId);

        // 转换为Map便于快速查找
        Map<String, String> configMap = configItems.stream()
                .collect(Collectors.toMap(SecurityConfigItemRespVO::getConfigKey, SecurityConfigItemRespVO::getConfigValue));

        // 解析配置
        int failedLockThreshold = parseIntConfig(configMap.get(SecurityConfigKey.failedLockThreshold.getConfigKey()), DEFAULT_FAILED_LOCK_THRESHOLD);
        int lockDuration = parseIntConfig(configMap.get(SecurityConfigKey.lockDuration.getConfigKey()), DEFAULT_LOCK_DURATION);
        String unlockMethod = configMap.get(SecurityConfigKey.unlockMethod.getConfigKey());

        return new AntiBruteForceConfig(failedLockThreshold, lockDuration, unlockMethod);
    }

    /**
     * 解析整型配置
     *
     * @param value        配置值
     * @param defaultValue 默认值
     * @return 解析结果
     */
    private int parseIntConfig(String value, int defaultValue) {
        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }

        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            log.warn("配置值解析失败，使用默认值，value: {}, defaultValue: {}", value, defaultValue);
            return defaultValue;
        }
    }

    /**
     * 构建失败次数Redis Key
     *
     * @param tenantId 租户ID
     * @param userId   用户ID
     * @return Redis Key
     */
    private String buildFailKey(Long tenantId, Long userId) {
        return REDIS_KEY_FAIL_COUNT + tenantId + ":" + userId;
    }

    /**
     * 构建锁定状态Redis Key
     *
     * @param tenantId 租户ID
     * @param userId   用户ID
     * @return Redis Key
     */
    private String buildLockKey(Long tenantId, Long userId) {
        return REDIS_KEY_LOCK + tenantId + ":" + userId;
    }

    /**
     * 格式化锁定提示消息
     *
     * @param lockSeconds 锁定秒数
     * @return 提示消息
     */
    private String formatLockTimeHint(long lockSeconds) {
        long minutes = lockSeconds / 60;
        long seconds = lockSeconds % 60;

        if (minutes > 0 && seconds > 0) {
            // 既有分钟又有秒数
            return String.format("%d分钟%d秒", minutes, seconds);
        } else if (minutes > 0) {
            // 只有分钟，没有秒数
            return String.format("%d分钟", minutes);
        } else {
            // 少于1分钟，只显示秒数
            return String.format("%d秒", Math.max(seconds, 1));
        }
    }

    /**
     * 防暴力破解配置内部类
     */
    private record AntiBruteForceConfig(int failedLockThreshold, int lockDuration, String unlockMethod) { }

}
