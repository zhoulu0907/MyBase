package com.cmsr.onebase.module.infra.service.security;

import com.cmsr.onebase.module.infra.service.security.dto.LoginFailureResult;

/**
 * 防暴力破解服务接口
 *
 * @author chengyuansen
 * @date 2025-11-14
 */
public interface AntiBruteForceService {

    /**
     * 检查账号是否被锁定
     *
     * @param tenantId 租户ID
     * @param userId   用户ID
     * @return 剩余锁定时间（秒），null表示未锁定
     */
    Long checkAccountLocked(Long tenantId, Long userId);

    /**
     * 记录登录失败，返回失败处理结果
     *
     * @param tenantId 租户ID
     * @param userId   用户ID
     * @return 失败处理结果（包含是否锁定、剩余次数等）
     */
    LoginFailureResult recordLoginFailure(Long tenantId, Long userId);

    /**
     * 登录成功后清除失败记录
     *
     * @param tenantId 租户ID
     * @param userId   用户ID
     */
    void clearLoginFailureRecord(Long tenantId, Long userId);

}
