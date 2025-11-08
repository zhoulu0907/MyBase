package com.cmsr.onebase.module.infra.service.security;

/**
 * 密码校验服务接口
 *
 * @author chengyuansen
 * @date 2025-11-07
 */
public interface PasswordValidationService {

    /**
     * 校验密码强度
     * 
     * 基于当前租户的密码策略配置，对密码进行强度检查
     * 如果密码不符合要求，会抛出异常
     *
     * @param password 待校验的密码
     * @throws com.cmsr.onebase.framework.common.exception.ServiceException 如果密码不符合强度要求
     */
    void validatePassword(String password);

}
