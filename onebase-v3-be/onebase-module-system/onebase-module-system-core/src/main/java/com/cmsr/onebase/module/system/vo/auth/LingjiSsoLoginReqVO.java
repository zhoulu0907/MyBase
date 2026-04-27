package com.cmsr.onebase.module.system.vo.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 灵畿平台 SSO 登录请求 VO
 */
@Data
public class LingjiSsoLoginReqVO {

    /**
     * 授权码
     */
    @NotBlank(message = "授权码不能为空")
    private String code;

    /**
     * 设备ID（可选）
     */
    private String deviceId;
}