package com.cmsr.onebase.module.infra.service.security;

import com.cmsr.onebase.module.infra.enums.security.SecurityConfigKey;

/**
 * 验证码安全配置服务接口
 *
 * @author chengyuansen
 * @date 2025-11-17
 */
public interface CaptchaSecurityService {

    /**
     * 获取验证码有效期配置（秒）
     * 
     * 从租户安全配置中读取expirySeconds配置项
     * 如果租户未配置，返回模板默认值
     *
     * @return 有效期（秒），默认600秒
     */
    Integer getCaptchaExpirySeconds();

    /**
     * 检查是否可以刷新验证码
     * 
     * 根据刷新间隔配置，检查距离上次刷新的时间是否足够
     * 如果刷新过快，抛出业务异常
     *
     * @param sessionKey 会话标识（IP+UserAgent）
     * @throws com.cmsr.onebase.framework.common.exception.ServiceException 刷新过快时抛异常
     */
    void checkCanRefreshCaptcha(String sessionKey);

    /**
     * 检查指定场景是否启用验证码
     * 
     * 从租户安全配置中读取enableScenarios配置项
     * 检查指定场景是否在启用列表中
     *
     * @param scenario 场景选项枚举
     * @return true=启用验证码，false=不启用验证码
     */
    Boolean isCaptchaEnabledForScenario(SecurityConfigKey.EnableScenariosOption scenario);

}
