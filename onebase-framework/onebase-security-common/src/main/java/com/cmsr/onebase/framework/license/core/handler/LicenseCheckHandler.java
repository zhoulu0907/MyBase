package com.cmsr.onebase.framework.license.core.handler;

import com.cmsr.onebase.framework.common.biz.system.license.LicenseCommonApi;
import com.cmsr.onebase.framework.common.biz.system.license.dto.LicenseRespDTO;
import com.cmsr.onebase.framework.common.exception.enums.GlobalErrorCodeConstants;
import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.exception;

/**
 * License 校验工具类，提供静态和实例两种使用方式。
 * <p>
 * 该类负责从 Redis 缓存或远程服务获取当前有效的 License 信息，并对有效性（是否启用、是否过期）进行校验。
 * 为了兼容历史调用（例如在拦截器中无法方便注入 Bean 的场景），同时暴露静态方法：
 * - public static void checkLicense(LicenseCommonApi, StringRedisTemplate)
 * - public static void clearLicenseCache(StringRedisTemplate)
 * <p>
 * 建议在 Spring 环境中注入此 Bean 后，调用实例方法 checkLicense() / clearLicenseCache()。
 *
 * @author matianyu
 * @date 2025-09-10
 */
@Slf4j
public class LicenseCheckHandler {

    /**
     * Redis Key：当前有效的 License
     */
    public static final  String LICENSE_KEY       = "tenant:license:current";
    /**
     * Redis 缓存过期时间（默认 5 分钟）
     */
    private static final long   CACHE_TTL_MINUTES = 5L;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private LicenseCommonApi licenseCommonApi;

    /**
     * 静态方法：检查系统 License 是否存在且未过期（向后兼容，便于在无法注入 Bean 的场景使用）。
     *
     * @throws RuntimeException 当没有启用的凭证或凭证已过期时，抛出带有对应错误码的业务异常
     */
    public void checkLicense() {
        LicenseRespDTO licenseRespDTO = null;
        try {
            licenseRespDTO = getActiveLicense();
        } catch (Exception e) {
            log.error("getActiveLicense error.", e);
            throw exception(GlobalErrorCodeConstants.LICENSE_GET_ERROR);
        }
        if (licenseRespDTO == null) {
            clearLicenseCache();
            log.error("未找到启用状态的 License");
            throw exception(GlobalErrorCodeConstants.LICENSE_NOT_ENABLE);
        }

        LocalDateTime expireTime = licenseRespDTO.getExpireTime();
        if (expireTime == null) {
            clearLicenseCache();
            log.error("License 未包含过期时间，视为无效：{}", JsonUtils.toJsonString(licenseRespDTO));
            throw exception(GlobalErrorCodeConstants.LICENSE_IS_EXPIRED);
        }

        if (expireTime.isBefore(LocalDateTime.now())) {
            // 从缓存中清除过期的 license
            clearLicenseCache();
            log.error("License 过期：{}", JsonUtils.toJsonString(licenseRespDTO));
            throw exception(GlobalErrorCodeConstants.LICENSE_IS_EXPIRED);
        }
    }

    /**
     * 从缓存获取 License 信息，如果缓存中没有则从远程服务查询并存入缓存（静态版）。
     *
     * @return License 信息，找不到时返回 null
     */
    private LicenseRespDTO getActiveLicense() {
        String licenseJson = stringRedisTemplate.opsForValue().get(LICENSE_KEY);

        if (StringUtils.isNotBlank(licenseJson)) {
            return JsonUtils.parseObject(licenseJson, LicenseRespDTO.class);
        }

        // 缓存未命中或解析失败，从远程服务获取
        CommonResult<LicenseRespDTO> resp = licenseCommonApi.getActiveLicense();
        if (resp == null || resp.isError()) {
            log.warn("远程调用 getLicenseByStatus 返回空结果");
            return null;
        }

        LicenseRespDTO licenseRespDTO = resp.getData();
        log.info("get license from db. --------------> {}", JsonUtils.toJsonString(licenseRespDTO));
        if (licenseRespDTO != null) {
            stringRedisTemplate.opsForValue().set(LICENSE_KEY, JsonUtils.toJsonString(licenseRespDTO), CACHE_TTL_MINUTES, TimeUnit.MINUTES);
        }

        return licenseRespDTO;
    }

    /**
     * 清除 Redis 中的 License 缓存（实例方法，使用注入的 RedisTemplate）。
     */
    public void clearLicenseCache() {
        stringRedisTemplate.delete(LICENSE_KEY);
    }
}