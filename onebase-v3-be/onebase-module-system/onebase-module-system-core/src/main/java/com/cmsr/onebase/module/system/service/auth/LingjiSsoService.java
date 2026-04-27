package com.cmsr.onebase.module.system.service.auth;

import com.cmsr.onebase.module.system.vo.auth.AuthLoginRespVO;

/**
 * 灵畿平台 SSO 登录 Service 接口
 */
public interface LingjiSsoService {

    /**
     * 灵畿平台 SSO 登录
     *
     * @param code        授权码
     * @param deviceId    设备ID
     * @param projectCode 项目编码
     * @return 登录结果
     */
    AuthLoginRespVO login(String code, String deviceId, String projectCode);
}