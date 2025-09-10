package com.cmsr.onebase.framework.common.util.licensecheck;

import com.cmsr.onebase.framework.common.biz.system.license.LicenseCommonApi;
import com.cmsr.onebase.framework.common.biz.system.license.dto.LicenseRespDTO;
import com.cmsr.onebase.framework.common.exception.enums.GlobalErrorCodeConstants;
import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.exception0;

/**
 * License 校验工具类，提供静态和实例两种使用方式。
 * <p>
 * 该类负责从 Redis 缓存或远程服务获取当前有效的 License 信息，并对有效性（是否启用、是否过期）进行校验。
 * 为了兼容历史调用（例如在拦截器中无法方便注入 Bean 的场景），同时暴露静态方法：
 * - public static void checkLicense(LicenseCommonApi, StringRedisTemplate)
 * - public static void clearLicenseCache(StringRedisTemplate)
 *
 * 建议在 Spring 环境中注入此 Bean 后，调用实例方法 checkLicense() / clearLicenseCache()。
 *
 * @author matianyu
 * @date 2025-09-10
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class LicenseCheckUtils {

    /** Redis 中存储的 License 开关状态值（表示启用） */
    private static final String LICENSE_STATUS_ENABLE = "enable";
    /** Redis Key：当前有效的 License */
    private static final String LICENSE_KEY = "system:license:current";
    /** Redis 缓存过期时间（默认 5 分钟） */
    private static final long CACHE_TTL_MINUTES = 5L;

    // 使用构造器注入，避免对 @Resource 注解的依赖，保持测试和可维护性
    private final LicenseCommonApi licenseCommonApi;

    private final StringRedisTemplate stringRedisTemplate;

    /**
     * 使用注入的依赖检查当前系统 License 是否存在且未过期。
     *
     * @return 返回 true 表示 License 有效
     * @throws RuntimeException 当没有启用的凭证或凭证已过期时，抛出带有对应错误码的业务异常
     */
    public boolean checkLicense() {
        checkLicense(this.licenseCommonApi, this.stringRedisTemplate);
        return true;
    }

    /**
     * 静态方法：检查系统 License 是否存在且未过期（向后兼容，便于在无法注入 Bean 的场景使用）。
     *
     * @param licenseCommonApi    License 远程 API（不得为 null）
     * @param stringRedisTemplate Redis 模板，可为 null，表示跳过缓存
     * @throws RuntimeException 当没有启用的凭证或凭证已过期时，抛出带有对应错误码的业务异常
     */
    public static void checkLicense(LicenseCommonApi licenseCommonApi, StringRedisTemplate stringRedisTemplate) {
        if (licenseCommonApi == null) {
            // 没有远程服务，直接认为未启用
            logStaticWarn("LicenseCommonApi 未提供，无法校验 License");
            throw exception0(GlobalErrorCodeConstants.LICENSE_NOT_ENABLE.getCode(), "没有启用状态的凭证");
        }

        LicenseRespDTO licenseRespDTO = getLicenseFromCache(licenseCommonApi, stringRedisTemplate);
        if (licenseRespDTO == null) {
            logStaticWarn("未找到启用状态的 License");
            throw exception0(GlobalErrorCodeConstants.LICENSE_NOT_ENABLE.getCode(), "没有启用状态的凭证");
        }

        LocalDateTime expireTime = licenseRespDTO.getExpireTime();
        if (expireTime == null) {
            logStaticWarn("License 未包含过期时间，视为无效：{}", JsonUtils.toJsonString(licenseRespDTO));
            clearLicenseCache(stringRedisTemplate);
            throw exception0(GlobalErrorCodeConstants.LICENSE_IS_EXPIRED.getCode(), "凭证已过期或无效");
        }

        if (expireTime.isBefore(LocalDateTime.now())) {
            // 从缓存中清除过期的 license
            clearLicenseCache(stringRedisTemplate);
            throw exception0(GlobalErrorCodeConstants.LICENSE_IS_EXPIRED.getCode(), "凭证已过期");
        }
    }

    /**
     * 从缓存获取 License 信息，如果缓存中没有则从远程服务查询并存入缓存（静态版）。
     *
     * @param licenseCommonApi    License 远程 API（不能为空）
     * @param stringRedisTemplate Redis 模板，可为 null
     * @return License 信息，找不到时返回 null
     */
    private static LicenseRespDTO getLicenseFromCache(LicenseCommonApi licenseCommonApi, StringRedisTemplate stringRedisTemplate) {
        try {
            String licenseJson = null;
            if (stringRedisTemplate != null) {
                try {
                    licenseJson = stringRedisTemplate.opsForValue().get(LICENSE_KEY);
                } catch (Exception e) {
                    logStaticWarn("读取 License 缓存失败，key={}", LICENSE_KEY, e);
                }
            }

            if (licenseJson != null && !licenseJson.isEmpty()) {
                try {
                    return JsonUtils.parseObject(licenseJson, LicenseRespDTO.class);
                } catch (Exception e) {
                    logStaticWarn("解析缓存中的 License 失败，key={}，json={}，将尝试从远程获取", LICENSE_KEY, licenseJson, e);
                }
            }

            // 缓存未命中或解析失败，从远程服务获取
            CommonResult<LicenseRespDTO> resp = null;
            try {
                resp = licenseCommonApi.getLicenseByStatus(LICENSE_STATUS_ENABLE);
            } catch (Exception e) {
                logStaticWarn("调用 licenseCommonApi.getLicenseByStatus 失败", e);
            }

            if (resp == null) {
                logStaticWarn("远程调用 getLicenseByStatus 返回空结果");
                return null;
            }

            LicenseRespDTO licenseRespDTO = resp.getData();
            if (licenseRespDTO != null && stringRedisTemplate != null) {
                try {
                    stringRedisTemplate.opsForValue().set(LICENSE_KEY, JsonUtils.toJsonString(licenseRespDTO),
                            CACHE_TTL_MINUTES, TimeUnit.MINUTES);
                } catch (Exception e) {
                    logStaticWarn("写入 License 缓存失败，key={}", LICENSE_KEY, e);
                }
            }

            return licenseRespDTO;
        } catch (Exception e) {
            logStaticError("获取 License 信息失败", e);
            return null;
        }
    }

    /**
     * 清除 Redis 中的 License 缓存（实例方法，使用注入的 RedisTemplate）。
     */
    public void clearLicenseCache() {
        clearLicenseCache(this.stringRedisTemplate);
    }

    /**
     * 静态方法：清除 Redis 中的 License 缓存（向后兼容）。
     *
     * @param stringRedisTemplate Redis 模板，不能为空
     */
    public static void clearLicenseCache(StringRedisTemplate stringRedisTemplate) {
        if (stringRedisTemplate == null) {
            logStaticWarn("StringRedisTemplate 未提供，无法清除 License 缓存");
            return;
        }
        try {
            stringRedisTemplate.delete(LICENSE_KEY);
        } catch (Exception e) {
            logStaticError("清除 License 缓存失败", e);
        }
    }

    // ----------------- 辅助日志方法（用于静态方法中记录日志） -----------------
    private static void logStaticWarn(String pattern, Object... params) {
        if (log != null) {
            try {
                // lombok 的 log 在静态上下文下可以直接使用
                log.warn(pattern, params);
            } catch (Throwable ignored) {
                // ignore logging failure
            }
        }
    }

    private static void logStaticError(String pattern, Object... params) {
        if (log != null) {
            try {
                log.error(pattern, params);
            } catch (Throwable ignored) {
            }
        }
    }

}